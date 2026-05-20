package com.example.squadup.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.squadup.data.repositories.AuthRepository
import com.example.squadup.data.repositories.ModalityRepository
import com.example.squadup.data.repositories.UserRepository

class RegisterViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val modalityRepository: ModalityRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                authRepository = authRepository,
                userRepository = userRepository,
                modalityRepository = modalityRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}