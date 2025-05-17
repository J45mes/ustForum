package com.example.hkustforum.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.CreatePostRequest
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import com.example.hkustforum.data.local.TokenDataStore
import com.example.hkustforum.data.remote.dto.BlockDto
import com.example.hkustforum.data.remote.dto.PostDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/*  ui/post/MyPostsViewModel.kt  */

@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val repo: ForumRepository
) : ViewModel() {

    var posts  by mutableStateOf<List<PostDto>>(emptyList())
        private set

    /* --- new ------------------------------------------------------------- */
    var blocks by mutableStateOf<Map<Long, List<BlockDto>>>(emptyMap())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    fun loadBlocks(postId: Long) = viewModelScope.launch {
        val bl = repo.listBlocks(postId)
        blocks = blocks + (postId to bl)
    }

    fun addBlock(postId: Long, content: String) = viewModelScope.launch {
        repo.addBlock(postId, content)
        loadBlocks(postId)
    }

    fun likePost(postId: Long) = viewModelScope.launch {
        repo.like(postId)
        refresh()                           // update counts
    }
    /* --------------------------------------------------------------------- */

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        isRefreshing = true
        posts = repo.listOwnPosts()
        isRefreshing = false
    }
}

