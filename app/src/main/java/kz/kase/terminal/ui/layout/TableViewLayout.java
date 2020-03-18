package kz.kase.terminal.ui.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import kz.kase.terminal.R;
import kz.kase.terminal.interfaces.RowInterface;
import kz.kase.terminal.other.Column;
import kz.kase.terminal.other.RowLineItem;
import kz.kase.terminal.other.Utils;
import kz.kase.terminal.viewmodel.InstrumentViewModel;


public class TableViewLayout extends RelativeLayout {

    public final String TAG = TableViewLayout.class.getSimpleName();

    private TableLayout tableLeftFixed;
    private TableLayout tableRightData;

    private HorizontalScrollView horizontalData;

    private ScrollView verticalLeftFixed;
    private ScrollView verticalData;

    private ArrayList<RowLineItem> cellData = new ArrayList<>();
    private ArrayList<RowLineItem> cellDataOriginal = new ArrayList<>();

    private int rowWidth = toDp(80);
//    private CompositeDisposable dispose = new CompositeDisposable();

//    public PublishSubject<ArrayList<RowLineItem>> subjectData = PublishSubject.create();

    public final String uniqueID = UUID.randomUUID().toString();
    public TableViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TableViewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public TableViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TableViewLayout(Context context) {
        super(context);
        init();
    }
    private void init(){
        this.initComponents();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();
        verticalLeftFixed.setVerticalScrollBarEnabled(false);
        verticalLeftFixed.setHorizontalScrollBarEnabled(false);
        verticalLeftFixed.addView(this.tableLeftFixed);
        verticalData.addView(this.horizontalData);
        horizontalData.addView(this.tableRightData);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        Integer size = displayMetrics.widthPixels;
        if(displayMetrics.widthPixels > displayMetrics.heightPixels)
            size = displayMetrics.heightPixels;
        int result = size / Utils.Companion.dp(getContext(), 4);
        rowWidth = Utils.Companion.dp(getContext(), result + 5);
        Utils.Companion.setRowWidth(rowWidth);

        this.addComponentToMainLayout();
        this.setBackgroundColor(Color.WHITE);


        setTableData(this.sampleObjects(), Column.Companion.statisticColumns());
        Log.d(TAG, "sampleObjects is set");
//        dispose.add(subjectData.subscribe(new Consumer<ArrayList<RowLineItem>>() {
//            @Override
//            public void accept(ArrayList<RowLineItem> rowLineItems) throws Exception {
//                setTableData(rowLineItems);
//            }
//
//        }));

        BroadcastReceiver refresh = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction() != null && intent.getAction().equals(uniqueID)) {
                    String row = intent.getStringExtra("row");
                    int column = intent.getIntExtra("column", -1);
                    int direct = intent.getIntExtra("direct", -2);
                    if (row != null) {

                    } else if(column != -1) {
                        sortCellData(column, direct);
                        refreshTableData();
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refresh,
                new IntentFilter(uniqueID));
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View is now attached
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    private void sortCellData(final int column,final int direct){
        cellData.clear();
        cellData.addAll(cellDataOriginal);
        for (RowInterface item: cellData.get(0).getRowArrayList()){
            if (item instanceof ColumnLayout && ((ColumnLayout)item).pos() != column){
                ((ColumnLayout) item).resetSort();
            }
        }
        if(direct != 0) {
            Collections.sort(cellData, new Comparator<RowLineItem>() {
                @Override
                public int compare(RowLineItem t1, RowLineItem t2) {
                    RowInterface item1 = t1.getRowArrayList().get(column);
                    RowInterface item2 = t2.getRowArrayList().get(column);
                    if (item1 instanceof ColumnLayout || item2 instanceof ColumnLayout)
                        return 1;
                    if(direct == 1)
                        return ((RowLayout) item1).getRowItem().compareTo(((RowLayout) item2).getRowItem());
                    else {
                        int res = ((RowLayout) item1).getRowItem().compareTo(((RowLayout) item2).getRowItem());
                        if (res == 1)
                            return -1;
                        else if (res == -1)
                            return 1;
                    }
                    return 0;
                }
            });
        }
    }
    private int toDp(double size){
        float scale = getResources().getDisplayMetrics().density;
        return (int) ((float)size * scale + 0.5f);
    }
    // this is just the sample data
    ArrayList<RowLineItem> sampleObjects(){
        ArrayList<RowLineItem> cellData = new ArrayList<RowLineItem>();
        //ArrayList<ColumnLayout> cols = Column.Companion.values(getContext(), uniqueID, Column.Companion.statisticColumns());
        //int columnCount = cols.size();
        //cellData.add(new RowLineItem(cols));
//        for(int x=1; x<=9; x++){
//            cellData.add(new RowLineItem(getContext(), columnCount, uniqueID, x % 2 == 1));
//        }
        return cellData;

    }

    // initalized components
    private void initComponents(){

        this.tableLeftFixed = new TableLayout(getContext());
        this.tableRightData = new TableLayout(getContext());
        this.horizontalData = new HScrollView(getContext());
        this.verticalLeftFixed = new MScrollView(getContext());
        this.verticalData = new MScrollView(getContext());
    }

    // set essential component IDs
    private void setComponentsId(){
        this.verticalLeftFixed.setId(R.id.vertical_left_fixed);
        this.verticalData.setId(R.id.vertical_data);
    }

    // set tags for some horizontal and vertical scroll view
    private void setScrollViewAndHorizontalScrollViewTag(){
        this.horizontalData.setTag("horizontalData");

        this.verticalLeftFixed.setTag("verticalLeftFixed");
        this.verticalData.setTag("verticalData");
    }

    private void addComponentToMainLayout(){

        RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableLeftFixed.getId());

        RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.verticalLeftFixed.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.verticalData.getId());

        this.addView(this.verticalLeftFixed, componentC_Params);
        this.addView(this.verticalData, componentD_Params);

    }
    public void setTableData(ArrayList<RowLineItem> data, ArrayList<Column> cols) {
        Log.d(TAG, "setTableData count:" + data.size());
        cellData.clear();
        cellDataOriginal.clear();
        data = new ArrayList<>(data);
        data.add(0, new RowLineItem(Column.Companion.values(getContext(), uniqueID, cols)));
        cellData.addAll(data);
        cellDataOriginal.addAll(cellData);
        refreshTableData();
        //refreshTableData();
    }
    public void refreshTableData(){
        removeAllCells(tableLeftFixed);
        removeAllCells(tableRightData);

        for(RowLineItem sampleObject : this.cellData){
            TableRow cellForLeftFixed = this.cellForLeftFixed(sampleObject);
            TableRow cellForForData = this.cellForForData(sampleObject);
            cellForLeftFixed.setBackgroundColor(Color.LTGRAY);
            cellForForData.setBackgroundColor(Color.LTGRAY);
            this.tableLeftFixed.addView(cellForLeftFixed);
            this.tableRightData.addView(cellForForData);
        }
    }
    private void removeAllCells(TableLayout table){
        if (table.getRootView() != null) {
            for(int x = 0; x < table.getChildCount(); x++) {
                View child = table.getChildAt(x);
                if(child instanceof TableRow) {
                    ((ViewGroup) child).removeAllViews();
                }
            }
            table.removeAllViews();
        }
    }
    private TableRow cellForLeftFixed(RowLineItem sampleObject){
        RowInterface item = sampleObject.getRowArrayList().get(0);
        TableRow.LayoutParams params = new TableRow.LayoutParams( rowWidth, toDp(item.getRowHeightPx()));
        params.setMargins(0, 0, 0, 0);
        TableRow cellForLeftFixed = new TableRow(getContext());
        cellForLeftFixed.addView((LinearLayout)item, params);
        return cellForLeftFixed;
    }

    private TableRow cellForForData(RowLineItem sampleObject){
        TableRow cellForForData = new TableRow(getContext());
        for(int x = 1 ; x < sampleObject.getRowArrayList().size(); x++){
            RowInterface item = sampleObject.getRowArrayList().get(x);
            TableRow.LayoutParams params = new TableRow.LayoutParams( rowWidth, toDp(item.getRowHeightPx()));
            params.setMargins(toDp(0), 0, 0, 0);
            cellForForData.addView((LinearLayout)item, params);
        }

        return cellForForData;

    }
    // horizontal scroll view custom class
    class HScrollView extends HorizontalScrollView{

        public HScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("horizontal scroll view b")){
                horizontalData.scrollTo(l, 0);
            }
        }

    }

    // scroll view custom class
    class MScrollView extends ScrollView{

        public MScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("verticalLeftFixed")){
                verticalData.scrollTo(0, t);
            }else{
                verticalLeftFixed.scrollTo(0,t);
            }
        }
    }



}