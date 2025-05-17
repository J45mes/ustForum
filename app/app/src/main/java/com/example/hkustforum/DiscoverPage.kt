package com.example.hkustforum

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview

import com.example.hkustforum.BottomNavigationRedesign
import com.example.hkustforum.DateTimeUtils
import com.example.hkustforum.Post
import com.example.hkustforum.PostItemRedesign
import com.example.hkustforum.BlocksSection
import com.example.hkustforum.data.remote.dto.BlockDto
import com.example.hkustforum.ui.home.HomeViewModel
import com.example.hkustforum.ui.discover.DiscoverViewModel
import com.example.hkustforum.ui.theme.HkustForumTheme

// HKUST Color Scheme
val TopicGreen  = Color(0xFF29954A)
val TopicPurple = Color(0xFF5F2EFF)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
    navController: NavController,
    vm: DiscoverViewModel = hiltViewModel()
) {
    // for comments & likes
    val homeVm: HomeViewModel = hiltViewModel()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    val selectedTab       = remember { mutableStateOf("Discover") }
    val allPosts          = vm.allPosts
    val filteredPosts     = vm.filteredPosts
    val trendingTagCounts = vm.trendingTags

    // pick posts to display
    val postsToShow = when {
        searchQuery.text.isNotBlank() -> filteredPosts
        selectedTag != null           -> allPosts.filter { it.tag == selectedTag }
        else                          -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = {
                    Icon(
                        painter            = painterResource(id = R.drawable.grid_view),
                        contentDescription = "Grid View",
                        tint               = HKUSTPrimary,
                        modifier           = Modifier
                            .clickable { /* TODO: grid toggle */ }
                            .padding(8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = Background,
                    titleContentColor      = TextPrimary,
                    actionIconContentColor = HKUSTPrimary
                )
            )
        },
        bottomBar = {
            BottomNavigationRedesign(
                selectedTab   = selectedTab.value,
                onTabSelected = { tab ->
                    selectedTab.value = tab
                    navController.navigate(tab) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Search bar ─────────────────────────────────────────────────────
            SearchBar(
                query         = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    vm.onSearchChanged(it.text)
                    selectedTag = null
                }
            )

            // ── If searching or filtering by tag, show posts with likes & replies ──
            if (searchQuery.text.isNotBlank() || selectedTag != null) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(postsToShow, key = { it.postId }) { dto ->
                        val uiPost = Post(
                            id             = dto.postId.toInt(),
                            author         = dto.author.displayName,
                            authorInitials = dto.author.displayName.take(2).uppercase(),
                            verified       = false,
                            time           = DateTimeUtils.formatRelative(dto.lastUpdateTime),
                            topic          = dto.category,
                            text           = dto.content,
                            imageUrl       = null,
                            likes          = dto.likes,
                            comments       = homeVm.blocks[dto.postId]?.size ?: 0,
                            shares         = 0,
                            liked          = false
                        )

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    homeVm.toggleExpand(dto.postId)
                                }
                        ) {
                            PostItemRedesign(
                                post       = uiPost,
                                likeAction = { homeVm.likePost(dto.postId) }
                            )
                            if (homeVm.isExpanded(dto.postId)) {
                                BlocksSection(
                                    postId  = dto.postId,
                                    blocks  = homeVm.blocks[dto.postId] ?: emptyList(),
                                    onReply = { homeVm.addBlock(dto.postId, it) }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }

            } else {
                // ── Trending Now ───────────────────────────────────────────────────
                SectionHeader("Trending Now", "See All") { /* TODO */ }
                TrendingTopics(trendingTagCounts) { tag ->
                    selectedTag = tag
                }

                // ── Categories ─────────────────────────────────────────────────────
                SectionHeader("Categories") { /* TODO */ }
                TopicCategories()

                // ── All Topics ─────────────────────────────────────────────────────
                SectionHeader("All Topics") { /* TODO */ }
                AllTopicsList()
            }
        }
    }
}

@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    androidx.compose.foundation.layout.Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 12.dp)
    ) {
        BasicTextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier
                .fillMaxWidth()
                .background(BackgroundLight, RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.Search,
                        contentDescription = null,
                        tint               = TextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    androidx.compose.foundation.layout.Box {
                        if (query.text.isEmpty()) {
                            Text(
                                "Search posts by keyword",
                                color    = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                        inner()
                    }
                }
            }
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment    = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        actionText?.let {
            Text(
                it,
                color      = HKUSTPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
fun TrendingTopics(
    tags: List<Pair<String, Int>>,
    onTagClick: (String) -> Unit
) {
    val colors = listOf(HKUSTPrimary, HKUSTAccent, HKUSTHighlight, TopicGreen, TopicPurple)
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 15.dp, bottom = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        tags.take(5).forEachIndexed { idx, (tag, count) ->
            TrendingTopicCard(
                topic   = TrendingTopic("#$tag", "$count posts", colors[idx % colors.size]),
                onClick = { onTagClick(tag) }
            )
        }
    }
}

@Composable
fun TrendingTopicCard(
    topic: TrendingTopic,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .size(width = 150.dp, height = 100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(topic.color)
            .clickable { onClick() }
            .padding(15.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(topic.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(topic.stats, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

data class TrendingTopic(val name: String, val stats: String, val color: Color)

@Composable
fun TopicCategories() {
    val categories = listOf(
        TopicCategory("Academic","Courses, projects, and study resources", R.drawable.school, HKUSTPrimary),
        TopicCategory("Student Life","Residence halls, clubs, and campus activities", R.drawable.business, HKUSTAccent),
        TopicCategory("Career & Jobs","Internships, job opportunities, and career advice", R.drawable.supervisor_account, HKUSTHighlight),
        TopicCategory("Events","Upcoming campus events and activities", R.drawable.event, TopicGreen),
        TopicCategory("Research","Research opportunities and projects", R.drawable.research, TopicPurple)
    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(Background)
    ) {
        categories.forEach { category ->
            TopicCategoryItem(category)
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
        }
    }
}

@Composable
fun TopicCategoryItem(category: TopicCategory) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(category.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(category.icon), contentDescription = null, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(15.dp))
        Column(Modifier.weight(1f)) {
            Text(category.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(
                category.description,
                color    = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
    }
}

data class TopicCategory(val title: String, val description: String, val icon: Int, val color: Color)

@Composable
fun AllTopicsList() {
    val topics = listOf(
        Topic("Computer Science","1.2k"),
        Topic("Engineering","845"),
        Topic("Business","723"),
        Topic("Science","612"),
        Topic("Mathematics","598"),
        Topic("Design","487"),
        Topic("Humanities","432")
    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(Background)
    ) {
        topics.forEach { topic ->
            TopicListItem(topic)
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
        }
    }
}

@Composable
fun TopicListItem(topic: Topic) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            Text(topic.name.take(1), color = HKUSTPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(15.dp))
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(topic.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Spacer(Modifier.weight(1f))
            Text(
                topic.count,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier
                    .background(BackgroundLight, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

data class Topic(val name: String, val count: String)

@Preview(showBackground = true)
@Composable
fun DiscoverPreview() {
    HkustForumTheme {
        DiscoverScreen(navController = rememberNavController())
    }
}
