package com.example.users.data.model

open class DataFetchState()
class Loading(): DataFetchState()
data class Success(var users: MutableList<User>): DataFetchState()
data class Error(var exception: Throwable): DataFetchState()