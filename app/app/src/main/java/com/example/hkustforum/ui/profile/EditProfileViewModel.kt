package com.example.hkustforum.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repo: ForumRepository
): ViewModel() {

    var onDone: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    fun save(displayName: String, username: String) = viewModelScope.launch {
        runCatching { repo.updateProfile(displayName, username) }
            .onSuccess { onDone?.invoke() }
            .onFailure { onError?.invoke(it.message.orEmpty()) }
    }
}
