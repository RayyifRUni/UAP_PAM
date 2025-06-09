package com.example.uap.api

import com.example.uap.model.Plant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PlantApiService {

    @GET("plant/all")
    fun getAllPlants(): Call<PlantListResponse>

    @GET("plant/{name}")
    fun getPlantByName(@Path("name") name: String): Call<Plant>

    @POST("plant/new")
    fun createPlant(@Body plant: Plant): Call<Plant>

    @PUT("plant/{name}")
    fun updatePlant(@Path("name") name: String, @Body plant: Plant): Call<Plant>

    @DELETE("plant/{name}")
    fun deletePlant(@Path("name") name: String): Call<Void>
}

data class PlantListResponse(
    val data: List<Plant>
)