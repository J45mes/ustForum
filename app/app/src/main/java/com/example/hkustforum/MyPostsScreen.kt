/*  ui/post/MyPostsScreen.kt  */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hkustforum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.hkustforum.data.remote.dto.PostDto
import com.example.hkustforum.ui.post.MyPostsViewModel
import com.example.hkustforum.DateTimeUtils
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MyPostsScreen(
    nav: NavController,
    vm: MyPostsViewModel = hiltViewModel()
) {
    /* ------------ state ------------ */
    val posts      = vm.posts                              // plain list
    val swipeState = rememberSwipeRefreshState(vm.isRefreshing)

    // rememberSaveable for the expanded IDs, via a listSaver
    val expandedIds = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { saved ->
                mutableStateListOf<Long>().apply { addAll(saved) }
            }
        )
    ) { mutableStateListOf<Long>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Posts") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationRedesign(
                selectedTab = "Profile",
                onTabSelected = { tab ->
                    nav.navigate(tab) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        SwipeRefresh(
            state     = swipeState,
            onRefresh = { vm.refresh() }
        ) {
            // Sort posts by lastUpdateTime descending (newest first)
            val sorted = posts.sortedByDescending { it.lastUpdateTime }

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(sorted, key = PostDto::postId) { dto ->

                    /* ---- build the card model ---- */
                    val uiPost = Post(
                        id             = dto.postId.toInt(),
                        author         = dto.author.displayName,
                        authorInitials = dto.author.displayName.take(2).uppercase(),
                        verified       = false,
                        time           = DateTimeUtils.formatRelative(dto.lastUpdateTime),
                        topic          = dto.category,      // kept for parity but optional
                        text           = dto.content,
                        imageUrl       = null,
                        likes          = dto.likes,
                        comments       = vm.blocks[dto.postId]?.size ?: 0,
                        shares         = 0,
                        liked          = false
                    )

                    /* ---- card + optional replies ---- */
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (dto.postId in expandedIds) {
                                    expandedIds.remove(dto.postId)
                                } else {
                                    expandedIds.add(dto.postId)
                                    // lazy-load comments when first expanded
                                    if (dto.postId !in vm.blocks) vm.loadBlocks(dto.postId)
                                }
                            }
                    ) {
                        PostItemRedesign(
                            post       = uiPost,
                            likeAction = { vm.likePost(dto.postId) }
                        )

                        if (dto.postId in expandedIds) {
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
