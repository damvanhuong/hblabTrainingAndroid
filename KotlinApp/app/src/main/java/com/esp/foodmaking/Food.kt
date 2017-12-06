package com.esp.foodmaking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Food(
        @SerializedName("publisher")
        @Expose
        val publisher: String,
        @SerializedName("f2f_url")
        @Expose
        val url: String,
        val title: String,
        @SerializedName("source_url")
        @Expose
        val sourceUrl: String,
        @SerializedName("recipe_id")
        @Expose
        val recipeId: String,
        @SerializedName("image_url")
        @Expose
        val imageUrl: String,
        @SerializedName("social_rank")
        @Expose
        val socialRank: Double,
        @SerializedName("publisher_url")
        @Expose
        val publisherUrl: String,
        val ingredients: List<String>,
        var likeCount: Int = 0) : Serializable