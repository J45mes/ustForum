package com.example.hkustforum

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.auth.AuthState
import com.example.hkustforum.ui.auth.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val state by viewModel.loginState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ust_logo),
            contentDescription = "HKUST Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, color = HKUSTPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Sign in to continue to HKUST Forum", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("HKUST Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor = Background,
                focusedIndicatorColor = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor = HKUSTPrimary,
                unfocusedLabelColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible.value) R.drawable.visibility else R.drawable.visibility_off
                        ),
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor = Background,
                focusedIndicatorColor = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor = HKUSTPrimary,
                unfocusedLabelColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = "Forgot Password?",
                color = HKUSTPrimary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.login(email.value, password.value) {
                    navController.navigate("Home") {
                        popUpTo("Login") { inclusive = true }
                    }
                }
            },
            enabled = state != AuthState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = HKUSTPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (state == AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Text("Log In", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (state is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?", color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sign Up", color = HKUSTPrimary, modifier = Modifier.clickable {
                navController.navigate("Register")
            })
        }
    }
}