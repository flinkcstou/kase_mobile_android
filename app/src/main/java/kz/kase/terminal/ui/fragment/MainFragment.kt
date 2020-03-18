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
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.TableType

class MainFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = kz.kase.terminal.databinding.FragmentTableBinding.inflate(inflater, container, false)
        Log.d("Fragment", "onCreateView "+ this.javaClass.simpleName)
        binding.listView.layoutManager = LinearLayoutManager(context)
//        val array = ArrayList<ListViewItem>()
//        array.add(ListViewItem(null, R.layout.cell_ads,null))
//        array.add(ListViewItem("Денежный рынок", R.layout.cell_table_header,null))
//        array.add(ListViewItem(null, R.layout.cell_stock,null, TableType.INDEX_MAIN))
//        array.add(ListViewItem("Акции", R.layout.cell_table_header,null))
//        array.add(ListViewItem(null, R.layout.cell_stock,null, TableType.SHARE_MAIN))
//        array.add(ListViewItem("Валюты", R.layout.cell_table_header,null))
//        array.add(ListViewItem(null, R.layout.cell_stock,null, TableType.CURRENCY_MAIN))
//        array.add(ListViewItem("Новости: Рынки", R.layout.cell_table_header,null))
//        array.add(ListViewItem("Новости: Компании", R.layout.cell_table_header,null))
//        binding.listView.adapter = InstrumentTableAdapter(array, context!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (listView.adapter != null) {
            val adapter = listView.adapter as InstrumentTableAdapter
            adapter.dispose()
            listView.adapter = null
            Log.d("Fragment", "onDestroyView " + this.javaClass.simpleName)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Fragment", "onCreateView "+ this.javaClass.simpleName)
        //listView.layoutManager = LinearLayoutManager(context)
        Handler().postDelayed({
            if (listView.adapter == null) {
                val array = ArrayList<ListViewItem>()
                array.add(ListViewItem(null, R.layout.cell_ads, null))
                array.add(ListViewItem("Денежный рынок", R.layout.cell_table_header, null))
                array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.INDEX_MAIN))
                array.add(ListViewItem("Акции", R.layout.cell_table_header, null))
                array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.SHARE_MAIN))
                array.add(ListViewItem("Валюты", R.layout.cell_table_header, null))
                array.add(ListViewItem(null, R.layout.cell_stock, null, TableType.CURRENCY_MAIN))
                array.add(ListViewItem("Новости: Рынки", R.layout.cell_table_header, null))
                array.add(ListViewItem(null, R.layout.cell_news_table, null, TableType.NEWS_MARKET_MAIN))
                array.add(ListViewItem("Новости: Компании", R.layout.cell_table_header, null))
                array.add(ListViewItem(null, R.layout.cell_news_table, null, TableType.NEWS_COMPANY_MAIN))
                listView.adapter = InstrumentTableAdapter(array, context!!)
            }
        }, 500)
    }

}
