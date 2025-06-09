package com.example.uap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uap.R
import com.example.uap.model.Plant

class PlantAdapter(
    private val plants: List<Plant>,
    private val onItemClick: (Plant) -> Unit,
    private val onDeleteClick: (Plant) -> Unit,
    private val onEditClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        return try {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
            PlantViewHolder(view)
        } catch (e: Exception) {
            throw RuntimeException("Error inflating layout R.layout.item_plant", e)
        }
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        if (position < plants.size) {
            val plant = plants[position]
            holder.bind(plant)
        }
    }

    override fun getItemCount(): Int = plants.size

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlantName: TextView? = itemView.findViewById(R.id.tvPlantName)
        private val tvPlantPrice: TextView? = itemView.findViewById(R.id.tvPlantPrice)
        private val tvPlantDescription: TextView? = itemView.findViewById(R.id.detailDescription)
        private val btnDelete: Button? = itemView.findViewById(R.id.btnDelete)
        private val btnDetail: Button? = itemView.findViewById(R.id.btnDetail)
        private val btnEdit: Button? = itemView.findViewById(R.id.btnUpdate)

        fun bind(plant: Plant) {
            try {
                tvPlantName?.text = plant.plant_name ?: "Unknown Plant"
                tvPlantPrice?.text = "Rp ${plant.price ?: 0}"

                val description = plant.description ?: "No description available"
                tvPlantDescription?.text = if (description.length > 100) {
                    "${description.substring(0, 100)}..."
                } else {
                    description
                }

                btnDetail?.setOnClickListener {
                    onItemClick(plant)
                }

                btnDelete?.setOnClickListener {
                    onDeleteClick(plant)
                }

                btnEdit?.setOnClickListener {
                    onEditClick(plant)
                }

                itemView.setOnClickListener {
                    onItemClick(plant)
                }
            } catch (e: Exception) {
                android.util.Log.e("PlantAdapter", "Error binding plant data", e)
            }
        }
    }
}