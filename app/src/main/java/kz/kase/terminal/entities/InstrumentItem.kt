package kz.kase.terminal.entities

import android.content.Context
import android.util.Log
import kz.kase.iris.model.IrisApiSecs
import kz.kase.terminal.R
import kz.kase.terminal.interfaces.RowItemInterface
import kz.kase.terminal.other.*
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.ui.layout.RowLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InstrumentItem(var id: Int, var symbol: String) : RowItemInterface {


    protected var TAG = this.javaClass.getSimpleName()

    private val df = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
    private val dfDateOnly = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    var description: String = "n/a"

    var code: String = "n/a"

    var date: Date? = null

    var tradeStatus: String = "n/a"

    var dealsCount: RowValue = RowValue(0)
    var ordersCount: RowValue = RowValue(0)

    var volumeKZT: RowValue = RowValue(0.0)

    var last: RowValue = RowValue(0.0)
    var volume: RowValue = RowValue(0.0)

    var bid: RowValue = RowValue(0.0)
    var bidQty: RowValue = RowValue(0)

    var ask: RowValue = RowValue(0.0)
    var askQty: RowValue = RowValue(0)

    var trend: RowValue = RowValue(0.0)
    var trendPrc: RowValue = RowValue(0.0)

    var open: RowValue = RowValue(0.0)
    var openVolume: RowValue = RowValue(0.0)

    var avg: RowValue = RowValue(0.0)

    var max: RowValue = RowValue(0.0)
    var maxVolume: RowValue = RowValue(0.0)

    var min: RowValue = RowValue(0.0)
    var minVolume: RowValue = RowValue(0.0)

    var type: IrisApiSecs.SecType = IrisApiSecs.SecType.UNRECOGNIZED

    val typeString : String
        get(){
            when(type){
                IrisApiSecs.SecType.SHARES -> return "Акции"
                IrisApiSecs.SecType.BONDS -> return "Облигации"
                IrisApiSecs.SecType.SBS -> return "ГПА"
                IrisApiSecs.SecType.UNIT -> return "Паи"
                IrisApiSecs.SecType.FUTURES -> return "Срочные контракты"
                IrisApiSecs.SecType.ETF -> return "ETF"
                IrisApiSecs.SecType.GDR -> return "Депозитарные расписки"
                else -> return "n/a"
            }
        }
    var isFavorite : Boolean
        get(){
            return StorageProvider.isFavorite(symbol)
        }
    set(value) {
        if (value)
            StorageProvider.setFavorite(symbol)
        else
            StorageProvider.removeFavorite(symbol)
    }
    val dateStr : String
    get(){
        if (date != null)
            return df.format(date)
        return "n/a"
    }
    val trendColor : Int
        get(){
            var color = R.color.colorGreenMiddle
            if (trend.doubleValue < 0)
                color = R.color.colorRed
            return color
        }
    val dateOnly : String
        get(){
            if (date != null)
                return dfDateOnly.format(date)
            return "n/a"
        }
    override fun rows(context: Context, array: List<Column>, isShaded: Boolean): List<RowLayout> {
        val list = ArrayList<RowLayout>()
        for (item in array){
            list.add(column(context, item).isShaded(isShaded))
        }
        return list
    }
    override fun rows(context: Context, array: List<Column>) : List<RowLayout>{
        val list = ArrayList<RowLayout>()
        for (item in array){
            list.add(column(context, item))
        }
        return list
    }
    fun rows(array: List<Column>) : List<RowItem>{
        val list = ArrayList<RowItem>()
        for (item in array){
            list.add(rowItem(item))
        }
        return list
    }
    fun column(context: Context, item: Column): RowLayout {
        return RowLayout(context, rowItem(item))
    }
    fun rowItem(item: Column): RowItem{
        when(item){
            Column.symbol -> return RowItem(RowType.FirstTimeRow, symbol).update(RowValue(symbol), RowValue(dateStr), R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.desc -> return RowItem(RowType.FirstTimeRow, symbol).update(RowValue(symbol), RowValue(description), R.color.colorGreyMiddle, R.color.colorBlue)
            Column.code -> return RowItem(RowType.FirstTimeRow, symbol).update(RowValue(symbol), RowValue(code), R.color.colorGreyMiddle, R.color.colorBlue)
            Column.last ->  return RowItem(RowType.SimpleRow, symbol).update(last, volume, R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.bid ->  return RowItem(RowType.SimpleRow, symbol).update(bid, bidQty, R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.ask ->  return RowItem(RowType.SimpleRow, symbol).update(ask, askQty, R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.date ->  return RowItem(RowType.SimpleRow, symbol).update(RowValue(dateStr), R.color.colorGreyLight)
            Column.volume ->  return RowItem(RowType.SimpleRow, symbol).update(volume, R.color.colorGreyLight)

            Column.trend -> {
                return RowItem(RowType.SimpleRow, symbol).update(trend, trendPrc, trendColor, trendColor)
            }
            Column.dealsCount ->  return RowItem(RowType.SimpleRow, symbol).update(dealsCount, R.color.colorGreyLight)
            Column.open ->  return RowItem(RowType.SimpleRow, symbol).update(open, openVolume, R.color.colorGreyMiddle, R.color.colorGreyLight)
            Column.avg ->  return RowItem(RowType.SimpleRow, symbol).update(avg, R.color.colorGreyLight)
            Column.min ->  return RowItem(RowType.SimpleRow, symbol).update(min, R.color.colorGreyLight)
            Column.max ->  return RowItem(RowType.SimpleRow, symbol).update(max, R.color.colorGreyLight)
            Column.tradeStatus ->  return RowItem(RowType.SimpleRow, symbol).update(RowValue(tradeStatus), R.color.colorGreyLight)
            Column.volumeKZT ->  return RowItem(RowType.SimpleRow, symbol).update(volumeKZT, R.color.colorGreyLight)
            else ->{
                Log.e(TAG, "unknown type: $item.string()")
                return RowItem(RowType.SimpleRow, symbol).update(RowValue("Error"), R.color.colorGreyLight)
            }
        }
    }
}
