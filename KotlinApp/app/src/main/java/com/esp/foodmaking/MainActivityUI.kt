package com.esp.foodmaking

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.view.animation.AnimationUtils
import android.widget.*
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.quinny898.library.persistentsearch.SearchBox
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class MainActivityUI : AnkoComponent<MainActivity> {

    lateinit var pullToRefresh: SwipeRefreshLayout
    lateinit var foodRecyclerView: RecyclerView
    lateinit var foodAdapter: FoodAdapter
    lateinit var progressBar: ProgressBar
    lateinit var onLoadMoreListener: OnLoadMoreListener
    lateinit var navigationView: LinearLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var sideMenu: RecyclerView
    lateinit var avatar: CircleImageView
    lateinit var userName: TextView
    lateinit var cover: KenBurnsView
    lateinit var menuButton: ImageView
    lateinit var searchButton: ImageView
    lateinit var toolbarTitle: TextView
    lateinit var noData: TextView
    var loading = false
    lateinit var fab: FloatingActionButton

    fun ViewManager.animatedImageView(init: KenBurnsView.() -> Unit): KenBurnsView {
        return ankoView({ KenBurnsView(it) }, 0, init)
    }

    fun ViewManager.searchBox(init: SearchBox.() -> Unit): SearchBox {
        return ankoView({ SearchBox(it) }, 0, init)
    }

    fun ViewManager.circleImageView(init: CircleImageView.() -> Unit): CircleImageView {
        return ankoView({ CircleImageView(it) }, 0, init)
    }

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        var visibleThreshold = 5
        var totalItemCount: Int
        var lastVisibleItem: Int

        relativeLayout {
            layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            drawerLayout = drawerLayout {
                layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
                pullToRefresh = swipeRefreshLayout {
                    relativeLayout {
                        relativeLayout {
                            id = Ids.toolbar
                            layoutParams = RelativeLayout.LayoutParams(
                                    matchParent,
                                    sp(56)
                            )
                            backgroundColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                context.getColor(R.color.main)
                            } else {
                                Color.parseColor("#ff5757")
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                elevation = 3f
                            }
                            setPadding(15, 0, 15, 0)
                            menuButton = imageView {
                                layoutParams = RelativeLayout.LayoutParams(
                                        sp(24),
                                        sp(24)
                                )
                                imageResource = R.drawable.ic_menu
                                scaleType = ImageView.ScaleType.FIT_XY
                                (layoutParams as RelativeLayout.LayoutParams).centerVertically()
                            }
                            toolbarTitle = textView {
                                layoutParams = RelativeLayout.LayoutParams(
                                        wrapContent,
                                        wrapContent
                                )
                                text = "HOME"
                                textSize = 22f
                                textColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    context.getColor(R.color.white)
                                } else {
                                    Color.WHITE
                                }
                                typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Typeface.create(context.resources.getFont(R.font.montserrat_regular), Typeface.BOLD)
                                } else {
                                    Typeface.createFromAsset(context.assets, "montserrat_regular.ttf")
                                }
                                (layoutParams as RelativeLayout.LayoutParams).centerInParent()
                            }
                            searchButton = imageView {
                                layoutParams = RelativeLayout.LayoutParams(
                                        sp(24),
                                        sp(24)
                                )
                                imageResource = R.drawable.ic_search
                                scaleType = ImageView.ScaleType.FIT_XY
                                (layoutParams as RelativeLayout.LayoutParams).alignParentEnd()
                                (layoutParams as RelativeLayout.LayoutParams).centerVertically()
                            }
                        }

                        foodRecyclerView = recyclerView {
                            layoutParams = RelativeLayout.LayoutParams(
                                    matchParent,
                                    matchParent)
                            (layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ABOVE, Ids.progressBar)
                            (layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.BELOW, Ids.toolbar)
                            var flex = FlexboxLayoutManager(context)
                            flex.flexDirection = FlexDirection.ROW
                            flex.justifyContent = JustifyContent.FLEX_START
                            layoutManager = flex
                            overScrollMode = View.OVER_SCROLL_NEVER
                            adapter = FoodAdapter(context)
                            foodAdapter = adapter as FoodAdapter
                            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    totalItemCount = layoutManager.itemCount
                                    lastVisibleItem = (layoutManager as FlexboxLayoutManager).findLastCompletelyVisibleItemPosition()
                                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount > 0) {
                                        loading = true
                                        onLoadMoreListener.onLoadMore()
                                    }
                                    if (lastVisibleItem < 10) {
                                        fab.hide()
                                    } else {
                                        if (!fab.isShown) {
                                            var scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                            fab.show()
                                            fab.show(object: FloatingActionButton.OnVisibilityChangedListener() {
                                                override fun onShown(fab: FloatingActionButton?) {
                                                    fab?.startAnimation(scaleUp)
                                                }
                                            })
                                        }
                                    }
                                }
                            })
                        }
                        progressBar = progressBar {
                            id = Ids.progressBar
                            layoutParams = RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            )
                            (layoutParams as RelativeLayout.LayoutParams)
                                    .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            (layoutParams as RelativeLayout.LayoutParams)
                                    .addRule(RelativeLayout.CENTER_HORIZONTAL)
                            visibility = View.GONE
                        }
                        noData = textView {
                            layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
                            (layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.CENTER_IN_PARENT)
                            gravity = Gravity.CENTER
                            backgroundColor = Color.WHITE
                            textColor = Color.BLACK
                            typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Typeface.create(context.resources.getFont(R.font.montserrat_regular), Typeface.BOLD)
                            } else {
                                Typeface.createFromAsset(context.assets, "montserrat_regular.ttf")
                            }
                        }

                        fab = floatingActionButton {
                            layoutParams = RelativeLayout.LayoutParams(
                                    wrapContent,
                                    wrapContent
                            )
                            (layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_END)
                            (layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            (layoutParams as RelativeLayout.LayoutParams).setMargins(sp(15), sp(15), sp(15), sp(15))
                            imageResource = R.drawable.ic_keyboard_arrow_up
                            size = FloatingActionButton.SIZE_NORMAL
                            scaleType = ImageView.ScaleType.CENTER
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.main))
                                rippleColor = context.getColor(R.color.white)
                            } else {
                                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff5757"))
                                rippleColor = Color.WHITE
                            }
//                            translationY = 300f
                        }
                    }
                }

                navigationView = verticalLayout {
                    layoutParams = DrawerLayout.LayoutParams(
                            sp(250),
                            DrawerLayout.LayoutParams.MATCH_PARENT
                    )
                    (layoutParams as DrawerLayout.LayoutParams).gravity = Gravity.START
                    backgroundColor = Color.parseColor("#EEEEEE")
                    frameLayout {
                        layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                sp(180)
                        )
                        cover = animatedImageView {
                            layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        avatar = circleImageView {
                            layoutParams = FrameLayout.LayoutParams(
                                    sp(100),
                                    sp(100)
                            )
                            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
                        }
                        userName = textView {
                            layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT
                            )
                            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
                            (layoutParams as FrameLayout.LayoutParams).bottomMargin = sp(5)
                            gravity = Gravity.CENTER
                            text = "ThanThai"
                            textColor = Color.WHITE
                            textSize = 18f
                        }
                    }

                    sideMenu = recyclerView {
                        layoutParams = LinearLayout.LayoutParams(
                                matchParent,
                                0
                        )
                        (layoutParams as LinearLayout.LayoutParams).weight = 1f
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = SideMenuAdapter(context)
                        overScrollMode = View.OVER_SCROLL_NEVER
                    }
                    textView {
                        layoutParams = LinearLayout.LayoutParams(
                                matchParent,
                                wrapContent
                        )
                        setPadding(sp(5), 0, sp(5), sp(10))
                        text = "v1.0.0"
                        textSize = 17f
                        textColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            context.getColor(R.color.gray)
                        } else {
                            Color.parseColor("#aaa")
                        }
                        typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Typeface.create(context.resources.getFont(R.font.montserrat_regular), Typeface.BOLD)
                        } else {
                            Typeface.createFromAsset(context.assets, "montserrat_regular.ttf")
                        }
                    }
                }
            }
        }
    }

    object Ids {
        val toolbar = 1
        val progressBar = 2
    }
}