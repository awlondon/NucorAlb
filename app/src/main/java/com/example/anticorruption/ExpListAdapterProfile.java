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
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class ExpListAdapterProfile extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Bundle>> listDataChild;

    private LruCache<String, Bitmap> bitmapMemoryCache;

    private UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    public ExpListAdapterProfile(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Bundle>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;

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
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DatabaseHelper dh = new DatabaseHelper(context);
        final Bundle item = (Bundle) getChild(groupPosition, childPosition);
        // Define all flexible variables
        String address, story, institution, corruptionType, title, introduction, category, post, thread, logo;
        String date, dateString;
        TextView positiveTv, negativeTv, addressTv, enrolledTv, storyTv, institutionTv, typeTv, dateTv, titleTv, introductionTv, categoryTv, usernameTv, postTv, threadTv, upVotesTv, downVotesTv;
        Float positive, negative;
        Integer upVotes, downVotes;
        LayoutInflater infalInflater;
        RelativeLayout titleBar;

        String s = item.getString("TABLE_NAME");
        if (s.equals(InstitutionTable.TABLE_NAME)) {
            System.out.println("Feeding a new institution summary view...");

            Integer id = item.getInt("ID");
            institution = item.getString("INSTITUTION");
            address = item.getString("ADDRESS");
            String city = item.getString("CITY");

            if (item.getString("LOGO") != null)
                logo = item.getString("LOGO");
            else
                logo = null;

            positive = Float.valueOf(item.getString("POSITIVE"));
            negative = Float.valueOf(item.getString("NEGATIVE"));

            infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.institution_summary, null);

            institutionTv = (TextView) convertView.findViewById(R.id.institution);
            addressTv = (TextView) convertView.findViewById(R.id.address);
            TextView cityTv = (TextView) convertView.findViewById(R.id.city);
            final ImageView logoIv = (ImageView) convertView.findViewById(R.id.logo);
            positiveTv = (TextView) convertView.findViewById(R.id.positive);
            negativeTv = (TextView) convertView.findViewById(R.id.negative);
            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

            titleBar.setVisibility(View.GONE);

            CustomTypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String addressString = "Address: " + address;
            SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
            addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String cityString = "City: " + city;
            SpannableStringBuilder citySpan = new SpannableStringBuilder(cityString);
            citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String posString = universalMethods.getPercentage("pos", positive, negative) + "%";
            SpannableStringBuilder posSpan = new SpannableStringBuilder(posString);
            //posSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String negString = universalMethods.getPercentage("neg", positive, negative) + "%";
            SpannableStringBuilder negSpan = new SpannableStringBuilder(negString);
            //negSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            institutionTv.setText(institution);
            addressTv.setText(addressSpan);
            cityTv.setText(citySpan);

            int resId = (int) (Math.random()*10000);
            if(logoIv!=null) {
                resId = logoIv.hashCode();
            }
            loadLogo(resId, R.dimen.logo_inst_wh, logoIv, institution, logo, R.drawable.ic_account_balance_black_48dp);

            positiveTv.setText(posSpan);
            negativeTv.setText(negSpan);

            System.out.println("Finished feeding an institution summary view...");

        } else if (s.equals(WikiTable.TABLE_NAME)) {
            CustomTypefaceSpan tfBold;
            Integer id;
            System.out.println("Starting to feed a new wiki view...");
            //convertView = null;
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
            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

            titleBar.setVisibility(View.GONE);

            // format text entries with SpannableStringBuilder
            tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

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

            dateString = universalMethods.getTimeFromNow(date);

            // set text to TextViews
            titleTv.setText(titleSpan);
            introductionTv.setText(introductionSpan);
            categoryTv.setText(categorySpan);
            dateTv.setText(dateString);
            System.out.println("Finished feeding a new wiki view...");

        } else if (s.equals(PostTable.TABLE_NAME)) {
            CustomTypefaceSpan tfBold;
            Integer id;
            System.out.println("Feeding a new forum post view...");

            infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.post_summary, null);

            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);
            titleBar.setVisibility(View.GONE);

            usernameTv = (TextView) convertView.findViewById(R.id.username);
            postTv = (TextView) convertView.findViewById(R.id.content);
            threadTv = (TextView) convertView.findViewById(R.id.thread);
            final ImageView profileIv = (ImageView) convertView.findViewById(R.id.profileIcon);
            dateTv = (TextView) convertView.findViewById(R.id.date);
            upVotesTv = (TextView) convertView.findViewById(R.id.upVotes);
            downVotesTv = (TextView) convertView.findViewById(R.id.downVotes);

            final String username = item.getString("USERNAME");

            final Bundle bundle = dh.getUser(UserTable.USERNAME, username);
            final String profileIcon = bundle.getString("ICON");

            int resId = (int) (Math.random()*10000);
            if(profileIv!=null) {
                resId = profileIv.hashCode();
            }
            loadLogo(resId, R.dimen.post_icon_wh, profileIv, username, profileIcon, R.drawable.ic_person_black_36dp);

            id = item.getInt("ID");
            post = item.getString("CONTENT");
            thread = item.getString("THREAD");
            date = item.getString("DATE");
            upVotes = Integer.valueOf(item.getString("UP_VOTES"));
            downVotes = Integer.valueOf(item.getString("DOWN_VOTES"));

            tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String postSub = post.substring(0, Math.min(post.length(), 100)) + "...";

            String threadString = "Thread: " + thread;
            SpannableStringBuilder threadSpan = new SpannableStringBuilder(threadString);
            threadSpan.setSpan(tfBold, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            dateString = universalMethods.getTimeFromNow(date);

            usernameTv.setText(username);
            postTv.setText(postSub);
            upVotesTv.setText(String.valueOf(upVotes));
            downVotesTv.setText(String.valueOf(downVotes));
            dateTv.setText(dateString);
            threadTv.setText(threadSpan);

            System.out.println("Finished feeding a new forum view...");

        }

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        int childrenCount = getChildrenCount(groupPosition);
        String childrenCountText;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView groupChildren = (TextView) convertView
                .findViewById(R.id.groupChildren);
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        if (childrenCount < 10) {
            childrenCountText = String.valueOf(childrenCount);
            groupChildren.setPaddingRelative(10, 5, 10, 5);
        } else {
            childrenCountText = String.valueOf(childrenCount);
        }

        lblListHeader.setText(headerTitle);
        groupChildren.setText(childrenCountText);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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

    public void loadLogo(int resId, int dimen, ImageView imageView, String imageKey, String imageUrl, int drawable) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(dimen, imageView, imageKey, imageUrl, drawable);
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
        private String imageKey;
        private String imageUrl;
        private int reqWidth;
        private int reqHeight;
        private int drawable;
        private int data = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public ThumbnailTask(int dimen, ImageView imageView, String imageKey, String imageUrl, int drawable) {
            this.imageKey = imageKey;
            this.imageUrl = imageUrl;
            this.drawable = drawable;
            this.reqWidth = (int) context.getResources().getDimension(dimen);
            this.reqHeight = (int) context.getResources().getDimension(dimen);
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(imageUrl ==null || imageKey == null || !universalMethods.checkURL(imageUrl)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        drawable);
            } else {
                if (!isBitmapInMemoryCache(imageKey)) {
                    addBitmapToMemoryCache(imageKey, universalMethods.getBitmapFromURL(imageUrl, reqWidth, reqHeight));
                }
                return getBitmapFromMemoryCache(imageKey);
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

