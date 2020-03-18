package kz.kase.terminal.ui.search;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import kz.kase.iris.mqtt.MQTTDataProvider;
import kz.kase.terminal.core.DefaultAdapterClickListener;
import kz.kase.terminal.entities.InstrumentItem;


public class SearchContainer extends FrameLayout implements SearchView.OnQueryTextListener {
    private static final String TAG = "SearchContainer";
    private List<InstrumentItem> items = new ArrayList<>();
    private int counter = 1;
    private String currentQuery = "";
    private SearchAdapter searchAdapter;

    public SearchContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        //if (isInEditMode()) return;
        searchAdapter = new SearchAdapter(new DefaultAdapterClickListener<Object>() {
            @Override
            public void onClick(Object item) {
                if (item instanceof InstrumentItem) {
                    InstrumentItem instrum = (InstrumentItem) item;
                    instrum.setFavorite(!instrum.isFavorite());
                }
                searchAdapter.notifyDataSetChanged();
                Log.w(TAG, "onClick: " + item);
            }

            @Override
            public void onMenuClick(int menuId, Object item) {
//                if(item instanceof ContactItem)
//                    onMenuItemSelected(menuId, (ContactItem)item);
            }

            @Override
            public void onLongClick(Object item) {
                super.onLongClick(item);
            }
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RecyclerView recyclerView = (RecyclerView) getChildAt(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("Search", newText);
        String search = newText.toUpperCase();
        ArrayList<Object> searchItems = new ArrayList<>();

        for (InstrumentItem item : items) {
            if (item.getSymbol().toUpperCase().contains(search) || item.getDescription().toUpperCase().contains(search) || item.getTypeString().toUpperCase().contains(search)) {
                searchItems.add(item);
            }
        }

        searchAdapter.setItems(searchItems);
        searchAdapter.notifyDataSetChanged();
        //refreshSearch(newText);
        return false;
    }
    public void refresh(){
        items = MQTTDataProvider.share.getInstrumentsInfo();
        searchAdapter.setItems(new ArrayList<Object>(items));
        searchAdapter.notifyDataSetChanged();
    }

}