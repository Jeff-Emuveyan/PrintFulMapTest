package com.example.users.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.data.model.DataFetchState
import com.example.users.data.model.Success
import com.example.users.data.model.User
import com.example.users.data.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _dataFetchState = MutableSharedFlow<DataFetchState>()
    var dataFetchState: SharedFlow<DataFetchState> = _dataFetchState

    private val currentUserList = mutableListOf<User>()

    fun getUsers() {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.user.collect {
                    addOrUpdateUserList(listOf(it))
                }
            }
    }

    private suspend fun addOrUpdateUserList(responseUserList: List<User>) {
        responseUserList.forEach { user ->
            var userAlreadyExist = false

            currentUserList.forEach { existingUser ->
                if (user.id == existingUser.id) {
                    userAlreadyExist = true
                    existingUser.previousPosition = existingUser.currentPosition
                    val lat = user.currentPosition?.latitude
                    val long = user.currentPosition?.longitude
                    if (lat != null && long != null) {
                        existingUser.currentPosition = LatLng(lat,long)
                    }
                }
            }
            if (!userAlreadyExist) {currentUserList.add(user)}
        }

        _dataFetchState.emit(Success(currentUserList))
    }

    fun setUserMarker(id: String, marker: Marker?) {
        currentUserList.forEach {
            if (it.id == id) {it.marker = marker}
        }
    }
}