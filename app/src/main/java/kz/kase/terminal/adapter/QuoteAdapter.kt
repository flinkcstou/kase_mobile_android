package kz.kase.terminal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import kz.kase.terminal.R
import kz.kase.terminal.entities.string
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.viewmodel.InstrumentViewModel

class QuoteAdapter (val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<QuoteItem>()
    val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()

    init {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val view = holder as QuoteHolder
        view.setItem(item)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cell_quote, parent, false)
        return QuoteHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        return 0
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }
    fun dispose(){
        dispose.dispose()
    }

}

class QuoteHolder (val view: View) : RecyclerView.ViewHolder(view) {
    var link = ""
    var quoteItem: QuoteItem? = null

    init {
        view.setOnClickListener {
            InstrumentViewModel.share.subjectQuoteTapEvent.onNext(quoteItem!!)
        }
    }
    fun setItem(item: QuoteItem){
        quoteItem = item
        val sell = itemView.findViewById<TextView>(R.id.sell)
        val buy = itemView.findViewById<TextView>(R.id.buy)
        val price = itemView.findViewById<TextView>(R.id.price)
        if (item.qtyAsk != null) {
            buy.visibility = View.GONE
            sell.visibility = View.VISIBLE
            sell.text = item.qty.string()
            price.text = item.price.string(2)
            price.setTextColor(view.context.getColor(R.color.colorGreenDark))
        }else if (item.qtyBid != null) {
            sell.visibility = View.GONE
            buy.visibility = View.VISIBLE
            buy.text = item.qty.string()
            price.text = item.price.string(2)
            price.setTextColor(view.context.getColor(R.color.colorRed))
        }
    }
}