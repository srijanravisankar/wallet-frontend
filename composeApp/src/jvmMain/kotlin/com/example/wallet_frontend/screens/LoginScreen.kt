// Create a new file: composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/screens/LoginScreen.kt

package com.example.wallet_frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.models.LoginRequest
import com.example.wallet_frontend.models.User
import com.example.wallet_frontend.network.UserApi
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = if (isSignUpMode) "Sign Up" else "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Show first name and last name only in sign up mode
                if (isSignUpMode) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            errorMessage = null
                        },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            errorMessage = null
                        },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Login/Sign Up button
                Button(
                    onClick = {
                        errorMessage = null

                        // Validation
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Email and password are required"
                            return@Button
                        }

                        if (isSignUpMode && (firstName.isBlank() || lastName.isBlank())) {
                            errorMessage = "First name and last name are required"
                            return@Button
                        }

                        isLoading = true

                        scope.launch {
                            try {
                                if (isSignUpMode) {
                                    // Sign Up
                                    val newUser = User(
                                        userId = null,
                                        firstName = firstName,
                                        lastName = lastName,
                                        email = email,
                                        password = password
                                    )
                                    val success = UserApi.signUp(newUser)

                                    if (success) {
                                        // After sign up, try to login
                                        val user = UserApi.login(email, password)
                                        if (user != null) {
                                            UserSession.login(user)
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = "Sign up successful, but login failed"
                                        }
                                    } else {
                                        errorMessage = "Sign up failed. Email might already exist"
                                    }
                                } else {
                                    // Login
                                    val user = UserApi.login(email, password)
                                    if (user != null) {
                                        UserSession.login(user)
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = "Invalid email or password"
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                                println("Login/SignUp error: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isSignUpMode) "Sign Up" else "Login")
                    }
                }

                // Toggle between login and sign up
                TextButton(
                    onClick = {
                        isSignUpMode = !isSignUpMode
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isSignUpMode)
                            "Already have an account? Login"
                        else
                            "Don't have an account? Sign Up"
                    )
                }
            }
        }
    }
}