package io.github.junrdev.hiddengems.domain.repo

import io.github.junrdev.hiddengems.data.model.Gem

interface GeminiRepo {
    suspend fun summarizeGem(gem: Gem): String
}