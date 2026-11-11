package com.qualityapplied.vitacare.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import com.qualityapplied.vitacare.ui.components.SectionCard
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose600
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.clean
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.Avatar
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.components.SuccessText
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.tr
import com.qualityapplied.vitacare.ui.uriToAvatarDataUrl
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(vm: AppViewModel, onOpenNotifications: () -> Unit = {}, onLogout: () -> Unit = {}) {
    val user = vm.user
    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(tr("common.loading"), color = Slate500, fontSize = 14.sp)
        }
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember(user.name) { mutableStateOf(user.name) }
    var age by remember(user.age) { mutableStateOf(user.age?.toString() ?: "") }
    var height by remember(user.heightCm) { mutableStateOf(user.heightCm?.clean() ?: "") }
    var weight by remember(user.weightKg) { mutableStateOf(user.weightKg?.clean() ?: "") }
    var avatar by remember(user.avatarUrl) { mutableStateOf(user.avatarUrl ?: "") }
    var error by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val dataUrl = uriToAvatarDataUrl(context, uri)
            if (dataUrl == null) {
                error = "INVALID_IMAGE"
            } else {
                error = ""
                saved = false
                avatar = dataUrl
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(avatar, name, 96.dp)
            Text(tr("profile.title"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.padding(top = 12.dp))
            Text(user.email ?: "", fontSize = 12.sp, color = Slate400)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallPill(tr("profile.choosePhoto"), onClick = { picker.launch("image/*") })
                if (avatar.isNotEmpty()) {
                    SmallPill(tr("profile.removePhoto"), onClick = {
                        saved = false
                        avatar = ""
                    }, background = Slate100, textColor = Slate500)
                }
            }
        }

        Column {
            Label(tr("auth.name"))
            AppTextField(name, { name = it; saved = false }, tr("auth.name"))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                Column {
                    Label(tr("profile.age"))
                    AppTextField(age, { age = it.filter { c -> c.isDigit() }.take(3); saved = false }, tr("profile.age"), keyboardType = KeyboardType.Number)
                }
            }
            Box(Modifier.weight(1f)) {
                Column {
                    Label(tr("profile.height"))
                    AppTextField(height, { height = it; saved = false }, "cm", keyboardType = KeyboardType.Decimal)
                }
            }
            Box(Modifier.weight(1f)) {
                Column {
                    Label(tr("profile.weight"))
                    AppTextField(weight, { weight = it; saved = false }, "kg", keyboardType = KeyboardType.Decimal)
                }
            }
        }

        ErrorText(error)
        if (saved) SuccessText(tr("profile.saved"))

        PrimaryButton(
            text = if (saving) tr("auth.wait") else tr("profile.save"),
            enabled = !saving,
            onClick = {
                if (name.isBlank()) {
                    error = "INVALID_INPUT"
                    return@PrimaryButton
                }
                error = ""
                saving = true
                scope.launch {
                    try {
                        vm.updateUser(ApiClient.updateProfile(name, age, height, weight, avatar))
                        saved = true
                    } catch (e: Exception) {
                        error = vm.handleError(e)
                    } finally {
                        saving = false
                    }
                }
            },
        )

        Spacer(Modifier.height(4.dp))
        SectionCard {
            Text(tr("profile.security"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate400)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallPill("🔒 " + tr("pw.title"), onClick = { showPassword = true })
                SmallPill("🔔 " + tr("notif.title"), onClick = onOpenNotifications)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Rose50)
                .clickable(onClick = onLogout)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("🚪  " + tr("profile.logout"), color = Rose600, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(Modifier.height(8.dp))
        DeveloperCard()
        Spacer(Modifier.height(4.dp))
    }

    if (showPassword) ChangePasswordDialog(vm, onDismiss = { showPassword = false })
}

@Composable
private fun ChangePasswordDialog(vm: AppViewModel, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var newPw by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text(tr("pw.title")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!done) {
                    AppTextField(current, { current = it; error = "" }, tr("pw.current"), password = true)
                    AppTextField(newPw, { newPw = it; error = "" }, tr("pw.new"), password = true)
                    AppTextField(confirm, { confirm = it; error = "" }, tr("pw.confirm"), password = true)
                }
                ErrorText(error)
                if (done) SuccessText(tr("pw.done"))
            }
        },
        confirmButton = {
            if (!done) {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        when {
                            current.isBlank() -> error = "INVALID_INPUT"
                            newPw.length < 6 -> error = "PASSWORD_SHORT"
                            newPw != confirm -> error = "PASSWORD_MISMATCH"
                            else -> {
                                busy = true
                                scope.launch {
                                    try {
                                        ApiClient.changePassword(current, newPw)
                                        done = true
                                    } catch (e: Exception) {
                                        error = vm.handleError(e)
                                    } finally {
                                        busy = false
                                    }
                                }
                            }
                        }
                    },
                ) { Text(if (busy) tr("auth.wait") else tr("pw.save")) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (done) tr("common.close") else tr("common.cancel")) }
        },
    )
}

@Composable
private fun Label(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(bottom = 4.dp))
}
