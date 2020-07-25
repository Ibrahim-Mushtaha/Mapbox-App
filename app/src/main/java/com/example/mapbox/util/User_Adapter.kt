package com.example.mapbox.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mapbox.R
import com.example.mapbox.model.TrackLocation
import kotlinx.android.synthetic.main.user_item.view.*
import java.util.*


class User_Adapter(
    var activity: Activity, var data: MutableList<TrackLocation>, val itemclick: onClick
) :
    RecyclerView.Adapter<User_Adapter.MyViewHolder>() {


    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {





    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }




    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.itemView.apply {
                all_card.setOnClickListener {
                    itemclick.onClickItem(holder.adapterPosition,1)
                }

                txt_name.setText(data[position].name)

            }


    }



    interface onClick {
        fun onClickItem(position: Int, type: Int)
    }


}
