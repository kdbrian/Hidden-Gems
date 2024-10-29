package io.github.junrdev.hiddengems.data.repoimpl

import com.google.firebase.firestore.FirebaseFirestore
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.domain.repo.ServingRepo
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


    override fun getServingById(servingId: String, onResource: (Resource<Serving>) -> Unit) {
        servings.document(servingId).get()
            .addOnSuccessListener {
                it.toObject(Serving::class.java)?.let { sv ->
                    onResource(Resource.Success(sv))
                }
            }.addOnFailureListener {
                onResource(Resource.Error(it.message.toString()))
            }
    }

    override fun saveServing(serving: Serving, onResource: (Resource<String>) -> Unit) {
        val servingRef = servings.document()
        val withId = serving.copy(id = servingRef.id)
        servingRef.set(withId)
            .addOnSuccessListener {
                onResource(Resource.Success(data = servingRef.id))
            }.addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }
    }
}