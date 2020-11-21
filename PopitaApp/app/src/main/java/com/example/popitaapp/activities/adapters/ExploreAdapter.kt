package com.example.popitaapp.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Explore
import java.lang.Math.random
import java.util.*
import kotlin.collections.ArrayList


class ExploreAdapter(val userList: ArrayList<Explore>, var clickListener: OnExploreItemClickListener): RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.txtDistance?.text = userList[position].distance.toString()
        holder?.txtName?.text = userList[position].user.first_name
        holder?.txtLocation?.text = userList[position].location
        holder?.txtDate?.text = "Last update: " + userList[position].timestamp

        //set random image to all cards
        val resId = intArrayOf(R.drawable.ic_localization_background_01, R.drawable.ic_localization_background_02, R.drawable.ic_localization_background_03)
        val rand = Random()
        val index: Int = rand.nextInt(resId.size - 1 - 0 + 1) + 0

        holder?.cardImage?.setImageResource(resId[index])

        holder.initialize(userList.get(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.explore_item_layout, parent, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    //for duplicated items
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.txtName)
        val txtDistance = itemView.findViewById<TextView>(R.id.txtDistance)
        val txtLocation = itemView.findViewById<TextView>(R.id.txtLocation)
        val cardImage = itemView.findViewById<ImageView>(R.id.cardImage)
        val txtDate = itemView.findViewById<TextView>(R.id.txtDate)

        fun initialize(item: Explore, action: OnExploreItemClickListener) {
            val id = item.id

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }
}

interface OnExploreItemClickListener {
    fun onItemClick(item: Explore, position: Int)
}