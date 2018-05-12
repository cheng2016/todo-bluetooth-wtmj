package com.wistron.swpc.android.WiTMJ.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;

/**
 * Created by WH1604025 on 2016/4/29.
 */
public class FlowTextView extends FrameLayout {

    private TextView fowTv;
    private ImageView deleteImg;
    private Context context;
    private FlowLayout fowLayout;

    private String text;

    public FlowTextView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.fow_textview, this);
        fowTv = (TextView) findViewById(R.id.fowTv);
        deleteImg = (ImageView) findViewById(R.id.deleteImg);
    }

    public FlowTextView(Context context, boolean isEdit) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.fow_textview, this);
        fowTv = (TextView) findViewById(R.id.fowTv);
        deleteImg = (ImageView) findViewById(R.id.deleteImg);
        if(isEdit){
            deleteImg.setVisibility(View.VISIBLE);
        }else{
            deleteImg.setVisibility(View.GONE);
        }
    }

    public FlowTextView(Context context, boolean isEdit, FlowLayout fowLayout) {
        super(context);
        this.context = context;
        this.fowLayout = fowLayout;
        LayoutInflater.from(context).inflate(R.layout.fow_textview, this);
        fowTv = (TextView) findViewById(R.id.fowTv);
        deleteImg = (ImageView) findViewById(R.id.deleteImg);
        if(isEdit){
            deleteImg.setVisibility(View.VISIBLE);
        }else{
            deleteImg.setVisibility(View.GONE);
        }
    }

    public void setText(String text){
        this.text = text;
        fowTv.setText(text);
    }

    public void setOnClickListener(){
        fowTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fowLayout==null)return;
                    fowLayout.removeView(FlowTextView.this);
                    fowLayout.getList().remove(text);
                    for (int i = 0; i < fowLayout.getParticipantsList().size(); i++) {
                        if(TextUtils.isEmpty(fowLayout.getParticipantsList().get(i).getUsername())){
                            fowLayout.getParticipantsList().remove(i);
                            continue;
                        }

                        if(fowLayout.getParticipantsList().get(i).getUsername().equals(text)){
                            fowLayout.getParticipantsList().remove(i);
                            break;
                        }
                    }
                }
        });
    }

    public FlowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
