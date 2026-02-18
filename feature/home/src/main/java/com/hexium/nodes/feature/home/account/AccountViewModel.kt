package com.hexium.nodes.feature.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val adRepository: AdRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = adRepository.isLoggedIn()
            val username = adRepository.getUsername()
            val email = adRepository.getEmail()

            _uiState.value = AccountUiState(
                isLoggedIn = loggedIn,
                username = username,
                email = email,
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            adRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedIn = false)
        }
    }
}
