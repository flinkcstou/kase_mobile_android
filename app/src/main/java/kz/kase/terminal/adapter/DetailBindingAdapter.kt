package kz.kase.terminal.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kz.kase.terminal.R
import kz.kase.terminal.entities.RowValue


@BindingAdapter("instrum")
fun setInstrum(text: TextView, value: RowValue) {
    text.text = value.string
}
@BindingAdapter("instrum")
fun setInstrum(text: TextView, value: String) {
    text.text = value
}
@BindingAdapter("instrumPrc")
fun setInstrumPrc(text: TextView, value: RowValue) {
    text.text = "${value.string}%"
}
@BindingAdapter("instrumColor")
fun setInstrumColor(text: TextView, value: Int) {
    text.setTextColor(ContextCompat.getColor(text.context, value))
}
@BindingAdapter("visibility")
fun setInstrumVisible(text: ImageView, value: Boolean) {
    if (value)
        text.visibility = View.VISIBLE
    else
        text.visibility = View.GONE
}
