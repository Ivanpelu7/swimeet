package com.example.swimeet.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.swimeet.R

class SpinnerAdapter(
    context: Context,
    resource: Int,
    items: Array<String>,
    private val fontPath: String,
    private val textSize: Float
) :
    ArrayAdapter<String>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        applyFont(view)
        view.textSize = textSize
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        applyFont(view)
        view.textSize = textSize
        return view
    }

    private fun applyFont(textView: TextView) {
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
        textView.typeface = typeface
    }
}