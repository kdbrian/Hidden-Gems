package io.github.junrdev.hiddengems.domain.repo

import android.net.Uri
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.data.model.GemDto
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.util.Resource

interface GemsRepo {
    fun getGems(onResource: (Resource<List<Gem>>) -> Unit)
    fun addGem(dto: GemDto, onResource: (Resource<Boolean>) -> Unit)
    fun saveImages(images: List<Uri>, gem: Gem, onResource: (Resource<Boolean>) -> Unit)
    fun searchForGemByLocation(query: String, onResource: (Resource<List<Gem>>) -> Unit)
    fun searchForGemByServing(query: Serving, onResource: (Resource<List<Gem>>) -> Unit)
}
