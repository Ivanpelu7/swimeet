package com.example.swimeet.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlaceAutocompleteAdapter(
    context: Context,
    private val onPlaceClick: (AutocompletePrediction) -> Unit
) : RecyclerView.Adapter<PlaceAutocompleteAdapter.ViewHolder>() {

    private var predictions: List<AutocompletePrediction> = emptyList()
    private val customTypeface: Typeface?

    init {
        // Inicializa la fuente personalizada
        customTypeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
    }

    fun setPredictions(predictions: List<AutocompletePrediction>) {
        this.predictions = predictions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.textView.text = prediction.getFullText(null).toString()
        holder.itemView.setOnClickListener {
            onPlaceClick(prediction)
        }
    }

    override fun getItemCount(): Int {
        return predictions.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)

        init {
            // Establece la fuente personalizada en el TextView si está disponible
            customTypeface?.let {
                textView.typeface = it
            }
        }
    }
}