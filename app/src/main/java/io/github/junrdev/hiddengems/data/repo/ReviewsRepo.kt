package io.github.junrdev.hiddengems.data.repo

import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.util.Resource

interface ReviewsRepo {
    suspend fun addReview(onResource: (Resource<String>)->Unit, review: Review)
    suspend fun getAllReviews(onResource: (Resource<List<Review>>)->Unit)
    suspend fun getGemReviews(gemId : String ,onResource: (Resource<List<Review>>)->Unit)
    suspend fun getReviewById(reviewId : String, onResource: (Resource<Review>)->Unit)
}