package kz.kase.terminal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import kz.kase.terminal.R
import kz.kase.terminal.ui.layout.TableViewLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.terminal.other.*
import kz.kase.terminal.viewmodel.InstrumentViewModel
import kotlin.collections.ArrayList


class InstrumentTableAdapter(val items : ArrayList<ListViewItem>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()
    private val viewModel = InstrumentViewModel.share
    private val arcTypes = arrayOf(TableType.FAVORITE_SHARES_ARC, TableType.FAVORITE_BONDS_ARC, TableType.SHARES_PAGE_ARC, TableType.BONDS_PAGE_ARC)


    init {
        for (item in items){
            Log.d(TAG, "onBindViewHolder for type " + item.type)
            when (item.type){
                TableType.FAVORITE_SHARES -> subscribe(arrayOf(TableType.FAVORITE_SHARES, TableType.FAVORITE_SHARES_ARC), item, Column.tradeColumns())
                TableType.FAVORITE_BONDS -> subscribe(arrayOf(TableType.FAVORITE_BONDS, TableType.FAVORITE_BONDS_ARC), item, Column.tradeColumns())
                TableType.SHARES_PAGE -> subscribe(arrayOf(TableType.SHARES_PAGE, TableType.SHARES_PAGE_ARC), item, Column.statisticColumns())
                TableType.BONDS_PAGE -> subscribe(arrayOf(TableType.BONDS_PAGE, TableType.BONDS_PAGE_ARC), item, Column.statisticColumns())
                TableType.NEWS_MARKET_MAIN, TableType.NEWS_COMPANY_MAIN -> {}
                TableType.UNSET -> {}
                else -> subscribe(arrayOf(item.type), item, Column.statisticColumns())
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items.get(position)
        when (items.get(position).cellId) {
            R.layout.cell_table_header -> {
                holder.itemView.findViewById<TextView>(R.id.text).text = item.name
            }
            R.layout.cell_stock -> {
                if (item.data != null) {
                    val table = holder.itemView as TableViewLayout
                    val data = item.data as ArrayList<RowLineItem>
                    table.setTableData(data, data[0].columns)
                }
            }
            R.layout.cell_news_table -> {
                val table = holder.itemView as RecyclerView
                table.layoutManager = LinearLayoutManager(context)
                table.adapter = NewsTableAdapter(items.get(position).type, context)
            }
        }
    }

    private fun subscribe(type: Array<TableType>, item: ListViewItem, columns: ArrayList<Column>){
        Log.d(TAG, "subscribe for types " + type)
        dispose.add(viewModel.subjectResponse
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    type.contains(it.type)}
                .map{pack ->
                    Log.d(TAG, "map message: " + pack.type)
                    val result = ArrayList<RowLineItem>()
                    var cols = columns
                    if (arcTypes.contains(pack.type)){
                        cols = Column.statisticColumns()
                    }
                    var isShaded = true

                    for (obj in pack.instruments!!){
                        result.add(RowLineItem(context, obj, cols, isShaded))
                        isShaded = !isShaded
                    }
                    return@map result
                }
                .subscribeBy(
                        onNext = {result ->
                            item.data = result
                            notifyItemChanged(items.indexOf(item))
                        },
                        onError = {
                            Log.e(TAG, "error:" + it.message)
                            it.printStackTrace()
                        },
                        onComplete = {
                            Log.d(TAG, "subjectResponse onComplete")
                        }
                ))
        viewModel.subjectUpdateTable.onNext(type[0])
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = items.get(viewType)
        val view = LayoutInflater.from(context).inflate(item.cellId, parent, false)
        return ViewHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        //val item = items.get(position)
        return position
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }
    fun dispose(){
        dispose.dispose()
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
}
