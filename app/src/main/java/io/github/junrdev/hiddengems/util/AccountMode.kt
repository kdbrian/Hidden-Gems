package io.github.junrdev.hiddengems.util

sealed class AccountMode(
    val mode: String
) {
    data object GITHUB_LOGIN : AccountMode(mode = "GITHUB_LOGIN")
    data object FIREBASE_LOGIN : AccountMode(mode = "FIREBASE_LOGIN")
    data object NO_LOGIN : AccountMode(mode = "NO_LOGIN")

    companion object {
        fun String.toMode(): AccountMode = when (this.uppercase()) {
            "GITHUB_LOGIN" -> GITHUB_LOGIN
            "FIREBASE_LOGIN" -> FIREBASE_LOGIN
            else -> NO_LOGIN
        }
    }
}