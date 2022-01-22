package com.example.users.data.repository

import android.util.Log
import com.example.users.data.model.User
import com.example.users.util.TcpClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket
import javax.inject.Inject

class UserRepository @Inject constructor() {

    lateinit var _bufferOut: PrintWriter
    lateinit var _bufferIn: BufferedReader

    private var socket: Socket? = null
    private val ServerIP = "ios-test.printful.lv"
    private val ServerPort = 6111

    private val _user = MutableSharedFlow<User?>()
    var user: SharedFlow<User?> = _user

    suspend fun fetchUsers() {
        Log.e("KANE", "JEFFFFF")
        Log.e("JEFF","Server is running")
        val t = TcpClient{
            Log.e("JEFF","My message $it")
        }
        t.run()
    }

    fun getUsersFromServerResponse(response: String): List<User> {
        val userList = mutableListOf<User>()
        val sanitizedResponse = response
            .replace("USERLIST", "")
            .replace("UPDATE", "")
            .trim()

        val stringOfUsers = sanitizedResponse.split(";")
        stringOfUsers.forEach { userAsString ->
            val stringOfUserDetails = userAsString.split(",")
            if (stringOfUserDetails.size == 5) {
                val id = stringOfUserDetails[0]
                val name = stringOfUserDetails[1]
                val imageUrl = stringOfUserDetails[2]
                val lat = stringOfUserDetails[3].toDouble()
                val lon = stringOfUserDetails[4].toDouble()
                val user = User(id, name, imageUrl, LatLng(lat, lon))
                userList.add(user)
            } else if (stringOfUserDetails.size == 3) {
                val id = stringOfUserDetails[0]
                val lat = stringOfUserDetails[1].toDouble()
                val lon = stringOfUserDetails[2].toDouble()
                val user = User(id, null, null, LatLng(lat, lon))
                userList.add(user)
            }
        }
        return userList
    }

}