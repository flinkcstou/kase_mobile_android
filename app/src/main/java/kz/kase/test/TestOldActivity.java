package kz.kase.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import kz.kase.terminal.R;

public class TestOldActivity extends AppCompatActivity {
    private String TAG = TestOldActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_old);



        Button reload = findViewById(R.id.button);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newView();
            }
        });

    }
    public void newView(){
        Intent myIntent = new Intent(this, TestNewActivity.class);
        this.startActivity(myIntent);
    }
}
