package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.data.model.GemDto
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
        gemsRepo.getGems { listResource ->
            _gems.postValue(listResource)
        }
    }

    fun searchGemsByName(query: String) {
        gemsRepo.searchForGemByName(query) { listResource -> _searchedgems.postValue(listResource) }
    }

    fun searchGemsByLocation(query: String) {
        gemsRepo.searchForGemByLocation(query) { listResource ->
            _searchedgems.postValue(
                listResource
            )
        }
    }

    fun searchGemsByServing(query: String) {
        viewModelScope.launch {
            getGems()
            val gemsMap = _gems.value?.data?.filter { gem ->
                gem.servings.any { serving ->
                    serving.name == query
                }
            } ?: emptyList()
            println("list ${_gems.value?.data}")
            println("list $gemsMap")
            _searchedgems.postValue(
                Resource.Success(data = gemsMap)
            )
        }

    }

    fun addGem(gemDto: GemDto, onResource: (Resource<Boolean>) -> Unit) {
        gemsRepo.addGem(dto = gemDto) { onResource(it); getGems() }
    }

}