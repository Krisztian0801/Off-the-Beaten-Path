package hu.krisztian.offthebeatenpath.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.krisztian.offthebeatenpath.R
import hu.krisztian.offthebeatenpath.model.Place
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesAdapter(
    private val places: List<Place>,
    private val onClick: (Place) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_list_item, parent, false)
        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size

    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)

        fun bind(place: Place) {
            nameTextView.text = place.poi_name
            categoryTextView.text = ""

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.categoryService.getCategory(place.category_id) // API hívás
                    withContext(Dispatchers.Main) {
                        categoryTextView.text = response.category
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        categoryTextView.text = ""
                    }
                }
            }


            itemView.setOnClickListener {
                onClick(place)
            }
        }
    }
}

