package kz.kase.terminal.core

import android.view.View

open class DefaultAdapterClickListener<T> : RecyclerViewAdapter.OnAdapterClickListener<T> {
    override fun onClick(item: T) {

    }

    override fun onClick(item: T, view: View) {

    }

    override fun onMenuClick(menuId: Int, item: T) {

    }

    override fun onMenuShow() {

    }

    override fun onMenuDismiss() {

    }

    override fun onLongClick(item: T) {

    }

    override fun onLongClick(item: T, view: View) {

    }
}