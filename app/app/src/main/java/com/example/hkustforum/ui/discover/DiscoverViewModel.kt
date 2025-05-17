package com.example.hkustforum.ui.discover

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.PostDto
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repo: ForumRepository
) : ViewModel() {

    /** All posts from the server */
    var allPosts by mutableStateOf<List<PostDto>>(emptyList())
        private set

    /** What the user has typed in the search bar */
    var searchQuery by mutableStateOf("")
        private set

    /** The posts whose content matches the search query */
    val filteredPosts: List<PostDto>
        get() = if (searchQuery.isBlank()) allPosts
        else allPosts.filter { it.content.contains(searchQuery, ignoreCase = true) }

    /** Top 5 tags + their post counts */
    var trendingTags by mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set

    init {
        loadPosts()
    }

    /** Load posts and recompute trending */
    private fun loadPosts() {
        viewModelScope.launch {
            allPosts = repo.listPosts()
            computeTrending()
        }
    }

    /** Whenever tags change, re-count them */
    private fun computeTrending() {
        trendingTags = allPosts
            .mapNotNull { it.tag }                              // each PostDto.tag: String?
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }
    }

    /** Called from the UI when the user types */
    fun onSearchChanged(new: String) {
        searchQuery = new
    }
}
