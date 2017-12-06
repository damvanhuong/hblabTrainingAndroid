package com.esp.foodmaking

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.coordinatorLayout

class FoodDetailActivityUI : AnkoComponent<FoodDetailActivity> {

    lateinit var toolbar : Toolbar
    lateinit var listView : ListView
    lateinit var pullToZoom : PullToZoomScrollViewEx

    fun ViewManager.pullToZoom(init: PullToZoomScrollViewEx.() -> Unit) : PullToZoomScrollViewEx{
        return ankoView({PullToZoomScrollViewEx(it)}, 0, init)
    }

    override fun createView(ui: AnkoContext<FoodDetailActivity>): View {
        return with(ui) {
            verticalLayout {
                orientation = LinearLayout.VERTICAL

                toolbar = toolbar {
                    backgroundResource = R.color.main
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        elevation = 5f
                    }
                    id = Ids.toolbar
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        title = context.getString(R.string.app_name)
                    }
                    setTitleTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        navigationIcon = context.getDrawable(R.drawable.ic_arrow_back)
                    }
                }.lparams(width = matchParent, height = sp(56))

                pullToZoom = pullToZoom {
                    zoomView = imageView {
                        id = Ids.imageFood
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        imageResource = R.drawable.food
                    }
                }
            }
        }
    }


    private object Ids {
        val imageFood = 2
        val toolbar = 4
    }
}