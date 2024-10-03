package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Serving(
    val id: String? = null,
    val price: Double? = 0.0,
    val priceFrom: Double? = 0.0,
    val priceTo: Double? = 0.0,
    val name: String? = null
) : Parcelable
