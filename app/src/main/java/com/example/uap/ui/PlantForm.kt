package com.example.uap.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uap.R
import com.example.uap.model.Plant
import com.example.uap.utils.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantForm : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var editDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var formImage: ImageView

    private var isUpdateMode = false
    private var plantToUpdate: Plant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plant_form)

        initViews()
        checkUpdateMode()

        btnSubmit.setOnClickListener {
            if (isUpdateMode) {
                updatePlant()
            } else {
                createPlant()
            }
        }
    }

    private fun initViews() {
        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        editDescription = findViewById(R.id.editDescription)
        btnSubmit = findViewById(R.id.btnSubmit)
        formImage = findViewById(R.id.formImage)
    }

    private fun checkUpdateMode() {
        val plantName = intent.getStringExtra("plant_name")
        val plantPrice = intent.getStringExtra("plant_price")
        val plantDesc = intent.getStringExtra("plant_description")

        if (!plantName.isNullOrEmpty() && !plantPrice.isNullOrEmpty() && !plantDesc.isNullOrEmpty()) {
            isUpdateMode = true
            plantToUpdate = Plant(plantName, plantDesc, plantPrice)
            setupForm()
        } else {
            btnSubmit.text = "Tambah"
        }
    }

    private fun setupForm() {
        plantToUpdate?.let { plant ->
            editName.setText(plant.plant_name)
            editPrice.setText(plant.price)
            editDescription.setText(plant.description)
            btnSubmit.text = "Update"
        }
    }

    private fun createPlant() {
        val name = editName.text.toString().trim()
        val price = editPrice.text.toString().trim()
        val description = editDescription.text.toString().trim()

        if (validateInput(name, price, description)) {
            val plant = Plant(name, description, price)

            ApiConfig.api.createPlant(plant).enqueue(object : Callback<Plant> {
                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PlantForm, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@PlantForm, "Gagal menambahkan tanaman: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Plant>, t: Throwable) {
                    Toast.makeText(this@PlantForm, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updatePlant() {
        val name = editName.text.toString().trim()
        val price = editPrice.text.toString().trim()
        val description = editDescription.text.toString().trim()

        if (validateInput(name, price, description)) {
            val plant = Plant(name, description, price)
            val originalName = plantToUpdate?.plant_name ?: ""

            ApiConfig.api.updatePlant(originalName, plant).enqueue(object : Callback<Plant> {
                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PlantForm, "Tanaman berhasil diupdate", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@PlantForm, "Gagal mengupdate tanaman: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Plant>, t: Throwable) {
                    Toast.makeText(this@PlantForm, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun validateInput(name: String, price: String, description: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(this, "Nama tanaman tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            price.isEmpty() -> {
                Toast.makeText(this, "Harga tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            description.isEmpty() -> {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            !isValidPrice(price) -> {
                Toast.makeText(this, "Harga harus berupa angka yang valid", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun isValidPrice(price: String): Boolean {
        return try {
            price.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}