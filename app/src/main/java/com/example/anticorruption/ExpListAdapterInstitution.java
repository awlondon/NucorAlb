package com.example.anticorruption;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
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

public class ExpListAdapterInstitution extends BaseExpandableListAdapter {

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, Integer[]> listDataChild;

    private LruCache<String, Bitmap> bitmapMemoryCache;

    private UniversalMethodsAndVariables um = new UniversalMethodsAndVariables();

    public ExpListAdapterInstitution(Context context, List<String> listDataHeader,
                                     HashMap<String, Integer[]> listChildData) {
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
                [childPosititon];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final int STORY = 0;
        final int WIKI = 1;
        final int POST = 2;

        final DatabaseHelper dh = new DatabaseHelper(context);
        final Integer mId = (Integer) getChild(groupPosition, childPosition);

        // Define all flexible variables
        String  story, institution, corruptionType, title, introduction, category, post, thread;
        String date;
        TextView storyTv, institutionTv, typeTv, dateTv, titleTv, introductionTv, categoryTv, usernameTv, postTv, threadTv;
        RelativeLayout titleBar;

        if (groupPosition == STORY) {
            final Bundle item = dh.getStory(StoryTable.ID, String.valueOf(mId));
            System.out.println("Starting to feed a new story view...");
            // Inflate appropriate layout
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.story_summary, null);

            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);
            titleBar.setVisibility(View.GONE);

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
            // format text entries with SpannableStringBuilder
            TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

            String storySub = story.substring(0, Math.min(story.length(), 100)) + "...";

            String institutionString = "Institution: " + institution;
            SpannableStringBuilder institutionSpan = new SpannableStringBuilder(institutionString);
            institutionSpan.setSpan(tfBold, 0, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String corruptionString = "Type: " + corruptionType;
            SpannableStringBuilder corruptionSpan = new SpannableStringBuilder(corruptionString);
            corruptionSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            String dateString = universalMethods.getTimeFromNow(date);

            // set text to TextViews
            storyTv.setText(storySub);
            institutionTv.setText(institutionSpan);
            typeTv.setText(corruptionSpan);
            dateTv.setText(dateString);
            System.out.println("Finished feeding a new story view...");

        } else if (groupPosition == WIKI) {
            final Bundle item = dh.getWiki(WikiTable.ID, String.valueOf(mId));
            TypefaceSpan tfBold;
            String dateString;
            LayoutInflater infalInflater;
            System.out.println("Starting to feed a new wiki view...");
            //convertView = null;
            infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.wiki_summary, null);

            titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);
            titleBar.setVisibility(View.GONE);

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

        } else if (groupPosition == POST) {
            final Bundle item = dh.getPost(PostTable.ID, String.valueOf(mId));
            String dateString;
            TypefaceSpan tfBold;
            LayoutInflater infalInflater;
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
            TextView upVotesTv = (TextView) convertView.findViewById(R.id.upVotes);
            TextView downVotesTv = (TextView) convertView.findViewById(R.id.downVotes);


            final String username = item.getString("USERNAME");
            System.out.println(username);

            Bundle bundle = dh.getUser(UserTable.USERNAME, username);
            String profileIcon = bundle.getString("ICON");

            int resId = (int) (Math.random()*10000);
            if(profileIcon!=null) {
                resId = profileIcon.hashCode();
            }
            loadLogo(resId, R.dimen.post_icon_wh, profileIv, username, profileIcon);

            int id = item.getInt("ID");
            post = item.getString("CONTENT");
            thread = item.getString("THREAD");
            date = item.getString("DATE");
            int upVotes = Integer.valueOf(item.getString("UP_VOTES"));
            int downVotes = Integer.valueOf(item.getString("DOWN_VOTES"));

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
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).length;
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
        String childrenCountText = null;
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

    public void loadLogo(int resId, int dimen, ImageView imageView, String inst, String logoURL) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(dimen, imageView, inst, logoURL);
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
        private String inst;
        private String logoURL;
        private int reqWidth;
        private int reqHeight;
        private int data = 0;

        public ThumbnailTask(int dimen, ImageView imageView, String inst, String logoURL) {
            this.inst = inst;
            this.logoURL = logoURL;
            this.reqWidth = (int) context.getResources().getDimension(dimen);
            this.reqHeight = (int) context.getResources().getDimension(dimen);
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(logoURL==null || inst == null || !um.checkURL(logoURL)) {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_black_36dp);
            } else {
                if (!isBitmapInMemoryCache(inst)) {
                    addBitmapToMemoryCache(inst, um.getBitmapFromURL(logoURL,reqWidth, reqHeight));
                }
                return getBitmapFromMemoryCache(inst);
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