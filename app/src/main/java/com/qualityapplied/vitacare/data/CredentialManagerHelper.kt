package com.qualityapplied.vitacare.data

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException

object CredentialManagerHelper {
    private const val TAG = "CredentialManager"

    /**
     * Prompts Google Credential Manager / Smart Lock to save the user's password credential.
     */
    suspend fun saveCredential(context: Context, username: String, password: String) {
        if (username.isBlank() || password.isBlank()) return
        try {
            val credentialManager = CredentialManager.create(context)
            val request = CreatePasswordRequest(
                id = username,
                password = password
            )
            credentialManager.createCredential(context, request)
            Log.d(TAG, "Successfully saved credentials for $username")
        } catch (e: CreateCredentialException) {
            Log.w(TAG, "Save credential canceled or failed: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving credential", e)
        }
    }

    /**
     * Retrieves saved credentials using Credential Manager (Auto-suggest / Autofill).
     */
    suspend fun getSavedCredential(context: Context): Pair<String, String>? {
        try {
            val credentialManager = CredentialManager.create(context)
            val getPasswordOption = GetPasswordOption()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getPasswordOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is PasswordCredential) {
                return Pair(credential.id, credential.password)
            }
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Get credential canceled or unavailable: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving credential", e)
        }
        return null
    }
}
