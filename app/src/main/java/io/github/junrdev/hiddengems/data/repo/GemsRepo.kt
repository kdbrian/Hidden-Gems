package io.github.junrdev.hiddengems.data.repo

import android.net.Uri
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.data.model.GemDto
import io.github.junrdev.hiddengems.util.Resource

interface GemsRepo {
    suspend fun getGems(onResource: (Resource<List<Gem>>) -> Unit)
    suspend fun addGem(dto: GemDto, onResource: (Resource<Boolean>) -> Unit)
    suspend fun searchForGem(query: String, onResource: (Resource<List<Gem>>) -> Unit)
    fun saveImages(images : List<Uri>, gem : Gem, onResource: (Resource<Boolean>) -> Unit)
}
