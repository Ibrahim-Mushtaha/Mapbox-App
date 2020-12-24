package com.example.mapbox.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapbox.R
import com.example.mapbox.model.location.TrackLocation
import kotlinx.android.synthetic.main.user_item.view.*


class UserAdapter(
    var activity: Activity, var data: MutableList<TrackLocation>, val itemclick: onClick
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    class UserViewHolder(item: View) : RecyclerView.ViewHolder(item)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }




    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

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
