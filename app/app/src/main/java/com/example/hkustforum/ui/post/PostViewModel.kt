package com.example.hkustforum.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.CreatePostRequest
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import com.example.hkustforum.data.local.TokenDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repo: ForumRepository,
    tokenStore: TokenDataStore            // ← inject store directly
) : ViewModel() {
    var tagSuggestions by mutableStateOf<List<String>>(emptyList())
        private set
    fun fetchTagSuggestions(prefix: String) = viewModelScope.launch {
        tagSuggestions = repo.autocompleteTags(prefix)
    }
    /** null until login has written it */
    val userName = tokenStore.displayName
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    var postSuccess: ((String) -> Unit)? = null
    var postError  : ((String) -> Unit)? = null

    fun createPost(
        title   : String,
        category: String,
        content : String
    ) = viewModelScope.launch {
        try {
            repo.createPost(
                CreatePostRequest(
                    postName    = title,
                    content     = content,      // ← NEW
                    category    = category,
                    subCategory = "General"
                )
            )

            postSuccess?.invoke("Post created")
        } catch (e: Exception) {
            val msg = (e as? HttpException)?.response()?.errorBody()?.string()
                ?: e.message.orEmpty()
            postError?.invoke(msg)
        }
    }
}
