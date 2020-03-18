package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_quote.*
import kz.kase.terminal.R
import kz.kase.terminal.adapter.QuoteAdapter
import kz.kase.terminal.other.ListViewItem
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.other.TableType
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog


class QuoteFragment(val tmpQuoteBid: QuoteItem, val tmpQuoteAsk: QuoteItem) : Fragment(){
    val TAG = this.javaClass.simpleName
    var tableId= TableType.QUOTES
    private var adapter: QuoteAdapter? = null
    private var items = ArrayList<QuoteItem>()
    private var quoteSize = 5

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "New copy fragment "+ this.javaClass.simpleName)
        val binding = kz.kase.terminal.databinding.FragmentQuoteBinding.inflate(inflater, container, false)
        binding.listView.layoutManager = LinearLayoutManager(context)
        binding.label.text = (getString(R.string.quote_depth) + " :" + 5)
        binding.settings.setOnClickListener{
            showQuoteDepthChange()
        }

        return binding.root
    }
    fun showQuoteDepthChange(){

        val picker = NumberPicker(context)
        picker.minValue = 2
        picker.maxValue = 10
        picker.value = quoteSize

        val layout = FrameLayout(context!!)
        layout.addView(picker, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER))

        AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.quote_depth))
                .setView(layout)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    quoteSize = picker.value
                    genQuotes()
                    val adapter = listView.adapter as QuoteAdapter
                    adapter.items = items
                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }
    fun genQuotes(){
        items.clear()
        val bid = ArrayList<QuoteItem>()
        for (i in 0 until quoteSize) {
            items.add(QuoteItem(qtyBid = null, qtyAsk = if (i == 0) tmpQuoteAsk.qtyAsk else (3 .. 10000).random(), price = tmpQuoteAsk.price + (if (i == 0) 0.0 else (i.toDouble() + (1 .. 100).random().toDouble() / 100.0))))
        }
        for (i in 0 until quoteSize) {
            bid.add(QuoteItem(qtyBid = if (i == 0) tmpQuoteBid.qtyBid else (3 .. 10000).random(), qtyAsk = null, price = tmpQuoteBid.price - (if (i == 0) 0.0 else i.toDouble() - (1 .. 100).random().toDouble() / 100.0)))
        }
        items.sortByDescending { it.price }
        bid.sortByDescending { it.price }
        items.addAll(bid)

        label.text = (getString(R.string.quote_depth) + " :" + quoteSize)
        //tableView.reloadData()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genQuotes()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (listView.adapter != null) {
            val adapter = listView.adapter as QuoteAdapter
            adapter.dispose()
            listView.adapter = null
            Log.d("Fragment", "onDestroyView " + this.javaClass.simpleName)
        }
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            if (listView.adapter == null) {
                if (adapter != null) {
                    listView.adapter = adapter
                }else{
                    val array = ArrayList<ListViewItem>()
                    array.add(ListViewItem(null, R.layout.cell_stock, null, tableId!!))
                    adapter = QuoteAdapter(context!!)
                    adapter?.items = items
                    listView.adapter = adapter
                }
            }
        }, 500)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy!! "+ this.javaClass.simpleName)
    }
}