package kz.kase.terminal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kz.kase.fixtablelayout.inter.IDataAdapter
import kz.kase.terminal.R
import kz.kase.terminal.entities.DealItem
import kz.kase.terminal.entities.InstrumentItem
import kz.kase.terminal.entities.OrderItem
import kz.kase.terminal.interfaces.RowInterface
import kz.kase.terminal.other.*
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.ui.layout.ColumnLayout
import kz.kase.terminal.ui.layout.RowLayout
import kz.kase.terminal.ui.layout.TableViewLayout
import kz.kase.terminal.viewmodel.InstrumentViewModel
import java.util.*
import kotlin.collections.ArrayList

class DealTableAdapter(val items : ArrayList<ListViewItem>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()
    private val viewModel = InstrumentViewModel.share

    init {
        for (item in items){
            Log.d(TAG, "onBindViewHolder for type " + item.type)
            when (item.type){
                TableType.DEALS_SHARE -> subscribe(arrayOf(item.type), item, Column.dealColumns())
                TableType.DEALS_BONDS -> subscribe(arrayOf(item.type), item, Column.dealColumns())
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
        }
    }

    private fun subscribe(type: Array<TableType>, item: ListViewItem, columns: ArrayList<Column>){
        Log.d(TAG, "subscribe for types " + type)
        val random = Random()
        val count = 3 + random.nextInt(10)
        var stock = StorageProvider.tableDict[TableType.FAVORITE_SHARES]!!
        if (type.contains(TableType.DEALS_BONDS))
            stock = StorageProvider.tableDict[TableType.FAVORITE_BONDS]!!
        val result = ArrayList<RowLineItem>()
        var isShaded = true
        for (i in 0 until count){
            val obj = DealItem(i, stock.get(random.nextInt(stock.count())))
            result.add(RowLineItem(context, obj, columns, isShaded))
            isShaded = !isShaded
        }
        item.data = result


//        dispose.add(viewModel.subjectResponse
//                .observeOn(AndroidSchedulers.mainThread())
//                .filter {
//                    type.contains(it.type)}
//                .map{pack ->
//                    Log.d(TAG, "map message: " + pack.type)
//                    val result = ArrayList<RowLineItem>()
//                    var cols = columns
//                    if (arcTypes.contains(pack.type)){
//                        cols = Column.statisticColumns()
//                    }
//                    var isShaded = true
//
//                    for (obj in pack.instruments!!){
//                        result.add(RowLineItem(context, obj, cols, isShaded))
//                        isShaded = !isShaded
//                    }
//                    return@map result
//                }
//                .subscribeBy(
//                        onNext = {result ->
//                            item.data = result
//                            notifyItemChanged(items.indexOf(item))
//                        },
//                        onError = {
//                            Log.e(TAG, "error:" + it.message)
//                            it.printStackTrace()
//                        },
//                        onComplete = {
//                            Log.d(TAG, "subjectResponse onComplete")
//                        }
//                ))
//        viewModel.subjectUpdateTable.onNext(type[0])
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

//class OrderTableAdapter(val context: Context): IDataAdapter {
//    private val TAG = this.javaClass.simpleName
//    private var dataArray: ArrayList<List<RowInterface>> = ArrayList()
//    val uniqueID: String = UUID.randomUUID().toString()
//    var columns = ArrayList<Column>()
//    val item = OrderItem(1, "")
//    init {
//        columns = Column.orderColumns()
//        dataArray.add(Column.values(context, uniqueID, columns))
//        val random = Random()
//
//        val count = 3 + random.nextInt(10)
//        val stock = StorageProvider.tableDict[TableType.FAVORITE_SHARES]!!
//        for (i in 0 until count) {
//            dataArray.add(OrderItem(i, stock.get(random.nextInt(stock.count()))).rows(columns))
//        }
//    }
//
//    override fun getColumnCount(): Int {
//        if (dataArray.size > 0)
//            return dataArray[0].size
//        return 0
//    }
//
//    override fun getRowCount(): Int {
//        return dataArray.size
//    }
//
//    override fun getItemView(col: Int, viewType: Int): View? {
//        Log.d(TAG, "getItemView++ $col")
//        if (viewType == 0) { //header
//            val column = ColumnLayout(context, columns.get(col), uniqueID, col)
//            if (col > 0)
//                column.hasSettings = false
//            return column
//        } else { //Data
//            return item.column(context, columns[col])
//        }
//    }
//
//    override fun convertData(row: Int, col: Int, view: View) {
//        val rowItem = dataArray[row][col]
//        if (view is RowLayout) {
//            if (rowItem is RowItem) {
//                view.update(rowItem);
//            }
//        }
//    }
//}
