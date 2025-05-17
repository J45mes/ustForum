// app/src/main/java/com/example/hkustforum/RegisterScreen.kt
package com.example.hkustforum

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.auth.AuthState
import com.example.hkustforum.ui.auth.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var major       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var pwdVisible  by remember { mutableStateOf(false) }
    val state       by viewModel.registerState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // HKUST Logo
        Image(
            painter           = painterResource(id = R.drawable.ust_logo),
            contentDescription = "HKUST Logo",
            modifier           = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Create Account", style = MaterialTheme.typography.headlineMedium, color = HKUSTPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Join HKUST Forum today", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))

        // Username
        OutlinedTextField(
            value         = username,
            onValueChange = { username = it },
            label         = { Text("Username") },
            singleLine    = true,
            colors        = TextFieldDefaults.colors(
                focusedContainerColor   = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor  = Background,
                focusedIndicatorColor   = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor       = HKUSTPrimary,
                unfocusedLabelColor     = TextSecondary
            ),
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("HKUST Email (@connect.ust.hk)") },
            singleLine       = true,
            keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors           = TextFieldDefaults.colors(
                focusedContainerColor   = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor  = Background,
                focusedIndicatorColor   = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor       = HKUSTPrimary,
                unfocusedLabelColor     = TextSecondary
            ),
            modifier         = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Display Name
        OutlinedTextField(
            value         = displayName,
            onValueChange = { displayName = it },
            label         = { Text("Display Name") },
            singleLine    = true,
            colors        = TextFieldDefaults.colors(
                focusedContainerColor   = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor  = Background,
                focusedIndicatorColor   = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor       = HKUSTPrimary,
                unfocusedLabelColor     = TextSecondary
            ),
            modifier      = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        /* ----------------- major -------------------- */
        OutlinedTextField(
            value = major,
            onValueChange = { major = it },
            label = { Text("Major (e.g. Computer Science)") },
            singleLine = true,
            /* colours identical */
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value                 = password,
            onValueChange         = { password = it },
            label                 = { Text("Password") },
            singleLine            = true,
            visualTransformation  = if (pwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon          = {
                IconButton(onClick = { pwdVisible = !pwdVisible }) {
                    val iconRes = if (pwdVisible) R.drawable.visibility else R.drawable.visibility_off
                    Icon(
                        painter           = painterResource(id = iconRes),
                        contentDescription= null,
                        tint               = TextSecondary
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor  = Background,
                focusedIndicatorColor   = HKUSTPrimary,
                unfocusedIndicatorColor = BorderColor,
                focusedLabelColor       = HKUSTPrimary,
                unfocusedLabelColor     = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Button
        Button(
            onClick = {
                /* simple client‑side check for domain */
                if (!email.endsWith("@connect.ust.hk", true)) {
                    viewModel.registerState.value =
                        AuthState.Error("Use your @connect.ust.hk e‑mail")
                    return@Button
                }
                viewModel.register(
                    username, email, password, displayName, major
                ) {
                    navController.navigate("Login") {
                        popUpTo("Register") { inclusive = true }
                    }
                }
            },
            /* rest identical */
        ) {
            if (state == AuthState.Loading) {
                CircularProgressIndicator(
                    color       = Color.White,
                    strokeWidth = 2.dp,
                    modifier    = Modifier.size(24.dp)
                )
            } else {
                Text("Sign Up", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (state is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Log In",
                color    = HKUSTPrimary,
                modifier = Modifier.clickable { navController.navigate("Login") }
            )
        }
    }
}
