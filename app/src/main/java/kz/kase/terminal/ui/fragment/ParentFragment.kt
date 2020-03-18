package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_table.*
import kz.kase.terminal.R
import kz.kase.terminal.adapter.InstrumentTableAdapter
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.TableType

abstract class ParentFragment : Fragment() {
    protected open val TAG = this.javaClass.simpleName

    abstract var tableId: TableType

    //var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "New copy fragment " + TAG)
        val binding = kz.kase.terminal.databinding.FragmentTableBinding.inflate(inflater, container, false)
        binding.listView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Fragment", "onDestroyView " + TAG)
    }
    open fun init(){
        val array = ArrayList<ListViewItem>()
        array.add(ListViewItem(null, R.layout.cell_stock, null, tableId))
        listView.adapter = InstrumentTableAdapter(array, context!!)

    }
    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (listView.adapter == null) {
//                if (adapter != null) {
//                    listView.adapter = adapter
//                }else{
                    init()
                    //adapter = listView.adapter
//                }
            }
        }, 500)
    }

    override fun onStop() {
        super.onStop()
        val adapter = listView.adapter as? InstrumentTableAdapter
        adapter?.dispose()
        listView.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy!! " + TAG)
    }
}
