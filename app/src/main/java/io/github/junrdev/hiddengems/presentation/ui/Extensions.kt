package io.github.junrdev.hiddengems.presentation.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun LatLng.getAdress(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>?
    val address: Address?
    var addressText = ""

    try {
        addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            address = addresses[0]
            addressText = address.getAddressLine(0) // Get the first address line
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return addressText
}




