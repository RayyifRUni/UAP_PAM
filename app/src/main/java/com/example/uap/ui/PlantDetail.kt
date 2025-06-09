package com.example.uap.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uap.R

class PlantDetail : AppCompatActivity() {

    private lateinit var ivPlantImage: ImageView
    private lateinit var tvPlantName: TextView
    private lateinit var tvPlantPrice: TextView
    private lateinit var tvPlantDescription: TextView
    private lateinit var btnEdit: Button

    private var plantImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plant_detail)

        initViews()
        getPlantDataFromIntent()
        setupClickListeners()
    }

    private fun initViews() {
        ivPlantImage = findViewById(R.id.detailImage)
        tvPlantName = findViewById(R.id.detailName)
        tvPlantPrice = findViewById(R.id.detailPrice)
        tvPlantDescription = findViewById(R.id.detailDescription)
        btnEdit = findViewById(R.id.btnUpdate)
    }

    private fun getPlantDataFromIntent() {
        val plantName = intent.getStringExtra("plant_name") ?: ""
        val plantPrice = intent.getStringExtra("plant_price") ?: ""
        val plantDescription = intent.getStringExtra("plant_description") ?: ""
        plantImage = intent.getStringExtra("plant_image") ?: ""

        tvPlantName.text = plantName
        tvPlantPrice.text = "Rp $plantPrice"
        tvPlantDescription.text = plantDescription

        Glide.with(this)
            .load(plantImage)
            .placeholder(R.drawable.plant_img)
            .error(R.drawable.plant_img)
            .centerCrop()
            .into(ivPlantImage)

        supportActionBar?.title = plantName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupClickListeners() {
        btnEdit.setOnClickListener {
            val intent = Intent(this, PlantForm::class.java)
            intent.putExtra("plant_name", tvPlantName.text.toString())
            intent.putExtra("plant_price", tvPlantPrice.text.toString().replace("Rp ", ""))
            intent.putExtra("plant_description", tvPlantDescription.text.toString())
            intent.putExtra("plant_image", plantImage ?: "")
            startActivityForResult(intent, EDIT_PLANT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PLANT_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Tanaman berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val EDIT_PLANT_REQUEST_CODE = 1002
    }
}