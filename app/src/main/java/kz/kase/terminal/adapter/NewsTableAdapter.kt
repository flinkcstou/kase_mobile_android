package kz.kase.terminal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kz.kase.terminal.R
import kz.kase.terminal.other.*
import kz.kase.terminal.viewmodel.InstrumentViewModel

class NewsTableAdapter(val type: TableType, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items = ArrayList<NewsEntity.NewsItem>()
    private val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()

    private fun loadNewsType(){
        var fileId = R.raw.news
        if (type == TableType.NEWS_MARKET_MAIN){
            fileId = R.raw.news_1
        }else if (type == TableType.NEWS_COMPANY_MAIN){
            fileId = R.raw.news_2
        }
        dispose.add(Observable.create<Array<NewsEntity.NewsItem>> { emitter ->
            val json: String = context.getResources().openRawResource(fileId).bufferedReader().use {it.readText()}
            val gson = Gson()
            val items = gson.fromJson<Array<NewsEntity.NewsItem>>(json, Array<NewsEntity.NewsItem>::class.java)
            emitter.onNext(items)
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext={result ->
                    items.clear()
                    items.addAll(result)
                    notifyDataSetChanged()
                }))
    }
    init {
        loadNewsType()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val view = holder as NewsHolder
        view.setItem(item)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cell_news, parent, false)
        return NewsHolder(view)
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

class NewsHolder (val view: View) : RecyclerView.ViewHolder(view) {
    var link = ""
    init {
        view.setOnClickListener {
            InstrumentViewModel.share.subjectNewsTapEvent.onNext(link)
        }
    }
    fun setItem(item: NewsEntity.NewsItem){
        this.link = item.link
        val symbol = itemView.findViewById<TextView>(R.id.symbol)
        symbol.text = item.title
        val date = itemView.findViewById<TextView>(R.id.date)
        date.text = item.pubDate
    }
}
