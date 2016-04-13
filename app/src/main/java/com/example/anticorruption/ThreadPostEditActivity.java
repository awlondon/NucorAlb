package com.example.anticorruption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Date;

/**
 * Created by alexlondon on 2/26/16.
 */
public class ThreadPostEditActivity extends AppCompatActivity {

    public android.support.v7.widget.Toolbar toolbar;

    public DatabaseHelper dh = new DatabaseHelper(this);
    public String previous;

    Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_post_edit);

        context = this;

        final String thread = getIntent().getStringExtra("THREAD");

        previous = String.valueOf(getIntent().getIntExtra("PREVIOUS", -1));

        if (previous.equals("-1")) {
            previous = null;
        }

        final EditText title = (EditText) findViewById(R.id.titleEdit);
        final EditText content = (EditText) findViewById(R.id.contentEdit);
        title.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        content.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        ImageView send = (ImageView) findViewById(R.id.sendIv);
        ImageView cancel = (ImageView) findViewById(R.id.cancel);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        final EditText threadEt = new EditText(this);
        if (thread==null) {
            threadEt.setHint("(Thread Name)");
            threadEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
            threadEt.setBackgroundColor(Color.WHITE);
            threadEt.setHintTextColor(title.getHintTextColors());
            threadEt.setPaddingRelative(title.getPaddingStart(), title.getPaddingTop(), title.getPaddingEnd(), title.getPaddingBottom());
            threadEt.setLayoutParams(title.getLayoutParams());
            mainLayout.addView(threadEt, 0);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().isEmpty() && !content.getText().toString().isEmpty()) {
                    String finalThread;
                    if (thread!=null){
                        finalThread = thread;
                    } else {
                        finalThread = threadEt.getText().toString();
                    }
                    dh.addPost(title.getText().toString(), content.getText().toString(), UserData.username, finalThread, previous, 0, 0, String.valueOf(new java.sql.Timestamp(new Date().getTime())));
                    Intent intent;
                    if (finalThread.contains("Story")) {
                        intent = new Intent(context, StoryPopUpActivity.class);
                        intent.putExtras(dh.getStory(StoryTable.ID, finalThread.substring(5)));
                    } else {
                        intent = new Intent(context, ThreadPopUpActivity.class);
                        intent.putExtras(dh.getLatestPost());
                    }
                    startActivity(intent);
                    finish();
                } else {
                    new AlertDialog.Builder(context).setMessage("Please complete both the title and comment").show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (thread!=null && thread.contains("Story")) {
                    intent = new Intent(context, StoryPopUpActivity.class);
                    intent.putExtras(dh.getStory(StoryTable.ID, thread.substring(5)));
                    startActivity(intent);
                }
                else if (thread!=null){
                    intent = new Intent(context, ThreadPopUpActivity.class);
                    intent.putExtras(dh.getPost(PostTable.THREAD, thread));
                    startActivity(intent);
                }
                finish();
            }
        });

    }
}
