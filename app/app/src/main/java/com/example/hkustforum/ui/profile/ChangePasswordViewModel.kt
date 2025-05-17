package com.example.hkustforum.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repo: ForumRepository
): ViewModel() {

    var onDone: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    fun change(oldPwd: String, newPwd: String) = viewModelScope.launch {
        runCatching { repo.changePassword(oldPwd, newPwd) }
            .onSuccess { onDone?.invoke() }
            .onFailure { onError?.invoke(it.message.orEmpty()) }
    }
}
