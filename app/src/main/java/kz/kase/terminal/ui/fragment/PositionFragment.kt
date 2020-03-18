package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.iris.mqtt.MQTTDataProvider
import kz.kase.terminal.R
import kz.kase.terminal.core.MainViewModel
import kz.kase.terminal.databinding.FragmentTabBinding
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.ui.dialog.NewArcDealDialog
import kz.kase.terminal.ui.dialog.NewOrderDialog
import kz.kase.terminal.viewmodel.InstrumentViewModel

class PositionFragment: Fragment() {
    val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dispose.add(InstrumentViewModel.share.subjectDealTapEvent.subscribeBy(
                onNext = {item ->
                    actionNewDeal()
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
    }
    private fun actionNewDeal(){
        val newFragment = NewArcDealDialog.newInstance()
        newFragment.setShowsDialog(true)
        newFragment.show(childFragmentManager, "dialog")
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

        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)!!
        toolbar.menu.findItem(R.id.menu_action_add_deal).isVisible = true

        return binding.root
    }
    private fun setupViewPaper(viewPager: ViewPager){
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(CashListFragment(), getString(R.string.position))
        adapter.addFragment(DealListFragment(), getString(R.string.deals_))
        adapter.addFragment(DealListFragment(), getString(R.string.archive))

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
