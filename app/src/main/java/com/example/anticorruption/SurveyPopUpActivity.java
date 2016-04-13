package com.example.anticorruption;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.HashMap;

public class SurveyPopUpActivity extends AppCompatActivity {

    private Context context;

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();
    DatabaseHelper dh = new DatabaseHelper(this);

    private HashMap<CheckBox, String> checkBoxes;

    Integer id;
    String institution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        final Bundle bundle = getIntent().getExtras();
        Integer layout = bundle.getInt("LAYOUT");
        setContentView(layout);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        id = getIntent().getIntExtra("ID", -1);
        institution = getIntent().getStringExtra("INSTITUTION");

        if (layout == R.layout.survey_1)
            displaySurvey1();
        else if (layout == R.layout.survey_1a)
            displaySurvey1a();
        else if (layout == R.layout.survey_2)
            displaySurvey2();
        else if (layout == R.layout.survey_2a)
            displaySurvey2a();
        else
            displaySurvey3();

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

    public void displaySurvey1() {
        Button noButton = (Button) findViewById(R.id.noButton);
        Button yesButton = (Button) findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_1a);
                finish();
                startActivity(intent);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_2);
                finish();
                startActivity(intent);
            }
        });
    }

    public void displaySurvey1a() {
        Button continueButton = (Button) findViewById(R.id.continueButton);

        CheckBox abuse = (CheckBox) findViewById(R.id.appointmentOption);
        CheckBox blackmail = (CheckBox) findViewById(R.id.blackmailOption);
        CheckBox bribery = (CheckBox) findViewById(R.id.briberyOption);
        CheckBox embezzlement = (CheckBox) findViewById(R.id.embezzlementOption);
        CheckBox extortion = (CheckBox) findViewById(R.id.extortionOption);
        CheckBox fraud = (CheckBox) findViewById(R.id.fraudOption);
        CheckBox nepotism = (CheckBox) findViewById(R.id.nepotismOption);
        CheckBox other = (CheckBox) findViewById(R.id.otherOption);

        checkBoxes = new HashMap<>();
        checkBoxes.put(abuse, InstitutionTable.ABUSE_OF_DISCRETION);
        checkBoxes.put(blackmail, InstitutionTable.BLACKMAIL);
        checkBoxes.put(bribery, InstitutionTable.BRIBERY);
        checkBoxes.put(embezzlement, InstitutionTable.EMBEZZLEMENT);
        checkBoxes.put(extortion, InstitutionTable.EXTORTION);
        checkBoxes.put(fraud, InstitutionTable.FRAUD);
        checkBoxes.put(nepotism, InstitutionTable.NEPOTISM);
        checkBoxes.put(other, InstitutionTable.OTHER_CORRUPTION);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox option : checkBoxes.keySet()) {
                    if (option.isChecked())
                        dh.updateInstitutionCorruptionType(id, checkBoxes.get(option));
                }
                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_2);
                finish();
                startActivity(intent);
            }
        });
    }

    public void displaySurvey2() {
        Button noButton = (Button) findViewById(R.id.noButton);
        Button yesButton = (Button) findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_2a);
                finish();
                startActivity(intent);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_3);
                finish();
                startActivity(intent);
            }
        });
    }

    public void displaySurvey2a() {
        Button continueButton = (Button) findViewById(R.id.continueButton);
        final CheckBox appointmentOption = (CheckBox) findViewById(R.id.appointmentOption);
        final CheckBox documentOption = (CheckBox) findViewById(R.id.documentOption);
        final CheckBox permitOption = (CheckBox) findViewById(R.id.permitOption);

        final Spinner appointmentSpinner = (Spinner) findViewById(R.id.appointmentSpinner);
        final Spinner documentSpinner = (Spinner) findViewById(R.id.documentSpinner);
        final Spinner permitSpinner = (Spinner) findViewById(R.id.permitSpinner);

        appointmentSpinner.setEnabled(false);
        documentSpinner.setEnabled(false);
        permitSpinner.setEnabled(false);

        appointmentOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appointmentSpinner.isEnabled())
                    appointmentSpinner.setEnabled(true);
                else
                    appointmentSpinner.setEnabled(false);
            }
        });

        documentOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!documentSpinner.isEnabled())
                    documentSpinner.setEnabled(true);
                else
                    documentSpinner.setEnabled(false);
            }
        });

        permitOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permitSpinner.isEnabled())
                    permitSpinner.setEnabled(true);
                else
                    permitSpinner.setEnabled(false);
            }
        });

        String[] appointmentArray = new String[]{"< 1 hour", "1-2 hours", "2-4 hours", "4-8 hours", "> 8 hours"};
        ArrayAdapter<String> appointmentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appointmentArray);
        appointmentSpinner.setAdapter(appointmentAdapter);

        String[] documentArray = new String[]{"< 1 day", "1-5 days", "1-2 weeks", "2-4 weeks", "> 4 weeks"};
        ArrayAdapter<String> documentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, documentArray);
        documentSpinner.setAdapter(documentAdapter);

        String[] permitArray = new String[]{"< 1 week", "1-2 weeks", "2-4 weeks", "1-2 months", "2-4 months", "4-8 months", "8-12 months", "> 1 year"};
        ArrayAdapter<String> permitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, permitArray);
        permitSpinner.setAdapter(permitAdapter);

        final HashMap<String, Integer> appointmentMap = new HashMap<>();
        for (int i = 0; i < appointmentArray.length; i++) {
            appointmentMap.put(appointmentArray[i], i + 1);
        }

        final HashMap<String, Integer> documentMap = new HashMap<>();
        for (int i = 0; i < documentArray.length; i++) {
            documentMap.put(documentArray[i], i + 1);
        }

        final HashMap<String, Integer> permitMap = new HashMap<>();
        for (int i = 0; i < permitArray.length; i++) {
            permitMap.put(permitArray[i], i + 1);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointmentOption.isChecked())
                    dh.updateInstitutionWaitTime(id, InstitutionTable.APPOINTMENT_WAIT_TIME,
                            InstitutionTable.APPOINTMENT_ENTRIES, appointmentMap, (String) appointmentSpinner.getSelectedItem());
                if (documentOption.isChecked())
                    dh.updateInstitutionWaitTime(id, InstitutionTable.DOCUMENT_WAIT_TIME,
                            InstitutionTable.DOCUMENT_ENTRIES, documentMap, (String) documentSpinner.getSelectedItem());
                if (permitOption.isChecked())
                    dh.updateInstitutionWaitTime(id, InstitutionTable.PERMIT_WAIT_TIME,
                            InstitutionTable.PERMIT_ENTRIES, permitMap, (String) permitSpinner.getSelectedItem());

                Intent intent = new Intent(context, SurveyPopUpActivity.class);
                intent.putExtra("ID", id)
                        .putExtra("INSTITUTION", institution)
                        .putExtra("LAYOUT", R.layout.survey_3);
                finish();
                startActivity(intent);
            }
        });
    }

    public void displaySurvey3() {
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        Button finishButton = (Button) findViewById(R.id.finishButton);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer sbValue = seekBar.getProgress();
                if (sbValue > 5) {
                    sbValue -= 6;
                    dh.updateInstitutionRating(id, InstitutionTable.POSITIVE, sbValue);
                } else if (sbValue < 5) {
                    switch (sbValue) {
                        case 1:
                            sbValue = 4;
                            break;
                        case 2:
                            sbValue = 3;
                            break;
                        case 3:
                            sbValue = 2;
                            break;
                        case 4:
                            sbValue = 1;
                            break;
                        default:
                            break;
                    }
                    dh.updateInstitutionRating(id, InstitutionTable.NEGATIVE, sbValue);
                }
                dh.updateUserFinishedSurveys(UserData.username, id);
                Bundle bundle = dh.getInstitution(InstitutionTable.ID, String.valueOf(id));
                Intent intent = new Intent(context, InstitutionPopUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }

}
