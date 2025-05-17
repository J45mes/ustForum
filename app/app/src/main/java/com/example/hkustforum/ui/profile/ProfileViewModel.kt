/*  app/src/main/java/com/example/hkustforum/ui/profile/ProfileViewModel.kt  */
package com.example.hkustforum.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.local.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Exposes the values that were persisted in [TokenDataStore] at login / register.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    tokenStore: TokenDataStore
) : ViewModel() {

    /** e.g. “Jason Chen”, nullable until the first login succeeds. */
    val displayName = tokenStore.displayName
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * Username / handle.
     * You can persist it in the DataStore later; for now we generate
     * “@jason_chen” from the display‑name as a graceful fallback.
     */
    val username = tokenStore.uid      // not really a username ‑ just a trigger
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val major = tokenStore.major      // not really a username ‑ just a trigger
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
