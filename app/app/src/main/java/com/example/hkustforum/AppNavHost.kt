package com.example.hkustforum

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "Login") {
        composable("Login") { LoginScreen(navController) }
        composable("Register") { RegisterScreen(navController) }
        composable("Home")    { HomePage(navController) }
        composable("Profile") { ProfileScreen(navController) }
        composable("Alerts")   { AlertScreen(navController) }
        composable("Discover")   { DiscoverScreen(navController) }
        composable("post")    { CreatePostScreen(navController) }
        composable("editProfile")   { EditProfileScreen(navController) }
        composable("changePassword"){ ChangePasswordScreen(navController) }
        composable("myPosts")       { MyPostsScreen(navController) }
    }
}
