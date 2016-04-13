package com.example.anticorruption;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.HashMap;
import java.util.List;

/**
 * Created by alexlondon on 2/17/16.
 */
public class ExpListAdapterComments extends BaseExpandableListAdapter {

    private LruCache<String, Bitmap> memoryCache;

    private LruCache<String, View> viewMemoryCache;

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Bundle>> listDataChild;

    private String thread;


    public ExpListAdapterComments(Context context, List<String> listDataHeader,
                                  HashMap<String, List<Bundle>> listChildData, String thread) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.thread = thread;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        viewMemoryCache = new LruCache<String, View>(cacheSize) {
            @Override
            protected int sizeOf(String key, View view) {
                return 0;
            }
        };
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
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
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        int childrenCount = getChildrenCount(groupPosition);
        String childrenCountText;

        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.list_group, null);

        TextView groupChildren = (TextView) convertView
                .findViewById(R.id.groupChildren);
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        if (childrenCount == 0) {
            childrenCountText = "+";
            groupChildren.setPaddingRelative(10, 5, 10, 5);
            groupChildren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ThreadPostEditActivity.class);
                    intent.putExtra("THREAD", thread);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        } else if (childrenCount < 10) {
            childrenCountText = String.valueOf(childrenCount);
            groupChildren.setPaddingRelative(10, 5, 10, 5);
        } else {
            childrenCountText = String.valueOf(childrenCount);
        }

        lblListHeader.setText(headerTitle);
        groupChildren.setText(childrenCountText);

        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final DatabaseHelper dh = new DatabaseHelper(context);
        Bundle child = (Bundle) getChild(groupPosition, childPosition);

        int threadPosition = 0;

        final Integer id = child.getInt("ID");
        String title = child.getString("TITLE");
        final String content = child.getString("CONTENT");
        String username = child.getString("USERNAME");
        final String thread = child.getString("THREAD");
        final String previous = child.getString("PREVIOUS");
        String up_votes = child.getString("UP_VOTES");
        String down_votes = child.getString("DOWN_VOTES");
        String date = child.getString("DATE");

        Bundle bundle = dh.getUser(UserTable.USERNAME, username);
        String profileIcon = bundle.getString("ICON");

        if (previous != null && !previous.equals("null")) {
            String prev = previous;
            do {
                Bundle bun = dh.getPost(PostTable.ID, prev);
                prev = bun.getString("PREVIOUS");
                threadPosition++;
                System.out.println("Recognizing it is a reply...");
            } while (prev != null);
        }

        boolean leftSide = false;
        if (threadPosition % 2 == 0) leftSide = true;

        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Set orientation to left or right
        if (leftSide) {
            convertView = infalInflater.inflate(R.layout.thread_post_view_left, null);
        } else {
            convertView = infalInflater.inflate(R.layout.thread_post_view_right, null);
        }

        TextView usernameTv = (TextView) convertView.findViewById(R.id.username);
        TextView dateTv = (TextView) convertView.findViewById(R.id.date);
        TextView contentTv = (TextView) convertView.findViewById(R.id.content);
        final TextSwitcher up_votesSwitcher = (TextSwitcher) convertView.findViewById(R.id.upVotes);
        final TextSwitcher down_votesSwitcher = (TextSwitcher) convertView.findViewById(R.id.downVotes);
        TextView titleTv = (TextView) convertView.findViewById(R.id.titleText);
        ImageView profileIconIv = (ImageView) convertView.findViewById(R.id.profileIcon);
        final ImageView up_votesIv = (ImageView) convertView.findViewById(R.id.upVotesIv);
        final ImageView down_votesIv = (ImageView) convertView.findViewById(R.id.downVotesIv);
        ImageView replyIv = (ImageView) convertView.findViewById(R.id.replyIv);

        up_votesSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(context);
                textView.setTextSize(14);
                textView.setTextColor(ContextCompat.getColor(context, R.color.green));
                return textView;
            }
        });

        down_votesSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(context);
                textView.setTextSize(14);
                textView.setTextColor(ContextCompat.getColor(context, R.color.red));
                return textView;
            }
        });

        Animation out = AnimationUtils.loadAnimation(context, R.anim.top_out);
        Animation in = AnimationUtils.loadAnimation(context, R.anim.bottom_in);
        up_votesSwitcher.setInAnimation(in);
        up_votesSwitcher.setOutAnimation(out);
        down_votesSwitcher.setInAnimation(in);
        down_votesSwitcher.setOutAnimation(out);

        resetVotesAlpha(id, UserData.username, up_votesIv, down_votesIv);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutBox);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.leftMargin = 10 * threadPosition;
        layout.setLayoutParams(params);

        usernameTv.setText(username);
        contentTv.setText(content);
        up_votesSwitcher.setCurrentText(up_votes);
        down_votesSwitcher.setCurrentText(down_votes);
        titleTv.setText(title);
        dateTv.setText(universalMethods.getTimeFromNow(date));

        if (profileIcon != null) {
            addBitmapToMemoryCache(username, universalMethods.getBitmapFromURL(profileIcon, profileIconIv.getWidth(), profileIconIv.getHeight()));
            profileIconIv.setImageBitmap(getBitmapFromMemCache(username));
        } else
            profileIconIv.setImageResource(R.drawable.ic_person_black_36dp);

        replyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThreadPostEditActivity.class);
                intent.putExtra("THREAD", thread);
                intent.putExtra("PREVIOUS", id);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    public View getViewFromMemoryCache(String key) {
        return viewMemoryCache.get(key);
    }

    public void addViewToMemoryCache(String key, View view) {
        if (getViewFromMemoryCache(key) == null) {
            viewMemoryCache.put(key, view);
        }
    }

    public void resetVotesAlpha(Integer id, String username, ImageView uv, ImageView dv) {
        final DatabaseHelper dh = new DatabaseHelper(context);
        String rating = dh.checkUserPostRated(UserData.username, id);
        if (rating != null) {
            if (rating.equals(PostTable.UP_VOTES)) {
                uv.setImageAlpha(255);
                dv.setImageAlpha(150);

            } else if (rating.equals(PostTable.DOWN_VOTES)) {
                uv.setImageAlpha(150);
                dv.setImageAlpha(255);
            }
        } else {
            uv.setImageAlpha(150);
            dv.setImageAlpha(150);
        }
    }

}
