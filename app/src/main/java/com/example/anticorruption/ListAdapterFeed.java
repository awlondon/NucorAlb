package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by alexlondon on 2/5/16.
 */
public class ListAdapterFeed extends BaseAdapter {

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    private Context context;
    private List<Bundle> listData;

    private LruCache<String, Bitmap> bitmapMemoryCache;

    public TextView storyTv;
    public TextView institutionNameTv;
    public TextView typeTv;
    public TextView dateTv;

    public TextView titleTv;
    public TextView categoryTv;
    public TextView introductionTv;

    public RelativeLayout mainLayout;
    public RelativeLayout titleBar;

    public ProgressBar progressBar;

    public TextView usernameTv;
    public TextView postTv;
    public TextView threadTv;

    public ImageView profileIv;
    public TextView upVotesTv;
    public TextView downVotesTv;
    public TextView institutionTv;
    public TextView addressTv;
    public TextView cityTv;

    public ImageView logoIv;
    public TextView positiveTv;
    public TextView negativeTv;

    public ListAdapterFeed(Context context, List<Bundle> listData) {
        this.context = context;
        this.listData = listData;

         int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

         int cacheSize = maxMemory / 8;

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
        Bundle item = (Bundle) getItem(position);

        String s = item.getString("TABLE_NAME");
        if (s.equals(StoryTable.TABLE_NAME)) {
            System.out.println("Starting to feed a new story view...");

            LayoutInflater infalInflater = LayoutInflater.from(context);
            convertView = infalInflater.inflate(R.layout.story_summary, parent, false);

            storyTv = (TextView) convertView.findViewById(R.id.story);
            institutionNameTv = (TextView) convertView.findViewById(R.id.institution);
            typeTv = (TextView) convertView.findViewById(R.id.type);
            dateTv = (TextView) convertView.findViewById(R.id.date);
            mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

            String story = item.getString("CONTENT");
            String institution = item.getString("INSTITUTION");
            String corruptionType = item.getString("TYPE");
            String date = item.getString("DATE");

            TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String storySub = story.substring(0, Math.min(story.length(), 100)) + "...";

            String institutionString = "Institution: " + institution;
            SpannableStringBuilder institutionSpan = new SpannableStringBuilder(institutionString);
            institutionSpan.setSpan(tfBold, 0, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String corruptionString = "Type: " + corruptionType;
            SpannableStringBuilder corruptionSpan = new SpannableStringBuilder(corruptionString);
            corruptionSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String dateString = universalMethods.getTimeFromNow(date);
            SpannableStringBuilder dateSpan = new SpannableStringBuilder(dateString);

            storyTv.setText(storySub);
            institutionNameTv.setText(institutionSpan);
            typeTv.setText(corruptionSpan);
            dateTv.setText(dateSpan);

            System.out.println("Finished feeding a new story view...");

        } else if (s.equals(WikiTable.TABLE_NAME)) {
            System.out.println("Starting to feed a new wiki view...");

            LayoutInflater infalInflater = LayoutInflater.from(context);
            convertView = infalInflater.inflate(R.layout.wiki_summary, parent, false);

            titleTv = (TextView) convertView.findViewById(R.id.titleText);
            introductionTv = (TextView) convertView.findViewById(R.id.introduction);
            categoryTv = (TextView) convertView.findViewById(R.id.category);
            dateTv = (TextView) convertView.findViewById(R.id.date);
            mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            String title = item.getString("TITLE");
            String introduction = item.getString("CONTENT");
            String category = item.getString("CATEGORY");
            String date = item.getString("CREATED");

            CustomTypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String introductionSub = introduction.substring(0, Math.min(introduction.length(), 100)) + "...";

            String titleString = "Title: " + title;
            SpannableStringBuilder titleSpan = new SpannableStringBuilder(titleString);
            titleSpan.setSpan(tfBold, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String introductionString = "Introduction: " + introductionSub;
            SpannableStringBuilder introductionSpan = new SpannableStringBuilder(introductionString);
            introductionSpan.setSpan(tfBold, 0, 14, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String categoryString = "Category: " + category;
            SpannableStringBuilder categorySpan = new SpannableStringBuilder(categoryString);
            categorySpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            String dateString = universalMethods.getTimeFromNow(date);

            titleTv.setText(titleSpan);
            introductionTv.setText(introductionSpan);
            categoryTv.setText(categorySpan);
            dateTv.setText(dateString);

            System.out.println("Finished feeding a new wiki view...");

        } else {
            if (s.equals(PostTable.TABLE_NAME)) {
                System.out.println("Feeding a new forum post view...");
                    LayoutInflater infalInflater = LayoutInflater.from(context);
                    convertView = infalInflater.inflate(R.layout.post_summary, parent, false);

                usernameTv = (TextView) convertView.findViewById(R.id.username);
                postTv = (TextView) convertView.findViewById(R.id.content);
                threadTv = (TextView) convertView.findViewById(R.id.thread);
                profileIv = (ImageView) convertView.findViewById(R.id.profileIcon);
                dateTv = (TextView) convertView.findViewById(R.id.date);
                upVotesTv = (TextView) convertView.findViewById(R.id.upVotes);
                downVotesTv = (TextView) convertView.findViewById(R.id.downVotes);
                mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
                titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

                String username = item.getString("USERNAME");

                Bundle bundle = dh.getUser(UserTable.USERNAME, username);
                String profileIcon;
                if (bundle.getString("ICON") != null)
                    profileIcon = bundle.getString("ICON");
                else
                    profileIcon = null;

                String post = item.getString("CONTENT");
                String thread = item.getString("THREAD");
                String date = item.getString("DATE");
                int upVotes = Integer.valueOf(item.getString("UP_VOTES"));
                int downVotes = Integer.valueOf(item.getString("DOWN_VOTES"));

                CustomTypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String postSub = post.substring(0, Math.min(post.length(), 100)) + "...";

                String threadString = "Thread: " + thread;
                SpannableStringBuilder threadSpan = new SpannableStringBuilder(threadString);
                threadSpan.setSpan(tfBold, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String dateString = universalMethods.getTimeFromNow(date);

                int resId = (int) (Math.random()*10000);
                if(profileIcon!=null) {
                    resId = profileIcon.hashCode();
                }
                loadLogo(resId, R.dimen.post_icon_wh, profileIv, profileIcon, R.drawable.ic_person_black_36dp);

                usernameTv.setText(username);
                postTv.setText(postSub);
                upVotesTv.setText(String.valueOf(upVotes));
                downVotesTv.setText(String.valueOf(downVotes));
                dateTv.setText(dateString);
                threadTv.setText(threadSpan);

                System.out.println("Finished feeding a new forum view...");

            } else if (s.equals(InstitutionTable.TABLE_NAME)) {
                System.out.println("Feeding a new institution summary view...");

                LayoutInflater infalInflater = LayoutInflater.from(context);
                convertView = infalInflater.inflate(R.layout.institution_summary, parent, false);

                institutionTv = (TextView) convertView.findViewById(R.id.institution);
                addressTv = (TextView) convertView.findViewById(R.id.address);
                cityTv = (TextView) convertView.findViewById(R.id.city);
                logoIv = (ImageView) convertView.findViewById(R.id.logo);
                positiveTv = (TextView) convertView.findViewById(R.id.positive);
                negativeTv = (TextView) convertView.findViewById(R.id.negative);
                mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
                titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

                String institution = item.getString("INSTITUTION");
                String address = item.getString("ADDRESS");
                String city = item.getString("CITY");

                String logo;
                if (item.getString("LOGO") != null)
                    logo = item.getString("LOGO");
                else
                    logo = null;

                float positive = Float.valueOf(item.getString("POSITIVE"));
                float negative = Float.valueOf(item.getString("NEGATIVE"));

                CustomTypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String addressString = "Address: " + address;
                SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
                addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String cityString = "City: " + city;
                SpannableStringBuilder citySpan = new SpannableStringBuilder(cityString);
                citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String posString = universalMethods.getPercentage("pos", positive, negative) + "%";

                String negString = universalMethods.getPercentage("neg", positive, negative) + "%";

                int resId = (int) (Math.random()*10000);
                if(logo!=null) {
                    resId = logo.hashCode();
                }
                loadLogo(resId, R.dimen.logo_inst_wh,logoIv, logo, R.drawable.ic_account_balance_black_48dp);

                institutionTv.setText(institution);
                addressTv.setText(addressSpan);
                cityTv.setText(citySpan);
                positiveTv.setText(posString);
                negativeTv.setText(negString);

                System.out.println("Finished feeding an institution summary view...");
            }
        }
        //convertView.setOnLongClickListener(new UniversalMethodsAndVariables.SwipeAway(mContext, position, listData, this));
        return convertView;
    }

    public String addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            bitmapMemoryCache.put(key, bitmap);
        }
        return key;
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
            final AsyncDrawable asyncDrawable = new AsyncDrawable(Resources.getSystem(), BitmapFactory.decodeResource(context.getResources(),
                    universalMethods.PLACE_HOLDER), task);
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
        private int reqWidth;
        private int reqHeight;
        private int drawable;
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
            if(imageUrl == null || !universalMethods.checkURL(imageUrl)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        drawable);
            } else {
                if (!isBitmapInMemoryCache(imageUrl)) {
                    addBitmapToMemoryCache(imageUrl, universalMethods.getBitmapFromURL(imageUrl, reqWidth, reqHeight));
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
            super (res, bitmap);
            thumbnailTaskWeakReference = new WeakReference<>(thumbnailTask);

        }
        public ThumbnailTask getThumbnailTask () {
            return thumbnailTaskWeakReference.get();
        }
    }
}
