package kz.kase.terminal.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.iris.mqtt.MQTTDataProvider
import kz.kase.terminal.R
import kz.kase.terminal.core.MainViewModel
import kz.kase.terminal.databinding.FragmentTabBinding
import kz.kase.terminal.entities.InstrumentItem
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.other.TableType
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.ui.dialog.NewOrderDialog
import kz.kase.terminal.viewmodel.InstrumentViewModel

class OrderFragment: Fragment() {
    val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabBinding.inflate(inflater, container, false)
        setupViewPaper(binding.viewpager)
        binding.resultTabs.setupWithViewPager(binding.viewpager)
        binding.resultTabs.tabGravity = TabLayout.GRAVITY_FILL
        binding.resultTabs.tabMode = TabLayout.MODE_FIXED
        return binding.root
    }
    private fun setupViewPaper(viewPager: ViewPager){
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(OrderListFragment(), getString(R.string.active))
        adapter.addFragment(OrderListFragment(), getString(R.string.executed))
        adapter.addFragment(OrderListFragment(), getString(R.string.canceled))

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 10

    }
    internal class Adapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {

            return mFragmentTitleList.get(position)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
            super.setPrimaryItem(container, position, obj)
            MainViewModel.share.currentView.onNext(obj)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
