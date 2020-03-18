package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_table.*
import kz.kase.terminal.R
import kz.kase.terminal.adapter.CashTableAdapter
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.TableType

class CashListFragment : ParentFragment(){
    override val TAG = this.javaClass.simpleName
    override var tableId = TableType.N_A
    override fun init(){
        val array = ArrayList<ListViewItem>()
        array.add(ListViewItem("KZT", R.layout.cell_table_header, null))
        array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.DEALS_POSITION_CASH))
        array.add(ListViewItem("Акции", R.layout.cell_table_header, null))
        array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.DEALS_POSITION_SHARE))
        array.add(ListViewItem(null, R.layout.cell_pos_share_result, null))
        array.add(ListViewItem("Облигации", R.layout.cell_table_header, null))
        array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.DEALS_POSITION_BONDS))
        array.add(ListViewItem(null, R.layout.cell_pos_bonus_result, null))
        listView.adapter = CashTableAdapter(array, context!!)
    }
}