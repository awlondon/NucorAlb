package com.example.anticorruption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;


public class AddStoryActivity extends AppCompatActivity {
    final String currentTime = String.valueOf(new java.sql.Timestamp(new Date().getTime()));
    EditText addStoryEt;
    Spinner corruptionTypeSpinner, institutionSpinner;
    Button reportButton, clearButton;
    String institution;

    Context context;

    final DatabaseHelper dh = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        context = this;

        if (getIntent().getStringExtra("INSTITUTION") != null){
            institution = getIntent().getStringExtra("INSTITUTION");
        }

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Main layout content
        addStoryEt = (EditText) findViewById(R.id.addStoryEt);
        corruptionTypeSpinner = (Spinner) findViewById(R.id.corruptionTypeSpinner);
        institutionSpinner = (Spinner) findViewById(R.id.institutionSpinner);
        reportButton = (Button) findViewById(R.id.reportButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        //Add items to corruptionTypeSpinner
        String[] corruptionTypes = new String[]{"(Select a type)", "Abuse of Discretion", "Blackmail",
                "Bribery", "Embezzlement", "Extortion", "Fraud", "Nepotism", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, corruptionTypes);
        corruptionTypeSpinner.setAdapter(typeAdapter);

        String institutions[] = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, new String[]{"(Select an institution)", "Add a new institution +"}, false);
        System.out.println("Just received institution names...");
        ArrayAdapter<String> institutionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, institutions);
        institutionSpinner.setAdapter(institutionAdapter);

        if (institution != null) {
            for (int i = 0; i < institutions.length; i++) {
                if (institutions[i].equals(institution)){
                    institutionSpinner.setSelection(i);
                }
            }
        }

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(context, AddInstitutionActivity.class);
                    intent.putExtra("FROM_STORY", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addStoryEt.getText().toString().isEmpty() && institutionSpinner.getSelectedItemPosition() != 0 && corruptionTypeSpinner.getSelectedItemPosition() != 0) {
                    new AlertDialog.Builder(context)
                            .setMessage("This corruption story will be reported anonymously. None of your user data, IP address, or other identifying information will be associated with this entry. Specific times, dates and locations will be removed by our administrators before publishing. Are you sure you want to report this corruption story?")
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dh.addStory(addStoryEt.getText().toString(),
                                            institutionSpinner.getSelectedItem().toString(),
                                            corruptionTypeSpinner.getSelectedItem().toString(),
                                            currentTime);
                                    finish();
                                    openAddedStory();
                                    System.out.println("Story successfully loaded!");
                                }
                            })
                            .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(context).setMessage("Please complete all of the fields").show();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to reset everything?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addStoryEt.clearComposingText();
                                corruptionTypeSpinner.setSelection(0);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openAddedStory() {
        Bundle bundle = dh.getStory(StoryTable.CONTENT, addStoryEt.getText().toString());
        Intent intent = new Intent(this, StoryPopUpActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
