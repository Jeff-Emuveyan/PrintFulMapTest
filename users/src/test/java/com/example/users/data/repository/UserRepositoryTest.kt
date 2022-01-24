package com.example.users.data.repository
import com.example.users.util.TcpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class UserRepositoryTest {

    @Test
    fun `getUsersFromServerResponse() should get users from USERLIST command response`() {
        val response = "USERLIST 101,Jānis Bērziņš,https://i4.ifrype.com/profile/000/324/v1559116100/ngm_324.jpg,56.9495677035,24.1064071655;" +
                "102,Pēteris Zariņš,https://i7.ifrype.com/profile/666/047/v1572553757/ngm_4666047.jpg,56.9503693176,24.1084241867;\n"

        val userRepository = UserRepository(TestTCPClient())
        val result = userRepository.getUsersFromServerResponse(response)

        val userA = result.first()
        val userB = result[1]

        assertEquals(2, result.size)
        assertEquals("101", userA.id)
        assertEquals("102", userB.id)
    }

    @Test
    fun `getUsersFromServerResponse() should get a user from UPDATE command response`() {
        val response = "UPDATE 101,56.9495677035,24.1064071655"
        val userRepository = UserRepository(TestTCPClient())
        val result = userRepository.getUsersFromServerResponse(response)

        val user = result.first()

        assertEquals("101", user.id)
    }

    inner class TestTCPClient(): TcpClient()
}