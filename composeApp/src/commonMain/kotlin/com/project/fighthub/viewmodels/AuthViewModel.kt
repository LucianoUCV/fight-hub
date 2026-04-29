package com.project.fighthub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fighthub.data.repository.AuthRepository
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.project.fighthub.data.model.Profile

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object AuthenticatedLogin : AuthState()
    object AuthenticatedRegister : AuthState()
    object ProfileUpdated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            val result = repository.getCurrentProfile()
            if (result.isSuccess) {
                _userProfile.value = result.getOrNull()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _userProfile.value = null
            _authState.value = AuthState.Idle
        }
    }

    init {
        viewModelScope.launch {
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                fetchProfile()
                _authState.value = AuthState.AuthenticatedLogin
            }
        }
    }

    fun signUp(name: String, email: String, pass: String) {
        if (name.isBlank()) return showError("Numele nu poate fi gol.")
        if (!email.contains("@") || !email.contains(".")) return showError("Email invalid.")
        if (pass.length < 6) return showError("Parola trebuie să aibă minim 6 caractere.")

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(name, email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.AuthenticatedRegister
            } else {
                showError("Eroare la creare cont. Poate email-ul există deja?")
            }
        }
    }

    fun signIn(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) return showError("Completează ambele câmpuri.")

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.AuthenticatedLogin
            } else {
                showError("Email sau parolă incorectă.")
            }
        }
    }

    fun saveProfile(age: Int, height: Int, weight: Int, avatarBytes: ByteArray?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.updateProfileDetails(age, height, weight, avatarBytes)
            if (result.isSuccess) {
                _authState.value = AuthState.ProfileUpdated
            } else {
                val realError = result.exceptionOrNull()?.message ?: "Eroare necunoscută"
                showError("Eroare: $realError")
            }
        }
    }

    private fun showError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun resetError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}