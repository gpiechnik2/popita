package com.example.popitaapp.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Room
import java.util.*
import kotlin.collections.ArrayList

class RoomAdapter(val userList: ArrayList<Room>, var clickListener: OnRoomItemClickListener): RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.txtName?.text = userList[position].receiver_name
        holder?.txtTitle?.text = userList[position].last_message
        holder?.txtLastMessageTimestamp?.text = userList[position].last_message_timestamp
        holder?.txtFirstLetter?.text = userList[position].receiver_name

        //set random image to all cards
        val resId = intArrayOf(R.color.yellow, R.color.green, R.color.pink, R.color.orange, R.color.blue)
        val rand = Random()
        val index: Int = rand.nextInt(resId.size - 1 - 0 + 1) + 0

        holder?.cardImage?.setImageResource(resId[index])

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
        val cardImage = itemView.findViewById<ImageView>(R.id.cardImage)
        val txtFirstLetter = itemView.findViewById<TextView>(R.id.txtFirstLetter)

        fun initialize(item: Room, action: OnRoomItemClickListener) {
            val id = item.id

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }

        }
    }


}

interface OnRoomItemClickListener {
    fun onItemClick(item: Room, position: Int)
}