
package kz.kase.terminal.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment


fun Fragment.initToolbar(toolbar: Toolbar, titleResId: Int, backEnabled: Boolean) {
  val appCompatActivity = activity as AppCompatActivity
  appCompatActivity.setSupportActionBar(toolbar)
  appCompatActivity.supportActionBar?.setTitle(titleResId)
  appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(backEnabled)
}
