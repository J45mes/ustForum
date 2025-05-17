package com.example.hkustforum.ui.home

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.BlockDto
import com.example.hkustforum.data.remote.dto.PostDto
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ForumRepository
) : ViewModel() {

    /* master & filtered lists */
    private var allPosts by mutableStateOf<List<PostDto>>(emptyList())
    var   posts          by mutableStateOf<List<PostDto>>(emptyList()); private set

    /* map of blocks we have already fetched */
    var blocks by mutableStateOf<Map<Long, List<BlockDto>>>(emptyMap()); private set

    /* remember which posts are expanded (shows UI state + prevents repeats) */
    private val expanded = mutableStateListOf<Long>()

    /* swipe-refresh indicator state */
    var isRefreshing by mutableStateOf(false); private set

    private var categoryFilter: String? = null
    init { refreshPosts() }

    /* ---------------- public API ---------------- */

    fun refreshPosts() {
        viewModelScope.launch {
            isRefreshing = true
            allPosts     = repo.listPosts()           // 🚫 do NOT pre-fetch blocks
            applyFilter()
            isRefreshing = false
        }
    }

    fun setCategoryFilter(id: String) {
        categoryFilter = if (id == "home") null else id
        applyFilter()
    }

    /** Called by the UI when the user expands / collapses a post. */
    fun toggleExpand(postId: Long) {
        if (expanded.remove(postId).not()) expanded += postId   // toggles membership

        // If the blocks were never loaded AND we are expanding, fetch now
        if (postId !in blocks && postId in expanded) loadBlocks(postId)
    }

    fun isExpanded(postId: Long) = postId in expanded

    fun likePost(postId: Long)          = viewModelScope.launch { repo.like(postId);     refreshPosts() }
    fun addBlock(postId: Long, content: String) = viewModelScope.launch {
        repo.addBlock(postId, content)
        loadBlocks(postId)                                // refresh comments for this post
    }

    /* ---------------- private helpers ---------------- */

    private fun applyFilter() {
        posts = allPosts
            .filter { p ->
                categoryFilter == null ||
                        p.category.equals(categoryFilter, ignoreCase = true)
            }
            .sortedByDescending { it.lastUpdateTime }
    }

     fun loadBlocks(postId: Long) {
        viewModelScope.launch {
            val bl = repo.listBlocks(postId)
            blocks = blocks + (postId to bl)
        }
    }
    val expandedIds: List<Long> get() = expanded
}
