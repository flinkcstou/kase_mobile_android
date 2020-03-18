package kz.kase.terminal.core


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Filter
import android.widget.Filterable

import java.util.ArrayList

abstract class RecyclerViewAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>, Filterable {
    var items: ArrayList<T>? = null
        protected set
    protected abstract var onAdapterClickListener: OnAdapterClickListener<T>
    protected var context: Context? = null
    protected var headerCount = 0
    protected var footerCount = 0

    override fun getItemCount(): Int {
        var count = headerCount + footerCount
        if (items != null) {
            count = count + items?.size!!
        }
        return count
    }

    protected var orig: MutableList<T>? = null

    constructor() {
        items = ArrayList()
    }

    @Deprecated("")
    constructor(items: ArrayList<T>) {
        this.items = items
    }

    constructor(onAdapterClickListener: OnAdapterClickListener<T>) {
        items = ArrayList()
        this.onAdapterClickListener = onAdapterClickListener
    }

    constructor(onAdapterClickListener: OnAdapterClickListener<T>, headerCount: Int, footerCount: Int) {
        items = ArrayList()
        this.onAdapterClickListener = onAdapterClickListener
        this.headerCount = headerCount
        this.footerCount = footerCount
    }

    @Deprecated("")
    constructor(items: ArrayList<T>, onAdapterClickListener: OnAdapterClickListener<T>) {
        this.items = items
        this.onAdapterClickListener = onAdapterClickListener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override  fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        context = null
    }

    fun itemDelete(item: T) {
        if (items != null) {
            val pos = items!!.indexOf(item)
            items!!.removeAt(pos)
            super.notifyItemRemoved(pos + headerCount)
        }
    }

    fun removeItem(pos: Int): T {
        val item = items!!.removeAt(pos)
        super.notifyItemRemoved(pos + headerCount)
        return item
    }

    fun itemUpdate(item: T) {
        if (items != null) {
            val pos = items!!.indexOf(item)
            super.notifyItemChanged(pos + headerCount)
        }
    }

    fun itemMoveUp(item: T) {
        if (items != null) {
            val pos = items!!.indexOf(item)
            if (pos != headerCount) {
                items!!.remove(item)
                items!!.add(0, item)
                super.notifyItemMoved(pos + headerCount, headerCount)
            }
            super.notifyItemChanged(headerCount)
        }
    }

    fun itemAdd(item: T) {
        if (items != null) {
            items!!.add(item)
            super.notifyItemInserted(itemCount - 1 - footerCount)
        }
    }

    fun itemAdd(item: T, pos: Int) {
        if (items != null) {
            items!!.add(pos, item)
            notifyItemInserted(pos + headerCount)
        }
    }

    fun itemAddArrayAtBegin(tmpItems: ArrayList<T>) {
        items!!.addAll(0, tmpItems)
        super.notifyItemRangeInserted(headerCount, tmpItems.size)
    }

    fun itemDeleteAll() {
        items!!.clear()
        super.notifyDataSetChanged()
    }

    fun clearItems() {
        //        synchronized (lock) {
        if (items != null)
            items!!.clear()
        if (orig != null) {
            orig!!.clear()
            orig = null
        }
        //        }
    }

    fun getAdapterItem(position: Int): T {
        return items!![position - headerCount]
    }

    fun getIndexItem(index: Int): T? {
        for (item in items!!) {
            if ((item as Indexed).indexField == index) return item
        }
        return null
    }

    //Animation:
    fun animateTo(newItems: List<T>) {
        trimListTo(newItems)
        addMissingItemsFrom(newItems)
        applyAndAnimateMovedItems(newItems)
    }

    private fun trimListTo(newItems: List<T>) {
        for (i in items!!.indices.reversed()) {
            val item = items!![i]
            if (!newItems.contains(item)) {
                removeItem(i)
            }
        }
    }

    private fun addMissingItemsFrom(newItems: List<T>) {
        var i = 0
        val count = newItems.size
        while (i < count) {
            val item = newItems[i]
            if (!items!!.contains(item)) {
                itemAdd(item, i)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(newModels: List<T>) {
        for (toPosition in newModels.indices.reversed()) {
            val item = newModels[toPosition]
            val fromPosition = items!!.indexOf(item)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = items!!.removeAt(fromPosition)
        items!!.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    interface OnAdapterClickListener<T> {
        fun onClick(item: T)
        fun onClick(item: T, view: View)
        fun onMenuClick(menuId: Int, item: T)
        fun onMenuShow()
        fun onMenuDismiss()
        fun onLongClick(item: T)
        fun onLongClick(item: T, view: View)
    }

    interface FilterData {
        val filterData: String
    }

    interface Indexed {
        val indexField: Int
    }

    //    final Object lock = new Object();
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                val oReturn = Filter.FilterResults()
                val results = ArrayList<T>()
                //                synchronized (lock) {
                if (orig == null && items != null) orig = ArrayList(items!!)
                if (constraint != null) {
                    if (orig != null && orig!!.size > 0) {
                        for (item in orig!!) {
                            if (item != null && (item as FilterData).filterData.toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(item)
                        }
                    }
                    oReturn.values = results
                }
                //                }
                return oReturn
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                if (results.values != null) {
                    items = results.values as ArrayList<T>
                    notifyDataSetChanged()
                }
            }
        }
    }
}
