package com.example.uap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uap.adapter.PlantAdapter
import com.example.uap.model.Plant
import com.example.uap.ui.PlantDetail
import com.example.uap.ui.PlantForm
import com.example.uap.utils.ApiConfig
import com.example.uap.api.PlantListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var rvPlants: RecyclerView
    private lateinit var btnAddPlant: Button
    private var plantAdapter: PlantAdapter? = null

    companion object {
        private const val ADD_PLANT_REQUEST_CODE = 1001
        private const val EDIT_PLANT_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadPlants()
    }

    private fun initializeViews() {
        rvPlants = findViewById(R.id.recyclerViewPlants)
        btnAddPlant = findViewById(R.id.btnAddPlant)
    }

    private fun setupRecyclerView() {
        rvPlants.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        btnAddPlant.setOnClickListener {
            val intent = Intent(this, PlantForm::class.java)
            startActivityForResult(intent, ADD_PLANT_REQUEST_CODE)
        }
    }

    private fun loadPlants() {
        Toast.makeText(this, "Memuat data...", Toast.LENGTH_SHORT).show()

        ApiConfig.api.getAllPlants().enqueue(object : Callback<PlantListResponse> {
            override fun onResponse(call: Call<PlantListResponse>, response: Response<PlantListResponse>) {
                if (response.isSuccessful) {
                    val plantResponse = response.body()
                    val plants = plantResponse?.data ?: emptyList()

                    if (plants.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Tidak ada data tanaman", Toast.LENGTH_SHORT).show()
                        setupEmptyAdapter()
                    } else {
                        Toast.makeText(this@MainActivity, "Data berhasil dimuat: ${plants.size} tanaman", Toast.LENGTH_SHORT).show()
                        setupAdapter(plants)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat data: ${response.message()}", Toast.LENGTH_LONG).show()
                    setupEmptyAdapter()
                }
            }

            override fun onFailure(call: Call<PlantListResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal memuat data: ${t.message}", Toast.LENGTH_LONG).show()
                setupEmptyAdapter()
            }
        })
    }

    private fun setupAdapter(plants: List<Plant>) {
        plantAdapter = PlantAdapter(
            plants = plants,
            onItemClick = { plant -> openPlantDetail(plant) },
            onDeleteClick = { plant -> deletePlant(plant) },
            onEditClick = { plant -> editPlant(plant) }
        )
        rvPlants.adapter = plantAdapter
    }

    private fun setupEmptyAdapter() {
        plantAdapter = PlantAdapter(
            plants = emptyList(),
            onItemClick = { },
            onDeleteClick = { },
            onEditClick = { }
        )
        rvPlants.adapter = plantAdapter
    }

    private fun openPlantDetail(plant: Plant) {
        val intent = Intent(this, PlantDetail::class.java).apply {
            putExtra("plant_name", plant.plant_name ?: "")
            putExtra("plant_price", plant.price ?: 0)
            putExtra("plant_description", plant.description ?: "")
        }
        startActivity(intent)
    }

    private fun editPlant(plant: Plant) {
        val intent = Intent(this, PlantForm::class.java).apply {
            putExtra("edit_mode", true)
            putExtra("plant_name", plant.plant_name ?: "")
            putExtra("plant_price", plant.price ?: 0)
            putExtra("plant_description", plant.description ?: "")
        }
        startActivityForResult(intent, EDIT_PLANT_REQUEST_CODE)
    }

    private fun deletePlant(plant: Plant) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus ${plant.plant_name ?: "tanaman ini"}?")
            .setPositiveButton("Hapus") { _, _ ->
                performDelete(plant)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performDelete(plant: Plant) {
        val plantName = plant.plant_name
        if (plantName.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid plant name", Toast.LENGTH_SHORT).show()
            return
        }

        ApiConfig.api.deletePlant(plantName).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "$plantName berhasil dihapus", Toast.LENGTH_SHORT).show()
                    refreshPlantList()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal menghapus tanaman", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun refreshPlantList() {
        loadPlants()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_PLANT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    refreshPlantList()
                }
            }
            EDIT_PLANT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    refreshPlantList()
                }
            }
        }
    }
}