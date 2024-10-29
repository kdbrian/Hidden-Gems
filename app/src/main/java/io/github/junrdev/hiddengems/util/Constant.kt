package io.github.junrdev.hiddengems.util

import io.github.junrdev.hiddengems.data.model.Gem

object Constant {
    const val gemscollection = "hidden-gems"
//    const val gemscollection = "gems-recommendations-test1"
    const val reviewscollection = "reviews-prod1"
    const val gemsimagesdir = "gemsimages-prod1"
    const val serving = "serving"
    const val gem = "gem"
    const val servingcollection = "servings-prod1"
//    const val servingcollection = "servings-autopy"
    const val githubuserscollection = "githubuserscollection-prod1"
    const val usercollection = "usercollection-prod1"


    fun getPromptForGem(gem : Gem) = """
      Summarize the following Gem details in a brief and user-friendly manner suitable for a snackbar display. 
      Highlight the place name, location, and names of the servings. 
      Use one of the following dynamic phrases for the offerings, 
      selected at random: mouthwatering servings, tasty options, scrumptious dishes, delightful meals, flavorful selections, appetizing choices, savory offerings, exquisite dishes. 
      Here are the details: $gem
    """.trimIndent()
}