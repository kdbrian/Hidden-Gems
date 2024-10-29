package io.github.junrdev.hiddengems.domain.repoimpl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.data.model.GemDto
import io.github.junrdev.hiddengems.data.model.GemDto.Companion.toGem
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.data.repo.GemsRepo
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import javax.inject.Inject

class GemsRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : GemsRepo {


    private val gems = firestore.collection(Constant.gemscollection)

    override fun getGems(onResource: (Resource<List<Gem>>) -> Unit) {
        onResource(Resource.Loading())
        gems.get()
            .addOnSuccessListener {
                val collected = it.toObjects(Gem::class.java)
                onResource(Resource.Success(data = collected))
            }.addOnFailureListener {
                onResource.invoke(Resource.Error(message = it.message.toString()))
            }
    }

    override fun addGem(dto: GemDto, onResource: (Resource<Boolean>) -> Unit) {
        onResource(Resource.Loading())

        val gemRef = gems.document()
        dto.gemId = gemRef.id
        val toGem = dto.toGem()

        gemRef.set(toGem)
            .addOnSuccessListener {
                if (dto.images.isNotEmpty()) {

                    // Upload images
                    saveImages(dto.images, toGem) { resource ->
                        when (resource) {
                            is Resource.Error -> onResource(Resource.Error(message = resource.message.toString()))
                            is Resource.Loading -> onResource(Resource.Loading())
                            is Resource.Success -> onResource(Resource.Success(data = true))
                        }
                    }
                } else {
                    onResource(Resource.Success(true))
                }
            }
            .addOnFailureListener {
                onResource.invoke(Resource.Error(message = it.message.toString()))
            }
    }

    override fun searchForGemByServing(query: Serving, onResource: (Resource<List<Gem>>) -> Unit) {
        onResource(Resource.Loading())
        gems
            .whereArrayContains("offerings", query.id.toString())
            .get()
            .addOnSuccessListener {
                val items = it.toObjects(Gem::class.java)
                onResource(Resource.Success(data = items))
            }.addOnFailureListener {
                onResource.invoke(Resource.Error(message = it.message.toString()))
            }
    }

    override fun searchForGemByLocation(query: String, onResource: (Resource<List<Gem>>) -> Unit) {
        onResource(Resource.Loading())
        gems.
        whereEqualTo("locationName", query)
            .get()
            .addOnSuccessListener {
                val items = it.toObjects(Gem::class.java)
                Resource.Success(data = items)
            }.addOnFailureListener {
                onResource.invoke(Resource.Error(message = it.message.toString()))
            }
    }

    override fun saveImages(
        images: List<Uri>,
        gem: Gem,
        onResource: (Resource<Boolean>) -> Unit
    ) {
        val imgsref = firebaseStorage.reference.child(Constant.gemsimagesdir)
        val downloadUrls = mutableListOf<String>()

        images.forEachIndexed { index, uri ->
            val ref = imgsref.child("${gem.gemId}_$index.jpg")
            ref.putFile(uri).addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {

                    //add image url to list
                    downloadUrls.add(it.toString())

                    if (downloadUrls.size == images.size) {
                        //all images uploaded
                        val updated = gem.copy(images = downloadUrls)
                        gems.document(gem.gemId)
                            .set(updated, SetOptions.merge())
                            .addOnSuccessListener {
                                onResource(Resource.Success(true))
                            }.addOnFailureListener {
                                onResource(Resource.Error(message = it.message.toString()))
                            }
                    }
                }

            }.addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }
        }
    }


}