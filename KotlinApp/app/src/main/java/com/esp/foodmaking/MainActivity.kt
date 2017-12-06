package com.esp.foodmaking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val view = MainActivityUI()
    val list: MutableList<Food> = ArrayList()
    var page = 1
    var refresh = false
    var canLoadmore = true
    var isLoadmore = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view.setContentView(this)
        initViews()
        User.instance = Auth.getUser(this)
        if (User.instance == null) {
            login()
        }
    }

    override fun onResume() {
        super.onResume()
        if (User.instance != null) {
            Glide.with(this).load(User.instance?.avatarUrl).into(view.avatar)
            view.userName.text = User.instance?.name
            Glide.with(this).load(User.instance?.coverUrl).into(view.cover)
            (view.sideMenu.adapter as SideMenuAdapter).login()
        } else{
            (view.sideMenu.adapter as SideMenuAdapter).logout()
        }
    }

    private fun initViews() {
        view.noData.visibility = View.GONE
        val query : MutableMap<String, String> = HashMap()
        query["key"] = "6ba36eb9a7cc90faf08efb5a50f786bf"
        query["page"] = page.toString()
        getData(query)
        view.onLoadMoreListener = object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (page < 4 && canLoadmore) {
                    page += 1
                    query["page"] = page.toString()
                    view.progressBar.visibility = View.VISIBLE
                    isLoadmore = true
                    getData(query)
                }
            }
        }
        view.pullToRefresh.setOnRefreshListener {
            page = 1
            view.toolbarTitle.text = "HOME"
            query["page"] = page.toString()
            isLoadmore = false
            getData(query)
        }
        view.searchButton.setOnClickListener {
            startActivity<SearchActivity>()
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        view.fab.setOnClickListener {
            view.foodRecyclerView.smoothScrollToPosition(0)
        }
        view.drawerLayout.closeDrawer(Gravity.START)
        view.menuButton.setOnClickListener {
            view.drawerLayout.openDrawer(Gravity.START)
        }
        (view.sideMenu.adapter as SideMenuAdapter).onItemClickListener = object : SideMenuAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        login()
                    }
                    1 -> {
                        if (User.instance == null) {
                            toast("You need login to use this function")
                        } else {
                            getListLike()
                            view.toolbarTitle.text = getString(R.string.liked)
                            canLoadmore = false
                            isLoadmore = false
                        }
                    }
                    2 -> {
                        canLoadmore = true
                        isLoadmore = false
                        refresh = true
                        val query = HashMap<String, String>()
                        page = 1
                        query["key"] ="6ba36eb9a7cc90faf08efb5a50f786bf"
                        query["page"] = page.toString()
                        query["sort"] = "t"
                        getData(query)
                        view.drawerLayout.closeDrawer(Gravity.START)
                        view.toolbarTitle.text = getString(R.string.top_trending)
                    }
                    3 -> {
                        canLoadmore = true
                        isLoadmore = false
                        refresh = true
                        val query = HashMap<String, String>()
                        page = 1
                        query["key"] ="6ba36eb9a7cc90faf08efb5a50f786bf"
                        query["page"] = page.toString()
                        query["sort"] = "r"
                        getData(query)
                        view.drawerLayout.closeDrawer(Gravity.START)
                        view.toolbarTitle.text = getString(R.string.top_rate)
                    }
                    4 -> {
                        //setting
                    }
                    5 -> {
                        //share
                    }
                    6 -> {
                        view.drawerLayout.closeDrawer(Gravity.START)
                        val dialog = FeedbackDialog(ctx)
                        dialog.setOnClickListener(object: FeedbackDialog.DialogInterface {
                            override fun onCancel(dialog: FeedbackDialog) {
                                dialog.dismiss()
                            }

                            override fun onSend(message: String, dialog: FeedbackDialog) {
                                dialog.dismiss()
                                email("thaibk210@gmail.com", "Feedback for app", message)
                            }
                        })
                        dialog.show()


                    }
                    7 -> {
                        logout()
                    }
                }
                view.drawerLayout.closeDrawer(Gravity.START)
            }
        }
    }

    private fun getListLike() {
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val query = HashMap<String, String>()
        query["id"] = User.instance?.id!!
        RetrofitClient.getAppAPIService()
                .getListLike(query)
                .enqueue(object : Callback<FoodListResponse> {
                    override fun onResponse(call: Call<FoodListResponse>?, response: Response<FoodListResponse>?) {
                        if(response == null) {
                            return
                        }
                        if (response.body() == null) {
                            return
                        }
                        if (response.body()?.recipes == null || response.body()?.recipes?.size == 0) {
                            view.noData.text = "You haven't liked any recipe yet!"
                            view.noData.visibility = View.VISIBLE
                        }
                        var newList = response?.body()?.recipes
                        var l : MutableList<String> = ArrayList()
                        newList!!.mapTo(l) { it.recipeId }
                        RetrofitClient.getAppAPIService()
                                .getLike(GetLikeCountRequest(l))
                                .enqueue(object : Callback<GetLikeCountResponse>{
                                    override fun onFailure(call: Call<GetLikeCountResponse>?, t: Throwable?) {
                                    }

                                    override fun onResponse(call: Call<GetLikeCountResponse>?, response: Response<GetLikeCountResponse>?) {
                                        for(i in 0 until newList.size) {
                                            newList[i].likeCount = response?.body()?.count?.get(i)!!
                                        }
                                        list.clear()
                                        list.addAll(newList.toMutableList())
                                        (view.foodRecyclerView.adapter as FoodAdapter).list = list
                                        view.foodRecyclerView.adapter?.notifyDataSetChanged()
                                        view.progressBar.visibility = View.GONE
                                        view.noData.visibility = View.GONE
                                        loadingDialog.dismiss()
                                    }
                                })
                    }

                    override fun onFailure(call: Call<FoodListResponse>?, t: Throwable?) {
                        loadingDialog.dismiss()
                    }
                })
    }

    fun getData(query: MutableMap<String, String> = HashMap()) {
        val loadingDialog = LoadingDialog(this)
        view.pullToRefresh.isRefreshing = false
        loadingDialog.show()
        val callback = object : Callback<FoodListResponse> {
            override fun onResponse(call: Call<FoodListResponse>?, response: Response<FoodListResponse>?) {
                if (view.pullToRefresh.isRefreshing) {
                    view.pullToRefresh.isRefreshing = false
                    list.clear()
                }
                if (refresh) {
                    list.clear()
                    refresh = false
                }
                var newList = response?.body()?.recipes
                var l : MutableList<String> = ArrayList()
                newList!!.mapTo(l) { it.recipeId }
                RetrofitClient.getAppAPIService()
                        .getLike(GetLikeCountRequest(l))
                        .enqueue(object : Callback<GetLikeCountResponse>{
                            override fun onFailure(call: Call<GetLikeCountResponse>?, t: Throwable?) {
                            }

                            override fun onResponse(call: Call<GetLikeCountResponse>?, response: Response<GetLikeCountResponse>?) {
                                if (response == null) {

                                }
                                for(i in 0 until newList.size) {
                                    newList[i].likeCount = response?.body()?.count?.get(i)!!
                                }
                                if (!isLoadmore) {
                                    list.clear()
                                }
                                list.addAll(newList.toMutableList())
                                (view.foodRecyclerView.adapter as FoodAdapter).list = list
                                view.foodRecyclerView.adapter?.notifyDataSetChanged()
                                view.progressBar.visibility = View.GONE
                                view.noData.visibility = View.GONE
                                loadingDialog.dismiss()
                                isLoadmore = false
                                view.loading = false
                            }
                        })
            }

            override fun onFailure(call: Call<FoodListResponse>?, t: Throwable?) {
                loadingDialog.dismiss()
                isLoadmore = false
            }
        }
        RetrofitClient.getFoodAPIService().create(APIService::class.java)
                .getFoodList(query).enqueue(callback)
    }

    private fun login() {
        var intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, Const.LOGIN_REQUEST_CODE)
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    private fun logout() {
        Auth.removeUser(ctx)
        User.instance = null
        (view.sideMenu.adapter as SideMenuAdapter).logout()
        view.avatar.imageResource = R.drawable.ic_user
        view.cover.setImageResource(R.color.main)
        view.userName.text = ""
        view.drawerLayout.closeDrawer(Gravity.START)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.LOGIN_REQUEST_CODE) {
            if (User.instance != null) {
                Glide.with(this).load(User.instance?.avatarUrl).into(view.avatar)
                view.userName.text = User.instance?.name
                Glide.with(this).load(User.instance?.coverUrl).into(view.cover)
                (view.sideMenu.adapter as SideMenuAdapter).login()
            } else {
                (view.sideMenu.adapter as SideMenuAdapter).logout()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    @Subscribe
    fun onChangeLikeEvent(event : ChangeLikeEvent) {
        (view.foodRecyclerView.adapter as FoodAdapter).list[event.position].likeCount = event.numOfLikes
        view.foodRecyclerView.adapter.notifyDataSetChanged()
    }
}
