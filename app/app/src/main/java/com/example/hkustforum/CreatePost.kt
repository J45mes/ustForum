package com.example.hkustforum

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hkustforum.ui.post.PostViewModel

val ErrorColor = Color(0xFFFF3B30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(                       // <- better name but file path the same
    navController: NavController
) {
    val context              = LocalContext.current
    val vm: PostViewModel    = hiltViewModel()

    /* observe the real name; fallback to empty string */
    val userName = vm.userName.collectAsState().value ?: ""

    var postText           by remember { mutableStateOf("") }
    var selectedCategory   by remember { mutableStateOf("Computer Engineering") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showImagePicker    by remember { mutableStateOf(false) }
    var selectedImageUri   by remember { mutableStateOf<Uri?>(null) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var showSuggestions by remember { mutableStateOf(false) }
    val maxChars    = 280
    val charCount   = postText.length
    val postEnabled = charCount in 1..maxChars

    /* callbacks from ViewModel */
    LaunchedEffect(Unit) {
        vm.postSuccess = {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("refreshHome", true)
            navController.popBackStack()
        }
        vm.postError = {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    /* image picker */
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Post",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel", color = TextSecondaryColor, fontWeight = FontWeight.Medium)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            vm.createPost(
                                title    = postText.take(30),
                                category = selectedCategory,
                                content  = postText
                            )
                        },
                        enabled = postEnabled,
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = if (postEnabled) PrimaryColor else SecondaryColor,
                            contentColor   = if (postEnabled) Color.White     else TextLightColor
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("Post", fontWeight = FontWeight.SemiBold) }
                }
            )
        },
        bottomBar = {
            ActionBar(
                onCameraClick   = { showImagePicker = true },
                onImageClick    = { showImagePicker = true },
                onDownloadClick = {},
                onMoodClick     = {},
                charCount       = charCount,
                maxChars        = maxChars
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            /* ---- user info header ---- */
            UserInfoSection(
                userName         = userName.ifBlank { "Unknown" },
                selectedCategory = selectedCategory,
                onCategoryClick  = { showCategoryDialog = true }
            )


            /* ---- text field ---- */
            Box {
                BasicTextField(
                    value = postText,
                    onValueChange = { new ->
                        postText = new

                        // 1) find the last “word” beginning with “#”
                        val lastHash = new.substringAfterLast(' ')
                        if (lastHash.startsWith("#") && lastHash.length > 1) {
                            vm.fetchTagSuggestions(lastHash.drop(1))   // strip “#”
                            showSuggestions = true
                        } else {
                            showSuggestions = false
                        }
                    },
                            modifier = Modifier
                            .fillMaxWidth()
                        .padding(16.dp),

                    textStyle = TextStyle(fontSize = 18.sp, lineHeight = 27.sp, color = TextPrimaryColor),
                    decorationBox = { inner ->
                        if (postText.isEmpty()) {
                            Text("What's on your mind?", color = TextLightColor, fontSize = 18.sp)
                        }
                        inner()
                    }


                )

                DropdownMenu(
                    expanded = showSuggestions && vm.tagSuggestions.isNotEmpty(),
                    onDismissRequest = { showSuggestions = false }
                ) {
                    vm.tagSuggestions.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text("#$tag") },
                            onClick = {
                                // replace the “#partial” in the text with “#fullTag ”
                                postText = postText.replaceAfterLast('#', tag) + " "
                                selectedTag = tag
                                showSuggestions = false
                            }
                        )
                    }
                }
            }

            /* ---- optional image preview ---- */
            selectedImageUri?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter  = painterResource(id = R.drawable.project_screenshot),
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                            .size(30.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove image", tint = TextPrimaryColor)
                    }
                }
            }
        }
    }

    /* ------------- category dialog ------------- */
    if (showCategoryDialog) {
        Dialog(onDismissRequest = { showCategoryDialog = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    listOf(
                        "Computer Engineering", "Marketing", "Physics",
                        "Ocean Science", "Accounting", "Hall 1", "Hall II"
                    ).forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory   = cat
                                showCategoryDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    /* ------------- image picker dialog ------------- */
    if (showImagePicker) {
        Dialog(onDismissRequest = { showImagePicker = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    Text("Choose an option",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                imagePicker.launch("image/*")
                                showImagePicker = false
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Camera", modifier = Modifier.size(48.dp), tint = PrimaryColor)
                            Text("Camera", color = TextPrimaryColor)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                imagePicker.launch("image/*")
                                showImagePicker = false
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Gallery", modifier = Modifier.size(48.dp), tint = PrimaryColor)
                            Text("Gallery", color = TextPrimaryColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoSection(
    userName: String,
    selectedCategory: String,
    onCategoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(SecondaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(2).uppercase(),
                color = PrimaryColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            // User name
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimaryColor
            )

            // Category selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCategoryClick() }
            ) {
                Text(
                    text = "Posting to ",
                    fontSize = 14.sp,
                    color = TextSecondaryColor
                )

                Text(
                    text = selectedCategory,
                    fontSize = 14.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ActionBar(
    onCameraClick: () -> Unit,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoodClick: () -> Unit,
    charCount: Int,
    maxChars: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, BorderColor, RoundedCornerShape(0.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Camera button
        ActionButton(
            icon = Icons.Default.Done,
            onClick = onCameraClick,
            tint = PrimaryColor
        )

        // Image button
        ActionButton(
            icon = Icons.Default.AccountBox,
            onClick = onImageClick,
            tint = PrimaryColor
        )

        // Download button
        ActionButton(
            icon = Icons.Default.KeyboardArrowDown,
            onClick = onDownloadClick,
            tint = PrimaryColor
        )

        // Mood button
        ActionButton(
            icon = Icons.Default.Face,
            onClick = onMoodClick,
            tint = PrimaryColor
        )

        Spacer(modifier = Modifier.weight(1f))

        // Character count
        Text(
            text = "$charCount/$maxChars",
            color = if (charCount > maxChars) ErrorColor else TextLightColor,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(Color.LightGray, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

