package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.terminal.R
import kz.kase.terminal.core.MainViewModel
import kz.kase.terminal.databinding.FragmentTabBinding
import kz.kase.terminal.other.TableType
import kz.kase.terminal.viewmodel.InstrumentViewModel

class NewsFragment: Fragment() {
    private val compDispose = CompositeDisposable()
    val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
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
        binding.resultTabs.tabGravity = TabLayout.GRAVITY_FILL
        binding.resultTabs.tabMode = TabLayout.MODE_FIXED
        return binding.root
    }
    private fun setupViewPaper(viewPager: ViewPager){
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(NewsListFragment(TableType.NEWS_MARKET), getString(R.string.editorial))
        adapter.addFragment(NewsListFragment(TableType.NEWS_KASE), getString(R.string.kase_news))

        viewPager.adapter = adapter
        //viewPager.setCurrentItem(1,false)
        viewPager.offscreenPageLimit = 10

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
    }
}
/*   */