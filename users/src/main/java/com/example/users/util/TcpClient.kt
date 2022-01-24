package com.example.users.util
import java.io.*
import java.net.InetAddress
import java.net.Socket
import javax.inject.Inject

open class TcpClient @Inject constructor() {

    private var shouldRun = false
    private var bufferOut: BufferedWriter? = null
    private var bufferIn: BufferedReader? = null
    private var socket: Socket? = null

    fun connect(serverAddress: String, serverPort: Int) {
        shouldRun = true
        val inetAddress = InetAddress.getByName(serverAddress)
        try {
            socket = Socket(inetAddress, serverPort)
            bufferOut = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
            bufferIn = BufferedReader(InputStreamReader(socket?.getInputStream()))
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun readFromServer(response: suspend (String?) -> Unit) {
        while (shouldRun) {
            val message = bufferIn?.readLine()
            response.invoke(message)
        }
    }

    fun writeToServer(message: String) {
        if (bufferOut != null) {
            bufferOut?.write(message)
            bufferOut?.flush()
        }
    }

    fun stopClient() {
        socket?.close()
        shouldRun = false

        bufferOut?.flush()
        bufferOut?.close()
        bufferOut = null

        bufferIn?.close()
        bufferIn = null
    }
}