package kz.kase.terminal.other

import kz.kase.terminal.R
import kz.kase.terminal.entities.RowValue
import kz.kase.terminal.interfaces.RowInterface

class RowItem(rowType: RowType, tag: String): RowInterface, Comparable<RowItem> {
    override val rowHeightPx = 30
    var top: RowValue? = null
    var middle: RowValue? = null
    var bottom: RowValue? = null
    var topColor: Int? = null
    var middleColor: Int? = null
    var bottomColor: Int? = null
    var rowType: RowType = RowType.SimpleRow
    var tag: String = "n/a"
    var middleTextAlign: TextAlignment = TextAlignment.Right

    init {
        this.tag = tag
        this.rowType = rowType
    }

    override val title: String?
    get() = null
    override val cellLayout: Int
    get() {
        if (rowType == RowType.SimpleRow) {
            return R.layout.cell_stock_row_trade
        }else if (rowType == RowType.FirstRow) {
            return R.layout.cell_stock_row_desc
        }else {
            return R.layout.cell_stock_row_time
        }
    }

    override fun compareTo(obj: RowItem) : Int {
        var value1: RowValue? = this.bottom
        var value2: RowValue? = obj.bottom

        if (obj.middle != null && this.middle != null) {
            value1 = obj.middle
            value2 = this.middle
        }
        if (obj.top != null && this.top != null) {
            value1 = obj.top
            value2 = this.top
        }

        if (value1 != null && value2 != null) {
            when (value1.valueType) {
                RowValue.NumberType.int-> if (value1.intValue == value2.intValue) {
                    return 0
                }else if (value1.intValue > value2.intValue) {
                    return 1
                }else if (value1.intValue < value2.intValue) {
                    return -1
                }else {
                    return 0
                }
                RowValue.NumberType.double -> if (value1.doubleValue == value2.doubleValue) {
                    return 0
                }else if (value1.doubleValue > value2.doubleValue) {
                    return 1
                }else if (value1.doubleValue < value2.doubleValue) {
                    return -1
                }else {
                    return 0
                }
                RowValue.NumberType.string -> if (value1.stringValue == value2.stringValue) {
                    return 0
                }else if (value1.stringValue > value2.stringValue) {
                    return 1
                }else if (value1.stringValue < value2.stringValue) {
                    return -1
                }else {
                    return 0
                }
            }
        }
        return 0
    }

    public fun update(top: RowValue, bottom: RowValue, topColor: Int, bottomColor: Int): RowItem {
        this.top = top
        this.bottom = bottom
        this.topColor = topColor
        this.bottomColor = bottomColor
        this.middle = null
        this.middleColor = null
        return this
    }

    public fun update(middle: RowValue, middleColor: Int, align: TextAlignment): RowItem {
        this.top = null
        this.bottom = null
        this.topColor = null
        this.bottomColor = null
        this.middle = middle
        this.middleColor = middleColor
        this.middleTextAlign = align
        return this
    }

    public fun update(middle: RowValue, middleColor: Int): RowItem {
        this.top = null
        this.bottom = null
        this.topColor = null
        this.bottomColor = null
        this.middle = middle
        this.middleColor = middleColor
        return this
    }
}

enum class RowType {
    SimpleRow,
    FirstRow,
    FirstTimeRow;
}
enum class TextAlignment {
    Middle,
    Left,
    Right;
}

enum class CompareResult(val res : Int){
    Equal(0),
    Smaller (-1),
    Bigger (1),
}
