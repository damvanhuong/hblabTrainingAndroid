package com.esp.foodmaking

import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET("/api/search")
    fun getFoodList(@QueryMap query:Map<String, String>):Call<FoodListResponse>

    @GET("/api/search")
    fun search(@QueryMap query: Map<String, String>) : Call<FoodListResponse>

    @GET("/api/get")
    fun getRecipe(@QueryMap query: Map<String, String>) : Call<RecipeResponse>

    @GET("/api/like")
    fun getListLike(@QueryMap query: Map<String, String>) : Call<FoodListResponse>

    @GET("api/recipe")
    fun getLikeCount(@QueryMap query: Map<String, String>) : Call<LikeResponse>

    @POST("/api/add_like")
    fun like(@Body body: AddLikeRequest) : Call<LikeOrUnlikeResponse>

    @FormUrlEncoded
    @POST("api/delete_like")
    fun unlike(@FieldMap body: HashMap<String, String>) : Call<LikeOrUnlikeResponse>

    @POST("api/like_count")
    fun getLike(@Body body: GetLikeCountRequest) : Call<GetLikeCountResponse>

}