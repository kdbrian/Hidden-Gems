package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class UserAccount(
    val uid: String,
    val email: String,
    val dateJoined: String = LocalDateTime.now().toString()
) : Parcelable

@Parcelize
data class AccountDto(
    val email: String,
    val password: String
) : Parcelable