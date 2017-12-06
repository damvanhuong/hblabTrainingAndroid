package com.esp.weather.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esp.weather.R;
import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.utils.TimeUtils;
import com.esp.weather.utils.WeatherUtils;

import java.text.DecimalFormat;
import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    private Context context;
    private List<HourForecast> list;

    public HourAdapter(Context context, List<HourForecast> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hour_row, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        DecimalFormat format = new DecimalFormat("##");
        HourForecast element = list.get(position);
        String stt = element.getWeather().get(0).getDescription();
        holder.hourView.setText(element.getDateTime().substring(11, 16));
        holder.stateView.setText(stt.substring(0, 1).toUpperCase() + stt.substring(1).toLowerCase());
        holder.tempView.setText(format.format(WeatherUtils.getCelsius(element.getMain().getTemp())) + "°C");
        holder.pressureView.setText(format.format(element.getMain().getPressure()) + "hPa");
        holder.windView.setText(element.getWind().getSpeed() + " m/s");
        holder.humidityView.setText(element.getMain().getHumidity() + "%");
        switch (element.getWeather().get(0).getMain()) {
            case "Rain": {
                holder.background.setBackgroundResource(R.drawable.bg_ice_rain);
                break;
            }
            case "Clouds": {
                holder.background.setBackgroundResource(R.drawable.bg_snow);
                break;
            }
            case "Clear": {
                holder.background.setBackgroundResource(R.drawable.bg_sandy);
                break;
            }
            default: {
                holder.background.setBackgroundResource(R.drawable.bg_foggy);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HourViewHolder extends RecyclerView.ViewHolder {

        TextView hourView;
        TextView stateView;
        TextView tempView;
        TextView windView;
        TextView pressureView;
        TextView humidityView;
        View background;

        public HourViewHolder(View itemView) {
            super(itemView);
            hourView = itemView.findViewById(R.id.hour_tv);
            stateView = itemView.findViewById(R.id.state_tv);
            tempView = itemView.findViewById(R.id.temperature_tv);
            windView = itemView.findViewById(R.id.wind_tv);
            pressureView = itemView.findViewById(R.id.pressure_tv);
            humidityView = itemView.findViewById(R.id.humidity_tv);
            background = itemView.findViewById(R.id.background);
        }
    }
}
