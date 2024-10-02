package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.data.repo.ServingRepo
import io.github.junrdev.hiddengems.util.Resource
import javax.inject.Inject

@HiltViewModel
class ServingsViewModel @Inject constructor(
    private val servingRepo: ServingRepo
) : ViewModel() {

    private val _servings: MutableLiveData<Resource<List<Serving>>> = MutableLiveData()
    val servings: LiveData<Resource<List<Serving>>> get() = _servings

    fun getServings() {
        _servings.postValue(Resource.Loading())
        servingRepo.getServings { listResource ->
            _servings.postValue(listResource)
        }
    }

    fun addServing(serving: Serving) {
        servingRepo.saveServing(serving) { _ -> }
    }

}