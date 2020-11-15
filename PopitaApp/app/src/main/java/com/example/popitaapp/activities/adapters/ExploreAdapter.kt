package com.example.popitaapp.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Explore
import com.example.popitaapp.activities.models.Room

class ExploreAdapter(val userList: ArrayList<Explore>, var clickListener: OnExploreItemClickListener): RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.txtName?.text = userList[position].receiver_name
        holder?.txtTitle?.text = userList[position].last_message
        holder?.txtLastMessageTimestamp?.text = userList[position].last_message_timestamp

        holder.initialize(userList.get(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.room_item_layout, parent, false)
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
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        val txtLastMessageTimestamp = itemView.findViewById<TextView>(R.id.txtLastMessageTimestamp)

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