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
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.terminal.R
import kz.kase.terminal.core.MainViewModel
import kz.kase.terminal.databinding.*
import kz.kase.terminal.viewmodel.InstrumentViewModel
import kotlin.collections.ArrayList

class HomePageFragment: Fragment() {
    private val compDispose = CompositeDisposable()
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
        retainInstance = true
        compDispose.add(InstrumentViewModel.share.subjectTapEvent.subscribeBy(
                onNext = {item ->
                    actionInstrumentDetails(item)
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
        compDispose.add(InstrumentViewModel.share.subjectNewsTapEvent.subscribeBy(
                onNext = {item ->
                    actionNewsDetails(item)
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabBinding.inflate(inflater, container, false)
        setupViewPaper(binding.viewpager)
        binding.resultTabs.setupWithViewPager(binding.viewpager)
        return binding.root
    }
    private fun setupViewPaper(viewPager: ViewPager){
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(FavoriteFragment(), "Избранное")
        adapter.addFragment(MainFragment(), "Главная")
        adapter.addFragment(SharesFragment(), "Акции")
        adapter.addFragment(CurrencyFragment(), "Валюты")
        adapter.addFragment(GsFragment(), "ГЦБ")
        adapter.addFragment(BondsFragment(), "Облигации")
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1,false)
        viewPager.offscreenPageLimit = 10

    }
    private fun actionInstrumentDetails(item: String){
        val bundle = Bundle()
        bundle.putString("symbol", item)
        findNavController().navigate(R.id.action_details, bundle)
    }
    private fun actionNewsDetails(item: String){
        val bundle = Bundle()
        bundle.putString("url", item)
        findNavController().navigate(R.id.action_web_view, bundle)
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
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.subtitle = ""
            val menu = toolbar.menu
            if (menu != null && menu.size() > 0) {
                menu.findItem(R.id.menu_action_add_favorite).isVisible = false
                menu.findItem(R.id.menu_action_new_order).isVisible = false
                menu.findItem(R.id.menu_action_remove_favorite).isVisible = false
                menu.findItem(R.id.menu_action_add_deal).isVisible = false
            }
        }
    }
}
