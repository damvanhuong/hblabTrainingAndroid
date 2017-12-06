package com.esp.foodmaking

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.imageResource

class SideMenuAdapter(ctx: Context) : RecyclerView.Adapter<SideMenuAdapter.SideMenuViewHolder>() {

    var context : Context = ctx
    var list : MutableList<SideMenuItem> = ArrayList()
    var login : Boolean = false
    var onItemClickListener : OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    init {
        list.add(SideMenuItem(R.drawable.ic_login, "Login", true))
        list.add(SideMenuItem(R.drawable.ic_menu_like, "Liked", true))
        list.add(SideMenuItem(R.drawable.ic_menu_trending, "Top trending", true))
        list.add(SideMenuItem(R.drawable.ic_top_rate, "Top rate", true))
        list.add(SideMenuItem(R.drawable.ic_menu_setting, "Setting", true))
        list.add(SideMenuItem(R.drawable.ic_menu_share, "Share", true))
        list.add(SideMenuItem(R.drawable.ic_menu_send, "Send", true))
        list.add(SideMenuItem(R.drawable.ic_logout, "Logout", false))
    }

    override fun onBindViewHolder(holder: SideMenuViewHolder?, position: Int) {
        var item = this.list[position]
        holder?.icon?.imageResource = item.res
        holder?.text?.text = item.text
        if (item.visibility) {
            holder?.itemView?.visibility = View.VISIBLE
            var params = holder?.itemView?.layoutParams
            params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder?.itemView?.layoutParams = params
        } else {
            holder?.itemView?.visibility = View.GONE
            var params = holder?.itemView?.layoutParams
            params?.height = 0
            holder?.itemView?.layoutParams = params
        }
        holder?.itemView?.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener?.onItemClick(position)
            }

        }
    }

    fun login() {
        list[0].visibility = false
        list[list.size - 1].visibility = true
        notifyDataSetChanged()
        login = true
    }

    fun logout() {
        list[0].visibility = true
        list[list.size - 1].visibility = false
        notifyDataSetChanged()
        login = false
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SideMenuViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.side_menu_item, parent, false)
        return SideMenuViewHolder(view)
    }

    class SideMenuViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val icon : ImageView = itemView.findViewById(R.id.icon_menu)
        val text : TextView = itemView.findViewById(R.id.text_menu)
    }


    class SideMenuItem(var res: Int, var text: String, var visibility : Boolean)

}


