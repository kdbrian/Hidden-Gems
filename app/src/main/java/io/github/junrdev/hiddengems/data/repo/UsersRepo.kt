package io.github.junrdev.hiddengems.data.repo

import com.google.firebase.auth.FirebaseUser
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.GithubUser
import io.github.junrdev.hiddengems.data.model.UserAccount
import io.github.junrdev.hiddengems.util.Resource

interface UsersRepo {

    fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
    fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
    fun getAllUsers(onResource: (Resource<List<UserAccount>>) -> Unit)

    //return firebase result(uid)
    fun saveGithubUser(githubUser: GithubUser, onResource: (Resource<String>) -> Unit)

    suspend fun getGithubUserTokenFromLoginCode(code: String): Result<String>

    suspend fun getGithubUserInfo(token: String): Result<GithubUser>

    suspend fun getGithubUserDetails(uid : String, onResource: (Resource<GithubUser>) -> Unit)

}