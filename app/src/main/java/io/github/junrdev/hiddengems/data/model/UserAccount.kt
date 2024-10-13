package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class UserAccount(
    val uid: String,
    val email: String,
    val dateJoined: String = LocalDateTime.now().toString(),
    val createdBy: String? = AppDatastore.FIREBASE_LOGIN
) : Parcelable

@Parcelize
data class AccountDto(
    val email: String,
    val password: String,
    val createdBy: String? = AppDatastore.FIREBASE_LOGIN
) : Parcelable


@Parcelize
data class GithubUser(
    val id: String? = null,
    val username: String? = null,
    val uid: String? = null,
    val followers: String? = null,
    val avatarUrl: String? = null,
    val createdBy: String? = AppDatastore.GITHUB_LOGIN
) : Parcelable