package com.example.hkustforum


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.profile.ProfileViewModel
import androidx.compose.runtime.collectAsState
import com.example.hkustforum.ui.theme.*
// HKUST Color Scheme
val HKUSTPrimary = Color(0xFF003366)
val HKUSTPrimaryDark = Color(0xFF002244)
val HKUSTPrimaryLight = Color(0xFF004488)
val HKUSTSecondary = Color(0xFFE5E5E5)
val HKUSTAccent = Color(0xFF8A1538)
val HKUSTAccentLight = Color(0xFF9A2548)
val HKUSTHighlight = Color(0xFFB8860B)
val TextPrimary = Color(0xFF14171A)
val TextSecondary = Color(0xFF555555)
val TextLight = Color(0xFF777777)
val Background = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF7F9FC)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, vm: ProfileViewModel = hiltViewModel()) {
    val pushNotificationsEnabled = remember { mutableStateOf(true) }
    val emailNotificationsEnabled = remember { mutableStateOf(true) }
    val displayName = vm.displayName.collectAsState().value ?: "Student"
    val username    = "@${displayName.lowercase().replace(' ', '_')}"
    val major = vm.major.collectAsState().value?: "Major"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(8.dp))
                },
                actions = {
                    Text(
                        text = "Done",
                        color = HKUSTPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = HKUSTPrimary
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Header
                    ProfileHeader(displayName, username, major)

                    // Account Settings
                    SettingsSection(title = "Account") {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = "Personal Information",
                            description = "Update your personal details",
                            onClick = { navController.navigate("editProfile") }
                        )
                        SettingsItem(
                            icon = Icons.Default.AccountBox,
                            title = "Password & Security",
                            description = "Change password and security settings" ,
                            onClick = { navController.navigate("changePassword") }
                        )
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "My Posts",
                            description = "See everything you posted",
                            onClick = { navController.navigate("myPosts") }
                        )
                        SettingsItem(
                            icon = Icons.Default.Phone,
                            title = "Contact Information",
                            description = "Manage your contact details"
                        )
                    }

                    // Notification Settings
                    SettingsSection(title = "Notifications") {
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Push Notifications",
                            description = "Receive notifications on your device",
                            trailing = {
                                Switch(
                                    checked = pushNotificationsEnabled.value,
                                    onCheckedChange = { pushNotificationsEnabled.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Background,
                                        checkedTrackColor = SuccessColor,
                                        uncheckedThumbColor = Background,
                                        uncheckedTrackColor = HKUSTSecondary
                                    )
                                )
                            }
                        )
                        SettingsItem(
                            icon = Icons.Default.Email,
                            title = "Email Notifications",
                            description = "Receive notifications via email",
                            trailing = {
                                Switch(
                                    checked = emailNotificationsEnabled.value,
                                    onCheckedChange = { emailNotificationsEnabled.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Background,
                                        checkedTrackColor = SuccessColor,
                                        uncheckedThumbColor = Background,
                                        uncheckedTrackColor = HKUSTSecondary
                                    )
                                )
                            }
                        )
                    }

                    // Appearance Settings
                    SettingsSection(title = "Appearance") {
                        SettingsItem(
                            icon = Icons.Default.Settings,
                            title = "Language",
                            trailing = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "English",
                                        color = TextSecondary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                    }

                    // Danger Zone
                    SettingsSection(
                        title = "Danger Zone",
                        backgroundColor = Background,
                        titleColor = HKUSTAccent
                    ) {
                        SettingsItem(
                            icon = Icons.Default.Settings,
                            title = "Delete Account",
                            titleColor = HKUSTAccent,
                            iconColor = HKUSTAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    )
}


@Composable
fun ProfileHeader(displayName: String,
                  username   : String,
                  major: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(HKUSTSecondary)
        ) {
            Text(
                text = displayName,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = HKUSTPrimary
            )

            // Edit button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(HKUSTPrimary)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = displayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = username,
            fontSize = 16.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = major,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(280.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))
    }

    Divider(color = BorderColor, thickness = 1.dp)
}

@Composable
fun SettingsSection(
    title: String,
    backgroundColor: Color = Background,
    titleColor: Color = TextSecondary,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = titleColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp, bottom = 5.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String? = null,
    titleColor: Color = TextPrimary,
    iconColor: Color = TextSecondary,
    trailing: @Composable (() -> Unit)? = null,
    onClick     : (() -> Unit)? = null

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp))

                    Spacer(modifier = Modifier.width(15.dp))

                    Column(
                    modifier = Modifier.weight(1f)
                    ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )

                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            if (trailing != null) {
                trailing()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
    }

    Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(start = 54.dp))
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    HkustForumTheme {
//        ProfileScreen(navController = rememberNavController())
//    }
//}