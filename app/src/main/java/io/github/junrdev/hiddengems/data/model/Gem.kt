package io.github.junrdev.hiddengems.data.model

import android.net.Uri
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Gem(
    var gemId: String?,
    val placeName: String,
    val latLng: LatLng? = null,
    val offerings: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val topics: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val dateAdded: String = LocalDateTime.now().toString()
) : Parcelable


@Parcelize
data class GemDto(
    var gemId: String?,
    val placeName: String,
    val latLng: LatLng? = null,
    val offerings: List<String> = emptyList(),
    val images: List<Uri> = emptyList(),
) : Parcelable {

    companion object{
        fun GemDto.toGem() = Gem(
            gemId = gemId,
            placeName = placeName,
            latLng = latLng,
            offerings = offerings
        )
    }

}


