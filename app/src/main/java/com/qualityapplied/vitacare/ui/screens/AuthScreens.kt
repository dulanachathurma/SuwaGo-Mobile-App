package com.qualityapplied.vitacare.ui.screens

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.CredentialManagerHelper
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.SuwagoLogo
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch

private fun isValidEmail(target: String): Boolean {
    return target.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(target.trim()).matches()
}

/**
 * Password strength validation:
 * - Minimum 8 characters long
 * - Must contain simple/lowercase English letters (a-z)
 * - Must contain uppercase English letters (A-Z) or numbers (0-9)
 */
fun validatePasswordStrength(password: String): String? {
    if (password.length < 8) {
        return "PASSWORD_MIN_LENGTH"
    }
    if (!password.any { it in 'a'..'z' }) {
        return "PASSWORD_NO_LOWERCASE"
    }
    if (!password.any { it in 'A'..'Z' } && !password.any { it in '0'..'9' }) {
        return "PASSWORD_NO_UPPER_OR_NUMBER"
    }
    return null
}

@Composable
fun LoginScreen(vm: AppViewModel, onGoRegister: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Auto-suggest & retrieve saved Google / Device credentials
    LaunchedEffect(Unit) {
        val saved = CredentialManagerHelper.getSavedCredential(context)
        if (saved != null) {
            email = saved.first
            password = saved.second
        }
    }

    val emailError = email.isNotEmpty() && !isValidEmail(email)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        SuwagoLogo(fontSize = 32.sp, showTagline = true)
        Spacer(Modifier.height(6.dp))
        Text(tr("auth.welcomeBack"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Slate800)
        Spacer(Modifier.height(4.dp))

        AppTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = tr("auth.email"),
            keyboardType = KeyboardType.Email,
            isError = emailError,
            errorMessage = if (emailError) "Invalid Email" else null,
        )
        AppTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = tr("auth.password"),
            keyboardType = KeyboardType.Password,
            password = true,
        )
        ErrorText(error)

        PrimaryButton(
            text = if (loading) tr("auth.wait") else tr("auth.login"),
            enabled = !loading && !emailError,
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "MISSING_FIELDS"
                    return@PrimaryButton
                }
                error = ""
                loading = true
                scope.launch {
                    try {
                        val r = ApiClient.login(email, password)
                        // Trigger Google Credential Manager save prompt on successful login
                        CredentialManagerHelper.saveCredential(context, email, password)
                        vm.onAuth(r.token, r.user, goToProfile = false)
                    } catch (e: Exception) {
                        error = vm.handleError(e)
                    } finally {
                        loading = false
                    }
                }
            },
        )

        Row2(tr("auth.noAccount"), tr("auth.register"), onGoRegister)
        DeveloperFooter()
    }
}

@Composable
fun RegisterScreen(vm: AppViewModel, onGoLogin: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val emailError = email.isNotEmpty() && !isValidEmail(email)
    val passwordStrengthErr = if (password.isNotEmpty()) validatePasswordStrength(password) else null
    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        SuwagoLogo(fontSize = 32.sp, showTagline = true)
        Spacer(Modifier.height(6.dp))
        Text(tr("auth.createAccount"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Slate800)
        Spacer(Modifier.height(4.dp))

        AppTextField(name, { name = it }, tr("auth.name"))
        AppTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = tr("auth.email"),
            keyboardType = KeyboardType.Email,
            isError = emailError,
            errorMessage = if (emailError) "Invalid Email" else null,
        )
        AppTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = tr("auth.password"),
            keyboardType = KeyboardType.Password,
            password = true,
            isError = passwordStrengthErr != null,
            errorMessage = if (passwordStrengthErr != null) tr("err.$passwordStrengthErr") else null,
        )
        AppTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = tr("auth.confirmPassword"),
            keyboardType = KeyboardType.Password,
            password = true,
            isError = passwordMismatch,
            errorMessage = if (passwordMismatch) tr("err.PASSWORD_MISMATCH") else null,
        )
        ErrorText(error)

        PrimaryButton(
            text = if (loading) tr("auth.wait") else tr("auth.register"),
            enabled = !loading && !emailError && passwordStrengthErr == null && !passwordMismatch,
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    error = "MISSING_FIELDS"
                    return@PrimaryButton
                }
                val pwdErr = validatePasswordStrength(password)
                if (pwdErr != null) {
                    error = pwdErr
                    return@PrimaryButton
                }
                if (password != confirmPassword) {
                    error = "PASSWORD_MISMATCH"
                    return@PrimaryButton
                }
                error = ""
                loading = true
                scope.launch {
                    try {
                        val r = ApiClient.register(name, email, password)
                        // Trigger Google Credential Manager save prompt on successful registration
                        CredentialManagerHelper.saveCredential(context, email, password)
                        vm.onAuth(r.token, r.user, goToProfile = true)
                    } catch (e: Exception) {
                        error = vm.handleError(e)
                    } finally {
                        loading = false
                    }
                }
            },
        )

        Row2(tr("auth.haveAccount"), tr("auth.login"), onGoLogin)
        DeveloperFooter()
    }
}

/** "Don't have an account?  Register" line. */
@Composable
private fun Row2(question: String, action: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(question, fontSize = 14.sp, color = Slate500)
        Text(
            action,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Teal600,
            modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
        )
    }
}
