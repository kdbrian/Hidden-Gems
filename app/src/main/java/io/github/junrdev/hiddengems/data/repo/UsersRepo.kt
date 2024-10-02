package io.github.junrdev.hiddengems.data.repo

import com.google.firebase.auth.FirebaseUser
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.util.Resource

interface UsersRepo {
    suspend fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
    suspend fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
}