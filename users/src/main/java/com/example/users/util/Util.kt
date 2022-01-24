package com.example.users.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.animation.ObjectAnimator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import android.animation.TypeEvaluator
import android.location.Geocoder
import android.util.Property
import com.example.users.data.model.User
import java.util.*

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap =
        Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun animateMarker(marker: Marker?, finalPosition: LatLng?, latLngInterpolator: LatLngInterpolator) {
    val typeEvaluator: TypeEvaluator<LatLng> =
        TypeEvaluator { fraction, startValue, endValue ->
            latLngInterpolator.interpolate(
                fraction,
                startValue!!,
                endValue!!
            )
        }
    val property: Property<Marker, LatLng> = Property.of(Marker::class.java, LatLng::class.java, "position")
    val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
    animator.duration = 3000
    animator.start()
}

fun getAddress(context: Context, lat: Double, lon: Double): String? {
    val geoCoder = Geocoder(context, Locale.getDefault())
    val resultCount = 1
    val address = geoCoder.getFromLocation(lat, lon, resultCount)
    return "${address.firstOrNull()?.countryName}," +
            " ${address.firstOrNull()?.locality}," +
            " ${address.firstOrNull()?.thoroughfare}"
}