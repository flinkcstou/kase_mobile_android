package kz.kase.terminal.ui.fragment


import kotlinx.android.synthetic.main.fragment_table.*
import kz.kase.terminal.other.TableType
import kz.kase.terminal.adapter.NewsTableAdapter

class NewsListFragment(val tableType: TableType): ParentFragment(){
    override var tableId = TableType.N_A
    override fun init() {
        listView.adapter = NewsTableAdapter(tableType, context!!)
    }
}