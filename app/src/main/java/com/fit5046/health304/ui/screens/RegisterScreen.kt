package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.viewmodel.AuthViewModel

/**
 * Register screen connected to Firebase Authentication through AuthViewModel.
 *
 */
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    RegisterContent(
        isLoading = authViewModel.isLoading,
        errorMessage = authViewModel.errorMessage,
        onRegisterClick = { firstName, lastName, email, password ->
            authViewModel.register(firstName, lastName, email, password, onRegisterSuccess)
        },
        onInputChanged = { authViewModel.clearError() },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterContent(
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: (firstName: String, lastName: String, email: String, password: String) -> Unit,
    onInputChanged: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val firstNameError = if (submitted) validateRequired(firstName, "First name") else null
    val lastNameError = if (submitted) validateRequired(lastName, "Last name") else null
    val emailError = if (submitted) validateEmail(email) else null
    val passwordError = if (submitted) validateNewPassword(password) else null
    // Mismatch is shown immediately; "empty" only after submitting
    val confirmError = when {
        confirmPassword.isNotEmpty() && confirmPassword != password -> "Passwords don't match"
        submitted && confirmPassword.isEmpty() -> "Please confirm your password"
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set up your profile to start tracking your health",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "* Required",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it; onInputChanged() },
            label = { Text("First Name *") },
            singleLine = true,
            isError = firstNameError != null,
            supportingText = { firstNameError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it; onInputChanged() },
            label = { Text("Last Name *") },
            singleLine = true,
            isError = lastNameError != null,
            supportingText = { lastNameError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; onInputChanged() },
            label = { Text("Email *") },
            singleLine = true,
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; onInputChanged() },
            label = { Text("Password *") },
            singleLine = true,
            isError = passwordError != null,
            // Guideline 2: the requirement is always visible; it turns into the error when invalid
            supportingText = {
                Text(passwordError ?: "Must be at least 8 characters, including a number")
            },
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; onInputChanged() },
            label = { Text("Confirm Password *") },
            singleLine = true,
            isError = confirmError != null,
            supportingText = { confirmError?.let { Text(it) } },
            visualTransformation = if (confirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Text(if (confirmPasswordVisible) "Hide" else "Show")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                submitted = true
                val formValid = validateRequired(firstName, "First name") == null &&
                        validateRequired(lastName, "Last name") == null &&
                        validateEmail(email) == null &&
                        validateNewPassword(password) == null &&
                        confirmPassword == password
                if (formValid) {
                    onRegisterClick(firstName.trim(), lastName.trim(), email.trim(), password)
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
                Text("Register")
            }
        }

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
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onNavigateToLogin) {
                Text("Log in")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    _304HealthTheme {
        RegisterContent(
            isLoading = false,
            errorMessage = null,
            onRegisterClick = { _, _, _, _ -> },
            onInputChanged = {},
            onNavigateToLogin = {}
        )
    }
}