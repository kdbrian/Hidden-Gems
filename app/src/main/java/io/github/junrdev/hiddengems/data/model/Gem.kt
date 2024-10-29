package io.github.junrdev.hiddengems.data.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity
data class Gem(
    @PrimaryKey var gemId: String = "",
    val placeName: String = "",
    val locationName: String? = null,
    val latLng: String? = null,
    val offerings: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val servings: List<Serving> = emptyList(),
    val topics: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val addedBy: String? = null,
    val dateAdded: String = LocalDateTime.now().toString()
) : Parcelable


@Parcelize
data class GemDto(
    var gemId: String?,
    val placeName: String,
    val locationName: String? = null,
    val latLng: String? = null,
    val offerings: List<String> = emptyList(),
    val servings: List<Serving> = emptyList(),
    val images: List<Uri> = emptyList(),
    val addedBy: String? = null,
) : Parcelable {

    companion object {
        fun GemDto.toGem() = Gem(
            gemId = gemId.orEmpty(),
            placeName = placeName,
            latLng = latLng,
            offerings = offerings,
            addedBy = addedBy,
            servings = servings,
            locationName = locationName
        )
    }

}


