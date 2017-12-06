package com.esp.foodmaking

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx
import com.facebook.share.model.ShareLinkContent
import kotlinx.android.synthetic.main.activity_food_detail.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodDetailActivity : AppCompatActivity() {

    lateinit var pullToZoom: PullToZoomScrollViewEx
    lateinit var contentView: LinearLayout
    lateinit var imageView: ImageView
    lateinit var listView: ListView
    lateinit var publisher : TextView
    lateinit var foodName : TextView
    lateinit var rateScore : TextView
    lateinit var likeScore : TextView
    lateinit var btnLike : ImageView
    var isLiked : Boolean = false
    lateinit var food : Food
    var likeCount = 0
    var isGetLikeFinish = false
    var isGetIngredientsFinish = false
    var position : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)
        food = intent.getSerializableExtra("item") as Food
        position = intent.getIntExtra("position", -1)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        fillData()
    }

    override fun onBackPressed() {
        onBack()
    }

    private fun initViews() {
        toolbar.setNavigationOnClickListener {
            onBack()
        }
        pullToZoom = findViewById(R.id.pullToZoom)
        contentView = LayoutInflater.from(this)
                .inflate(R.layout.food_detail_content, window.decorView.findViewById(android.R.id.content), false) as LinearLayout
        var params = contentView.layoutParams
        params.height = resources.displayMetrics.heightPixels - sp(56) - Utils.getStatusHeight(this) - sp(50)
        contentView.layoutParams = params
        pullToZoom.headerView = LayoutInflater.from(this).inflate(R.layout.header, null, false)
        pullToZoom.headerView.layoutParams = FrameLayout.LayoutParams(Utils.getWidth(this), sp(200))
        pullToZoom.setScrollContentView(contentView)
        pullToZoom.zoomView = LayoutInflater.from(this).inflate(R.layout.image, null, false)
        imageView = pullToZoom.zoomView.findViewById(R.id.imageFood)
        listView = pullToZoom.pullRootView.findViewById(R.id.ingredients)
        publisher = pullToZoom.pullRootView.findViewById(R.id.publisher)
        foodName = pullToZoom.pullRootView.findViewById(R.id.food_name)
        pullToZoom.pullRootView.isFillViewport = true
        rateScore = pullToZoom.pullRootView.findViewById(R.id.rate)
        likeScore = pullToZoom.pullRootView.findViewById(R.id.like)
        btnLike = pullToZoom.pullRootView.findViewById(R.id.btnLike)
        btnLike.setOnClickListener {
            val user = User.instance
            if (!isLiked) {
                if (user != null) {
                    isLiked = true
                    btnLike.imageResource = R.drawable.ic_like
                    likeCount += 1
                    likeScore.text = likeCount.toString()
                    val loadingDialog = LoadingDialog(this)
                    loadingDialog.show()
                    RetrofitClient.getAppAPIService()
                            .like(AddLikeRequest(user.id, food))
                            .enqueue(object : Callback<LikeOrUnlikeResponse> {
                                override fun onFailure(call: Call<LikeOrUnlikeResponse>?, t: Throwable?) {
                                    toast("Error occurred! Try later")
                                    likeCount -= 1
                                    likeScore.text = likeCount.toString()
                                    btnLike.imageResource = R.drawable.ic_no_like
                                    loadingDialog.dismiss()
                                }

                                override fun onResponse(call: Call<LikeOrUnlikeResponse>?, response: Response<LikeOrUnlikeResponse>?) {
                                    loadingDialog.dismiss()
                                }
                            })
                }
            } else {
                if (user != null) {
                    isLiked = false
                    btnLike.imageResource = R.drawable.ic_no_like
                    likeCount -= 1
                    likeScore.text = likeCount.toString()
                    val body = HashMap<String, String>()
                    body["id"] = user.id
                    body["recipe_id"] = food.recipeId
                    RetrofitClient.getAppAPIService()
                            .unlike(body)
                            .enqueue(object : Callback<LikeOrUnlikeResponse>{
                                override fun onResponse(call: Call<LikeOrUnlikeResponse>?, response: Response<LikeOrUnlikeResponse>?) {
                                    val loadingDialog = LoadingDialog(ctx)
                                    loadingDialog.dismiss()
                                }

                                override fun onFailure(call: Call<LikeOrUnlikeResponse>?, t: Throwable?) {
                                    toast("Error occurred! Try later")
                                    btnLike.imageResource = R.drawable.ic_like
                                    likeCount += 1
                                    likeScore.text = likeCount.toString()
                                    val loadingDialog = LoadingDialog(ctx)
                                    loadingDialog.dismiss()
                                }

                            })
                }


            }
        }
    }

    private fun fillData() {
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        Glide.with(this).load(food.imageUrl).listener(object: RequestListener<Drawable?> {
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                imageView.setImageDrawable(resource)
                imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                imageView.requestLayout()
                return true
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                return false
            }
        }).into(imageView)
        foodName.text = food.title
        pullToZoom.requestLayout()
        publisher.text = food.publisher
        rateScore.text = food.socialRank.toInt().toString()
        var likeReq = HashMap<String, String>()
        likeReq["recipe_id"] = food.recipeId
        if (User.instance != null) {
            likeReq["id"] = User.instance?.id!!
        }
        RetrofitClient.getAppAPIService()
                .getLikeCount(likeReq)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>?, response: Response<LikeResponse>?) {
                        if (response == null) {
                            return
                        }
                        if (response.body() == null) {
                            return
                        }
                        likeCount = response.body()?.count!!
                        likeScore.text = response.body()?.count.toString()
                        if(response.body()?.liked!!) {
                            btnLike.imageResource = R.drawable.ic_like
                            isLiked = true
                        } else {
                            btnLike.imageResource = R.drawable.ic_no_like
                            isLiked = false
                        }
                        isGetLikeFinish = true
                        if (isGetIngredientsFinish) {
                            isGetLikeFinish = false
                            isGetIngredientsFinish = false
                            loadingDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<LikeResponse>?, t: Throwable?) {
                        isGetLikeFinish = true
                        if (isGetIngredientsFinish) {
                            isGetLikeFinish = false
                            isGetIngredientsFinish = false
                            loadingDialog.dismiss()
                        }
                    }

                })
        var adapter = ArrayAdapter<String>(this, R.layout.ingredients_row, R.id.ingredient_text)
        listView.adapter = adapter
        var query : MutableMap<String, String> = HashMap<String, String>()
        query["key"] = "6ba36eb9a7cc90faf08efb5a50f786bf"
        query["rId"] = food.recipeId
        RetrofitClient.getFoodAPIService().create(APIService::class.java).getRecipe(query)
                .enqueue(object : Callback<RecipeResponse> {
                    override fun onResponse(call: Call<RecipeResponse>?, response: Response<RecipeResponse>?) {
                        var list = ArrayList<String>()
                        for (ingredient in response?.body()?.recipe?.ingredients!!) {
                            list.add(Html.fromHtml(ingredient).toString())
                        }
                        adapter.addAll(response.body()?.recipe?.ingredients)
                        isGetIngredientsFinish = true
                        if (isGetIngredientsFinish) {
                            isGetLikeFinish = false
                            isGetIngredientsFinish = false
                            loadingDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<RecipeResponse>?, t: Throwable?) {
                        isGetIngredientsFinish = true
                        if (isGetIngredientsFinish) {
                            isGetLikeFinish = false
                            isGetIngredientsFinish = false
                            loadingDialog.dismiss()
                        }
                    }
                })
    }


    fun viewPage(view : View) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("publisher", false)
        intent.putExtra("food", food)
        startActivity(intent)
    }

    fun viewPublisher(view: View) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("publisher", true)
        intent.putExtra("food", food)
        startActivity(intent)
    }

    fun shareFacebook(view: View) {
        var content : ShareLinkContent = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(food.sourceUrl))
                .build()
        shareButton.shareContent = content
        shareButton.performClick()
    }

    fun onBack() {
        if (position >= 0) {
            EventBus.getDefault().postSticky(ChangeLikeEvent(position, likeCount))
        }
        finish()
        overridePendingTransition(R.anim.no_translation, R.anim.exit_to_bottom)
    }
}

