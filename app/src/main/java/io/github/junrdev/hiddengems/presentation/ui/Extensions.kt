package io.github.junrdev.hiddengems.presentation.ui

import android.content.Context
import android.location.Geocoder
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.junrdev.hiddengems.R
import java.util.Locale

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.getLocationName(latitude: Double, longitude: Double): String? {
    val geocoder = Geocoder(this, Locale.getDefault())
    val addresses = geocoder.getFromLocation(latitude, longitude, 1)

    return if (!addresses.isNullOrEmpty()) {
        addresses[0].getAddressLine(0) // Returns full address
    } else {
        null // No address found
    }
}




