package kz.kase.terminal.ui.search


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import kz.kase.terminal.R
import kz.kase.terminal.core.RecyclerViewAdapter
import kz.kase.terminal.databinding.CellSearchInstrumBinding
import kz.kase.terminal.entities.InstrumentItem

import java.util.Collections
import android.R.animator
import androidx.recyclerview.widget.SimpleItemAnimator



class SearchAdapter(private val adapterClickListener: RecyclerViewAdapter.OnAdapterClickListener<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: ArrayList<Any> = ArrayList();
    //private var contactManager: ContactManager? = null

    override fun getItemCount(): Int {
        return items?.size!!
    }

    init {
        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        //contactManager = ContactManagerKt.getContactManager(recyclerView.getContext())
    }

    override fun getItemViewType(position: Int): Int {
        return SEARCH_HEADER
    }

    override  fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CellSearchInstrumBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return InstrumentViewHolder(binding)
    }
    internal inner class InstrumentViewHolder(binding: CellSearchInstrumBinding) : RecyclerView.ViewHolder(binding.root){
        var item: InstrumentItem? = null
        var binding: CellSearchInstrumBinding? = null
        init{
            this.binding = binding

        }
        fun bind(item: InstrumentItem) {
            this.item = item
            binding!!.instrument = item
        }
        init{
            binding.root.setOnClickListener { v -> adapterClickListener.onClick(item!!) }
        }
    }

    override  fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items!![position] as InstrumentItem
        (holder as InstrumentViewHolder).bind(item)
    }

    fun setItems(items: ArrayList<Any>) {
        this.items = items
    }

    fun removeFooter(): Boolean {
        return false
    }

    fun addItems(items: List<Any>) {
        this.items!!.addAll(items)
    }

    fun itemUpdate(item: Any) {
        if (items != null) {
            val pos = items!!.indexOf(item)
            super.notifyItemChanged(pos)
        }
    }

    fun itemDelete(item: Any) {
        if (items != null) {
            val pos = items!!.indexOf(item)
            items!!.removeAt(pos)
            super.notifyItemRemoved(pos)
        }
    }
    override fun getItemId(position: Int): Long {
        val item = items!![position] as InstrumentItem
        return item.id.toLong()
    }

    /**
     * ViewHolders
     */

    internal class SearchHeaderItem

    internal inner class SearchHeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        var counter: TextView

        init {
            counter = view.findViewById<View>(R.id.text) as TextView
        }
    }



    companion object {
        private val SEARCH_HEADER = 201
        private val SEARCH_FOOTER = 202
        private val SEARCH_CONTACT_ITEM = 203
        private val SEARCH_CHAT_ITEM = 204
        private val ADDRESSBOOK_ITEM = 303
    }
}