package com.example.anticorruption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by alexlondon on 2/24/16.
 */
public class AddInstitutionActivity extends AppCompatActivity {
    Button addButton, clearButton, addImageButton;
    ImageView imageView;
    String imageString;
    AddInstitutionActivity mContext;
    String foundInstitution;

    boolean fromStory;

    final DatabaseHelper dh = new DatabaseHelper(this);

    final UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institution);

        mContext = this;

        final EditText institutionEt = (EditText) findViewById(R.id.institutionEt);
        final EditText addressEt = (EditText) findViewById(R.id.addressEt);
        final Spinner citySpinner = (Spinner) findViewById(R.id.citySpinner);
        final EditText managerEt = (EditText) findViewById(R.id.managerEt);
        final EditText phoneEt = (EditText) findViewById(R.id.phoneEt);
        final Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);

        addressEt.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);


        addButton = (Button) findViewById(R.id.addButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        addImageButton = (Button) findViewById(R.id.addImageButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, null, imageView, addImageButton);

        fromStory = getIntent().getBooleanExtra("FROM_STORY", false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        String[] institutionTypes = new String[]{"(Select a type)", "National Government", "Regional Government", "Local Government", "School", "Medical", "Police"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, institutionTypes);
        typeSpinner.setAdapter(typeAdapter);

        String[] citiesArray = new String[]{"(Select a city)", "Bălți", "Biruința", "Briceni", "Costești", "Cupcini", "Dondușeni", "Drochia",
                "Edineț", "Fălești", "Florești", "Frunză", "Ghindești", "Glodeni", "Lipcani", "Mărculești", "Ocnița",
                "Otaci", "Rîșcani", "Sîngerei", "Soroca", "Anenii Noi", "Bucovăț", "Căinari", "Călărași", "Căușeni", "Cornești",
                "Criuleni", "Hîncești", "Ialoveni", "Nisporeni", "Orhei", "Rezina", "Strășeni", "Șoldănești",
                "Ștefan Vodă", "Telenești", "Tighina (Bender)", "Ungheni", "Chișinău", "Cordu", "Cricova", "Durlești",
                "Sîngera", "Vadul lui Vodă", "Vatra", "Basarabeasca", "Cantemir", "Cahul", "Ceadîr-Lunga", "Cimișlia", "Comrat",
                "Iargara", "Leova", "Taraclia", "Tvardița", "Vulcănești", "Camenca", "Crasnoe", "Dnestrovsc", "Dubăsari",
                "Grigoriopol", "Rîbnița", "Maiac", "Slobozia", "Tiraspol", "Tiraspolul Nou"};
        Arrays.sort(citiesArray);
        final ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, citiesArray);
        citySpinner.setAdapter(citiesAdapter);

        final Intent goToInstitution = new Intent(mContext, InstitutionPopUpActivity.class);

        final DialogInterface.OnClickListener institutionDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        goToInstitution.putExtras(dh.getInstitution(InstitutionTable.INSTITUTION, foundInstitution));
                        mContext.startActivity(goToInstitution);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        imageView.setOnClickListener(um.imageButtonClicked);
        addImageButton.setOnClickListener(um.imageButtonClicked);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, institutionEt.getText().toString())) {
                    AlertDialog.Builder institutionExists = new AlertDialog.Builder(mContext);
                    institutionExists.setMessage("This institution already exists. Do you want to go to "
                            + institutionEt.getText().toString() + "'s page?")
                            .setPositiveButton("Yes", institutionDialogClickListener)
                            .setNegativeButton("No", institutionDialogClickListener)
                            .show();
                } else if (institutionEt.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please add an institution name")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (addressEt.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please add an address")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (typeSpinner.getSelectedItemPosition() == 0) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please select an institution type")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (citySpinner.getSelectedItemPosition() == 0) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please select a city")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else if (managerEt.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please add a manager")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (phoneEt.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Please add a phone number")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    dh.addInstitution(institutionEt.getText().toString(), addressEt.getText().toString(), citySpinner.getSelectedItem().toString(),
                            typeSpinner.getSelectedItem().toString(), imageString, managerEt.getText().toString(),
                            phoneEt.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserData.mStringInstitution = null;
                            if (fromStory) {
                                Intent intent = new Intent(mContext, AddStoryActivity.class);
                                intent.putExtra("INSTITUTION", institutionEt.getText().toString());
                                startActivity(intent);
                            } else {
                                goToInstitution.putExtras(dh.getInstitution(InstitutionTable.INSTITUTION, institutionEt.getText().toString()));
                                startActivity(goToInstitution);
                            }
                        }
                    });
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                institutionEt.setText("");
                addressEt.setText("");
                managerEt.setText("");
                phoneEt.setText("");
                typeSpinner.setSelection(0);
                imageString = null;
                imageView.setVisibility(View.GONE);
                addImageButton.setVisibility(View.VISIBLE);
            }
        });

        institutionEt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);

        institutionEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                UserData.mStringInstitution = institutionEt.getText().toString();
                if (!hasFocus) {
                    String institution = institutionEt.getText().toString();
                    foundInstitution = dh.isInstitutionAlreadyEntered(institution);
                    if (dh.isInstitutionAlreadyEntered(institution) != null && !institution.isEmpty()) {
                        View view = null;
                        try {
                            view = populateInstitutionFrame(dh.getInstitution(InstitutionTable.INSTITUTION, foundInstitution));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new AlertDialog.Builder(mContext)
                                .setView(view)
                                .setMessage("Is this the institution you want to add?")
                                .setPositiveButton("Yes", institutionDialogClickListener)
                                .setNegativeButton("No", institutionDialogClickListener)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            UserData.mStringInstitution = null;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public View populateInstitutionFrame(Bundle bundle) throws IOException {
        RelativeLayout titleBar;
        View convertView = getLayoutInflater().inflate(R.layout.institution_summary, null);

        titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);
        titleBar.setVisibility(View.GONE);
        final String institution = bundle.getString("INSTITUTION");
        final String address = bundle.getString("ADDRESS");
        final String logo = bundle.getString("LOGO");
        final int positive = Integer.valueOf(bundle.getString("POSITIVE"));
        final int negative = Integer.valueOf(bundle.getString("NEGATIVE"));


        TextView institutionTv = (TextView) convertView.findViewById(R.id.institution);
        TextView addressTv = (TextView) convertView.findViewById(R.id.address);
        ImageView logoIv = (ImageView) convertView.findViewById(R.id.logo);
        TextView positiveTv = (TextView) convertView.findViewById(R.id.positive);
        TextView negativeTv = (TextView) convertView.findViewById(R.id.negative);

        TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

        String addressString = "Address: " + address;
        SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
        addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String posString = universalMethods.getPercentage("pos", positive, negative) + "%";
        SpannableStringBuilder posSpan = new SpannableStringBuilder(posString);

        String negString = universalMethods.getPercentage("neg", positive, negative) + "%";
        SpannableStringBuilder negSpan = new SpannableStringBuilder(negString);

        assert logo != null;
        URL logoURL = new URL(logo);
        Bitmap logoBmp = BitmapFactory.decodeStream(logoURL.openConnection().getInputStream());

        institutionTv.setText(institution);
        addressTv.setText(addressSpan);
        logoIv.setImageBitmap(logoBmp);
        positiveTv.setText(posSpan);
        negativeTv.setText(negSpan);

        return convertView;
    }
}
