package com.example.uap.model

import com.google.gson.annotations.SerializedName

data class Plant(
    @SerializedName("plant_name")
    val plant_name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: String
) {
    constructor() : this("", "", "")
}