package io.github.junrdev.hiddengems.domain.repoimpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.UserAccount
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import javax.inject.Inject

private const val TAG = "UsersRepoImpl"

class UsersRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UsersRepo {

    override fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.createUserWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                println("result ${authResult.user}")
                authResult.user?.let { firebaseUser ->
                    firestore.collection(Constant.usercollection)
                        .add(UserAccount(uid = firebaseUser.uid, email = firebaseUser.email!!))
                        .addOnSuccessListener { onResource(Resource.Success(data = firebaseUser)) }
                        .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "signUp: ${it.message.toString()}")
                onResource(Resource.Error(message = it.localizedMessage.toString()))
            }
    }

    override fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.signInWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let {
                    onResource(Resource.Success(data = it))
                }
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }

    override fun getAllUsers(onResource: (Resource<List<UserAccount>>) -> Unit) {
        val users = firestore.collection(Constant.usercollection)
        users.get()
            .addOnSuccessListener {
                val accounts = it.toObjects(UserAccount::class.java)
                onResource(Resource.Success(data = accounts))
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }
}