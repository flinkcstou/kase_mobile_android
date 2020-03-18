package kz.kase.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kz.kase.fixtablelayout.FixTableLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.kase.terminal.R;
import kz.kase.terminal.adapter.TableInstrumentAdapter;

public class TestNewActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    public List<String> title = Arrays.asList("title1","title2","title3","title4","title5","title6","title7", "title8","title9","title1","title2","title3","title4","title5");
    public List<List<String>> data = new ArrayList<>();

    int currentPage = 1;
    int totalPage = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        for (int i = 0; i < 9; i++) {
            data.add(Arrays.asList("KZTK","data1","data2","data3","data4","data5","data6","data7", "data8","title1","title2","title3","title4","title5"));
        }

        FixTableLayout fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout1);
        //fixTableLayout.setAdapter(new FixTableAdapter(title,data, getBaseContext()));
        fixTableLayout.setAdapter(new TableInstrumentAdapter(getBaseContext(), "1234"));

        //fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout2);
        //fixTableLayout.setAdapter(new TableInstrumentAdapter(getBaseContext(), "12345"));
        //fixTableLayout.setAdapter(new FixTableAdapter(title,data, getBaseContext()));

//        fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout3);
//        fixTableLayout.setAdapter(new TableInstrumentAdapter(getBaseContext(), "12346"));

        Button reload = findViewById(R.id.button);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newView();
            }
        });
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                data.clear();
//                Log.d(TAG, "dataUpdate");
//                for (int i = 0; i < 100; i++) {
//                    data.add(Arrays.asList("KZTK Up","data1 Up","data2 Up","data3 Up","data4 Up","data5 Up","data6 Up","data7 Up", "data8 Up"));
//                }
//                fixTableAdapter.setData(data);
//                fixTableAdapter.setTitle(Arrays.asList("title1 Up","title2 Up","title3 Up","title4 Up","title5 Up","title6 Up","title7 Up", "title8 Up","title9 Up"));
//                fixTableLayout.dataUpdate();
//            }
//        }, 5000);


//        fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout2);
//        fixTableAdapter = new FixTableAdapter(title,data, getBaseContext());
//        fixTableLayout.setAdapter(fixTableAdapter);
//
//        fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout3);
//        fixTableAdapter = new FixTableAdapter(title,data, getBaseContext());
//        fixTableLayout.setAdapter(fixTableAdapter);



//        fixTableLayout.enableLoadMoreData();
//
//        fixTableLayout.setLoadMoreListener(new ILoadMoreListener() {
//            @Override
//            public void loadMoreData(final Message message) {
//                Log.i("feng"," Load next 50 --- ");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (currentPage <= totalPage) {
//                            for (int i = 0; i < 50; i++) {
//                                data.add(Arrays.asList("KZTK " + i,"data1","data2","data3","data4","data5","data6","data7", "data8"));
//                            }
//                            currentPage++;
//                            message.arg1 = 50;
//                        } else {
//                            message.arg1 = 0;
//                        }
//                        message.sendToTarget();
//                    }
//                }).start();
//            }
//        });
    }
    public void newView(){
        Intent myIntent = new Intent(this, TestOldActivity.class);
        this.startActivity(myIntent);
    }
}
