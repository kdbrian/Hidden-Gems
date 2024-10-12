package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.data.model.GemDto
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.data.repo.GemsRepo
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GemsViewModel @Inject constructor(
    private val gemsRepo: GemsRepo
) : ViewModel() {

    private val _gems: MutableLiveData<Resource<List<Gem>>> = MutableLiveData()
    val gems: LiveData<Resource<List<Gem>>> get() = _gems

    private val _searchedgems: MutableLiveData<Resource<List<Gem>>> = MutableLiveData()
    val searchedgems: LiveData<Resource<List<Gem>>> get() = _searchedgems

    init {
        viewModelScope.launch {
            getGems()
        }
    }


    fun getGems() {
        _gems.postValue(Resource.Loading())
        gemsRepo.getGems { listResource ->
            _gems.postValue(listResource)
        }
    }


    fun searchGemsByLocation(query: String) {
        gemsRepo.searchForGemByLocation(query) { listResource ->
            _searchedgems.postValue(
                listResource
            )
        }
    }

    fun searchGemsByServing(query: Serving) {
        println("invoked ${query.id}")
        gemsRepo.searchForGemByServing(query) {
            println("got $it")
            _searchedgems.postValue(it)
        }
    }

    fun addGem(gemDto: GemDto, onResource: (Resource<Boolean>) -> Unit) {
        gemsRepo.addGem(dto = gemDto) {
            viewModelScope.launch {
                getGems()
            }
            onResource(it)
        }
    }


}