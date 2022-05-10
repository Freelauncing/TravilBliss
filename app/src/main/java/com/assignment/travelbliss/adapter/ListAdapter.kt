package com.assignment.travelbliss.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assignment.travelbliss.R
import com.assignment.travelbliss.model.Location
import com.assignment.travelbliss.uitel.loadImage
import com.assignment.travelbliss.view.DetailsActivity

class ListAdapter (var mContext: Context, var locationList:List<Location>):
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    inner class ListViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        var imgT = v.findViewById<ImageView>(R.id.LocationImageView)
        var nameT = v.findViewById<TextView>(R.id.nameTextView)
        var descriT = v.findViewById<TextView>(R.id.descriptionTextView)
        var ratingT = v.findViewById<TextView>(R.id.ratingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var infalter = LayoutInflater.from(parent.context)
        var v = infalter.inflate(R.layout.row_item, parent, false)
        return ListViewHolder(v)
    }

    override fun getItemCount(): Int = locationList.size

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var newList = locationList[position]
        holder.nameT.text = newList.name
        holder.descriT.text = newList.description
        holder.ratingT.text = newList.rating
        //holder.locatT.text = newList.locationLtLn
        holder.imgT.loadImage(newList.imageUrl)
            // When the spot is clicked, navigate to details screen
        holder.v.setOnClickListener {

            val name = newList.name
            val descrip = newList.description
            val imgUri = newList.imageUrl
            val rating = newList.rating
            val locatn = newList.locationLtLn

            val mIntent = Intent(mContext, DetailsActivity::class.java)
            mIntent.putExtra("NAMET", name)
            mIntent.putExtra("DESCRIT", descrip)
            mIntent.putExtra("IMGURI", imgUri)
            mIntent.putExtra("RATING", rating)
            mIntent.putExtra("LOCAT", locatn)


            mContext.startActivity(mIntent)
        }
    }
}
