package com.example.lastmiledelivery.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.User
import com.example.lastmiledelivery.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val context: Application
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)

            result.onSuccess { response ->
                with(sharedPreferences.edit()) {
                    putInt("user_id", response.user.id)
                    putString("user_name", response.user.name)
                    putString("user_email", response.user.email)
                    putString("user_role", response.user.role)
                    apply()
                }
                onResult(true, response.user.role)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun getUserDetails(): User {
        return User(
            id = sharedPreferences.getInt("user_id", -1),
            name = sharedPreferences.getString("user_name", "Unknown") ?: "Unknown",
            email = sharedPreferences.getString("user_email", "Unknown") ?: "Unknown",
            role = sharedPreferences.getString("user_role", "Unknown") ?: "Unknown"
        )
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains("user_id") // Check if user ID exists
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

        fun getUserRole(): String? {
        return sharedPreferences.getString("user_role", null) // Get saved role
    }
}


