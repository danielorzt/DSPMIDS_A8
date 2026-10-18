package com.danielorzt.climamovil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danielorzt.climamovil.data.Ubicacion
import com.danielorzt.climamovil.databinding.ItemUbicacionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private object UbicacionDiff : DiffUtil.ItemCallback<Ubicacion>() {
    override fun areItemsTheSame(a: Ubicacion, b: Ubicacion) = a.id == b.id
    override fun areContentsTheSame(a: Ubicacion, b: Ubicacion) = a == b
}

class UbicacionAdapter(
    private val onEliminar: (Ubicacion) -> Unit
) : ListAdapter<Ubicacion, UbicacionAdapter.VH>(UbicacionDiff) {

    class VH(val binding: ItemUbicacionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemUbicacionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = getItem(position)
        val fecha = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(u.actualizado))
        val viento = String.format(Locale.getDefault(), "%.1f", u.viento)
        with(holder.binding) {
            tvEmoji.text = emojiPara(u.icono)
            tvCiudad.text = if (u.pais.isEmpty()) u.ciudad else "${u.ciudad}, ${u.pais}"
            tvDescripcion.text = u.descripcion
            tvTemp.text = "${u.temp.roundToInt()}°C"
            tvDetalle.text = "Humedad ${u.humedad} %  ·  Viento $viento m/s"
            tvActualizado.text = "Actualizado $fecha"
            btnEliminar.setOnClickListener { onEliminar(u) }
        }
    }

    /** OpenWeatherMap manda un código de ícono (por ejemplo "10d"); lo traduzco a un emoji. */
    private fun emojiPara(codigo: String): String = when (codigo.take(2)) {
        "01" -> if (codigo.endsWith("n")) "🌙" else "☀️"
        "02" -> "⛅"
        "03", "04" -> "☁️"
        "09" -> "🌧️"
        "10" -> "🌦️"
        "11" -> "⛈️"
        "13" -> "❄️"
        "50" -> "🌫️"
        else -> "🌡️"
    }
}
