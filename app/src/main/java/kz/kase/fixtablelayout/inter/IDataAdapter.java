package kz.kase.fixtablelayout.inter;

import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by feng on 2017/4/4.
 */

public interface IDataAdapter {

    /**
     * @return
     */
    int getColumnCount();

    /**
     * @return
     */
    int getRowCount();
    /**
     * @return
     */
    View getItemView(int index, int viewType);
    void convertData(int row, int col, View view);
}
