package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexlondon on 2/20/16.
 */
public class ListAdapterResults extends BaseAdapter {
    private Context context;
    private List<Bundle> listData;

    private String query;

    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables();

    private LruCache<String, Bitmap> bitmapMemoryCache;


    public ListAdapterResults(Context context, List<Bundle> listData, String query) {
        this.context = context;
        this.listData = listData;
        this.query = query;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        bitmapMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    @Override
    public int getCount() {
        return this.listData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DatabaseHelper dh = new DatabaseHelper(context);

        final Bundle tempItem = (Bundle) getItem(position);


        String city, story, institution, corruptionType, title, introduction, category, username, post, thread, logo;
        String date;
        Integer id;

        TextView header, positiveTv, negativeTv, cityTv, storyTv, institutionTv, typeTv, dateTv, titleTv, introductionTv, categoryTv, usernameTv, postTv, threadTv, upVotesTv, downVotesTv;
        String profileIcon;

        String s = tempItem.getString("TABLE_NAME");
        if (s.equals(StoryTable.TABLE_NAME)) {
            final Bundle item = dh.getStory(StoryTable.ID, String.valueOf(tempItem.getInt("ID")));
            System.out.println("Starting to feed a story view...");
            // Inflate appropriate layout
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.story_summary, null);
            // Assign object[] items to strings
            story = item.getString("CONTENT");
            institution = item.getString("INSTITUTION");
            corruptionType = item.getString("TYPE");
            date = item.getString("DATE");

            // Assign TextView variables to appropriate views in layout
            storyTv = (TextView) convertView
                    .findViewById(R.id.story);
            institutionTv = (TextView) convertView
                    .findViewById(R.id.institution);
            typeTv = (TextView) convertView
                    .findViewById(R.id.type);
            dateTv = (TextView) convertView
                    .findViewById(R.id.date);
            header = (TextView) convertView.findViewById(R.id.header);
            header.setText("Story");
            // format text entries with SpannableStringBuilder
            TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String storySub = story.substring(0, Math.min(story.length(), 100)) + "...";

            Spannable storySpan = findQueryInString(storySub);

            String institutionString = "Institution: " + institution;
            Spannable institutionSpan = findQueryInString(institutionString);
            institutionSpan.setSpan(tfBold, 0, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String corruptionString = "Type: " + corruptionType;
            Spannable corruptionSpan = findQueryInString(corruptionString);
            corruptionSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String dateString = um.getTimeFromNow(date);
            Spannable dateSpan = new SpannableString(dateString);

            // set text to TextViews
            storyTv.setText(storySpan);
            institutionTv.setText(institutionSpan);
            typeTv.setText(corruptionSpan);
            dateTv.setText(dateSpan);
            System.out.println("Finished feeding a story view...");

        } else if (s.equals(WikiTable.TABLE_NAME)) {
            final Bundle item = dh.getWiki(WikiTable.ID, String.valueOf(tempItem.getInt("ID")));
            LayoutInflater infalInflater;
            String dateString;
            Spannable dateSpan;
            TypefaceSpan tfBold;
            System.out.println("Starting to feed a wiki view...");
            infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.wiki_summary, null);

            id = item.getInt("ID");
            title = item.getString("TITLE");
            introduction = item.getString("CONTENT");
            category = item.getString("CATEGORY");
            date = item.getString("CREATED");

            titleTv = (TextView) convertView
                    .findViewById(R.id.titleText);
            introductionTv = (TextView) convertView
                    .findViewById(R.id.introduction);
            categoryTv = (TextView) convertView
                    .findViewById(R.id.category);
            dateTv = (TextView) convertView
                    .findViewById(R.id.date);
            header = (TextView) convertView.findViewById(R.id.header);
            header.setText("Wiki Article");

            // format text entries with Spannable
            tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String titleString = "Title: " + title;
            Spannable titleSpan = findQueryInString(titleString);
            titleSpan.setSpan(tfBold, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String introductionSub = introduction.substring(0, Math.min(introduction.length(), 100)) + "...";

            String introductionString = "Introduction: " + introductionSub;
            Spannable introductionSpan = findQueryInString(introductionString);
            introductionSpan.setSpan(tfBold, 0, 14, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String categoryString = "Category: " + category;
            Spannable categorySpan = findQueryInString(categoryString);
            categorySpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            dateString = um.getTimeFromNow(date);
            dateSpan = new SpannableString(dateString);

            // set text to TextViews
            titleTv.setText(titleSpan);
            introductionTv.setText(introductionSpan);
            categoryTv.setText(categorySpan);
            dateTv.setText(dateSpan);
            System.out.println("Finished feeding a wiki view...");

        } else if (s.equals(PostTable.TABLE_NAME)) {
            final Bundle item = dh.getPost(PostTable.ID, String.valueOf(tempItem.getInt("ID")));
            LayoutInflater infalInflater;
            String dateString;
            Spannable dateSpan;
            TypefaceSpan tfBold;
            System.out.println("Feeding a post view...");

            infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.post_summary, null);

            username = item.getString("USERNAME");
            post = item.getString("CONTENT");
            thread = item.getString("THREAD");
            date = item.getString("DATE");

            Bundle bundle = dh.getUser(UserTable.USERNAME, username);

            if (bundle.getString("ICON") != null)
                profileIcon = bundle.getString("ICON");
            else
                profileIcon = null;

            usernameTv = (TextView) convertView
                    .findViewById(R.id.username);
            postTv = (TextView) convertView
                    .findViewById(R.id.content);
            threadTv = (TextView) convertView
                    .findViewById(R.id.thread);
            final ImageView profileIv = (ImageView) convertView
                    .findViewById(R.id.profileIcon);
            dateTv = (TextView) convertView
                    .findViewById(R.id.date);
            upVotesTv = (TextView) convertView
                    .findViewById(R.id.upVotes);
            downVotesTv = (TextView) convertView
                    .findViewById(R.id.downVotes);
            ImageView up = (ImageView) convertView
                    .findViewById(R.id.upVotesIv);
            ImageView down = (ImageView) convertView
                    .findViewById(R.id.downVotesIv);
            up.setVisibility(View.INVISIBLE);
            down.setVisibility(View.INVISIBLE);
            upVotesTv.setVisibility(View.INVISIBLE);
            downVotesTv.setVisibility(View.INVISIBLE);

            assert thread != null;
            if (!thread.contains("Story")) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                threadTv.setLayoutParams(params);
            } else {
                threadTv.setVisibility(View.GONE);
            }
            header = (TextView) convertView.findViewById(R.id.header);
            header.setText("Forum Post");


            // format text entries with Spannable
            tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String postSub = post.substring(0, Math.min(post.length(), 100)) + "...";
            Spannable postSpan = findQueryInString(postSub);

            String threadString = "Thread: " + thread;
            Spannable threadSpan = findQueryInString(threadString);
            threadSpan.setSpan(tfBold, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            dateString = um.getTimeFromNow(date);
            dateSpan = new SpannableString(dateString);

            int resId = (int) (Math.random() * 10000);
            if (profileIcon != null) {
                resId = profileIcon.hashCode();
            }
            loadLogo(resId, R.dimen.post_icon_wh,profileIv, profileIcon, R.drawable.ic_person_black_36dp);

            usernameTv.setText(username);
            postTv.setText(postSpan);
            dateTv.setText(dateSpan);
            threadTv.setText(threadSpan);

            System.out.println("Finished feeding a post view...");

        } else if (s.equals(InstitutionTable.TABLE_NAME)) {
            final Bundle item = dh.getInstitution(InstitutionTable.ID, String.valueOf(tempItem.getInt("ID")));
            LayoutInflater infalInflater;
            TypefaceSpan tfBold;
            System.out.println("Feeding an institution view...");

            institution = item.getString("INSTITUTION");
            String address = item.getString("ADDRESS");
            city = item.getString("CITY");

            if (item.getString("LOGO") != null)
                logo = item.getString("LOGO");
            else
                logo = null;

            infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.institution_summary, null);

            institutionTv = (TextView) convertView.findViewById(R.id.institution);

            cityTv = (TextView) convertView.findViewById(R.id.city);
            final ImageView logoIv = (ImageView) convertView.findViewById(R.id.logo);
            TextView addressTv = (TextView) convertView.findViewById(R.id.address);
            TextView surveysTv = (TextView) convertView.findViewById(R.id.surveys);
            surveysTv.setVisibility(View.GONE);
            positiveTv = (TextView) convertView.findViewById(R.id.positive);
            negativeTv = (TextView) convertView.findViewById(R.id.negative);
            positiveTv.setVisibility(View.GONE);
            negativeTv.setVisibility(View.GONE);
            header = (TextView) convertView.findViewById(R.id.header);
            header.setText("Institution");

            tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String addressString = "Address: " + address;
            Spannable addressSpan = findQueryInString(addressString);
            addressSpan.setSpan(tfBold, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String cityString = "City: " + city;
            Spannable citySpan = findQueryInString(cityString);
            citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            int resId = (int) (Math.random() * 10000);
            if (logo != null) {
                resId = logo.hashCode();
            }
            loadLogo(resId, R.dimen.logo_inst_wh, logoIv, logo, R.drawable.ic_account_balance_black_48dp);

            institutionTv.setText(findQueryInString(institution));
            addressTv.setText(addressSpan);
            cityTv.setText(citySpan);

            System.out.println("Finished feeding an institution view...");

        } else {
        }
        return convertView;
    }

    public Spannable findQueryInString(String string) {
        Spannable spannable = new SpannableString(string);
        String[] queryArray = query.split("[,\\s\\-:\\?]");
        for (String aQueryArray : queryArray) {
            Pattern pattern = Pattern.compile(aQueryArray.toLowerCase());
            Matcher matcher = pattern.matcher(string.toLowerCase());
            while (matcher.find()) {
                spannable.setSpan(new BackgroundColorSpan(Color.rgb(255, 245, 178)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                System.out.println(aQueryArray + " was spanned...");
            }
        }
        return spannable;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            bitmapMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return bitmapMemoryCache.get(key);
    }

    public boolean isBitmapInMemoryCache(String key) {
        return bitmapMemoryCache.get(key) != null;
    }

    public void loadLogo(int resId, int dimen, ImageView imageView, String imageUrl, int drawable) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(dimen, imageView, imageUrl, drawable);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(Resources.getSystem(), BitmapFactory.decodeResource(context.getResources(), um.PLACE_HOLDER), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }
    public static boolean cancelPotentialWork(int data, ImageView imageView){
        final ThumbnailTask thumbnailTask = getThumbnailWorkerTask(imageView);

        if (thumbnailTask != null) {
            final int bitmapData = thumbnailTask.data;
            if (bitmapData == 0 || bitmapData != data) {
                thumbnailTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static ThumbnailTask getThumbnailWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getThumbnailTask();
            }
        }
        return null;
    }

    private class ThumbnailTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl;
        private int drawable;
        private int reqWidth;
        private int reqHeight;
        private int data = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public ThumbnailTask(int dimen, ImageView imageView, String imageUrl, int drawable) {
            this.imageUrl = imageUrl;
            this.drawable = drawable;
            this.reqWidth = (int) context.getResources().getDimension(dimen);
            this.reqHeight = (int) context.getResources().getDimension(dimen);
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(imageUrl ==null || !um.checkURL(imageUrl)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        drawable);
            } else {
                if (!isBitmapInMemoryCache(imageUrl)) {
                    addBitmapToMemoryCache(imageUrl, um.getBitmapFromURL(imageUrl, reqWidth, reqHeight));
                }
                return getBitmapFromMemoryCache(imageUrl);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap = null;
            }

            if(imageViewReference!=null && bitmap!=null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView!=null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private  final WeakReference<ThumbnailTask> thumbnailTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ThumbnailTask thumbnailTask) {
            super(res, bitmap);
            thumbnailTaskWeakReference = new WeakReference<>(thumbnailTask);
        }
        public ThumbnailTask getThumbnailTask () {
            return thumbnailTaskWeakReference.get();
        }
    }

}

