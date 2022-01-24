package com.example.users.ui

import com.example.users.data.model.User
import com.example.users.data.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UsersViewModelTest {

    @Test
    fun `test to add a user to the list`() = runBlocking {
        val viewModel = UsersViewModel(MyFakeRepository())

        val user = User("101", "jeff", "image.jpg", null)
        viewModel.addOrUpdateUserList(user)

        Assert.assertEquals(1, viewModel.currentUserList.size)
    }

    @Test
    fun `test to update a user in the list`() = runBlocking {
        val viewModel = UsersViewModel(MyFakeRepository())

        val user = User("101", "jeff", "image.jpg", LatLng(2.22, 2.22))
        val userUpdate = User("101", null, null, LatLng(3.33, 3.33))
        viewModel.addOrUpdateUserList(user)
        viewModel.addOrUpdateUserList(userUpdate)

        val userInList = viewModel.currentUserList.first()

        //assert that the update was done
        Assert.assertEquals(1, viewModel.currentUserList.size)
        Assert.assertEquals(3.33, user.currentPosition!!.latitude, 0.0)
        Assert.assertEquals(3.33, user.currentPosition!!.longitude, 0.0)
    }

    class MyFakeRepository() : UserRepository()
}