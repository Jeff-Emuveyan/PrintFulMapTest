package com.example.users.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.data.model.DataFetchState
import com.example.users.data.model.Success
import com.example.users.data.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    val ua = User("1", "A", "", LatLng(3.444, 12.444))
    val ub = User("1", "A", "", LatLng(9.444, 63.444))
    val uc = User("3", "C", "", LatLng(5.444, 14.444))

    val ud = User("1", "D", "", LatLng(6.444, 15.444))
    val ue = User("2", "E", "", LatLng(7.444, 16.444))
    val uf = User("3", "F", "", LatLng(8.444, 17.444))

    val ug = User("1", "G", "", LatLng(9.444, 18.444))
    val uh = User("2", "H", "", LatLng(10.444, 18.444))
    val ui = User("3", "I", "", LatLng(11.444, 20.444))

    var a = listOf(ua)
    var aa = listOf(ub)

    var b = listOf(ud, ue, uf)
    var c = listOf(ug, uh, ui)

    private val _dataFetchState = MutableSharedFlow<DataFetchState>()
    var dataFetchState: SharedFlow<DataFetchState> = _dataFetchState

    private val currentUserList = mutableListOf<User>()

    fun getUsers() {
            viewModelScope.launch(Dispatchers.IO) {
                delay(10_000)
                addOrUpdateUserList(a)
                delay(10_000)
                addOrUpdateUserList(aa)
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