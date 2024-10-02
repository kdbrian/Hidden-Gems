package io.github.junrdev.hiddengems.domain.repoimpl

import com.google.firebase.firestore.FirebaseFirestore
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.data.repo.ServingRepo
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource

class ServingRepoImpl(
    private val firebaseFirestore: FirebaseFirestore
) : ServingRepo {

    private val servings = firebaseFirestore.collection(Constant.servingcollection)

    override fun getServings(onResource: (Resource<List<Serving>>) -> Unit) {
        servings.get()
            .addOnSuccessListener {
                val servings = it.toObjects(Serving::class.java)
                onResource(Resource.Success(servings))
            }.addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }
    }

    override fun saveServing(serving: Serving, onResource: (Resource<Any>) -> Unit) {
        val servingRef = servings.document()
        val withId = serving.copy(id = servingRef.id)
        servingRef.set(withId)
            .addOnSuccessListener {
                onResource(Resource.Success(data = true))
            }.addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }
    }
}