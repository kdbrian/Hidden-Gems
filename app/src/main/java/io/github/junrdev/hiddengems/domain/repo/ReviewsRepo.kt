package io.github.junrdev.hiddengems.domain.repo

import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.util.Resource

interface ReviewsRepo {
    suspend fun addReview(review: Review, onResource: (Resource<String>)->Unit)
    suspend fun getAllReviews(onResource: (Resource<List<Review>>)->Unit)
    suspend fun getGemReviews(gemId : String ,onResource: (Resource<List<Review>>)->Unit)
    suspend fun getReviewById(reviewId : String, onResource: (Resource<Review>)->Unit)
}