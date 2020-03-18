package kz.kase.terminal.other

import android.content.Context
import kz.kase.terminal.ui.layout.ColumnLayout

enum class Column{

    //--Instrument--//
    symbol,
    last,
    bid,
    ask,
    date,
    volume,
    trend,
    dealsCount,
    open,
    avg,
    max,
    min,
    tradeStatus,
    volumeKZT,
    //--Order,Deal--//
    direct,
    price,
    income,
    qty,
    status,
    serial,
    remainder,
    expire,
    current,
    incoming,
    cupon,
    dividends,
    yieldF,
    code,
    desc,
    orderCount,
    dtmDate,
    serial_order;


    fun description(): String {
        if (this == symbol) {
            return "Symbol"
        } else if (this == trend) {
            return "Тренд"
        } else if (this == dealsCount) {
            return "Сделок"
        } else if (this == volumeKZT) {
            return "Объем KZT"
        } else if (this == last) {
            return "Last"
        } else if (this == bid) {
            return "Bid"
        } else if (this == ask) {
            return "Ask"
        } else if (this == date) {
            return "Пос.Сд."
        } else if (this == volume) {
            return "Объём"
        } else if (this == open) {
            return "Откр."
        } else if (this == avg) {
            return "AVG"
        } else if (this == max) {
            return "Max"
        } else if (this == min) {
            return "Мин"
        } else if (this == tradeStatus) {
            return "Сост.Торг."
        } else if (this == direct) {
            return "Направление"
        } else if (this == price) {
            return "Цена"
        } else if (this == income) {
            return "Доход"
        } else if (this == qty) {
            return "Кол-во"
        } else if (this == status) {
            return "Стутус"
        } else if (this == serial) {
            return "Номер"
        } else if (this == remainder) {
            return "Остаток"
        } else if (this == expire) {
            return "Истекает"
        } else if (this == current) {
            return "Текущая"
        } else if (this == incoming) {
            return "Входящая"
        } else if (this == cupon) {
            return "Купон"
        } else if (this == dividends) {
            return "Девиденты"
        } else if (this == yieldF) {
            return "Yield"
        } else if (this == dtmDate) {
            return "DTM"
        } else if (this == orderCount) {
            return "Заявок"
        }
        return "n/a"
    }
    fun pos(): Int {
        return this.ordinal
    }
    companion object {
        fun values(context: Context, uniqueID: String, cols: ArrayList<Column>): ArrayList<ColumnLayout> {
            var dateCall: Column? = null
            if (cols.equals(statisticColumns())) {
                dateCall = volumeKZT
            }
            val columns = ArrayList<ColumnLayout>()
            for (index in 0 until cols.size){
                val item = cols[index]
                val col = ColumnLayout(context, item, uniqueID, index)
                col.hasSettings = item == symbol
                col.isDate = dateCall == item
                columns.add(col)
            }
            return columns
        }
        fun tradeColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, last, bid, ask, date, volume, trend, dealsCount, open, avg, max, min, tradeStatus, volumeKZT)
        }
        fun historyColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, trend, last, dealsCount, volumeKZT, date)
        }

        fun portfolioKZTCols(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, current, incoming, remainder)
        }

        fun portfolioStockCols(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, income, current, incoming, remainder, dividends)
        }

        fun portfolioBondsCols(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, income, current, incoming, remainder, cupon)
        }

        fun orderColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, direct, price, qty, serial, volume, remainder, expire)
        }
        fun dealColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, direct, price, qty, serial, serial_order, volume, expire)
        }
        fun statisticColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, last, trend, volumeKZT)
        }

        fun gsColumns(): ArrayList<Column> {
            return arrayListOf<Column>(symbol, yieldF, dtmDate)
        }
    }
}