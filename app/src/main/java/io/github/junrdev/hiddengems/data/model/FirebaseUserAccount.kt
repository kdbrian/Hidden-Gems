package io.github.junrdev.hiddengems.data.model

import android.os.Parcelable
import io.github.junrdev.hiddengems.util.AccountMode
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

//default app user
open class AppUser(
    //firebase id
    val uid: String,
    val rememberMe: Boolean = false,
    val locationSharing: Boolean = false,
)

@Parcelize
data class FirebaseUserAccount(
    val id: String,
    val email: String,
    val dateJoined: String = LocalDateTime.now().toString(),
    val createdBy: String? = AccountMode.FIREBASE_LOGIN.mode,
) : Parcelable, AppUser(uid = id)

@Parcelize
data class AccountDto(
    val email: String,
    val password: String,
    val createdBy: String? = AccountMode.FIREBASE_LOGIN.mode
) : Parcelable


@Parcelize
data class GithubUserAccount(
    private var fireBaseId: String = "null",
    val id: String? = null, //github id
    val username: String? = null,
    val followers: String? = null,
    val avatarUrl: String? = null,
    val createdBy: String? = AccountMode.GITHUB_LOGIN.mode
) : Parcelable, AppUser(uid = fireBaseId)