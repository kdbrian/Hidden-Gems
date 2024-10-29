package io.github.junrdev.hiddengems.data.repoimpl

import com.google.ai.client.generativeai.GenerativeModel
import io.github.junrdev.hiddengems.BuildConfig
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.domain.repo.GeminiRepo
import io.github.junrdev.hiddengems.util.Constant
import timber.log.Timber

class GeminiRepoImpl : GeminiRepo {

    private val geminiApiKey = BuildConfig.geminiApiKey

    private val generativeModel = GenerativeModel(
        "gemini-1.5-flash",
        apiKey = geminiApiKey
    )

    override suspend fun summarizeGem(gem: Gem): String {
        val prompt = Constant.getPromptForGem(gem)
        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Something unexpected happened."
        } catch (e: Exception) {
            Timber.d(e)
            "Something unexpected happened."
        }
    }
}