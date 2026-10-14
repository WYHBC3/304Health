package com.fit5046.health304.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.viewmodel.AuthViewModel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton

/**
 * Login screen connected to Firebase Authentication through AuthViewModel.
 *
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current

    LoginContent(
        isLoading = authViewModel.isLoading,
        errorMessage = authViewModel.errorMessage,
        onLoginClick = { email, password ->
            authViewModel.login(email, password, onLoginSuccess)
        },
        onGoogleClick = {
            authViewModel.signInWithGoogle(context, onLoginSuccess)
        },
        onSendResetEmail = { email ->
            authViewModel.sendPasswordReset(email) { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        },
        onInputChanged = { authViewModel.clearError() },
        onNavigateToRegister = onNavigateToRegister
    )
}

/** Stateless UI so it can be previewed without a ViewModel (state hoisting). */
@Composable
fun LoginContent(
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: (email: String, password: String) -> Unit,
    onGoogleClick: () -> Unit,
    onSendResetEmail: (email: String) -> Unit,
    onInputChanged: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Field errors appear only after the first Log In attempt, not while the user is still typing
    val emailError = if (submitted) validateEmail(email) else null
    val passwordError = if (submitted) validateRequired(password, "Password") else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Account Login",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Log in to continue tracking your wellness journey",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onInputChanged()
            },
            label = { Text("Email") },
            singleLine = true,
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                onInputChanged()
            },
            label = { Text("Password") },
            singleLine = true,
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                // Guideline 1: show/hide password toggle
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Hide" else "Show")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Guideline 2: clearly visible "Forgot password?" option
        TextButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                submitted = true
                if (validateEmail(email) == null && password.isNotEmpty()) {
                    onLoginClick(email.trim(), password)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Log In")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  or  ",
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue with Google")
        }

        // Error returned by Firebase (wrong password, no network, ...)
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onNavigateToRegister) {
                Text("Sign up")
            }
        }
    }

    if (showResetDialog) {
        ResetPasswordDialog(
            initialEmail = email,
            onDismiss = { showResetDialog = false },
            onSend = { resetEmail ->
                onSendResetEmail(resetEmail)
                showResetDialog = false
            }
        )
    }
}

@Composable
private fun ResetPasswordDialog(
    initialEmail: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var resetEmail by remember { mutableStateOf(initialEmail) }
    var tried by remember { mutableStateOf(false) }
    val error = if (tried) validateEmail(resetEmail) else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset password") },
        text = {
            Column {
                Text("Enter your account email and we will send you a link to reset your password.")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tried = true
                if (validateEmail(resetEmail) == null) onSend(resetEmail.trim())
            }) {
                Text("Send link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    _304HealthTheme {
        LoginContent(
            isLoading = false,
            errorMessage = null,
            onLoginClick = { _, _ -> },
            onGoogleClick = {},
            onSendResetEmail = {},
            onInputChanged = {},
            onNavigateToRegister = {}
        )
    }
}