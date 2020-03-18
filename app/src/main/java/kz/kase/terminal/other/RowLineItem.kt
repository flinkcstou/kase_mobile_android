package kz.kase.terminal.other

import android.content.Context
import kz.kase.terminal.R
import kz.kase.terminal.entities.RowValue
import kz.kase.terminal.interfaces.RowInterface
import kz.kase.terminal.interfaces.RowItemInterface
import kz.kase.terminal.ui.layout.ColumnLayout
import kz.kase.terminal.ui.layout.RowLayout
import java.util.*
import kotlin.collections.ArrayList

class RowLineItem {
    var rowArrayList = ArrayList<RowInterface>()
    var columns = ArrayList<Column>()
    constructor(column: ArrayList<ColumnLayout>){
        for (item in column){
            rowArrayList.add(item)
        }
    }
    constructor(context: Context, count : Int, isShaded: Boolean){
        val random = Random()
        for (i in 0 until count){
            if (i == 0){
                rowArrayList.add(RowLayout(context, RowItem(RowType.FirstTimeRow, i.toString()).update(RowValue("KZTK"), RowValue("23.01.19 23:22"), R.color.colorGreyDark, R.color.colorGreyDark)).isShaded(isShaded))
            }else {
                rowArrayList.add(RowLayout(context, RowItem(RowType.SimpleRow, i.toString()).update(RowValue(random.nextDouble() * 1000 + random.nextDouble()), RowValue(random.nextDouble() * 1000 + random.nextDouble()), R.color.colorGreyLight, R.color.colorGreyLight)).isShaded(isShaded))
            }
        }
    }
    constructor(context: Context, item: RowItemInterface, columns: ArrayList<Column>, isShaded: Boolean){
        this.columns = columns
        rowArrayList.addAll(item.rows(context, columns, isShaded))
    }
}