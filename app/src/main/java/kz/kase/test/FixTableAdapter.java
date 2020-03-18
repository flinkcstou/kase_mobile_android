package kz.kase.test;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.util.List;

import kz.kase.fixtablelayout.inter.IDataAdapter;
import kz.kase.fixtablelayout.widget.TextViewUtils;


public class FixTableAdapter implements IDataAdapter {
    private String TAG = FixTableAdapter.class.getSimpleName();

    private List<String> titles;

    private List<List<String>> data;

    private Context context;

    public FixTableAdapter(List<String> titles, List<List<String>> data, Context context) {
        this.titles = titles;
        this.data = data;
        this.context = context;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
    public void setTitle(List<String> titles) {
        this.titles = titles;
    }

    @Override
    public int getColumnCount() {
        return titles.size();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public View getItemView(int index, int viewType) {
        Log.d(TAG, "getItemView++ " + index);
        TextView textView = null;
        if (viewType == 0) { //header
            textView = TextViewUtils.generateTextView(context,
                    " ",
                    0,
                    toDp(100),
                    toDp(8));
        } else { //Data
            textView = TextViewUtils.generateTextView(context,
                    " ",
                    0,
                    toDp(100),
                    toDp(8));
        }
        return textView;
    }

    @Override
    public void convertData(int row, int col, View view) {
        String txt = data.get(row).get(col);
        if (view instanceof TextView) {
            if (row == 0)
                ((TextView)view).setText(titles.get(col));
            else
                ((TextView)view).setText(txt);
        }
    }
    private int toDp(double size){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float)size * scale + 0.5f);
    }
}
