package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Review(
    val reviewId: String? = null,
    val userId: String? = null,
    val gemId: String? = null,
    val dateAdded: String = LocalDateTime.now().toString(),
    val reviewText: String? = null
) : Parcelable


