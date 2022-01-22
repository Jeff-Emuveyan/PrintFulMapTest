package com.example.users.data.model
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class User(val id: String,
                var name: String?,
                val profileImage: String?,
                var currentPosition: LatLng?,
                var previousPosition: LatLng? = null) {
    var marker: Marker? = null
}