package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_table.*
import kz.kase.terminal.R
import kz.kase.terminal.adapter.InstrumentTableAdapter
import kz.kase.terminal.adapter.OrderTableAdapter
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.TableType

class OrderListFragment(): ParentFragment(){
    override var tableId = TableType.N_A
    override fun init() {
        val array = ArrayList<ListViewItem>()
        array.add(ListViewItem("Акции", R.layout.cell_table_header, null))
        array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.ORDER_SHARE_ACTIVE))
        array.add(ListViewItem("Облигации", R.layout.cell_table_header, null))
        array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.ORDER_BONDS_ACTIVE))
        listView.adapter = OrderTableAdapter(array, context!!)
    }
}