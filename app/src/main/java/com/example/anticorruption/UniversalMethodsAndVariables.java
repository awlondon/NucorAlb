/*This class holds all of the methods and variables
that are used by other classes in this app.
By making a class that holds these universal
methods and variables we are able to eliminate
redundancy and save on code clutter.*/

package com.example.anticorruption;

//These are all the external classes that are needed to run this class.
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2/16/16.
 */
public final class UniversalMethodsAndVariables {

   /* The upper part of a class always contains the variables that are needed throughout the class.
   Variables that begin with 'private' can only be accessed and used by the class. 'Public' variables
   can also be accessed and used from other classes.*/

    private View mViewDialogEditText;
    private ImageView mImageView;
    private Button mButtonImage;
    private AlertDialog.Builder mAlertDialogBuildAddImage;
    private Context mContext;
    public String mStringImage;
    private String mStringUsername;
    public List<Bundle> resultsListData;
    private String current_view;
    private LinearLayout highlightMenu;
    public final int PLACE_HOLDER = R.drawable.image_place_holder;
    public final int PLACE_HOLDER_ALT = R.drawable.com_facebook_close;
    List<Integer> mListIntegerTemp;
    List<Integer> mListIntegerFinal;
    public ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private DatabaseHelper dh;

    /*The following 4 methods are class initializers. Other classes create instances of this class within their class
    * in order to access this class's methods and variables. Some of the methods in this class require variable arguments
    * specific to the activity that is accessing these methods. For example, many of these methods need to know the 'mContext'
    * of the activity (i.e. the specific screen UI that you want to perform actions on). Other methods control the 'button menu'
    * which needs to know from what activity the app is moving from and which button to highlight.*/

    //Start class initializers
    public UniversalMethodsAndVariables() {

    }

    public UniversalMethodsAndVariables(Context mContext) {
        this.mContext = mContext;
        this.dh = new DatabaseHelper(mContext);
    }

    public UniversalMethodsAndVariables(Context mContext, String mStringUsername, ImageView mImageView, Button mButtonImage) {

        this.mContext = mContext;
        this.mImageView = mImageView;
        this.mButtonImage = mButtonImage;
        this.mStringUsername = mStringUsername;

        mViewDialogEditText = ((Activity)mContext).getLayoutInflater().inflate(R.layout.alert_dialogue_edit_text, null);

        mAlertDialogBuildAddImage = new AlertDialog.Builder(mContext);

        this.dh = new DatabaseHelper(mContext);
    }

    public UniversalMethodsAndVariables(Context context, List<Bundle> resultsListData, String current_view, LinearLayout highlightMenu) {
        this.mContext = context;
        this.resultsListData = resultsListData;
        this.current_view = current_view;
        this.highlightMenu = highlightMenu;
        this.dh = new DatabaseHelper(mContext);
    }
    //End class initializers

    /*The following method 'getTimeFromNow' compares the current date to a date added as an argument and returns
    * a String stating "how much time" ago the argument date was. This method is used to show the dates on stories,
    * wikis and forum posts.*/
    public String getTimeFromNow(String date) {
        //The variable constants below describe how many miliseconds (a milisecond = 1/1000 of a second) are in the given
        //unit of time.
        final long SECOND = 1_000;
        final long MINUTE = 60_000;
        final long HOUR = 3_600_000;
        final long DAY = 86_400_000;
        //The following variable constants describe how many days are in the given unit of time.
        final int WEEK = 7;
        final int MONTH = 30;
        final int YEAR = 365;

        //A timestamp is a java class that stores detailed time information.
        //'postedTime' is a Timestamp value of the date argument to be compared.
        //'now' is a Timstamp create from the system's current time.
        Timestamp postedTime = Timestamp.valueOf(date);
        Timestamp now = new Timestamp((new Date().getTime()));
        //The following variables contain the milliscond and day difference between these two TimeStamps.
        long compareToMillis = TimeUnit.MILLISECONDS.toMillis(now.getTime() - postedTime.getTime());
        long compareToDays = TimeUnit.MILLISECONDS.toDays(now.getTime() - postedTime.getTime());
        String result = null;

        //This if/else routine finds the most appropriate description of the time between the date argument and now.
        if (compareToMillis < MINUTE) {
            result = String.valueOf(compareToMillis / SECOND) + "s ago";
        } else if (compareToMillis > MINUTE && compareToMillis < HOUR) {
            result = String.valueOf(compareToMillis / MINUTE) + "m ago";
        } else if (compareToMillis > HOUR && compareToMillis < DAY) {
            result = String.valueOf(compareToMillis / HOUR) + "h ago";
        } else if (compareToMillis > DAY && compareToDays < WEEK) {
            result = String.valueOf(compareToMillis / DAY) + "d ago";
        } else if (compareToDays > WEEK && compareToDays < MONTH) {
            result = String.valueOf(compareToDays / WEEK) + "w ago";
        } else if (compareToDays > MONTH && compareToDays < YEAR) {
            result = String.valueOf(compareToDays / MONTH) + "m ago";
        } else if (compareToDays > YEAR) {
            result = String.valueOf(compareToDays / YEAR) + "y ago";
        }
        return result;
    }

    //'getPercentage' is a simple method is used to find the percentage of positive surveys and negative surveys
    //that are registered with an institution.
    public int getPercentage(String posOrNeg, float positive, float negative) {
        float value = 0f;
        switch (posOrNeg) {
            case "pos":
                value = (positive / (positive + negative)) * 100;
                break;
            case "neg":
                value = (negative / (positive + negative)) * 100;
                break;
        }
        return Math.round(value);
    }

    /*'getBitmapFromUrl' is a complex method that is used often in this app.
    * The method receives an argument of String for the url text needed to download
    * an image from the Internet. 'reqWidth' and 'reqHeight' are arguments containing
    * the width and height of the ImageView that is being targeted.*/
    public Bitmap getBitmapFromURL(String image, int reqWidth, int reqHeight) {
        //Rect is an object class that contains the lengths of the 4 sides of a rectangle.
        Rect rect = new Rect(0,0,0,0);
        //A BitmapFactory is a class that creates bitmaps.
        //The BitmapFactory.Options subclass allows you to change settings associated with a bitmap
        //and access properties of the bitmap.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //URLs must be used (instead of Strings) to send information to the Internet
        URL imageURL;
        //A try/catch routine must be applied to deal with errors when sending data outside of the app
        try {
            imageURL = new URL(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            //The following line downloads the bitmap from the URL and applies the bitmap info to the options variable.
            BitmapFactory.decodeStream(imageURL.openConnection().getInputStream(), rect, options);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //'inSampleSize' is used to decrease the quality of large images that are going to be shown in the UI in a
        //smaller ImageView. We decrease the quality in order to save on memory (the high quality is not needed if
        //the ImageView is smaller.
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream(), rect, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) {
        System.out.println("ImageView height: " + reqHeight);
        System.out.println("ImageView width: " + reqWidth);
        final int height = options.outHeight;
        System.out.println("Image height: " + height);
        final int width = options.outWidth;
        System.out.println("Image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize) > reqHeight && (halfWidth/inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        System.out.println("inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    //The following method first checks to see if the URL has the proper syntax, and then tries to find the file
    //on the Internet and test if it is actually a bitmap. We call 'checkURL' before calling 'getBitmapFromURL'
    //in order to make sure that exceptions are not thrown.
    public boolean checkURL(String string) {
        URL url;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Thus far unused method
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    //This method is actually an instance of 'DialogInterface.OnClickListener'. This is a complex Listener
    //that deals with adding an institution logo or a profile icon.
    public DialogInterface.OnClickListener imageDialogClickListener = new DialogInterface.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onClick(final DialogInterface dialog, int which) {
            final DialogInterface superDialog = dialog;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    EditText editText = (EditText) mViewDialogEditText.findViewById(R.id.urlEt);
                    mStringImage = editText.getText().toString();
                    editText.setText(null);
                    dialogSetImage(mStringImage, superDialog, dialog);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    dialog.cancel();
                    mAlertDialogBuildAddImage.setView(null);
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    GridView mGridView = new GridView(mContext);
                    mGridView.setPaddingRelative(5, 5, 5, 5);
                    mGridView.setNumColumns(3);
                    mGridView.setGravity(Gravity.CENTER);
                    mListIntegerTemp = new ArrayList<>();
                    mListIntegerFinal = new ArrayList<>();
                    if (UserData.mStringInstitution != null) {
                        Integer[] mIntegerArray = dh.getInstitutionsByQuery(UserData.mStringInstitution);
                        System.out.println("Adding mBundleArray to mListBundleArray");
                        Collections.addAll(mListIntegerTemp, mIntegerArray);
                        for (Integer mInteger : mListIntegerTemp) {
                            String mStringLogoURL = dh.getInstitution(InstitutionTable.ID, String.valueOf(mInteger)).getString("LOGO");
                            System.out.println("Got LogoURL string");
                            Recursive:
                            {
                                for (Integer integer : mListIntegerFinal) {
                                    String mStringLogoURL1 = dh.getInstitution(InstitutionTable.ID, String.valueOf(integer)).getString("LOGO");
                                    if (mStringLogoURL1 != null && !mStringLogoURL1.isEmpty() && mStringLogoURL.equals(mStringLogoURL1)) {
                                        System.out.println("Breaking out of Recursive");
                                        break Recursive;
                                    }
                                }
                                System.out.println("Added bundle");
                                mListIntegerFinal.add(mInteger);
                            }
                        }
                    }
                    if (mListIntegerFinal.isEmpty()){
                        Toast.makeText(mContext, "No similar logos found", Toast.LENGTH_SHORT).show();
                    } else {
                        final AlertDialog newDialog = new AlertDialog.Builder(mContext).setView(mGridView).create();
                        newDialog.show();
                        GridAdapterImageTitle mGridAdapter = new GridAdapterImageTitle(mContext, mListIntegerFinal);
                        mGridView.setAdapter(mGridAdapter);
                        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = dh.getInstitution(InstitutionTable.ID, String.valueOf(mListIntegerFinal.get(position)));
                                String mStringLogoURL = bundle.getString("LOGO");
                                dialogSetImage(mStringLogoURL, superDialog, superDialog);
                                newDialog.dismiss();
                            }
                        });
                    }

            }
        }
    };

    //The following method is closely associated with the OnClickListener instance above.
    //This method sets the downloaded bitmap to the given ImageView or alerts the user that the URL is not good.
    private void dialogSetImage(String mStringImage, final DialogInterface superDialog, DialogInterface dialog) {
        DatabaseHelper dh = new DatabaseHelper(mContext);
        if (checkURL(mStringImage)) {
            mImageView.setImageBitmap(getBitmapFromURL(mStringImage, mImageView.getWidth(), mImageView.getHeight()));
            mImageView.setVisibility(View.VISIBLE);
            if (mButtonImage !=null) {
                mButtonImage.setVisibility(View.GONE);
            } else {
                dh.updateUserIcon(mStringUsername, mStringImage);
            }
            dialog.dismiss();
            dialog.cancel();
        } else {
            mAlertDialogBuildAddImage.setView(null);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext)
                    .setTitle("Bad URL")
                    .setMessage("Do you want to try another URL or use a default image?")
                    .setPositiveButton("Use default", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mImageView.setVisibility(View.GONE);
                            if(mButtonImage !=null) {
                                mButtonImage.setVisibility(View.VISIBLE);
                            }
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Try another URL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                            AlertDialog alertDialog = (AlertDialog) superDialog;
                            alertDialog.show();
                        }
                    });
            alertBuilder.show();
        }
    }

    //This is an instance of the View.OnClickListener class. This instance shows an AlertDialog.Builder
    //that ask the user to add an image url, to browse similar institutions, or to browse internal icons that
    //can be used as a profile image.
    public View.OnClickListener imageButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mViewDialogEditText.getParent() != null)
                ((ViewGroup) mViewDialogEditText.getParent()).removeView(mViewDialogEditText);
            mAlertDialogBuildAddImage
                    .setTitle("Image URL ")
                    .setMessage("Add an image URL: ")
                    .setView(mViewDialogEditText)
                    .setPositiveButton("OK", imageDialogClickListener)
                    .setNegativeButton("Cancel", imageDialogClickListener);
            if (UserData.mStringInstitution != null) {
                mAlertDialogBuildAddImage.setNeutralButton("Browse Similar", imageDialogClickListener);
            } else {
                mAlertDialogBuildAddImage.setNeutralButton("Browse Icons", imageDialogClickListener);
            }
            mAlertDialogBuildAddImage.create();
            mAlertDialogBuildAddImage.show();
        }
    };

    //All four main activities - 'Institutions, Wiki, Forum, Profile' - and the home screen, have search capability
    //from the Toolbar. When the user enters a search query the following method is called to match the query to any
    //table values in the database.
    public void prepareResultsListData(String query) {
        DatabaseHelper dh = new DatabaseHelper(mContext);
        resultsListData = new ArrayList<>();

        Integer[] stories, wikis, posts, institutions;

        System.out.println(query);

        stories = dh.getStoriesByQuery(query);
        wikis = dh.getWikisByQuery(query);
        posts = dh.getPostsByQuery(query);
        institutions = dh.getInstitutionsByQuery(query);
        if (stories != null) for (Integer integer : stories) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", integer);
            bundle.putString("TABLE_NAME", StoryTable.TABLE_NAME);
            resultsListData.add(bundle);
        }
        if (wikis != null) for (Integer integer : wikis) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", integer);
            bundle.putString("TABLE_NAME", WikiTable.TABLE_NAME);
            resultsListData.add(bundle);
        }
        if (posts != null) for (Integer integer : posts) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", integer);
            bundle.putString("TABLE_NAME", PostTable.TABLE_NAME);
            resultsListData.add(bundle);
        }
        if (institutions != null) for (Integer integer : institutions) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", integer);
            bundle.putString("TABLE_NAME", InstitutionTable.TABLE_NAME);
            resultsListData.add(bundle);
        }

    }

    //When list items are clicked from the ListView on the home screen or during a search, the following method
    //is called. This method takes as arguments an int describing which category of data the view is, the position of
    //the view in the ListView, and the List of Bundles that hold the data for every view in the ListView.
    //The method then opens the correct PopUpActivity associated with the list item.
    public void openPopUpActivity(int category, int position, List<Bundle> data) {
        Intent intent;
        final int storySummary = 0;
        final int wikiSummary = 1;
        final int postSummary = 2;
        final int institutionSummary = 3;
        Bundle tempBundle = data.get(position);
        Bundle bundle;
        boolean isFinalBundle = false;
        bundle = tempBundle;
        if (tempBundle.containsKey("TABLE_NAME")) {
            System.out.println("Bundle is complete");
            isFinalBundle = true;
        }

        switch (category) {
            case storySummary:
                if (!isFinalBundle) {
                    bundle = dh.getStory(StoryTable.ID, tempBundle.getString("ID"));
                }
                intent = new Intent(mContext, StoryPopUpActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case wikiSummary:
                if (!isFinalBundle) {
                    bundle = dh.getWiki(WikiTable.ID, tempBundle.getString("ID"));
                }
                intent = new Intent(mContext, WikiPopUpActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case postSummary:
                if (!isFinalBundle) {
                    bundle = dh.getPost(PostTable.ID, tempBundle.getString("ID"));
                }
                intent = new Intent(mContext, ThreadPopUpActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case institutionSummary:
                if (!isFinalBundle) {
                    bundle = dh.getInstitution(InstitutionTable.ID, tempBundle.getString("ID"));
                }
                intent = new Intent(mContext, InstitutionPopUpActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }

    //The following method controls which Activity to open when an item on the 'button menu' is clicked.
    public void openActivity(View view) {
        if (view == ((Activity) mContext).findViewById(R.id.institutionsLayout) && !current_view.equals("mIntegerArrayInstitutions")) {
            Intent intent = new Intent(mContext, InstitutionsActivity.class);
            mContext.startActivity(intent);
        } else
        if (view == ((Activity) mContext).findViewById(R.id.wikiLayout) && !current_view.equals("wiki")) {
            Intent intent = new Intent(mContext, WikiActivity.class);
            mContext.startActivity(intent);
        } else
        if (view == ((Activity) mContext).findViewById(R.id.forumLayout) && !current_view.equals("forum")) {
            Intent intent = new Intent(mContext, ForumActivity.class);
            mContext.startActivity(intent);
        } else
        if (view == ((Activity) mContext).findViewById(R.id.profileLayout) && !current_view.equals("profile")) {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
    }

    //Sets the username and password to null, end all other activities in the background, and reopens MainActivity
    //which will show the initial login screen.
    public void logout() {
        UserData.username = null;
        UserData.password = null;
        Intent intent = new Intent (mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        mContext.startActivity(intent);
    }

    //The following 5 methods control common icon functionality on the mToolbar.
    public void addStory(String institution) {
        Intent intent = new Intent(mContext, AddStoryActivity.class);
        if (institution!=null) {
            intent.putExtra("INSTITUTION", institution);
        }
        mContext.startActivity(intent);
    }

    public void addInstitution() {
        Intent intent = new Intent(mContext, AddInstitutionActivity.class);
        mContext.startActivity(intent);
    }

    public void addWiki() {
        Intent intent = new Intent(mContext, WikiPopUpActivity.class);
        intent.putExtra("NEW", true);
        mContext.startActivity(intent);
    }

    public void share(String type, String name) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("I want to share this ").append(type).append(" with you called ").append(name).append(". Click the link to see: ");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, (Serializable) stringBuilder);
        intent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(intent, "Share this app with..."));
    }

    public void sendEmail (String email) {
        ShareCompat.IntentBuilder.from((Activity) mContext)
        .setType("message/rfc822")
        .addEmailTo(email)
        .setSubject(null)
        .setText(null)
        //.setHtmlText(body); //If you are using HTML in your body text
        .setChooserTitle("Send email...")
        .startChooser();
    }

    //Controls the background color of the Activity on the 'button menu' that is now being viewed.
    public void setHighlightMenu(int linearLayout) {
        highlightMenu = (LinearLayout) ((Activity) mContext).findViewById(linearLayout);
        highlightMenu.setBackgroundColor(ContextCompat.getColor(mContext, R.color.menuHighlightColor));
    }

    //When the user clicks and holds (a LongClick) on a ListView item on the home screen, the user
    //is altered to the option of removing the item from the ListView.
    public static class RemoveListViewItem implements AdapterView.OnItemLongClickListener {
        private Context context;
        private List<Bundle> listData;
        private ListAdapterFeed listAdapter;

        public RemoveListViewItem (Context context, List<Bundle> listData, ListAdapterFeed listAdapter) {
            this.context = context;
            this.listData = listData;
            this.listAdapter = listAdapter;
        }
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
            view.setAlpha(.5f);
            new AlertDialog.Builder(context).setMessage("Remove?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listData.remove(position);
                            listAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setAlpha(1f);
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
    }

    //A multi-purpose method that returns all the children in a ViewGroup as an Object Array (Object[])
    public Object[] getChildrenArray (ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        Object result[]  = new Object[count];
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            result[i] = view;
        }
        return result;
    }

    public String[] searchList() {
        DatabaseHelper dh = new DatabaseHelper(mContext);
        ArrayList<String> mArrayList = new ArrayList<>();

        String[] institutions = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, null, false);
        String[] cities = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.CITY, null, false);
        String[] types = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.TYPE, null, false);
        String[] addresses = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.ADDRESS, null, false);
        String[] managers = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.MANAGER, null, false);

        String[] tags = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.TAGS, null, false);
        String[] categories = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.CATEGORY, null, false);
        String[] titles = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.TITLE, null, false);

        String[] threads = dh.getStringArray(PostTable.TABLE_NAME, PostTable.THREAD, null, false);
        String[] post_titles = dh.getStringArray(PostTable.TABLE_NAME, PostTable.TITLE, null, false);

        Collections.addAll(mArrayList, institutions);
        Collections.addAll(mArrayList, cities);
        Collections.addAll(mArrayList, types);
        Collections.addAll(mArrayList, addresses);
        Collections.addAll(mArrayList, managers);
        Collections.addAll(mArrayList, tags);
        Collections.addAll(mArrayList, categories);
        Collections.addAll(mArrayList, titles);
        Collections.addAll(mArrayList, threads);
        Collections.addAll(mArrayList, post_titles);

        return (String[])mArrayList.toArray();
    }

    public String getStringFromRawResource(int mRawResource) throws IOException {
        InputStream mInputStream = mContext.getResources().openRawResource(mRawResource);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
        String mString = "";
        while (bufferedReader.readLine() != null) {
            mString += bufferedReader.readLine();
        }
        return mString;
    }
}