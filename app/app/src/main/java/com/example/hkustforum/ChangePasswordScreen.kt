@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.hkustforum

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.profile.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    nav: NavController,
    vm: ChangePasswordViewModel = hiltViewModel()
) {
    var oldPwd by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        vm.onDone  = { nav.popBackStack() }
        vm.onError = { Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    TextButton(onClick = { vm.change(oldPwd, newPwd) }) { Text("Save") }
                }
            )
        }
    ) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = oldPwd,
                onValueChange = { oldPwd = it },
                label = { Text("Current password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = newPwd,
                onValueChange = { newPwd = it },
                label = { Text("New password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}
