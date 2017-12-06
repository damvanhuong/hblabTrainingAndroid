package com.esp.weather.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esp.weather.R;
import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.data.network.responses.CurrentWeatherResponse;
import com.esp.weather.utils.ImageUtils;
import com.esp.weather.utils.LanguageUtils;
import com.esp.weather.utils.TimeUtils;
import com.esp.weather.utils.WeatherUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.wanderingcan.persistentsearch.PersistentSearchView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    private static final String TAG = "Location";

    private MainActivityPresenter presenter;

    //View
    private ImageView background;
    private LineChart dayChart;
    private PersistentSearchView searchView;
    private TextView temperatureView;
    private TextView addressView;
    private TextView secondaryAddressView;
    private TextView statusView;
    private TextView windView;
    private TextView pressureView;
    private TextView humidityView;
    private TextView dateView;
    private TextView timeView;

    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private LineChart windChart;

    private View moreView;
    private boolean isShared = false;

    private LinearLayout dayForecastLayout;

    private ProgressDialog progressDialog;

    private SwipeRefreshLayout pullToRefresh;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initViews();
        initWeatherDetail();
        initComponents();
        initDayChart();
        initWindChart();
        requestPermissions();
        initCurrentLocation();
    }

    private void requestPermissions() {
        boolean per = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (per) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_WIFI_STATE}, 1);
            }
        }

    }

    private void initWeatherDetail() {

    }

    private void initComponents() {
        presenter = new MainActivityPresenterImp(this, this);
    }

    private void initCurrentLocation() {
        presenter.initLocation();
    }

    private void initDayChart() {
        XAxis xAxis = dayChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(7);
        xAxis.enableGridDashedLine(16, 12, 0);
        xAxis.setAxisLineWidth(1);
        xAxis.setAxisLineColor(Color.argb(50, 255, 0, 0));
        YAxis left = dayChart.getAxisLeft();
        YAxis right = dayChart.getAxisRight();
        left.setAxisMinimum(0);
        left.setDrawZeroLine(false);
        left.setDrawGridLines(false);
        left.setAxisLineColor(Color.argb(50, 255, 0, 0));
        right.setAxisLineColor(Color.argb(50, 255, 0, 0));
        left.setAxisLineWidth(1);
        right.setAxisLineWidth(1);
        right.setAxisMinimum(0);
        left.setAxisMaximum(40);
        left.setDrawLabels(false);
        right.setDrawLabels(false);
        right.setDrawZeroLine(false);
        right.setDrawGridLines(false);
        dayChart.setPinchZoom(false);
        dayChart.setDoubleTapToZoomEnabled(false);
        dayChart.setTouchEnabled(false);
    }

    private void initWindChart() {
        XAxis xAxis = windChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(7);
        xAxis.enableGridDashedLine(16, 12, 0);
        xAxis.setAxisLineWidth(1);
        xAxis.setAxisLineColor(Color.parseColor("#74d3c8"));
        YAxis left = windChart.getAxisLeft();
        YAxis right = windChart.getAxisRight();
        right.setEnabled(true);
        right.setDrawLabels(false);
        left.setAxisMinimum(0);
        left.setDrawZeroLine(false);
        left.setDrawGridLines(false);
        left.setDrawLabels(false);
        right.setAxisMinimum(0);
        right.setDrawGridLines(false);
        left.setAxisLineColor(Color.parseColor("#74d3c8"));
        right.setAxisLineColor(Color.parseColor("#74d3c8"));
        left.setAxisLineWidth(1);
        right.setAxisLineWidth(1);
        windChart.setPinchZoom(false);
        windChart.setDoubleTapToZoomEnabled(false);
        windChart.setTouchEnabled(false);
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        progressDialog = new com.esp.weather.ui.ProgressDialog(this);
        temperatureView = findViewById(R.id.temperature);
        background = findViewById(R.id.background);
        addressView = findViewById(R.id.address);
        secondaryAddressView = findViewById(R.id.secondary_address);
        statusView = findViewById(R.id.status);
        windView = findViewById(R.id.wind);
        pressureView = findViewById(R.id.pressure);
        humidityView = findViewById(R.id.humidity);
        dayChart = findViewById(R.id.chart);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        windChart = findViewById(R.id.wind_chart);
        Description description = new Description();
        description.setEnabled(false);
        windChart.setDescription(description);
        dayChart.setDescription(description);
        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);
        moreView = findViewById(R.id.more);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED && isShared) {
                    Bitmap image = ImageUtils.takeScreenShot(getWindow().getDecorView().getRootView());
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    ShareDialog shareDialog = new ShareDialog(MainActivity.this);
                    CallbackManager callbackManager = CallbackManager.Factory.create();
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(FacebookException error) {
                        }
                    });
                    isShared = false;
                    shareDialog.show(content);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                moreView.setAlpha(1-slideOffset);
            }
        });
        pullToRefresh = findViewById(R.id.pull_to_refresh);
        pullToRefresh.setProgressViewOffset(true, 0, 350);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.initLocation();
                pullToRefresh.setRefreshing(false);
            }
        });
        dayForecastLayout = findViewById(R.id.day_forecast);
        searchView = findViewById(R.id.search_view);
        searchView.setOnIconClickListener(new PersistentSearchView.OnIconClickListener() {
            @Override
            public void OnNavigationIconClick() {
                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                }
            }

            @Override
            public void OnEndIconClick() {

            }
        });
        searchView.setOnSearchListener(new PersistentSearchView.OnSearchListener() {
            @Override
            public void onSearchOpened() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawable = getDrawable(R.drawable.ic_arrow_back);
                    searchView.setNavigationDrawable(drawable);
                }
            }

            @Override
            public void onSearchClosed() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawable = getDrawable(R.drawable.ic_search);
                    searchView.setNavigationDrawable(drawable);
                }
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(CharSequence term) {

            }

            @Override
            public void onSearch(CharSequence query) {
                if (query == null || query.equals("")) {
                    return;
                }
                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    Address address = geocoder.getFromLocationName(query.toString(), 1).get(0);
                    presenter.search(address.getLatitude(), address.getLongitude());
                    searchView.clearFocus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSuccess(CurrentWeatherResponse body) {
        DecimalFormat format = new DecimalFormat("##");
        temperatureView.setText(format.format(WeatherUtils.getCelsius(body.getMain().getTemp())) + "°C");
        addressView.setText(LanguageUtils.getAddress(this, body.getName(), body.getSys().getCountry()));
        secondaryAddressView.setText(LanguageUtils.getAddress(this, body.getName(), body.getSys().getCountry()));
        windView.setText(String.valueOf(body.getWind().getSpeed().intValue()) + " m/s");
        pressureView.setText(body.getMain().getPressure().toString() + "hPa");
        humidityView.setText(body.getMain().getHumidity().toString() + "%");
        String stt = body.getWeather().get(0).getDescription();
        statusView.setText(stt.substring(0, 1).toUpperCase() + stt.substring(1).toLowerCase());
        dateView.setText(TimeUtils.getDateFromTimestamp(body.getDt()));
        timeView.setText(TimeUtils.getCurrentTime());
        switch (body.getWeather().get(0).getMain()) {
            case "Rain": {
                background.setImageResource(R.drawable.rain);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    moreView.setBackgroundColor(getColor(R.color.rain));
                }
                break;
            }
            case "Clouds": {
                background.setImageResource(R.drawable.cloudy);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    moreView.setBackgroundColor(getColor(R.color.cloudy));
                }
                break;
            }
            case "Clear": {
                background.setImageResource(R.drawable.sunny);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    moreView.setBackgroundColor(getColor(R.color.sunny));
                }
                break;
            }
            default: {
                background.setImageResource(R.drawable.bg_foggy);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    moreView.setBackgroundColor(getColor(R.color.rain));
                }
            }
        }
    }

    @Override
    public void onFailed() {

    }

    @Override
    public void updateChart(LineData tempLineData, LineData windLineData, final float offset) {
        dayChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int time = (int) (3 * value + offset);
                return String.format("%02d:00", (time < 24) ? time : (time - 24));
            }
        });
        windChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int time = (int) (3 * value + offset);
                return String.format("%02d:00", (time < 24) ? time : (time - 24));
            }
        });
        windChart.setData(windLineData);
        windChart.invalidate();
        dayChart.setData(tempLineData);
        dayChart.invalidate();
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void cancelProgressDialog() {
        progressDialog.cancel();
    }

    public void onViewClick(View view) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void updateFiveDay(final List<HourForecast> list) {
        dayForecastLayout.removeAllViews();
        ArrayList<Integer> days = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            final HourForecast element = list.get(i);
            int[] date = TimeUtils.getDate(element.getDateTime());
            if (!days.contains(date[2]) && count < 5) {
                days.add(date[2]);
                count += 1;
                LinearLayout dayView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.day, null, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                dayView.setLayoutParams(param);
                TextView dayTextView = dayView.findViewById(R.id.day_tv);
                ImageView dayWeather = dayView.findViewById(R.id.weather);
                dayTextView.setText(date[2] + "/" + date[1]);
                String icon = element.getWeather().get(0).getIcon();
                String url = "http://openweathermap.org/img/w/" + icon + ".png";
                try {
                    InputStream is = getAssets().open(icon + ".png");
                    Drawable drawable = Drawable.createFromStream(is, null);
                    dayWeather.setImageDrawable(drawable);
                } catch (IOException e) {
                    e.printStackTrace();
                    Glide.with(this).load(url).into(dayWeather);
                }
                dayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.prepareDay(list, TimeUtils.getDate(element.getDateTime()));
                    }
                });
                dayForecastLayout.addView(dayView);
            }
        }
    }

    public void shareFacebook(View view) {
        isShared = true;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_again), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void changeBackground(int resId) {
        background.setImageResource(resId);
    }

    @Override
    public void showDaySheet(List<HourForecast> list) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.day_dialog, null, false);
        dialog.setContentView(view);
        TextView date = view.findViewById(R.id.date);
        date.setText(TimeUtils.getDateFromTimestamp(list.get(0).getDt()));
        RecyclerView recyclerView = view.findViewById(R.id.day_state);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        HourAdapter adapter = new HourAdapter(this, list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
            } else {
//                presenter.initLocation();
            }
        }
    }
}
