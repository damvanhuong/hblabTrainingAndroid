package com.esp.foodmaking

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ResultAdapter(context: Context, layoutRes: Int) : ArrayAdapter<Food>(context,layoutRes){

    var list : MutableList<Food> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view : View? = convertView
        var food = list[position]
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null)
        }
        var title = view?.findViewById<TextView>(android.R.id.text1)
        var publisher = view?.findViewById<TextView>(android.R.id.text2)
        title?.text = Html.fromHtml(food.title).toString()
        publisher?.text = food.publisher

        return view!!
    }

    override fun getItem(position: Int): Food {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }
}