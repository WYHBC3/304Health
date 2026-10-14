package com.fit5046.health304.ui.screens

import android.util.Patterns

/** Each function returns an error message, or null if the input is valid. */

fun validateRequired(value: String, fieldName: String): String? =
    if (value.isBlank()) "$fieldName is required" else null

fun validateEmail(email: String): String? = when {
    email.isBlank() -> "Email is required"
    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
        "Enter a valid email, e.g. name@example.com"
    else -> null
}

// Used only when creating a password (Register); matches the hint shown on the screen
fun validateNewPassword(password: String): String? = when {
    password.isEmpty() -> "Password is required"
    password.length < 8 -> "Password must be at least 8 characters"
    password.none { it.isDigit() } -> "Password must include at least one number"
    else -> null
}