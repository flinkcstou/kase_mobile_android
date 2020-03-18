package kz.kase.terminal

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.top_toolbar.view.*
import kz.kase.terminal.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_tab.*
import kz.kase.iris.mqtt.MQTTDataProvider
import kz.kase.terminal.core.MainViewModel
import kz.kase.terminal.entities.InstrumentPack
import kz.kase.terminal.other.Column
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.other.TableType
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.ui.fragment.FavoriteFragment
import kz.kase.terminal.viewmodel.InstrumentViewModel


class MainActivity : AppCompatActivity(){
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var news : TextView
    private lateinit var trend : TextView
    private lateinit var portfolio : TextView
    private lateinit var ntf : TextView
    private lateinit var  mNavigationView: NavigationView
    private var searchItem: MenuItem? = null
    private var searchView: SearchView? = null
    private val compDisp = CompositeDisposable()

    protected var TAG = this.javaClass.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_main)
        MQTTDataProvider.share

        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up ActionBar
        var toolbar = binding.topBar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        supportActionBar?.setDisplayShowTitleEnabled(true)
        //binding.topBar.logo_image.isVisible = false
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Set up navigation menu
        mNavigationView = binding.navigationView
        binding.navigationView.setupWithNavController(navController)
        configSideMenu()
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorGreenDark))
    }

    fun configSideMenu(){
        news = mNavigationView.getMenu().findItem(R.id.news_fragment).getActionView() as TextView
        trend = mNavigationView.getMenu().findItem(R.id.trend_fragment).getActionView() as TextView
        portfolio = mNavigationView.getMenu().findItem(R.id.portfolio_fragment).getActionView() as TextView
        ntf = mNavigationView.getMenu().findItem(R.id.menu_ntf).getActionView() as TextView
        example();
    }
    fun example(){
        news.setGravity(Gravity.CENTER_VERTICAL);
        news.setTypeface(null, Typeface.BOLD);
        news.setTextColor(getResources().getColor(R.color.colorOrange));
        news.setText("3");

        trend.setGravity(Gravity.CENTER_VERTICAL);
        trend.setTypeface(null,Typeface.BOLD);
        trend.setTextColor(getResources().getColor(R.color.colorGreenDark));
        trend.setText("12");

        portfolio.setGravity(Gravity.CENTER_VERTICAL);
        portfolio.setTypeface(null,Typeface.BOLD);
        portfolio.setTextColor(getResources().getColor(R.color.colorGreenDark));
        portfolio.setText("+0.24%");

        ntf.setGravity(Gravity.CENTER_VERTICAL);
        ntf.setTypeface(null,Typeface.BOLD);
        ntf.setTextColor(getResources().getColor(R.color.colorRed));
        ntf.setText("30");
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchItem = menu?.findItem(R.id.menu_action_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        compDisp.add(MainViewModel.share.currentView
                .distinctUntilChanged()
                .subscribe {result ->
                    Log.d(TAG, "Page: " + result)
                    if ((result as? FavoriteFragment) != null){
                        searchItem?.isVisible = true
                    }else{
                        searchItem?.isVisible = false
                    }

        })
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return searchContainer != null && searchContainer.onQueryTextSubmit(query)
            }

            override fun onQueryTextChange(searchQuery: String): Boolean {
                return searchContainer != null && searchContainer.onQueryTextChange(searchQuery)
            }
        })

        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchContainer.visibility = View.GONE
                Log.d(TAG, "searchContainer GONE")
                InstrumentViewModel.share.subjectUpdateTable.onNext(TableType.FAVORITE_SHARES)
                InstrumentViewModel.share.subjectUpdateTable.onNext(TableType.FAVORITE_BONDS)
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
  //              searchContainer.setActiveTab(adapter.getTabByPos(viewPager.currentItem))
                searchContainer.visibility = View.VISIBLE
                Log.d(TAG, "searchContainer VISIBLE")
                searchContainer.refresh()
                return true  // Return true to expand action view
            }
        })


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId!!
        //subjectFavoriteInstrum
        when (id){
            R.id.menu_action_add_deal ->{
                InstrumentViewModel.share.subjectDealTapEvent.onNext("")
            }
            R.id.menu_action_search ->{

            }
            R.id.menu_action_add_favorite ->{
                InstrumentViewModel.share.subjectFavoriteInstrum.onNext(true)
            }
            R.id.menu_action_remove_favorite ->{
                InstrumentViewModel.share.subjectFavoriteInstrum.onNext(false)
            }
            R.id.menu_action_new_order ->{
                InstrumentViewModel.share.subjectQuoteTapEvent.onNext(QuoteItem(0, null, 0.0))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun hideSearch(): Boolean {
        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
            if (searchView != null) searchView?.setQuery("", true)
            MenuItemCompat.collapseActionView(searchItem)
            return true
        }
        return false
    }
}