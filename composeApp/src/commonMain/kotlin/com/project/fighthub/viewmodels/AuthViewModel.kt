package com.project.fighthub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fighthub.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object ProfileUpdated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUp(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(name, email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Eroare necunoscută")
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Date incorecte")
            }
        }
    }

    fun saveProfile(age: Int, height: Int, weight: Int) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.updateProfileDetails(age, height, weight)
            if (result.isSuccess) {
                _authState.value = AuthState.ProfileUpdated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Eroare la salvare")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}