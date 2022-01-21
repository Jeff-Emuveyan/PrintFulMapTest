package com.example.users.data.repository

import android.util.Log
import com.example.users.data.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class UserRepository @Inject constructor() {

    private val _user = MutableSharedFlow<User?>()
    var user: SharedFlow<User?> = _user

    init {
        fetchUsers()
    }

    private fun fetchUsers() {

    }
}