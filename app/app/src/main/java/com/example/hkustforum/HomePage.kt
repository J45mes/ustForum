package com.example.hkustforum

import com.example.hkustforum.DateTimeUtils
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.hkustforum.data.remote.dto.BlockDto
import com.example.hkustforum.data.remote.dto.PostDto
import com.example.hkustforum.ui.home.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
val topicMap = listOf(
    "Home"          to "home",
    "Computer Eng"  to "Computer Engineering",
    "Marketing"     to "marketing",
    "Physics"       to "physics",
    "Ocean Sci"     to "Ocean Science",
    "Accounting"    to "Accounting",
    "Hall 1"        to "Hall 1",
    "Hall II"       to "Hall II"
)

@Composable
fun HomePage(navController: NavController) {
    val selectedTab     = remember { mutableStateOf("Home") }
    val selectedTopicId = remember { mutableStateOf("home") }
    val vm: HomeViewModel = hiltViewModel()

    val refreshing = vm.isRefreshing
    val swipeState = rememberSwipeRefreshState(refreshing)

    // 🧠 Listen to SavedStateHandle to refresh after post
    val currentBackStackEntry = navController.currentBackStackEntry
    val shouldRefresh = currentBackStackEntry?.savedStateHandle?.get<Boolean>("refreshHome") == true
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            vm.refreshPosts()
            currentBackStackEntry?.savedStateHandle?.set("refreshHome", false)
        }
    }

    Scaffold(
        topBar = {
            Column {
                HeaderRedesign()
                TopicNavigationRedesign(
                    selectedId = selectedTopicId.value,
                    onSelect = {
                        selectedTopicId.value = it
                        vm.setCategoryFilter(it)
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationRedesign(
                selectedTab = selectedTab.value,
                onTabSelected = { tab ->
                    selectedTab.value = tab
                    navController.navigate(tab) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = { FloatingActionButtonRedesign(navController) }
    ) { padding ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = { vm.refreshPosts()
                vm.expandedIds.forEach { vm.loadBlocks(it) }
            }
        ) {
            Box(modifier = Modifier.padding(padding)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item {
                        val headerTitle = topicMap
                            .firstOrNull { it.second == selectedTopicId.value }
                            ?.first ?: "All Posts"
                        TopicHeaderRedesign(headerTitle)
                    }

                    items(vm.posts, key = { it.postId }) { dto ->

                        /* 1️⃣ build UI model (unchanged) */
                        val uiPost = Post(
                            id             = dto.postId.toInt(),
                            author         = dto.author.displayName,
                            authorInitials = dto.author.displayName.take(2).uppercase(),
                            verified       = false,
                            time = DateTimeUtils.formatRelative(dto.lastUpdateTime),
                            topic          = dto.category,
                            text           = dto.content,
                            imageUrl       = null,
                            likes          = dto.likes,
                            comments       = vm.blocks[dto.postId]?.size ?: 0,
                            shares         = 0,
                            liked          = false
                        )

                        /* 2️⃣ Row is clickable → toggles expansion */
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable { vm.toggleExpand(dto.postId)}

                                  // NEW
                        ) {
                            PostItemRedesign(
                                post       = uiPost,
                                likeAction = { vm.likePost(dto.postId) }
                            )

                            /* 3️⃣ only show BlocksSection when expanded */
                            if (vm.isExpanded(dto.postId)) {
                                BlocksSection(
                                    postId  = dto.postId,
                                    blocks  = vm.blocks[dto.postId] ?: emptyList(),
                                    onReply = { vm.addBlock(dto.postId, it) }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
/*───────────────────────────────────────────────────────────────────────────*/
/*  BLOCKS (COMMENTS)                                                        */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun BlocksSection(
    postId: Long,
    blocks: List<BlockDto>,
    onReply: (String) -> Unit
) {
    var reply by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        blocks.forEach { b ->
            CommentItemRedesign(
                authorInitials = b.author.displayName.take(2).uppercase(),
                author         = b.author.displayName,
                text           = b.content
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value            = reply,
                onValueChange    = { reply = it },
                modifier         = Modifier.weight(1f),
                placeholder      = { Text("Add a reply…") },
                singleLine       = true,
                trailingIcon     = {
                    IconButton(onClick = {
                        if (reply.isNotBlank()) {
                            onReply(reply.trim())
                            reply = ""
                        }
                    }) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint               = PrimaryColor
                        )
                    }
                }
            )
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  HEADER & TOPIC NAV                                                       */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun HeaderRedesign() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .border(1.dp, BorderColor, MaterialTheme.shapes.small),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(5.dp))
            Image(
                painter           = painterResource(R.drawable.ust_logo),
                contentDescription= "HKUST Logo",
                modifier          = Modifier.size(28.dp).clip(CircleShape),
                contentScale      = ContentScale.Crop
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text       = "HKUST Forum",
                color      = TextPrimaryColor,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector        = Icons.Default.Search,
            contentDescription = "Search",
            tint               = PrimaryColor,
            modifier           = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TopicNavigationRedesign(
    selectedId: String,
    onSelect: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(scrollState)
            .padding(vertical = 10.dp)
            .border(1.dp, BorderColor, MaterialTheme.shapes.small),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.width(15.dp))
        topicMap.forEach { (title, id) ->
            val selected = id == selectedId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) PrimaryColor.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onSelect(id) }
                    .padding(horizontal = 15.dp, vertical = 6.dp)
            ) {
                Text(
                    text       = title,
                    color      = if (selected) PrimaryColor else TextSecondaryColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }
        Spacer(Modifier.width(15.dp))
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  TOPIC HEADER                                                             */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun TopicHeaderRedesign(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(15.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier.size(38.dp).background(PrimaryColor, CircleShape).padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = title,
                    color      = TextPrimaryColor,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
            }
            Button(onClick = { /* follow */ }, shape = RoundedCornerShape(20.dp)) {
                Text("Follow", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  POST CARD                                                                */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun PostItemRedesign(post: Post, likeAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(1.dp, shape = MaterialTheme.shapes.small)
    ) {
        /* header */
        Row(
            modifier          = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier.size(45.dp).clip(CircleShape).background(SecondaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = post.authorInitials,
                    color      = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.author, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryColor)
                    if (post.verified) {
                        Spacer(Modifier.width(3.dp))
                        Text("✓", color = PrimaryColor, fontSize = 14.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.time, color = TextSecondaryColor, fontSize = 13.sp)
                    Text(" • ", color = TextSecondaryColor, fontSize = 13.sp)
                    Text(post.topic, color = PrimaryColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextSecondaryColor, modifier = Modifier.size(20.dp))
        }

        /* body */
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)) {
            Text(post.text, fontSize = 16.sp, lineHeight = 24.sp, modifier = Modifier.padding(bottom = 10.dp))
            post.imageUrl?.let {
                Image(
                    painter           = painterResource(R.drawable.project_screenshot),
                    contentDescription= "Post image",
                    modifier          = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(15.dp)),
                    contentScale      = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        /* actions */
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PostActionRedesign(Icons.Default.Favorite, post.likes.toString(), active = post.liked, activeColor = AccentColor, onClick = likeAction)
            PostActionRedesign(Icons.Default.Search,   post.comments.toString())
            PostActionRedesign(Icons.Default.Share,    post.shares.toString())
            PostActionRedesign(Icons.AutoMirrored.Filled.Send, "Share")
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  COMMENT LIST ITEM                                                        */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun CommentItemRedesign(authorInitials: String, author: String, text: String) {
    Box(modifier = Modifier.padding(bottom = 15.dp)) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(30.dp)
                .background(BorderColor)
                .align(Alignment.TopStart)
        )
        Row {
            Box(
                modifier         = Modifier.size(35.dp).clip(CircleShape).background(SecondaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(authorInitials, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(author, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryColor, modifier = Modifier.padding(bottom = 3.dp))
                Text(text, fontSize = 15.sp, color = TextPrimaryColor)
            }
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  ACTION ICON + TEXT                                                       */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun PostActionRedesign(
    icon: ImageVector,
    text: String,
    active: Boolean = false,
    activeColor: Color = PrimaryColor,
    onClick: () -> Unit = {}
) {
    Row(
        modifier              = Modifier.clickable { onClick() }.padding(vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = text, tint = if (active) activeColor else TextSecondaryColor, modifier = Modifier.size(16.dp))
        Text(text, color = if (active) activeColor else TextSecondaryColor, fontSize = 14.sp)
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  BOTTOM NAV                                                               */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun BottomNavigationRedesign(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        "Home"     to Icons.Default.Home,
        "Discover" to Icons.Default.Search,
        "Alerts"   to Icons.Default.Notifications,
        "Profile"  to Icons.Default.Person
    )
    Row(
        modifier              = Modifier.fillMaxWidth().height(60.dp).background(Color.White).shadow(2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        items.forEach { (title, icon) ->
            Column(
                modifier           = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(title) }
                    .padding(vertical = 5.dp),
                horizontalAlignment= Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = title, tint = if (title == selectedTab) PrimaryColor else TextSecondaryColor, modifier = Modifier.size(24.dp))
                Text(title, color = if (title == selectedTab) PrimaryColor else TextSecondaryColor, fontSize = 12.sp)
            }
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  FAB                                                                      */
/*───────────────────────────────────────────────────────────────────────────*/
@Composable
fun FloatingActionButtonRedesign(navController: NavController) {
    FloatingActionButton(
        onClick  = { navController.navigate("post") },
        modifier = Modifier.size(55.dp).shadow(4.dp, CircleShape)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Create Post", tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

/*───────────────────────────────────────────────────────────────────────────*/
/*  DATA MODEL & COLORS                                                      */
/*───────────────────────────────────────────────────────────────────────────*/
data class Post(
    val id: Int,
    val author: String,
    val authorInitials: String,
    val verified: Boolean,
    val time: String,
    val topic: String,
    val text: String,
    val imageUrl: String?,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val liked: Boolean = false
)

// Colors
val PrimaryColor = Color(0xFF003366)
val PrimaryDarkColor = Color(0xFF002244)
val SecondaryColor = Color(0xFFE5E5E5)
val AccentColor = Color(0xFF8A1538)
val AccentLightColor = Color(0xFF9A2548)
val HighlightColor = Color(0xFFB8860B)
val TextPrimaryColor = Color(0xFF14171A)
val TextSecondaryColor = Color(0xFF555555)
val TextLightColor = Color(0xFF777777)
val BackgroundColor = Color(0xFFFFFFFF)
val BackgroundLightColor = Color(0xFFF7F9FC)
val BorderColor = Color(0xFFE5E5E5)
val SuccessColor = Color(0xFF4BB543)

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    HkustForumTheme {
//        HomePage()
//    }
//}