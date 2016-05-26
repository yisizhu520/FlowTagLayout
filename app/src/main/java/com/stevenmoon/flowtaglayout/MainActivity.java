package com.stevenmoon.flowtaglayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    WJFlowLayout flowLayout;
    FlowLayout flowLayout2;
    View child1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowLayout = (WJFlowLayout) findViewById(R.id.flow1);
        flowLayout2 = (FlowLayout) findViewById(R.id.flow2);
        child1 =  findViewById(R.id.child1);
    }

    int spacing = 0;
    boolean increase = true;

    public void addNewTag(View v){
        TextView textView = new TextView(this);
//        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 15;
        lp.rightMargin = 15;
        lp.topMargin = 15;
        lp.bottomMargin = 15;
        textView.setBackgroundColor(Color.GRAY);
        textView.setText("New Item");

        LinearLayout ll = new LinearLayout(this);

        flowLayout2.addView(textView,lp);
    }

    public void refresh(View v){
        if(increase){
            spacing += 8;
            if(spacing == 48){
                increase = false;
            }
        }else{
            spacing -= 8;
            if(spacing == 0){
                increase = true;
            }
        }
        flowLayout.setSpacing(spacing,spacing);
    }
}
