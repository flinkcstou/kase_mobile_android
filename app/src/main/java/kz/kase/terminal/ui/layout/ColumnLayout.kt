package kz.kase.terminal.ui.layout

import android.widget.LinearLayout
import kz.kase.terminal.R
import kz.kase.terminal.interfaces.RowInterface
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent
import androidx.core.content.ContextCompat
import kz.kase.terminal.other.Column
import kz.kase.terminal.other.Utils


class ColumnLayout(context: Context, private var column: Column, private final val uniqueID: String, private var pos: Int, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), RowInterface {
    private lateinit var imgSettings: ImageView
    private lateinit var imgSort: ImageView
    private lateinit var textTitle: TextView
    private var viewBackground: ViewGroup? = null

    override val rowHeightPx = 25
    override val cellLayout = R.layout.cell_stock_header

    var isDateField = false

    var isDate: Boolean
    get() {
        return isDateField
    }
    set(value) {
        isDateField = value
        textTitle.text = title
    }

    override val title: String
        get() {
            if (isDateField) {
                return "%△1D"
            }else {
                return column.description()
            }
        }
    private var settings = false

    var hasSettings : Boolean
        get(){
            return settings
        }
        set(value){
            settings = value
            if (settings)
                imgSettings.visibility = View.VISIBLE
            else
                imgSettings.visibility = View.GONE
        }

    var isHidden = false

    public fun pos(): Int{
        return pos
    }
    private var sort = SortDirection.SortOff
    public fun resetSort(){
        sort = SortDirection.SortOff
        imgSort.background = ContextCompat.getDrawable(context,R.drawable.ic_sort_col_off);
    }

    private fun init(){
        LayoutInflater.from(context).inflate(R.layout.cell_stock_header, this, true)
        imgSettings = findViewById(R.id.settings);
        imgSettings.setOnClickListener {
            Toast.makeText(context,"Settings pressed",Toast.LENGTH_LONG).show()
        }
        imgSort = findViewById(R.id.sort);
        textTitle = findViewById(R.id.text);
        textTitle.text = title
        viewBackground = findViewById(R.id.back)
        viewBackground?.setOnClickListener {
            actionSort()
        }
        viewBackground?.setBackgroundColor(context.getColor(R.color.colorStockTable))
        viewBackground?.layoutParams = LayoutParams(Utils.rowWidth, Utils.dp(context, rowHeightPx.toDouble()))
    }
    private fun actionSort(){
        val refresh = Intent(uniqueID)
        when (sort){
            SortDirection.SortOff -> {
                sort = SortDirection.SortDescend
                imgSort.background = ContextCompat.getDrawable(context,R.drawable.ic_sort_col_down);
            }
            SortDirection.SortDescend ->{
                sort = SortDirection.SortAscend
                imgSort.background = ContextCompat.getDrawable(context,R.drawable.ic_sort_col_up);
            }
            SortDirection.SortAscend ->{
                sort = SortDirection.SortOff
                imgSort.background = ContextCompat.getDrawable(context,R.drawable.ic_sort_col_off);
            }
        }
        refresh.putExtra("direct", sort.value)
        refresh.putExtra("column", pos)
        LocalBroadcastManager.getInstance(context).sendBroadcast(refresh)
        Toast.makeText(context,"Sort pressed",Toast.LENGTH_LONG).show()
    }

    init {
        init()
    }
}
enum class SortDirection(val value: Int){
    SortOff(0),
    SortAscend (1),
    SortDescend (-1)
}
