package com.esp.foodmaking

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.jetbrains.anko.sp
import org.jetbrains.anko.startActivity
import java.io.Serializable
import java.util.*
import kotlin.properties.Delegates

class FoodAdapter(context: Context, list: List<Food>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    var context: Context by Delegates.notNull()
    var list: List<Food> by Delegates.notNull()
    init {
        this.context = context
        this.list = list

    }

    constructor(context: Context) : this(context, ArrayList())

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = list[position]
        holder.itemView.layoutParams.width = context.resources.displayMetrics.widthPixels
        Glide.with(context).load(food.imageUrl).into(holder.image)
        holder.name.text = Html.fromHtml(food.title).toString()
        holder.publisher.text = food.publisher
        holder.rate.text = food.socialRank.toInt().toString()
        holder.like.text = food.likeCount.toString()
        holder.itemView.setOnClickListener({
            context.startActivity<FoodDetailActivity>("item" to food as Serializable, "position" to position)
            (context as Activity).overridePendingTransition(R.anim.enter_from_bottom, R.anim.no_translation)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false)
        return FoodViewHolder(view)
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_food)
        val name: TextView = itemView.findViewById(R.id.food_name)
        val publisher: TextView = itemView.findViewById(R.id.publisher)
        val rate : TextView = itemView.findViewById(R.id.rate)
        val like : TextView = itemView.findViewById(R.id.like)
        init {
            var displayMetrics = itemView.context.resources.displayMetrics
            image.layoutParams.height = (displayMetrics.widthPixels) / 2
        }


    }

}