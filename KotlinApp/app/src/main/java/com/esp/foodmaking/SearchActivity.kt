package com.esp.foodmaking

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when(menuItem?.itemId) {
            R.id.trending -> {
                type = Type.TRENDING
                filterBtn.text = "Top Trending"
                searchBtn.performClick()
            }
            R.id.rate -> {
                type = Type.RATE
                filterBtn.text = "Top Rate"
                searchBtn.performClick()
            }
        }
        return true
    }

    private var type : Type = Type.RATE
    private lateinit var adapter : ResultAdapter

    enum class Type {
        TRENDING, RATE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
    }

    private fun initViews() {
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
        filterBtn.setOnClickListener( {v ->
            var popup = PopupMenu(this, v)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.popup)
            popup.show()
        })
        searchView.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBtn.performClick()
            }
            true
        }
        adapter = ResultAdapter(this, android.R.layout.simple_list_item_2)
        searchResult.adapter = adapter
        searchResult.setOnItemClickListener({adapterView: AdapterView<*>?, view: View?, i: Int, l: Long ->
            var food = adapter.list[i]
            startActivity<FoodDetailActivity>("item" to food as Serializable)
        })
    }

    fun search(view : View) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        var str = searchView.text.toString().trim()
        if (str.isEmpty()) {
            alert("", "Invalid keyword")
            val dialog = AlertEmptyDialog(this)
            dialog.setOnCancelClickInterface(object: AlertEmptyDialog.OnCancelClickInterface {
                override fun onClick() {
                    dialog.dismiss()
                }
            })
            dialog.show()
            return
        }
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        var query = HashMap<String, String>()
        query["key"] = "6ba36eb9a7cc90faf08efb5a50f786bf"
        query["q"] = str
        if (type == Type.TRENDING) {
            query["sort"] = "t"
        } else if (type == Type.RATE) {
            query["sort"] = "r"
        }
        RetrofitClient.getFoodAPIService().create(APIService::class.java)
                .search(query)
                .enqueue(object: Callback<FoodListResponse?> {
                    override fun onResponse(call: Call<FoodListResponse?>?, response: Response<FoodListResponse?>?) {
                        loadingDialog.dismiss()
                        if (response == null) {
                            return
                        }
                        val list = response.body()?.recipes
                        if (list == null || list.isEmpty()) {
                            emptyView.visibility = View.VISIBLE
                        } else {
                            adapter.list = response.body()?.recipes?.toMutableList()!!
                            adapter.notifyDataSetChanged()
                            emptyView.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<FoodListResponse?>?, t: Throwable?) {
                        emptyView.visibility = View.VISIBLE
                    }
                })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}
