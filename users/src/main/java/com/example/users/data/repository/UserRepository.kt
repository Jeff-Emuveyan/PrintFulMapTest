package com.example.users.data.repository

import com.example.users.data.model.User
import com.example.users.util.TcpClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class UserRepository @Inject constructor (val tcpClient: TcpClient) {

    companion object {
        const val SERVER_IP = "ios-test.printful.lv"
        const val SERVER_PORT = 6111
    }

    var user = flow<User?> {
        try{
            tcpClient.connect(SERVER_IP, SERVER_PORT)
            tcpClient.writeToServer("AUTHORIZE \n")
            tcpClient.readFromServer {
                if (it == null) {
                    emit(null)
                } else {
                    val users = getUsersFromServerResponse(it)
                    users.forEach { user ->
                        delay(4000)
                        emit(user)
                    }
                }
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun stopTCPClient() {
        tcpClient.stopClient()
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