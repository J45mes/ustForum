package com.example.hkustforum.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.RegisterRequest
import com.example.hkustforum.data.remote.dto.LoginRequest
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface AuthState {
    object Idle    : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: ForumRepository
) : ViewModel() {

    val loginState    = mutableStateOf<AuthState>(AuthState.Idle)
    val registerState = mutableStateOf<AuthState>(AuthState.Idle)

    /* ------------- LOGIN ---------------- */

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loginState.value = AuthState.Loading
            runCatching { repo.login(email, password) }
                .onSuccess {
                    loginState.value = AuthState.Success
                    onSuccess()
                }.onFailure { e ->
                    val msg = (e as? HttpException)?.response()?.errorBody()?.string()
                        ?: e.message.orEmpty()
                    loginState.value = AuthState.Error(msg)
                }
        }
    }

    /* ------------- REGISTER ------------- */

    fun register(
        username   : String,
        email      : String,
        password   : String,
        displayName: String,
        major      : String,
        onSuccess  : () -> Unit
    ) {
        viewModelScope.launch {
            registerState.value = AuthState.Loading
            runCatching {
                repo.register(RegisterRequest(username, email, password, displayName, major))
            }.onSuccess {
                registerState.value = AuthState.Success
                onSuccess()
            }.onFailure { e ->
                val msg = (e as? HttpException)?.response()?.errorBody()?.string()
                    ?: e.message.orEmpty()
                registerState.value = AuthState.Error(msg)
            }
        }
    }
}
