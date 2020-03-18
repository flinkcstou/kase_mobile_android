package kz.kase.terminal.ui.layout

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kz.kase.terminal.R
import kz.kase.terminal.interfaces.RowInterface
import kz.kase.terminal.other.RowItem
import kz.kase.terminal.other.RowType
import kz.kase.terminal.other.Utils
import kz.kase.terminal.viewmodel.InstrumentViewModel

class RowLayout @JvmOverloads constructor(context: Context, rowItem: RowItem, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), RowInterface {

    private var textTop: TextView? = null
    private var textMiddle: TextView? = null
    private var textBottom: TextView? = null
    private var imgTime: ImageView? = null
    private var viewBackground: ViewGroup? = null
    var rowItem : RowItem

    override val rowHeightPx = rowItem.rowHeightPx
    override val title: String? = rowItem.title

    fun isShaded(status: Boolean): RowLayout {
        if (status)
            viewBackground?.setBackgroundColor(context.getColor(R.color.colorStockTableDark))
        else
            viewBackground?.setBackgroundColor(context.getColor(R.color.colorStockTable))
        return this
    }

    override val cellLayout: Int
        get() {
            if (rowItem.rowType == RowType.SimpleRow) {
                return R.layout.cell_stock_row_trade
            }else if (rowItem.rowType == RowType.FirstRow) {
                return R.layout.cell_stock_row_desc
            }else {
                return R.layout.cell_stock_row_time
            }
        }

    fun init(){
        LayoutInflater.from(context).inflate(cellLayout, this, true)
        textTop = findViewById(R.id.top)
        textMiddle = findViewById(R.id.middle)
        textBottom = findViewById(R.id.bottom)
        imgTime = findViewById(R.id.time_icon)
        viewBackground = findViewById(R.id.back)
        viewBackground?.setOnClickListener {
            actionTap()
        }
        viewBackground?.layoutParams = LayoutParams(Utils.rowWidth, Utils.dp(context, rowItem.rowHeightPx.toDouble()))
    }
    fun actionTap(){
        InstrumentViewModel.share.subjectTapEvent.onNext(rowItem.tag)
        //val refresh = Intent(rowItem.tableID)
        //refresh.putExtra("row", rowItem.tag)
        //LocalBroadcastManager.getInstance(context).sendBroadcast(refresh)
        //Toast.makeText(context,"Row pressed:" + rowItem.tag, Toast.LENGTH_LONG).show()
    }

    fun update(source: RowItem){
        this.rowItem = source
        update()
    }
    fun update(){
        if (rowItem.middle != null) {
            textMiddle?.visibility = View.VISIBLE
            textMiddle?.text = rowItem.middle?.string
            textMiddle?.setTextColor(context.getColor(rowItem.middleColor!!))

        }else
            textMiddle?.visibility = View.GONE

        if (rowItem.top != null) {
            textTop?.visibility = View.VISIBLE
            textTop?.text = rowItem.top?.string
            textTop?.setTextColor(context.getColor(rowItem.topColor!!))
        }else
            textTop?.visibility = View.GONE

        if (rowItem.bottom != null) {
            textBottom?.text = rowItem.bottom?.string
            textBottom?.visibility = View.VISIBLE

            textBottom?.setTextColor(context.getColor(rowItem.bottomColor!!))
        }else
            textBottom?.visibility = View.GONE
    }
    init {
        this.rowItem = rowItem
        init()
        update()
    }
}