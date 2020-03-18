package kz.kase.terminal.entities

import android.content.Context
import android.util.Log
import kz.kase.terminal.R
import kz.kase.terminal.interfaces.RowItemInterface
import kz.kase.terminal.other.Column
import kz.kase.terminal.other.RowItem
import kz.kase.terminal.other.RowType
import kz.kase.terminal.ui.layout.RowLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PositionItem(val id: Int, val symbol:String): RowItemInterface {
    protected var TAG = this.javaClass.getSimpleName()
    var date = Date()
    var direct: Boolean = Random().nextBoolean()
    var expire = Date()

    private val dfDateOnly = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    val dateOnly : String
        get(){
            if (date != null)
                return dfDateOnly.format(date)
            return "n/a"
        }

    override fun rows(context: Context, array: List<Column>, isShaded: Boolean): List<RowLayout> {
        val list = ArrayList<RowLayout>()
        for (item in array) {
            list.add(column(context, item).isShaded(isShaded))
        }
        return list
    }

    override fun rows(context: Context, array: List<Column>): List<RowLayout> {
        val list = ArrayList<RowLayout>()
        for (item in array) {
            list.add(column(context, item))
        }
        return list
    }

    fun rows(array: List<Column>): List<RowItem> {
        val list = ArrayList<RowItem>()
        for (item in array) {
            list.add(rowItem(item))
        }
        return list
    }

    fun column(context: Context, item: Column): RowLayout {
        return RowLayout(context, rowItem(item))
    }
    fun randomDouble(max: Int): Double{
        val random = Random()
        return ((random.nextInt(max) - max / 2)  * 0.01)
    }
    fun rowItem(item: Column): RowItem {
        val random = Random()
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, random.nextInt(1000))

        when (item) {//symbol, income, current, incoming, remainder, dividends cupon
            Column.symbol -> return RowItem(RowType.FirstTimeRow, symbol).update(RowValue(symbol), RowValue(dateOnly), R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.income -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(10000).toDouble() * 0.01), RowValue(randomDouble(100).string(2) + "%"), R.color.colorGreenMiddle, R.color.colorGreenMiddle)
            Column.current -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(1000000)), R.color.colorGreenMiddle)
            Column.incoming -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(1000000)), R.color.colorOrange)
            Column.remainder -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(1000000) ), R.color.colorGreenMiddle)
            Column.dividends -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(100) ), R.color.colorGreyMiddle)
            Column.cupon -> return RowItem(RowType.SimpleRow, symbol).update(RowValue(random.nextInt(100)), R.color.colorGreyMiddle)

            else -> {
                Log.e(TAG, "unknown type: $item.string()")
                return RowItem(RowType.SimpleRow, symbol).update(RowValue("Error"), R.color.colorGreyLight)
            }
        }
    }
}