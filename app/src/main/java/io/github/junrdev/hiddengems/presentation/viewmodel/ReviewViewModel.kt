package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.domain.repo.ReviewsRepo
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.launch


class ReviewViewModel constructor(
    private val reviewsRepo: ReviewsRepo
) : ViewModel() {


    private val _allreviews: MutableLiveData<Resource<List<Review>>> = MutableLiveData()
    val allreviews: LiveData<Resource<List<Review>>> get() = _allreviews


    private val _gemreviews: MutableLiveData<Resource<List<Review>>> = MutableLiveData()
    val gemreviews: LiveData<Resource<List<Review>>> get() = _gemreviews

    init {
        viewModelScope.launch {
            getAllReviews()
        }
    }

    suspend fun addReview(review: Review, onResource: (Resource<String>) -> Unit) {
        reviewsRepo.addReview(review) {
            onResource(it)
            viewModelScope.launch {
                getGemReviews(review.gemId!!)
            }
        }
    }


    suspend fun getAllReviews() {
        reviewsRepo.getAllReviews {
            _allreviews.value = (it)
        }
    }

    suspend fun getGemReviews(gemId: String) {
        _gemreviews.value = Resource.Loading()
        reviewsRepo.getGemReviews(gemId) {
            _gemreviews.value = it
        }
    }

    suspend fun getReviewById(reviewId: String, onResource: (Resource<Review>) -> Unit) {
        reviewsRepo.getReviewById(reviewId) {
            onResource(it)
        }
    }

}
