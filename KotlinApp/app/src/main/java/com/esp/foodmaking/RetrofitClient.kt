package com.esp.foodmaking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        var retrofit: Retrofit? = null
        fun getFoodAPIService(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl("http://food2fork.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }

        fun getAppAPIService(): APIService {
            return Retrofit.Builder()
                    .baseUrl("https://foodmaking.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIService::class.java)
        }
    }
}
