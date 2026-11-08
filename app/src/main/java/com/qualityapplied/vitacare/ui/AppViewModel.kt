package com.qualityapplied.vitacare.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qualityapplied.vitacare.data.AiLocal
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.ApiException
import com.qualityapplied.vitacare.data.ChatMsg
import com.qualityapplied.vitacare.data.NotificationCenter
import com.qualityapplied.vitacare.data.SessionStore
import com.qualityapplied.vitacare.data.User
import com.qualityapplied.vitacare.data.localizedContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

/** Holds the logged-in user. The session is restored from the phone every time the app starts. */
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SessionStore(app)

    var user by mutableStateOf(store.loadUser())
        private set

    /** Tab to show first after signing in (0 = Home, 4 = Profile after registering). */
    var startTab by mutableStateOf(0)
        private set

    /** AI assistant conversation (kept while the app is open, cleared on logout / "New chat"). */
    val chat = mutableStateListOf<ChatMsg>()
    var chatBusy by mutableStateOf(false)
        private set

    init {
        ApiClient.tokenProvider = { store.token }
        NotificationCenter.load(app)
        // Refresh the profile in the background; never log out because of a network problem.
        if (user != null) {
            onSignedIn()
            viewModelScope.launch {
                try {
                    updateUser(ApiClient.getProfile())
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    private fun onSignedIn() {
        NotificationCenter.welcomeOnce(getApplication())
        NotificationCenter.scheduleDaily(getApplication())
    }

    fun onAuth(token: String, newUser: User, goToProfile: Boolean) {
        store.save(token, newUser)
        startTab = if (goToProfile) 4 else 0
        user = newUser
        onSignedIn()
    }

    fun updateUser(newUser: User) {
        store.saveUser(newUser)
        user = newUser
    }

    fun logout() {
        store.clear()
        NotificationCenter.onLogout(getApplication())
        chat.clear()
        startTab = 0
        user = null
    }

    /** Returns the error code to display. Only an invalid/expired token logs the user out. */
    fun handleError(e: Throwable): String {
        if (e is ApiException) {
            if (e.status == 401 && e.code == "AUTH_REQUIRED") logout()
            return e.code
        }
        return "SERVER_ERROR"
    }

    // ---------- AI assistant ----------

    fun clearChat() {
        if (!chatBusy) chat.clear()
    }

    fun sendChat(text: String, language: String) {
        val msg = text.trim()
        if (msg.isEmpty() || chatBusy) return
        chat.add(ChatMsg("user", msg))
        chatBusy = true
        // The API wants the conversation to start with a user message
        val history = chat.takeLast(12).dropWhile { it.role != "user" }
        viewModelScope.launch {
            try {
                val reply = ApiClient.chat(history, language, profileForAi())
                chat.add(ChatMsg("assistant", reply))
            } catch (e: Exception) {
                if (e is ApiException && e.status == 401) {
                    handleError(e)
                } else {
                    // Server missing / offline -> simple built-in answer
                    val ctx = localizedContext(getApplication())
                    chat.add(ChatMsg("assistant", AiLocal.reply(ctx, msg, user), offline = true))
                }
            } finally {
                chatBusy = false
            }
        }
    }

    /** Only age / height / weight / BMI are shared with the AI (never name, email or photo). */
    private fun profileForAi(): JSONObject? {
        val u = user ?: return null
        val o = JSONObject()
        u.age?.let { o.put("age", it) }
        u.heightCm?.let { o.put("heightCm", it) }
        u.weightKg?.let { o.put("weightKg", it) }
        val h = u.heightCm
        val w = u.weightKg
        if (h != null && w != null && h > 0) {
            val m = h / 100.0
            o.put("bmi", String.format(Locale.US, "%.1f", w / (m * m)).toDouble())
        }
        return if (o.length() == 0) null else o
    }
}
