package com.game.score.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.game.score.R
import com.game.score.ScoreModel

/**
 * [RecyclerView.Adapter] that can display a [ScoreModel].
 */
class ScoreItemRecyclerViewAdapter(
    private val values: List<ScoreModel>
) : RecyclerView.Adapter<ScoreItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.orderView.text = item.order.toString()
        holder.nameView.text = item.name
        holder.scoreView.text = item.score.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderView: TextView = view.findViewById(R.id.item_scoreModel_order)
        val nameView: TextView = view.findViewById(R.id.item_scoreModel_name)
        val scoreView: TextView = view.findViewById(R.id.item_scoreModel_score)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}