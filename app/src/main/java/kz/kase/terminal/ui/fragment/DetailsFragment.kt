package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kz.kase.iris.mqtt.MQTTDataProvider
import kz.kase.terminal.R
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.viewmodel.InstrumentViewModel
import android.animation.ValueAnimator
import androidx.navigation.fragment.findNavController
import kz.kase.terminal.entities.InstrumentItem
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.other.TableType
import kz.kase.terminal.ui.dialog.NewOrderDialog


class DetailsFragment: Fragment(){
    private val TAG = this.javaClass.simpleName
    private val dispose = CompositeDisposable()
    private var instrument: InstrumentItem? = null
    var symbol: String? = null
    val viewModel = InstrumentViewModel.share
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dispose.add(InstrumentViewModel.share.subjectNewsTapEvent.subscribeBy(
                onNext = {item ->
                    actionNewsDetails(item)
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
        dispose.add(InstrumentViewModel.share.subjectQuoteTapEvent.subscribeBy(
                onNext = {item ->
                    actionNewOrder(instrument!!.symbol, item.price, item.qty, item.qtyAsk == null)
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
    }
    private fun actionNewsDetails(item: String){
        val bundle = Bundle()
        bundle.putString("url", item)
        findNavController().navigate(R.id.action_web_view, bundle)
    }
    private fun actionNewOrder(item: String, price: Double, qty: Int, directBid: Boolean){
        val newFragment = NewOrderDialog.newInstance(item, price, qty, directBid)
        newFragment.setShowsDialog(true)
        newFragment.show(childFragmentManager, "dialog")

//        val bundle = Bundle()
//        bundle.putString("symbol", item)
//        bundle.putDouble("price", price)
//        bundle.putInt("qty", qty)
//        findNavController().navigate(R.id.action_new_order, bundle)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
        val binding = kz.kase.terminal.databinding.FragmentDetailsBindingImpl.inflate(inflater, container, false)

        symbol = arguments?.getString("symbol", "")
        instrument = MQTTDataProvider.share.getInstrment(symbol)
        binding.instrument = instrument

        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)!!
        toolbar.title = symbol
        toolbar.subtitle = MQTTDataProvider.share.getInstrumDesc(symbol)

        val isFavorite = StorageProvider.isFavorite(symbol)
        val menu = toolbar.menu
        menu.findItem(R.id.menu_action_search).isVisible = false
        menu.findItem(R.id.menu_action_new_order).isVisible = true
        menu.findItem(R.id.menu_action_add_favorite).isVisible = !isFavorite
        menu.findItem(R.id.menu_action_remove_favorite).isVisible = isFavorite

        if(!StorageProvider.isFavoriteCanAdd(symbol)){
            menu.findItem(R.id.menu_action_add_favorite).isVisible = false
            menu.findItem(R.id.menu_action_remove_favorite).isVisible = false
        }
        toolbar.menu.findItem(R.id.menu_action_add_deal).isVisible = false
        binding.viewMore.visibility = View.GONE
        binding.actionMore.setOnClickListener {
            if (binding.viewMore.visibility == View.GONE){
                binding.viewMore.visibility = View.VISIBLE
                binding.statusIcon.background = ContextCompat.getDrawable(context!!, R.drawable.ic_arrow_more_a)
            }else{
                binding.viewMore.visibility = View.GONE
                binding.statusIcon.background = ContextCompat.getDrawable(context!!, R.drawable.ic_arrow_more)
            }
        }

        dispose.add(InstrumentViewModel.share.subjectFavoriteInstrum.subscribeBy(
                onNext = {result ->
                    if (result){
                        menu.findItem(R.id.menu_action_add_favorite).isVisible = false
                        menu.findItem(R.id.menu_action_remove_favorite).isVisible = true
                        StorageProvider.setFavorite(symbol)
                    }else{
                        menu.findItem(R.id.menu_action_add_favorite).isVisible = true
                        menu.findItem(R.id.menu_action_remove_favorite).isVisible = false
                        StorageProvider.removeFavorite(symbol)
                    }
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
        binding.tabs.setupWithViewPager(binding.viewpager)

        setupViewPaper(binding.viewpager)
        return binding.root
    }

    fun animateHeight(view: View){
        val anim = ValueAnimator.ofInt(view.measuredHeight, -100)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        anim.duration = 300
        anim.start()
    }
    private fun setupViewPaper(viewPager: ViewPager){
        val adapter = HomePageFragment.Adapter(childFragmentManager)
        val bid = QuoteItem(instrument!!.bidQty.intValue, null,instrument!!.bid.doubleValue)
        val ask = QuoteItem(null, instrument!!.askQty.intValue,instrument!!.ask.doubleValue)
        adapter.addFragment(QuoteFragment(bid,ask), getString(R.string.quotes))
        adapter.addFragment(ChartFragment(), getString(R.string.chart))
        adapter.addFragment(NewsListFragment(TableType.NEWS_KASE), getString(R.string.news))
        adapter.addFragment(WebViewFragment("file:///android_res/raw/example.htm"), getString(R.string.inf))
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 10

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.dispose()
    }
}
