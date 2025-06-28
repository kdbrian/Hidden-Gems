package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.domain.repo.ServingRepo
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.launch


class ServingsViewModel constructor(
    private val servingRepo: ServingRepo
) : ViewModel() {

    private val _servings: MutableLiveData<Resource<List<Serving>>> = MutableLiveData()
    val servings: LiveData<Resource<List<Serving>>> get() = _servings

    init {
        viewModelScope.launch {
            getServings()
        }
    }
    fun getServings() {
        _servings.postValue(Resource.Loading())
        servingRepo.getServings { listResource ->
            _servings.postValue(listResource)
        }
    }

    fun addServing(serving: Serving, onResource: (Resource<String>) -> Unit) {
        servingRepo.saveServing(serving) { onResource(it) }
    }

}
