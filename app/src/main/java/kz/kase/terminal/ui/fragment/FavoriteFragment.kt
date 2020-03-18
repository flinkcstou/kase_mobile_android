package kz.kase.terminal.ui.fragment

import kotlinx.android.synthetic.main.fragment_table.*
import kz.kase.terminal.R
import kz.kase.terminal.adapter.InstrumentTableAdapter
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.TableType

class FavoriteFragment(override var tableId: TableType = TableType.FAVORITE_BONDS) : ParentFragment() {
    override fun init() {
        val array = ArrayList<ListViewItem>()
        array.add(ListViewItem("Акции", R.layout.cell_table_header,null))
        array.add(ListViewItem(null, R.layout.cell_stock,null, TableType.FAVORITE_SHARES))
        array.add(ListViewItem("Корпоративные облигации", R.layout.cell_table_header,null))
        array.add(ListViewItem(null, R.layout.cell_stock,null,TableType.FAVORITE_BONDS))
        listView.adapter = InstrumentTableAdapter(array, context!!)
    }
}
