package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Serving(
    val id: String?,
    val price: Double?,
    val priceFrom: Double?,
    val priceTo: Double?,
    val name: String?
) : Parcelable
