package es.upm.btb.helloworldkt.apirest

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.upm.btb.helloworldkt.R

class WeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val cityName: TextView = view.findViewById(R.id.citytv)
    val temperature: TextView = view.findViewById(R.id.tvTemperture)
    val tempFL: TextView = view.findViewById(R.id.tvTempertureFeelsLike)
    val tempMax: TextView = view.findViewById(R.id.tvTempertureMax)
    val tempMin: TextView = view.findViewById(R.id.tvTempertureMin)
    val humidity: TextView = view.findViewById(R.id.tvHumadity)
}
