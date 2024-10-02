package io.github.junrdev.hiddengems.domain.repoimpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.util.Resource
import javax.inject.Inject

class UsersRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UsersRepo {

    override suspend fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.createUserWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let {
                    onResource(Resource.Success(data = it))
                }
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }

    override suspend fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.signInWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let {
                    onResource(Resource.Success(data = it))
                }
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }

}