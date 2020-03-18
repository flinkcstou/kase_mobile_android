package kz.kase.terminal.interfaces

import android.content.Context
import kz.kase.terminal.other.Column
import kz.kase.terminal.ui.layout.RowLayout

interface RowItemInterface {
    fun rows(context: Context, array: List<Column>) : List<RowLayout>
    fun rows(context: Context, array: List<Column>, isShaded: Boolean) : List<RowLayout>
}