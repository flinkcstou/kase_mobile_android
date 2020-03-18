package kz.kase.terminal.adapter

import android.content.Context
import android.util.Log
import android.view.View
import kz.kase.fixtablelayout.inter.IDataAdapter
import kz.kase.terminal.entities.InstrumentItem
import kz.kase.terminal.interfaces.RowInterface
import kz.kase.terminal.other.*
import kz.kase.terminal.ui.layout.ColumnLayout
import kz.kase.terminal.ui.layout.RowLayout

class TableInstrumentAdapter(val context: Context, val uniqueID: String): IDataAdapter {
    private val TAG = this.javaClass.simpleName
    private var dataArray: ArrayList<List<RowInterface>> = ArrayList()
    var columns = ArrayList<Column>()
    val instrument = InstrumentItem(10, "KASE")
    init {
        columns = Column.tradeColumns()

    Utils.duration("TableInstrumentAdapter init", Runnable {
        dataArray.add(Column.values(context, uniqueID, columns))
        for (i in 0 until 9) {
            dataArray.add(InstrumentItem(10, "KASE").rows(columns))
//            dataArray.add(InstrumentItem(10, "KZTO").rows(columns, uniqueID))
//            dataArray.add(InstrumentItem(10, "GB_ENRC").rows(columns, uniqueID))
//            dataArray.add(InstrumentItem(10, "KZMS").rows(columns, uniqueID))
//            dataArray.add(InstrumentItem(10, "CCBN").rows(columns, uniqueID))
//            dataArray.add(InstrumentItem(10, "HSBK").rows(columns, uniqueID))
        }
        Log.d(TAG, "init completed")
    })

    }

    override fun getColumnCount(): Int {
        if (dataArray.size > 0)
            return dataArray[0].size
        return 0
    }

    override fun getRowCount(): Int {
        return dataArray.size
    }

    override fun getItemView(col: Int, viewType: Int): View? {
        Log.d(TAG, "getItemView++ $col")
        if (viewType == 0) { //header
            val column = ColumnLayout(context, columns.get(col), uniqueID, col)
            if (col > 0)
                column.hasSettings = false
            return column
        } else { //Data
            return instrument.column(context, columns[col])
        }
    }

    override fun convertData(row: Int, col: Int, view: View) {
        val rowItem = dataArray[row][col]
        if (view is RowLayout) {
            if (rowItem is RowItem) {
                view.update(rowItem);
            }
        }
    }
}