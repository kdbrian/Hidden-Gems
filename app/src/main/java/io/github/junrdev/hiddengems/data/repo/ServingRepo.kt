package io.github.junrdev.hiddengems.data.repo

import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.util.Resource

interface ServingRepo {
    fun getServings(onResource: (Resource<List<Serving>>) -> Unit)
    fun saveServing(serving: Serving,onResource:( Resource<Any>)->Unit)
}