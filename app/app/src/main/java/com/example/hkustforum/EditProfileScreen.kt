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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.profile.EditProfileViewModel

@Composable
fun EditProfileScreen(
    nav: NavController,
    vm: EditProfileViewModel = hiltViewModel()
) {
    var name     by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        vm.onDone  = { nav.popBackStack() }
        vm.onError = { Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    TextButton(onClick = { vm.save(name, username) }) { Text("Save") }
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Display name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
