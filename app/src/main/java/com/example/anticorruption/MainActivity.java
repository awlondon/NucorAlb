/*This Activity is opened when the App is first opened. There are three parts: 1) a login screen (with an option
* to register as a user); 2) a 'new user information' ViewFlipper that explains what the app is for, what it does,
* and certain features and buttons that are not so intuitive; 2) the home screen, which shows the feed of new
* app items, the toolbar, and the button menu.*/

package com.example.anticorruption;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //The following int variables control how many items are displayed on the home screen feed
    int standardFeedLoad = 5;
    int numberOfStories = standardFeedLoad;
    int numberOfWikis = standardFeedLoad;
    int numberOfPosts = standardFeedLoad;
    int numberOfInstitutions = standardFeedLoad;

    //Context describes information about this Activity to be used by other classes
    public Context mContext = this;
    //The following 4 variables are used in the ViewFlipper screen
    private float lastX;
    private ViewFlipper mViewFlipper;
    private boolean newUser;
    private RadioGroup mRadioGroup;
    //SearchView is hidden in the toolbar
    SearchView searchView;

    //To populate a ListView you need 3 items: an adapter, the ListView widget, and data (list of items)
    ListAdapterFeed mListAdapterFeed;
    ListView mListViewFeed;
    List<Bundle> mListDataFeed;

    ListAdapterResults mListAdapterResults;
    ListView mListViewResults;
    List<Bundle> mListBundleResults;

    //content_view String is used exclusively in the 'openActivity' method
    String content_view = "home";

    //The following line creates a UniversalMethodsAndVariables object that can be used to access the common variables and methods
    //This specific object uses a constructor with 4 arguments in order to access the methods related to the SearchView
    //and 'openActivity' method.
    final UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, mListBundleResults, content_view, null);

    //The DatabaseHelper class contains methods that interact with the database
    final DatabaseHelper dh = new DatabaseHelper(this);

    ProgressBar mProgressBar;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //When the Activity is created the computer checks to see if a screen already exists in the background
        super.onCreate(savedInstanceState);

        //A Thread is an object that performs tasks in the background, or not in the User Interface (UI)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    addPreliminaryData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //If/else routine that checks if the user is logged in or not
        if (UserData.username == null) {
            checkLogin();
        } else {
            finishOnCreate();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates a layout that describes the items to display on the toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Controls what happens when a toolbar item is clicked
        int id = item.getItemId();
        if (id == R.id.addStory) {
            um.addStory(null);
        } else if (id == R.id.addInstitution) {
            um.addInstitution();
        } else if (id == R.id.addWiki) {
            um.addWiki();
        } else if (id == R.id.share) {
            um.share("app", getResources().getString(R.string.app_name));
        } else if (id == R.id.logout) {
            um.logout();
        }

        //When the search icon is clicked all the toolbar items' visibility are set to GONE and the
        //SearchView widget appears. The 'back to home' icon appears on the left side of the toolbar.
        //Animations control the UI transition. When the 'home' button is clicked the original toolbar
        //items reappear.
        else if (id == android.R.id.home) {
            ListView lvFeed = (ListView) super.findViewById(R.id.lvFeed);
            lvFeed.setVisibility(View.VISIBLE);
            lvFeed.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            lvFeed.animate().start();

            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setVisibility(View.VISIBLE);
            button_menu.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            button_menu.animate().start();

            searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.from_scale_x_right));
            searchView.animate().start();

            mToolbar = (Toolbar) findViewById(R.id.tool_bar);
            mToolbar.setLogo(R.drawable.nucoralb_logo_white_simple_text_24dp);
            setSupportActionBar(mToolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            searchView.setVisibility(View.GONE);
            lvResults.setVisibility(View.GONE);

        } else if (id == R.id.searchAll) {
            ListView lvFeed = (ListView) super.findViewById(R.id.lvFeed);
            lvFeed.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            lvFeed.animate().start();

            mToolbar.getMenu().removeItem(R.id.searchAll);
            mToolbar.getMenu().removeItem(R.id.addStory);
            mToolbar.getMenu().removeItem(R.id.addInstitution);
            mToolbar.getMenu().removeItem(R.id.addWiki);
            mToolbar.getMenu().removeItem(R.id.share);
            mToolbar.getMenu().removeItem(R.id.logout);
            mToolbar.setLogo(null);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.to_scale_x_left));
            searchView.setVisibility(View.VISIBLE);
            searchView.animate().start();
            searchView.setIconified(false);

            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            button_menu.animate().start();
            button_menu.setVisibility(View.GONE);
            lvFeed.setVisibility(View.GONE);

            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            lvResults.setAlpha(1.00f);
            lvResults.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    //This method is called to gather the newest items from the database
    private void prepareFeedListData() {
        DatabaseHelper dh = new DatabaseHelper(this);
        mListDataFeed = new ArrayList<>();

        // Adding header data
        Bundle[] stories, wikis, posts, institutions;

        stories = dh.getNewStories(numberOfStories);
        wikis = dh.getNewWikis(numberOfWikis);
        posts = dh.getPopularPosts(numberOfPosts);
        institutions = dh.getPopularInstitutions(numberOfInstitutions);

        mListDataFeed.addAll(Arrays.asList(stories).subList(0, stories.length));

        mListDataFeed.addAll(Arrays.asList(wikis).subList(0, wikis.length));

        mListDataFeed.addAll(Arrays.asList(posts).subList(0, posts.length));

        mListDataFeed.addAll(Arrays.asList(institutions).subList(0, institutions.length));

        Collections.shuffle(mListDataFeed);

    }

    public void openActivity(View view) {
        um.openActivity(view);
    }

    public void addPreliminaryData() throws IOException {
        addForumPosts();
        addManuallyCodedInstitutions();
        addPublicNotaries();
        addStories();
        addUsers();
        addWikis();
        //addInstitutionsFromFile(R.raw.public_schools);
        //addInstitutionsFromFile(R.raw.public_notaries);
        //Toast.makeText(mContext, "Database is finished updating", Toast.LENGTH_SHORT).show();
    }

    public void addForumPosts(){
        if (!dh.isInTable(PostTable.TABLE_NAME, PostTable.TITLE, "Battling Corruption")) {
            System.out.println("Just added a forum post...");
            dh.addPost("Battling Corruption", "You need to stand up for your rights! Don't give in to their corrupt demands.",
                    "KatieCue21", "Battling Corruption", null, 21, 6, String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(PostTable.TABLE_NAME, PostTable.TITLE, "Getting a driver's license")) {
            System.out.println("Just added a forum post...");
            dh.addPost("Getting a driver's license", "You have to go to the right driving school. Some are not corrupt, such as these:",
                    "MoldovaGuy2", "Getting a driver's license", null, 54, 2, String.valueOf(new Timestamp(new Date().getTime())));
        }
    }

    public void addUsers(){
        if (!dh.isInTable(UserTable.TABLE_NAME, UserTable.USERNAME, "admin")) {
            System.out.println("Just added a user...");
            dh.addUser("admin", "lonale!97", "Alex", "London", null,
                    "Taraclia, Moldova", "alexlwlondon@gmail.com",
                    String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(UserTable.TABLE_NAME, UserTable.USERNAME, "User123")) {
            System.out.println("Just added a user...");
            dh.addUser("User123", "hfuwb3891js1", "Jane", "Doe",
                    "https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/user_female2-128.png",
                    "Chişinău, Moldova", "user123@gmail.com",
                    String.valueOf(new Timestamp(new Date().getTime())));
        }
        if (!dh.isInTable(UserTable.TABLE_NAME, UserTable.USERNAME, "KatieCue21")) {
            System.out.println("Just added a user...");
            dh.addUser("KatieCue21", "hf892nofc92", "Katie", "Gilmore",
                    null, "Balti, Moldova", "KatieCue21@gmail.com",
                    String.valueOf(new Timestamp(new Date().getTime())));
        }
        if (!dh.isInTable(UserTable.TABLE_NAME, UserTable.USERNAME, "MoldovaGuy2")) {
            System.out.println("Just added a user...");
            dh.addUser("MoldovaGuy2", "iodhq810rn382", "Henry", "Feinstein",
                    "http://findicons.com/files/icons/730/soft/128/user_male.png",
                    "Hincesti, Moldova","MoldovaGuy2@gmail.com",
                    String.valueOf(new Timestamp(new Date().getTime())));
        }
    }

    public void addStories(){
        if (!dh.isInTable(StoryTable.TABLE_NAME, StoryTable.CONTENT, "This is a story about corruption. This happened to me recently. I was working with this group and they" +
                " demanded that I pay 2000 lei to get a permit. I heard from other people that the permit only " +
                "costs 500 lei, so I felt ripped off. I confronted them, but they told me that I had to pay a special price.")) {
            System.out.println("Just added a story...");
            dh.addStory("This is a story about corruption. This happened to me recently. I was working with this group and they" +
                            " demanded that I pay 2000 lei to get a permit. I heard from other people that the permit only " +
                            "costs 500 lei, so I felt ripped off. I confronted them, but they told me that I had to pay a special price.",
                    "Ministry of Education", "Bribery", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(StoryTable.TABLE_NAME, StoryTable.CONTENT, "They stole money from me because I didn't want to go to jail.")) {
            System.out.println("Just added a story...");
            dh.addStory("They stole money from me because I didn't want to go to jail.",
                    "Ministry of Justice", "Extortion", String.valueOf(new Timestamp(new Date().getTime())));
        }
    }

    public void addWikis() throws IOException {
        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Traffic Laws")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Traffic Laws", getStringFromRawResource(R.raw.traffic_laws_wiki),
                    "National Laws", "Moldova;Laws;UNECE;Traffic", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Traffic Fines")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Traffic Fines", getStringFromRawResource(R.raw.traffic_fines_wiki),
                    "National Laws", "Moldova;Laws;Traffic;Fines", String.valueOf(new Timestamp(new Date().getTime())));
        }


        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Extortion")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Extortion", getStringFromRawResource(R.raw.extortion_wiki),
                    "Types of Corruption", "Extortion;Plahotniuc;Ministry of Commerce;", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Bribery")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Bribery", getStringFromRawResource(R.raw.bribery_wiki),
                    "Types of Corruption", "Bribery;Money;Economics;Bureaucracy", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Nepotism")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Nepotism", getStringFromRawResource(R.raw.nepotism_wiki),
                    "Types of Corruption", "Nepotism;Bureaucracy;Political Parties;Ceaușescu", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Fraud")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Fraud", getStringFromRawResource(R.raw.fraud_wiki),
                    "Types of Corruption", "Fraud;Common Law;Contracts;Law", String.valueOf(new Timestamp(new Date().getTime())));
        }

        if (!dh.isInTable(WikiTable.TABLE_NAME, WikiTable.TITLE, "Embezzlement")) {
            System.out.println("Just added a wiki post...");
            dh.addWiki("Embezzlement", getStringFromRawResource(R.raw.embezzlement_wiki),
                    "Types of Corruption", "Embezzlement;Theft;Financial Fraud", String.valueOf(new Timestamp(new Date().getTime())));
        }
    }

    public void addManuallyCodedInstitutions() {
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Justice")) {
            dh.addInstitution("Ministry of Justice",
                    "Strada 31 August 1989 82", "Chişinău",
                    "National Government",
                    "https://lh3.googleusercontent.com/--hV2O11Q1mY/To2tqQvOKYI/AAAAAAAAGfo/6Cf8Z3Z8kIE/s213-k-no/ ",
                    "Vladimir Cebotari",
                    "(+373 22) 20 14 20 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "State Registration Chamber, Chişinău")) {
            dh.addInstitution("State Registration Chamber, Chişinău",
                    "bd. Stefan cel Mare, 73", "Chişinău",
                    "National Government",
                    "http://photos.casata.md/files/s4d24d5fa71dfe.jpg",
                    "Victor Cebotari",
                    "(022) 266-200");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "State Tax Service")) {
            dh.addInstitution("State Tax Service",
                    "str. Constantin Tanase 9", "Chişinău",
                    "National Government",
                    "http://photos.casata.md/files/s4d24d5fa71dfe.jpg",
                    "N/A",
                    "823-353");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Territorial Cadastral Office")) {
            dh.addInstitution("Territorial Cadastral Office",
                    "str. Puşkin 47", "Chişinău",
                    "National Government",
                    "http://www.cadastre.md/templates/cadastru-main/images/logo.png",
                    "Ms. Inga-Stăvilă Grecu",
                    "022 88 10 00");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Education")) {
            dh.addInstitution("Ministry of Education",
                    "Piaţa Marii Adunări Naţionale nr. 1, Casa Guvernului", "Chişinău",
                    "National Government",
                    "http://tribuna.md/wp-content/uploads/2014/10/ministerul-educatiei-rm.jpg ",
                    "Corina Fusu",
                    "25 05 88");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Labor")) {

            dh.addInstitution("Ministry of Labor",
                    "Strada Vasile Alecsandri 1", "Chişinău",
                    "National Government",
                    "http://ipn.md/_files/jpg/23109.jpg",
                    "Stela Grigoraș",
                    "022 26-93-01");

        }

        //Girls' added mIntegerArrayInstitutions - start - Nadia N.
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Moldova State University")) {
            dh.addInstitution("Moldova State University", "Str. Alexe Mateevici, 60", "Chişinău",
                    "University", "http://usm.md/wp-content/themes/usm/resources/images/usm-logo.png ",
                    "Gheorghe Ciocanu ", null);
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Technical University of Moldova")) {
            dh.addInstitution ("Technical University of Moldova", "Ștefan cel Mare Bl. 1", "Chişinău", "University",
                    "http://www.utm.md/wp-content/uploads/2015/10/sigla1.png", "Viorel BOSTAN", "022 23-78-61");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Academy of Economic Studies of Moldova")) {
            dh.addInstitution ("Academy of Economic Studies of Moldova", "Strada Bodoni Banulescu 61",
                    "Chişinău", "University", "http://www.ase.md/images/logo_ase.png ",
                    "Grigore Belostecinic ", "22-41-28 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Information Society Development Institute")) {
            dh.addInstitution ("Information Society Development Institute",
                    " str. Academiei, 5A", "Chişinău", "National Government ",
                    "http://idsi.md/sites/all/themes/idsi/img/logo.png ",  "Igor COJOCARU  ",
                    "(+373-22) 28 98 40");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Social Protection, Family and Child")) {
            dh.addInstitution ("Ministry of Social Protection, Family and Child",
                    "str. Alecsandri 1, MD2028", "Chişinău", "National Government ",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Coat_of_arms_of_Moldova.svg/477px-Coat_of_arms_of_Moldova.svg.png ",
                    " Pavel Filip ", " (3732) 737572 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Finance")) {
            dh.addInstitution ("Ministry of Finance", "str. Constantin Tănase, 7",
                    "Chişinău", "National Government ",
                    "https://scontent.fotp1-1.fna.fbcdn.net/hprofile-xlp1/v/t1.0-1/c9.0.160.160/p160x160/10407420_880620418621807_6344712236885716825_n.jpg?oh=e285caaba4f3fff2a5b266147baae2f0&oe=57789E1C",
                    " Valeriu Secaş ", "(+373 22) 226629");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Economy and Trade")) {
            dh.addInstitution ("Ministry of Economy and Trade", "Piata Marii Adunarii Nationale №1", "Chişinău",
                    "National Government ", " https://img4.eadaily.com/r650x400/o/393/ab7c37adde56f17aa2fb17190fc64.jpg ",
                    "Vitalie IURCU", "(373 22) 250 591");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "National Bureau for Statistics")) {
            dh.addInstitution ("National Bureau for Statistics", "str. Grenoble, 106, MD-2019",
                    "Chişinău", "National Government ", "https://upload.wikimedia.org/wikipedia/ro/7/7f/BNS.png",
                    "Marin Gospodarenco", "+37322 40 30 00 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Banca Nationala a Moldovei")) {
            dh.addInstitution ("Banca Nationala a Moldovei", "Bulevardul Renasterii 7",
                    "Chişinău", "National Government ", " http://glocdn.investing.com/central_banks/national-bank-of-moldova_1255244805.gif",
                    "Marin Moloşag", "373 22 228 642");
        }

        //Valya's input
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Nicolae Testemițanu State University of Medicine and Pharmacy")) {
            dh.addInstitution ("Nicolae Testemițanu State University of Medicine and Pharmacy",
                    "Bulevardul Ștefan cel Mare și Sfînt 165", "Chişinău", "University",
                    "http://usmf.md/wp-content/themes/usmf/assets/blue/img/logo.png",  "Ion Ababii",
                    "+373 22 243 408");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "State Agrarian University of Moldova")) {
            dh.addInstitution ("State Agrarian University of Moldova", "Strada Mirceşti 42, 2049",
                    "Chişinău", "University ", "https://lh3.googleusercontent.com/-b8_Nz9tnmqQ/AAAAAAAAAAI/AAAAAAAAAaY/FYuXOAi1fWc/s120-c/photo.jpg",
                    "Cimpoieș Gheorghe", " +373 22 312 258");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ion Creangă Pedagogical State University")) {
            dh.addInstitution ("Ion Creangă Pedagogical State University", "Str. Ion Creangă, 1 2069, Strada Ion Creangă 1,2069",
                    "Chişinău", "University ", "https://scontent.fotp1-1.fna.fbcdn.net/hprofile-xap1/v/t1.0-1/c12.12.155.155/265105_501689076541467_842889976_n.png?oh=c930e58a10295cb31257b0584fbe366b&oe=579094B1 ",
                    "Nicolae Chicuș", " +373 22 747 208");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "The Ministry of Agriculture and Food Industry")) {
            dh.addInstitution ("The Ministry of Agriculture and Food Industry", "p. Ștefan cel Mare, 162",
                    "Chişinău", "National Government", "http://www.maia.gov.md/sites/all/themes/maia2/_/i/logo.png",
                    "Ion Sula", "+373 (22) 23-34-27");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Ecology and Natural Resources")) {
            dh.addInstitution ("Ministry of Ecology and Natural Resources", "Str. Cosmonauți, 9", "Chişinău",
                    "National Government ", "http://www.mediu.gov.md/images/Min%20Mediului%20logou.jpg",
                    "Valeriu Munteanu", " +373 (22) 20-45-07 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Ministry of Health")) {
            dh.addInstitution ("Ministry of Health", "Str. Vasile Alecsandri, 2", "Chişinău",
                    "National Government", "http://media1.noi.md/uploads/images/Institutii/ministerul_sanatatii_afi_md.jpg",
                    " Ruxanda GLAVAN", "+373 (22) 26-88-18 ");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "The Ministry of Culture")) {
            dh.addInstitution ("The Ministry of Culture", "Piaţa Marii Adunări Naţionale, nr. 1", "Chişinău",
                    "National Government ", "http://tribuna.md/wp-content/uploads/2013/09/logo-ministerul-culturii_0013215240012.jpg",
                    "Monica Babuc", "+373 (22) 22-76-20");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "The Ministry of Local Public Administration")) {
            dh.addInstitution ("The Ministry of Local Public Administration", "Piaţa Marii Adunări Naţionale, nr. 1", "Chişinău", "National Government ",
                    "http://www.md.undp.org/content/dam/moldova/img/PressCenter/Estonia%20group%20pics.jpg/jcr:content/renditions/cq5dam.web.540.390.jpeg",
                    "VRABIE VITALIE ", "+373 (22) 20-01-70");
        }
        //Nadia Cheban's input
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Emergency Medicine Institute")) {
            dh.addInstitution ("Emergency Medicine Institute", "Strada Toma Ciorbă 1",
                    "Chişinău", "Medical", " http://www.urgenta.md/imgReferinte/001.jpg",
                    "Mihail Ciocanu", " (+373 22) 25 07 01");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, " Municipal Hospital \"St. Archangel Michael\"")) {
            dh.addInstitution ("Municipal Hospital \"St. Archangel Michael\"", "Strada Arhanghel Mihail 38", "Chişinău", "Medical", "http://photos.wikimapia.org/p/00/01/71/21/29_big.jpg",  " Mihai Ciobanu ", "(+373 22) 29-26-52");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, " Medpark")) {
            dh.addInstitution ("Medpark International Hospital", " Strada Andrei Doga 24",
                    "Chişinău", "Medical", "http://lindeco.com/wp-content/gallery/med-park/medpark170211_2.jpg",
                    "Olga Şchiopu", "(+373 22) 40-00-00");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Municipal Children Hospital No.1")) {
            dh.addInstitution ("Municipal Children Hospital No.1", "Strada Serghei Lazo 7",
                    "Chişinău", "Medical", "http://photos.wikimapia.org/p/00/01/58/23/73_big.jpg",
                    "Serghei Simco", "(+373 22) 243123");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, " Family Medicine Center \"Galaxia\"")) {
            dh.addInstitution ("Family Medicine Center \"Galaxia\"", " Strada Alexandru cel Bun 54",
                    "Chişinău", "Medical", "http://photos.wikimapia.org/p/00/01/71/05/60_big.jpg",
                    "Valeriu Maciuca", "(+373 22) 85-85-04");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, " Republican Hospital")) {
            dh.addInstitution ("Republican Hospital", "Strada Constantin Vârnav", "Chişinău",
                    "Medical", " http://www.scr.md/images/logo-copy.gif",  "Ciubotaru Anatol",
                    "(+373 22) 40 36 22");
        }
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, " MedExpert")) {
            dh.addInstitution ("MedExpert", "str. Gh. Asachi, 42", "Chişinău", "Medical",
                    "https://www.medexpert.md/sites/default/files/field/attachment_image/article/274a2501dsasds_0.jpg",  "Eugenia Spoiala", "(+373 22) 81 11 81");
        }
    }

    public void addPublicNotaries() {

        //Public notaries - start -
        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 248")) {
            dh.addInstitution("Public Notary, 248",
                    "str. Pushkin, 44", "Bălţi",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Aliona Agachi",
                    "0(231)63402");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 191")) {
            dh.addInstitution("Public Notary, 191",
                    "str. M. Kogălniceanu, 68, ap. 1", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Amihalachioaie-Ţurcan Mirela",
                    "0(231)63402");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 171")) {
            dh.addInstitution("Public Notary, 171",
                    "str. Ion Creangă, 62", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Andronatii Andrei",
                    "0(22)740447");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 040")) {
            dh.addInstitution("Public Notary, 040",
                    "str. Ion Creangă, 62", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Andronatii Valentina",
                    "0(22)740447");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 224")) {
            dh.addInstitution("Public Notary, 224",
                    "bd. Decebal, 99, bl. 3, bir.1020a", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Andronic-Gabura Olga",
                    "0(22)890909");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 080")) {
            dh.addInstitution("Public Notary, 080",
                    "str. N. Zelinski, 12, ap. 43", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Antoci Vera",
                    "0(22)551276");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 274")) {
            dh.addInstitution("Public Notary, 274",
                    "str. Tighina, 111", "Varniţa",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Antohi Veronica",
                    "0(265)46374");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 057")) {
            dh.addInstitution("Public Notary, 057",
                    "str. Titulescu, 1", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Anton Tereza",
                    "0(22)556654");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 295")) {
            dh.addInstitution("Public Notary, 295",
                    "str.Lenin, 100, ap. 2", "Ceadîr Lunga",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Arnaut Ana",
                    "0(291)23047");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 130")) {
            dh.addInstitution("Public Notary, 130",
                    "bd. Ştefan cel Mare și Sfînt, 64, of.34", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Avram Nina",
                    "0(22)278682");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 270")) {
            dh.addInstitution("Public Notary, 270",
                    "bd. Cuza-Vodă, nr. 30/1, of. 8", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Badalova Viorica",
                    "0(22)661330");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 117")) {
            dh.addInstitution("Public Notary, 117",
                    "bd. Mihai Eminescu, 8, bir. 63", "Căuşeni",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bagrin Maria",
                    "0(243)23031");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 079")) {
            dh.addInstitution("Public Notary, 079",
                    "str. Burebista, nr. 76", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Balaban Galia",
                    "0(22)800441");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 175")) {
            dh.addInstitution("Public Notary, 175",
                    "str. Ştefan cel Mare, 27/10", "Bălţi",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Balan Marina",
                    "0(231)63328");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 045")) {
            dh.addInstitution("Public Notary, 045",
                    "bd. Negruzzi, 8, of. 1A", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bazaochi Andrei",
                    "0(22)541961");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 302")) {
            dh.addInstitution("Public Notary, 302",
                    "str. 31 August, nr. 11 A", "Ştefan Vodă",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bejenar Denis",
                    "0(242)93422");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 110")) {
            dh.addInstitution("Public Notary, 110",
                    "str. V. Lupu, 15, ap. 13", "Orhei",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bejenar Tatiana",
                    "0(235)21800");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 105")) {
            dh.addInstitution("Public Notary, 105",
                    "str. Meşterul Stanciu, 3", "Căuşeni",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Belciug Petru",
                    "0(243)22039");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 303")) {
            dh.addInstitution("Public Notary, 303",
                    "str. A. Puşkin, nr. 37, of. 21", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Beliban-Raţoi Ludmila",
                    "0(22)224754");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 203")) {
            dh.addInstitution("Public Notary, 203",
                    "str. A. Bernardazzi, nr. 97, ap. 12", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Belicesco Oleg",
                    "0(22)222446");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 176")) {
            dh.addInstitution("Public Notary, 176",
                    "str. Puşkin, 24, of. 15", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Berghii Evghenii",
                    "0(22)220204");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 119")) {
            dh.addInstitution("Public Notary, 119",
                    "str. 31 August, 5, ap.15", "Hînceşti",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bicec Victor",
                    "0(269)24949");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 243")) {
            dh.addInstitution("Public Notary, 243",
                    "str. 27 August, 46, ap. 2", "Rezina",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bîrliba Lilia",
                    "0(254)24413");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 088")) {
            dh.addInstitution("Public Notary, 088",
                    "str. Miron Costin, 12, of.1", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bîstriţcaia Tatiana",
                    "0(22)434507");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 251")) {
            dh.addInstitution("Public Notary, 251",
                    "str. 31 August, nr. 122, ap. 6", "Criuleni",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bîstriţchi Vladimir",
                    "0(248)21967");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 206")) {
            dh.addInstitution("Public Notary, 206",
                    "str. Academiei, 12", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bloşenco Andrei",
                    "0(22)736362");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 271")) {
            dh.addInstitution("Public Notary, 271",
                    "str. Academiei, 12", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bloşenco Diana",
                    "0(22)729946");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 177")) {
            dh.addInstitution("Public Notary, 177",
                    "str. 31 August, 6", "Anenii Noi",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bobeico Maria",
                    "0(265)24574");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 162")) {
            dh.addInstitution("Public Notary, 162",
                    "str. Maria Cibotari, 37", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bodiu Mariana",
                    "0(22)881620");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 178")) {
            dh.addInstitution("Public Notary, 178",
                    "str. Mitropolit Varlaam, 88 of.5", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Boico Liliana",
                    "0(22)222895");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 231")) {
            dh.addInstitution("Public Notary, 231",
                    "str. M. Eminescu, 11", "Soroca",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Boldescu Radu",
                    "0(230)30611");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 139")) {
            dh.addInstitution("Public Notary, 139",
                    "str. M. Eminescu, 21/37", "Călăraşi",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Boldurescu Valentina",
                    "0(244)23140");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 157")) {
            dh.addInstitution("Public Notary, 157",
                    "bd. D. Cantemir, 1/1", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bondarciuc Olga",
                    "0(22)809160");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 195")) {
            dh.addInstitution("Public Notary, 195",
                    "str. M.Kogălniceanu, 25, of. 4", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Brihuneţ Svetlana",
                    "0(22)542256");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 234")) {
            dh.addInstitution("Public Notary, 234",
                    "str. Chişinăului, nr. 1", "Anenii Noi",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Buga Georgeta",
                    "0(265)22180");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 304")) {
            dh.addInstitution("Public Notary, 304",
                    "str. Victoriei, 27", "Şoldăneşti",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bulgac Svetlana",
                    "0(272)25880");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 118")) {
            dh.addInstitution("Public Notary, 118",
                    "str. Puşkin 22, bir. 205", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bumbu Adelina",
                    "0(22)238077");
        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 167")) {
            dh.addInstitution("Public Notary, 167",
                    "str. Puşkin 22, bir. 205", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Bumbu Cristian",
                    "0(22)238077");

        }

        if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, "Public Notary, 174")) {
            dh.addInstitution("Public Notary, 174",
                    "str. Decebal 11, et II", "Chişinău",
                    "Local Government",
                    "http://monitorul.fisc.md/uploads/topics/preview/00/00/28/39/43cda958e5_200crop.jpg",
                    "Burac Lilia",
                    "0(241)24814");

        }


        //Public notaries - end -
    }

    public void addInstitutionsFromFile(int mRawResource) throws IOException{
        Timestamp mStartTime = new Timestamp(new Date().getTime());
        String mInstitution, mAddress, mCity, mType, mLogo, mManager, mPhone;
        InputStream mInputStream = getResources().openRawResource(mRawResource);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
        String mString;
        int i = 0;
        while ((mString = bufferedReader.readLine()) != null) {
            String mStringArray[] = mString.split(";");
            if (mStringArray.length < 7) {
                System.out.println("While loop line breaks!");
            } else {
                mInstitution = mStringArray[0];
                mAddress = mStringArray[1];
                mCity = mStringArray[2];
                mType = mStringArray[3];
                mLogo = mStringArray[4];
                mManager = mStringArray[5];
                mPhone = mStringArray[6];
                if (!dh.isInTable(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, mInstitution)) {
                    dh.addInstitution(mInstitution, mAddress, mCity, mType, mLogo, mManager, mPhone);
                }
                i++;
            }
        }
        System.out.println("Institutions finished adding from file - count, " + i);
        System.out.println("Adding institutions started " + um.getTimeFromNow(String.valueOf(mStartTime)));
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void finishOnCreate() {

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setLogo(ContextCompat.getDrawable(mContext, R.drawable.nucoralb_logo_white_simple_text_24dp));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        View logo = mToolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                um.openActivity(v);
            }
        });

        mListViewFeed = (ListView) findViewById(R.id.lvFeed);
        mListViewResults = (ListView) findViewById(R.id.lvResults);

        Thread backgroundData = new Thread(new Runnable() {
            @Override
            public void run() {
                prepareFeedListData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapterFeed = new ListAdapterFeed(mContext, mListDataFeed);
                        mListAdapterFeed.notifyDataSetChanged();
                        mListViewFeed.setAdapter(mListAdapterFeed);

                        mListViewFeed.setOnItemLongClickListener(new UniversalMethodsAndVariables.RemoveListViewItem(mContext, mListDataFeed, mListAdapterFeed));
                        mListViewFeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView activityType = (TextView) view.findViewById(R.id.header);

                                switch (activityType.getText().toString()) {
                                    case "New story":
                                        System.out.println("Opening story activity...");
                                        um.openPopUpActivity(0, position, mListDataFeed);
                                        break;
                                    case "New wiki article":
                                        System.out.println("Opening wiki article activity...");
                                        um.openPopUpActivity(1, position, mListDataFeed);
                                        break;
                                    case "Popular forum post":
                                        System.out.println("Opening forum post activity...");
                                        um.openPopUpActivity(2, position, mListDataFeed);
                                        break;
                                    case "Frequently searched institution":
                                        System.out.println("Opening institution activity...");
                                        um.openPopUpActivity(3, position, mListDataFeed);
                                }
                            }
                        });

                        mListViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView activityType = (TextView) view.findViewById(R.id.header);

                                switch (activityType.getText().toString()) {
                                    case "Story":
                                        System.out.println("Opening story activity...");
                                        um.openPopUpActivity(0, position, mListBundleResults);
                                        break;
                                    case "Wiki Article":
                                        System.out.println("Opening wiki article activity...");
                                        um.openPopUpActivity(1, position, mListBundleResults);
                                        break;
                                    case "Forum Post":
                                        System.out.println("Opening forum post activity...");
                                        um.openPopUpActivity(2, position, mListBundleResults);
                                        break;
                                    case "Institution":
                                        System.out.println("Opening institution activity...");
                                        um.openPopUpActivity(3, position, mListBundleResults);
                                }
                            }
                        });

                        searchView = (SearchView) mToolbar.findViewById(R.id.searchView);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                um.prepareResultsListData(query);
                                mListBundleResults = um.resultsListData;
                                mListViewResults.setAdapter(mListAdapterResults);
                                mListAdapterResults = new ListAdapterResults(mContext, mListBundleResults, query);
                                mListAdapterResults.notifyDataSetChanged();
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                um.prepareResultsListData(newText);
                                mListBundleResults = um.resultsListData;
                                mListViewResults.setAdapter(mListAdapterResults);
                                mListAdapterResults = new ListAdapterResults(mContext, mListBundleResults, newText);
                                mListAdapterResults.notifyDataSetChanged();
                                return true;
                            }
                        });
                    }
                });
            }
        });
        backgroundData.start();
    }

    public void checkLogin() {
        final EditText usernameEt = new EditText(mContext);
        usernameEt.requestFocus();
        usernameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameEt.setTextColor(Color.WHITE);
        usernameEt.setHintTextColor(Color.WHITE);
        if (UserData.username==null){
            usernameEt.setHint("Username");
        } else {
            usernameEt.setText(UserData.username);
        }
        final EditText passwordEt = new EditText(mContext);
        passwordEt.setTextColor(Color.WHITE);
        passwordEt.setHintTextColor(Color.WHITE);
        passwordEt.setHint("Password");
        passwordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final Button register = new Button(mContext);
        register.setText("Register");
        final Button login = new Button(mContext);
        login.setText("Login");
        final LinearLayout layout = new LinearLayout(mContext);
        BitmapDrawable tiledBg = (BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.texture_1);
        tiledBg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        layout.setBackground(tiledBg);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView appLogo = new ImageView(mContext);
        appLogo.setImageResource(R.drawable.nucoralb_logo_text_white_48dp);

        LinearLayout.LayoutParams appLogoParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        appLogoParams.gravity = Gravity.CENTER;
        appLogo.setLayoutParams(appLogoParams);

        layout.setLayoutParams(layoutParams);
        layout.setPaddingRelative(10, 100, 10, 0);
        layout.addView(appLogo);

        setContentView(layout);

        final AnimationSet fadeIn = new AnimationSet(true);
        fadeIn.addAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
        fadeIn.setDuration(1000);
        appLogo.setAnimation(fadeIn);
        appLogo.animate().start();
        appLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout loginStuff = new LinearLayout(mContext);
                loginStuff.setOrientation(LinearLayout.VERTICAL);
                loginStuff.addView(usernameEt);
                loginStuff.addView(passwordEt);
                loginStuff.addView(register);
                loginStuff.addView(login);
                layout.addView(loginStuff);
                loginStuff.setAnimation(fadeIn);
                loginStuff.animate().start();
            }
        }, 1000);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (usernameEt.getText() != null && passwordEt.getText() != null) {
                    Bundle bundle = dh.getUser(UserTable.USERNAME, usernameEt.getText().toString().trim());
                    if (bundle == null) {
                        Toast.makeText(MainActivity.this, "Username does not exist", Toast.LENGTH_SHORT).show();
                    } else {
                        if (passwordEt.getText().toString().equals(bundle.getString("PASSWORD"))) {
                            //newUser = true; //just for testing purposes! Code should only run when a user is first registered
                            UserData.username = usernameEt.getText().toString().trim();
                            UserData.password = passwordEt.getText().toString();
                            Toast.makeText(MainActivity.this, "Welcome, " + UserData.username + "!", Toast.LENGTH_SHORT).show();
                            if (newUser)
                                firstUserHelp();
                            else
                                finishOnCreate();
                        } else {
                            Toast.makeText(MainActivity.this, "Password does not match username!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText firstNameEt = new EditText(mContext);
                firstNameEt.requestFocus();
                firstNameEt.setHint("first name");
                firstNameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                final EditText lastNameEt = new EditText(mContext);
                lastNameEt.setHint("last name");
                lastNameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                final EditText locationEt = new EditText(mContext);
                locationEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                locationEt.setHint("location");
                final EditText emailEt = new EditText(mContext);
                emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailEt.setHint("email");
                final EditText usernameEt = new EditText(mContext);
                usernameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                usernameEt.setHint("username");
                final EditText passwordEt = new EditText(mContext);
                passwordEt.setHint("password");
                passwordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final EditText passwordEt2 = new EditText(mContext);
                passwordEt2.setHint("password (again)");
                passwordEt2.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                Button cancel = new Button(mContext);
                cancel.setText("Cancel");
                Button clear = new Button(mContext);
                clear.setText("Clear");
                Button register = new Button(mContext);
                register.setText("Register");

                final LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(layoutParams);
                layout.setPaddingRelative(10, 10, 10, 0);
                layout.setBackgroundColor(Color.WHITE);
                layout.addView(firstNameEt);
                layout.addView(lastNameEt);
                layout.addView(locationEt);
                layout.addView(emailEt);
                layout.addView(usernameEt);
                layout.addView(passwordEt);
                layout.addView(passwordEt2);
                layout.addView(register);
                layout.addView(clear);
                layout.addView(cancel);
                setContentView(layout);

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstNameEt.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a first name", Toast.LENGTH_SHORT).show();
                        } else if (lastNameEt.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a last name", Toast.LENGTH_SHORT).show();
                        } else if (locationEt.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
                        } else if (emailEt.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                        } else if (usernameEt.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                        } else if (passwordEt.getText().toString().isEmpty() || passwordEt2.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter your password twice", Toast.LENGTH_SHORT).show();
                        } else if (dh.getUser(UserTable.USERNAME, usernameEt.getText().toString()) != null) {
                            Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        } else if (passwordEt.getText().toString().equals(passwordEt2.getText().toString())) {
                            dh.addUser(usernameEt.getText().toString(), passwordEt.getText().toString(), firstNameEt.getText().toString(), lastNameEt.getText().toString(), null, locationEt.getText().toString(), emailEt.getText().toString(), String.valueOf(new Timestamp(new Date().getTime())));
                            checkLogin();
                            Toast.makeText(MainActivity.this, "You are now registered as " + usernameEt.getText().toString(), Toast.LENGTH_SHORT).show();
                            newUser = true;
                        } else {
                            Toast.makeText(MainActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstNameEt.getText().clear();
                        lastNameEt.getText().clear();
                        locationEt.getText().clear();
                        usernameEt.getText().clear();
                        passwordEt.getText().clear();
                        passwordEt2.getText().clear();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkLogin();
                    }
                });
            }
        });
    }

    public void firstUserHelp() {
        mViewFlipper = new ViewFlipper(this);
        LinearLayout mLinearLayout = new LinearLayout(this);
        setContentView(mLinearLayout);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setPaddingRelative(20, 20, 20, 20);
        mLinearLayout.addView(mViewFlipper);

        mLinearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        LinearLayout mLinearLayout1 = new LinearLayout(this);
        LinearLayout mLinearLayout2 = new LinearLayout(this);
        LinearLayout mLinearLayout3 = new LinearLayout(this);
        LinearLayout mLinearLayout4 = new LinearLayout(this);
        LinearLayout mLinearLayout5 = new LinearLayout(this);

        LinearLayout.LayoutParams mpmpParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams mpwcParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mLinearLayout1.setLayoutParams(mpmpParams);
        mLinearLayout2.setLayoutParams(mpmpParams);
        mLinearLayout3.setLayoutParams(mpmpParams);
        mLinearLayout4.setLayoutParams(mpmpParams);
        mLinearLayout5.setLayoutParams(mpmpParams);

        mViewFlipper.addView(mLinearLayout1);
        mViewFlipper.addView(mLinearLayout2);
        mViewFlipper.addView(mLinearLayout3);
        mViewFlipper.addView(mLinearLayout4);
        mViewFlipper.addView(mLinearLayout5);
        mViewFlipper.setOnTouchListener(mViewFlipperListener);

        int mImageResource[] = {R.drawable.nucoralb_logo_large, R.drawable.home_and_search_slide,
                R.drawable.home_and_share_story_slide, R.drawable.inst_and_survey_slide,
                R.drawable.post_and_user_info_slide};
        String mString[] = {"NucorAlb is an anti-corruption app for sharing corruption stories," +
                " insight, and holding corrupt institutions accountable. This app's content is " +
                "user-driven and used by individuals concerned with deterring corruption " +
                "in their everyday lives.",
                "This is the home screen. You can scroll through and open recently added, updated and popular corruption stories," +
                 " institutions, wikis and forum posts. You can always return to the home screen by clicking the NucorAlb " +
                 "logo on the Toolbar. Click the search icon to search all items in the app.",
                "You can share your corruption story by clicking the \"plus\" icon on the mToolbar. Click the overflow button " +
                "on the right side to add an institution or wiki entry.",
                "On an institution page click the survey icon to give feedback about the institution. Click on the " +
                "star icon to follow the institution. Click on \"corruption stats+\" to see detailed statistics.",
                "On a forum post you can view a user's information by clicking their icon. You can send another user an email " +
                        "by clicking their email address."};

        Object objectArray[] = um.getChildrenArray(mViewFlipper);
        int i = 0;
        for (Object object : um.getChildrenArray(mViewFlipper)) {
            if (object instanceof LinearLayout) {
                ((LinearLayout) object).setGravity(Gravity.CENTER);
                ((LinearLayout) object).setOrientation(LinearLayout.VERTICAL);
                ImageView imageView = new ImageView(this);
                TextView textView = new TextView(this);
                if (object == objectArray[0]) {
                    imageView.setImageResource(mImageResource[0]);
                    textView.setText(mString[0]);
                } else if (object == objectArray[1]) {
                    imageView.setImageResource(mImageResource[1]);
                    textView.setText(mString[1]);
                } else if (object == objectArray[2]) {
                    imageView.setImageResource(mImageResource[2]);
                    textView.setText(mString[2]);
                } else if (object == objectArray[3]) {
                    imageView.setImageResource(mImageResource[3]);
                    textView.setText(mString[3]);
                } else if (object == objectArray[4]) {
                    imageView.setImageResource(mImageResource[4]);
                    textView.setText(mString[4]);
                }
                else {
                    imageView.setImageResource(R.drawable.ic_flag_black_48dp);
                    textView.setText("Dummy text"+i);
                }
                textView.setPaddingRelative(0,0,0,25);

                textView.setGravity(Gravity.CENTER);
                //textView.setLayoutParams(mpwcParams);
                ((LinearLayout) object).addView(imageView);
                ((LinearLayout) object).addView(textView);
                if (object == objectArray[objectArray.length-1]) {
                    Button button = new Button(this);
                    button.setText("done");
                    ((LinearLayout)object).addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishOnCreate();
                        }
                    });
                }
            }
            i++;
        }

        mRadioGroup = new RadioGroup(this);
        mRadioGroup.setOrientation(RadioGroup.HORIZONTAL);
        mRadioGroup.setGravity(Gravity.CENTER);
        mLinearLayout.addView(mRadioGroup);

        int k = 0;
        while (k < objectArray.length) {
            RadioButton radioButton = new RadioButton(this);
            mRadioGroup.addView(radioButton);
            radioButton.setEnabled(false);
            radioButton.setClickable(false);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
                        if (mRadioGroup.getChildAt(i) == v) {
                            mRadioGroup.check(i+1);
                            mViewFlipper.setDisplayedChild(i);
                        }
                    }
                }
            });
            k++;
        }
        mRadioGroup.check(1);
    }

    View.OnTouchListener mViewFlipperListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float currentX = event.getX();

                    if (lastX < currentX) {
                        if (mViewFlipper.getDisplayedChild() == 0) break;
                        mViewFlipper.setInAnimation(mContext, R.anim.slide_in_from_left);
                        mViewFlipper.setOutAnimation(mContext, R.anim.slide_out_to_right);
                        mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild()-1);
                        mRadioGroup.check(mViewFlipper.getDisplayedChild()+1);
                    } else if (lastX > currentX) {
                        if (mViewFlipper.getDisplayedChild() == mViewFlipper.getChildCount()-1) break;
                        mViewFlipper.setInAnimation(mContext, R.anim.slide_in_from_right);
                        mViewFlipper.setOutAnimation(mContext, R.anim.slide_out_to_left);
                        mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                        mRadioGroup.check(mViewFlipper.getDisplayedChild()+1);
                    }
            }
            return true;
        }
    };

    public String getStringFromRawResource(int mRawResource) throws IOException {
        InputStream mInputStream = getResources().openRawResource(mRawResource);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
        String mStringAll = "";
        String mString;
        while ((mString = bufferedReader.readLine()) != null) {
            mStringAll += mString + "\n\n";
        }
        return mStringAll;
    }

}
