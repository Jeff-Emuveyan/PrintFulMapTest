package com.example.users.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.data.model.DataFetchState
import com.example.users.data.model.Error
import com.example.users.data.model.Loading
import com.example.users.data.model.Success
import com.example.users.data.model.User
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class UsersViewModel : ViewModel() {

    val ua = User("1", "A", "", LatLng(3.444, 12.444))
    val ub = User("2", "B", "", LatLng(4.444, 13.444))
    val uc = User("3", "C", "", LatLng(5.444, 14.444))

    val ud = User("1", "D", "", LatLng(6.444, 15.444))
    val ue = User("2", "E", "", LatLng(7.444, 16.444))
    val uf = User("3", "F", "", LatLng(8.444, 17.444))

    val ug = User("1", "G", "", LatLng(9.444, 18.444))
    val uh = User("2", "H", "", LatLng(10.444, 18.444))
    val ui = User("3", "I", "", LatLng(11.444, 20.444))

    var a = mutableListOf(ua, ub, uc)
    var b = mutableListOf(ud, ue, uf)
    var c = mutableListOf(ug, uh, ui)

    private val _userList = MutableStateFlow(DataFetchState())
    var userList: StateFlow<DataFetchState> = _userList

    fun getUsers() {
            viewModelScope.launch(Dispatchers.IO) {
                delay(10_000)
                _userList.value = Success(a)
                delay(10_000)
                _userList.value = Error(Exception("Network error"))
                delay(10_000)
                _userList.value = Success(b)
                delay(10_000)
                _userList.value = Success(c)
            }
    }
}