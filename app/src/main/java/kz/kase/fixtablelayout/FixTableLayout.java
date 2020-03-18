package kz.kase.fixtablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import kz.kase.fixtablelayout.adapter.TableAdapter;
import kz.kase.fixtablelayout.inter.IDataAdapter;
import kz.kase.fixtablelayout.inter.ILoadMoreListener;
import kz.kase.fixtablelayout.widget.SingleLineItemDecoration;
import kz.kase.fixtablelayout.widget.TableLayoutManager;
import kz.kase.terminal.R;

import java.lang.ref.WeakReference;

/**
 * Created by feng on 2017/4/2.
 */
public class FixTableLayout extends FrameLayout {
    public static final int MESSAGE_FIX_TABLE_LOAD_COMPLETE = 1001;

    RecyclerView recyclerView;
    RecyclerView leftViews;
    FrameLayout fl_load_mask;

    int divider_height;
    int divider_color;
    int col_1_color;
    int col_2_color;
    int title_color;
    int item_width;
    int item_padding;
    int item_gravity;

    private IDataAdapter dataAdapter;

    private boolean isLoading = false;
    private ILoadMoreListener loadMoreListener;
    private boolean hasMoreData = true;

    public FixTableLayout(Context context) {
        this(context, null);
    }

    public FixTableLayout(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixTableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FixTableLayout);

        divider_height = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_divider_height,
                getResources().getDimensionPixelOffset(R.dimen.divider_default_value));
        divider_color = array.getColor(R.styleable.FixTableLayout_fixtable_divider_color,
                Color.BLACK);
        col_1_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_1_color,
                Color.WHITE);
        col_2_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_2_color,
                Color.WHITE);
        title_color = array.getColor(R.styleable.FixTableLayout_fixtable_title_color, Color.GRAY);
        item_width = array.getDimensionPixelOffset(R.styleable.FixTableLayout_fixtable_item_width,
                getResources().getDimensionPixelOffset(R.dimen.item_width_default_value));
        item_padding = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_item_top_bottom_padding, 0);
        item_gravity = array.getInteger(R.styleable.FixTableLayout_fixtable_item_gravity, 0);

        switch (item_gravity) {
            case 0:
                item_gravity = Gravity.CENTER;
                break;
            case 1:
                item_gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case 2:
                item_gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
        }

        array.recycle();

        View view = inflate(context, R.layout.table_view, null);
        init(view);
        addView(view);
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMain);
        leftViews = (RecyclerView) view.findViewById(R.id.recyclerViewLeft);
        fl_load_mask = (FrameLayout) view.findViewById(R.id.load_mask);

        TableLayoutManager t1 = new TableLayoutManager();
        TableLayoutManager t2 = new TableLayoutManager();

//        Log.i("feng"," -- t : " + t1.string().substring(54) + " t_left: " + t2.string()
//                .substring(54));

        recyclerView.setLayoutManager(t1);
        leftViews.setLayoutManager(t2);

        leftViews.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //将事件发送到RV
                recyclerView.onTouchEvent(event);
                return true;
            }
        });

        SingleLineItemDecoration itemDecoration = new SingleLineItemDecoration(divider_height, divider_color);

        leftViews.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                leftViews.scrollBy(0, dy);
            }
        });

    }

    public void setAdapter(IDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        initRecyclerViewAdapter();
    }

    int lastVisablePos = -1;
    FixTableHandler fixTableHandler;

    public void enableLoadMoreData() {
        fixTableHandler = new FixTableHandler(FixTableLayout.this, recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当用户滑动到底部并且使用fling手势
                if (!isLoading && hasMoreData &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisablePos == recyclerView.getAdapter().getItemCount() - 1) {

                    isLoading = true;
                    fl_load_mask.setVisibility(VISIBLE);

                    if (loadMoreListener != null) {
                        loadMoreListener.loadMoreData(
                                fixTableHandler.obtainMessage(FixTableLayout.MESSAGE_FIX_TABLE_LOAD_COMPLETE));
                    }
                }
                //                    Log.i("feng","滑动到底部 -- 此时的View Bottom：" + recyclerView.getLayoutManager()
                //                            .getDecoratedBottom
                //                            (bottomView) + " recyclerView Height:" +recyclerView.getHeight());

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                lastVisablePos = recyclerView.getChildAdapterPosition(bottomView);
            }
        });
    }

    /**
     * 只有 enableLoadMoreData()被执行此方法设置才有效果
     * @param loadMoreListener
     */
    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private void initRecyclerViewAdapter() {
        TableAdapter.Builder builder = new TableAdapter.Builder();
        TableAdapter tableAdapter = builder
                .setParametersHolder(
                        new TableAdapter.ParametersHolder(col_1_color, col_2_color, title_color,
                                item_width, item_padding, item_gravity))
                .setLeftViews(leftViews)
                .setDataAdapter(dataAdapter)
                .create();

        recyclerView.setAdapter(tableAdapter);
    }

    public void dataUpdate() {
        recyclerView.removeAllViews();
        leftViews.removeAllViews();
        TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
        tableAdapter.notifyLoadData();
        tableAdapter.notifyItemRangeChanged(0, dataAdapter.getRowCount());
    }

    private static class FixTableHandler extends Handler {
        WeakReference<RecyclerView> recyclerViewWeakReference;
        WeakReference<FixTableLayout> fixTableLayoutWeakReference;

        FixTableHandler(FixTableLayout fixTableLayout, RecyclerView recyclerView) {
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
            fixTableLayoutWeakReference = new WeakReference<>(fixTableLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_FIX_TABLE_LOAD_COMPLETE) {
                RecyclerView recyclerView = recyclerViewWeakReference.get();
                FixTableLayout fixTableLayout = fixTableLayoutWeakReference.get();

                TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
                int startPos = tableAdapter.getItemCount() - 1;
                int loadNum = msg.arg1;
                if (loadNum > 0) {
                    //通知Adapter更新数据
                    tableAdapter.notifyLoadData();

//                    Log.i("kz.kase.fixtablelayout","load more completed loadNum :" + loadNum + "scrollTo " +
//                            ":" + fixTableLayout.lastVisableMask);

                } else {
                    //没有数据了
                    fixTableLayout.hasMoreData = false;
                }

                fixTableLayout.fl_load_mask.setVisibility(GONE);
                fixTableLayout.isLoading = false;
            }
        }
    }
}
