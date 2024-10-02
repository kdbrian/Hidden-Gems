package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Review(
    val userId: String,
    val dateAdded: String = LocalDateTime.now().toString(),
    val reviewText: String
) : Parcelable

