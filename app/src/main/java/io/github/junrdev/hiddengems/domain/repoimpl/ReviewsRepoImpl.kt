package io.github.junrdev.hiddengems.domain.repoimpl

import com.google.firebase.firestore.FirebaseFirestore
import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.data.repo.ReviewsRepo
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource

class ReviewsRepoImpl(
    private val firestore: FirebaseFirestore
) : ReviewsRepo {

    private val reviewCollection = firestore.collection(Constant.reviewscollection)

    override suspend fun addReview(review: Review, onResource: (Resource<String>) -> Unit) {
        val id = reviewCollection.document()
        reviewCollection.add(review.copy(reviewId = id.id))
            .addOnSuccessListener {
                onResource(Resource.Success(it.id))
            }
            .addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }
    }

    override suspend fun getAllReviews(onResource: (Resource<List<Review>>) -> Unit) {
        reviewCollection.get()
            .addOnSuccessListener {
                val reviews = it.toObjects(Review::class.java)
                onResource(Resource.Success(reviews))
            }
            .addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }

    }

    override suspend fun getGemReviews(
        gemId: String,
        onResource: (Resource<List<Review>>) -> Unit
    ) {
        reviewCollection
            .whereEqualTo("gemId", gemId)
            .get()
            .addOnSuccessListener {
                val reviews = it.toObjects(Review::class.java)
                onResource(Resource.Success(reviews))
            }
            .addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }

    }

    override suspend fun getReviewById(reviewId: String, onResource: (Resource<Review>) -> Unit) {
        reviewCollection.document(reviewId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(Review::class.java)?.also {
                    onResource(Resource.Success(it))
                }
            }
            .addOnFailureListener {
                onResource(Resource.Error(message = it.message.toString()))
            }

    }


}