package kz.kase.fixtablelayout.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import kz.kase.fixtablelayout.inter.IDataAdapter;
import kz.kase.fixtablelayout.widget.SingleLineLinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by feng on 2017/3/28.
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private RecyclerView leftViews;

    private ParametersHolder parametersHolder;

    private IDataAdapter dataAdapter;

    private TableAdapter(RecyclerView leftViews, ParametersHolder parametersHolder, IDataAdapter dataAdapter) {
        super();
        this.leftViews = leftViews;
        this.parametersHolder = parametersHolder;
        this.dataAdapter = dataAdapter;

        initViews();
    }

    private void initViews() {
        leftViews.setAdapter(new LeftViewAdapter());
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TableViewHolder", "viewType: " + viewType);
        SingleLineLinearLayout singleLineLinearLayout = new SingleLineLinearLayout(parent.getContext());
        for (int i = 1; i < dataAdapter.getColumnCount() - 1; i++) {
            View view = dataAdapter.getItemView(i, viewType);
            singleLineLinearLayout.addView(view, i - 1);
        }
        return new TableViewHolder(singleLineLinearLayout);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        SingleLineLinearLayout content = (SingleLineLinearLayout) holder.itemView;
        for (int i = 1; i < dataAdapter.getColumnCount() - 1; i++) {
            View view = (View) content.getChildAt(i - 1);
            dataAdapter.convertData(position, i, view);
        }
        setBackgrandForItem(position, content);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    private void setBackgrandForItem(int position, SingleLineLinearLayout content) {
        if (position % 2 != 0) {
            content.setBackgroundColor(parametersHolder.col_1_color);
        } else {
            content.setBackgroundColor(parametersHolder.col_2_color);
        }
    }

    @Override
    public int getItemCount() {
        return dataAdapter.getRowCount();
    }

    class LeftViewAdapter extends RecyclerView.Adapter<LeftViewAdapter.LeftViewHolder> {

        @Override
        public LeftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SingleLineLinearLayout singleLineLinearLayout = new SingleLineLinearLayout(
                    parent.getContext());
            singleLineLinearLayout.addView(dataAdapter.getItemView(0, viewType));
            return new LeftViewHolder(singleLineLinearLayout);
        }

        @Override
        public void onBindViewHolder(LeftViewHolder holder, int position) {
            SingleLineLinearLayout content = (SingleLineLinearLayout) holder.itemView;
            View view = content.getChildAt(0);
            setBackgrandForItem(position, content);
            dataAdapter.convertData(position, 0, view);
        }
        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            return 1;
        }
        @Override
        public int getItemCount() {
            return dataAdapter.getRowCount();
        }

        class LeftViewHolder extends RecyclerView.ViewHolder {
            LeftViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        TableViewHolder(View itemView) {
            super(itemView);
        }
    }
    class TableTitleHolder extends RecyclerView.ViewHolder {
        TableTitleHolder(View itemView) {
            super(itemView);
        }
    }
    public static class ParametersHolder {
        int col_1_color;
        int col_2_color;
        int title_color;
        int item_width;
        int item_padding;
        int item_gravity;

        public ParametersHolder(int s_color, int b_color, int title_color,
                                int item_width, int item_padding, int item_gravity) {
            this.col_1_color = s_color;
            this.col_2_color = b_color;
            this.title_color = title_color;
            this.item_width = item_width;
            this.item_padding = item_padding;
            this.item_gravity = item_gravity;
        }
    }

    public static class Builder {
        RecyclerView leftViews;

        ParametersHolder parametersHolder;
        IDataAdapter dataAdapter;

        public Builder setLeftViews(RecyclerView leftViews) {
            this.leftViews = leftViews;
            return this;
        }

        public Builder setParametersHolder(
                ParametersHolder parametersHolder) {
            this.parametersHolder = parametersHolder;
            return this;
        }

        public Builder setDataAdapter(IDataAdapter dataAdapter) {
            this.dataAdapter = dataAdapter;
            return this;
        }

        public TableAdapter create() {
            return new TableAdapter(leftViews, parametersHolder, dataAdapter);
        }
    }

    public void notifyLoadData() {
        notifyDataSetChanged();
        leftViews.getAdapter().notifyDataSetChanged();
    }
}
