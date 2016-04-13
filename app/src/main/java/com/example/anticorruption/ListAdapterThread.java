package com.example.anticorruption;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by alexlondon on 2/16/16.
 */
public class ListAdapterThread extends BaseAdapter {
    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables();

    public Bundle bundle;

    private LruCache<String, Bitmap> bitmapMemoryCache;

    private Context context;
    private List<Bundle> listData;

    public ListAdapterThread(Context context, List<Bundle> listData) {
        this.context = context;
        this.listData = listData;

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
        final DatabaseHelper dh = new DatabaseHelper(context);
        Bundle item = (Bundle) getItem(position);

        int threadPosition = 0;

        final Integer id = item.getInt("ID");
        String title = item.getString("TITLE");
        final String content = item.getString("CONTENT");
        String username = item.getString("USERNAME");
        final String thread = item.getString("THREAD");
        final String previous = item.getString("PREVIOUS");
        String up_votes = item.getString("UP_VOTES");
        String down_votes = item.getString("DOWN_VOTES");
        String date = item.getString("DATE");

        bundle = dh.getUser(UserTable.USERNAME, username);
        String profileIcon = bundle.getString("ICON");

        if (previous != null) {
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
        final ImageView profileIv = (ImageView) convertView.findViewById(R.id.profileIcon);
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
        dateTv.setText(um.getTimeFromNow(date));

        int resId = (int) (Math.random()*10000);
        if(profileIcon!=null) {
            resId = profileIcon.hashCode();
        }
        loadLogo(resId, R.dimen.post_icon_wh,profileIv, username, profileIcon);

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

        up_votesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = dh.getPost(PostTable.ID, String.valueOf(id));
                String oldUpVotes = bundle.getString("UP_VOTES");
                String oldDownVotes = bundle.getString("DOWN_VOTES");

                dh.updatePostRatingAndUserHistory(id, PostTable.UP_VOTES, UserData.username);
                resetVotesAlpha(id, UserData.username, up_votesIv, down_votesIv);

                bundle = dh.getPost(PostTable.ID, String.valueOf(id));
                String newUpVotes = bundle.getString("UP_VOTES");
                String newDownVotes = bundle.getString("DOWN_VOTES");
                if (!oldUpVotes.equals(newUpVotes)) up_votesSwitcher.setText(newUpVotes);
                if (!oldDownVotes.equals(newDownVotes)) down_votesSwitcher.setText(newDownVotes);
            }
        });

        down_votesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = dh.getPost(PostTable.ID, String.valueOf(id));
                String oldUpVotes = bundle.getString("UP_VOTES");
                String oldDownVotes = bundle.getString("DOWN_VOTES");

                dh.updatePostRatingAndUserHistory(id, PostTable.DOWN_VOTES, UserData.username);
                resetVotesAlpha(id, UserData.username, up_votesIv, down_votesIv);

                bundle = dh.getPost(PostTable.ID, String.valueOf(id));
                String newUpVotes = bundle.getString("UP_VOTES");
                String newDownVotes = bundle.getString("DOWN_VOTES");
                if (!oldUpVotes.equals(newUpVotes)) up_votesSwitcher.setText(newUpVotes);
                if (!oldDownVotes.equals(newDownVotes)) down_votesSwitcher.setText(newDownVotes);
            }
        });

        profileIv.setOnClickListener(viewUser);

        dh.updateViews(id, PostTable.TABLE_NAME, PostTable.ID, PostTable.VIEWS);

        return convertView;
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

    public void resetVotesAlpha(Integer id, String username, ImageView uv, ImageView dv) {
        final DatabaseHelper dh = new DatabaseHelper(context);
        String rating = dh.checkUserPostRated(username, id);
        if (rating != null) {
            if (rating.equals(PostTable.UP_VOTES)) {
                uv.setImageAlpha(200);
                dv.setImageAlpha(100);

            } else if (rating.equals(PostTable.DOWN_VOTES)) {
                uv.setImageAlpha(100);
                dv.setImageAlpha(200);
            }
        } else {
            uv.setImageAlpha(100);
            dv.setImageAlpha(100);
        }
    }

    ImageView.OnClickListener viewUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(context).setView(displayUserInfo()).show();
        }
    };

    private View displayUserInfo() {
        View convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.activity_profile, null);

        final TextView usernameTv = (TextView) convertView.findViewById(R.id.usernameProfile);
        final TextView firstnameTv = (TextView) convertView.findViewById(R.id.firstnameProfile);
        final TextView lastnameTv = (TextView) convertView.findViewById(R.id.lastnameProfile);
        final TextView locationTv = (TextView) convertView.findViewById(R.id.locationProfile);
        final TextView emailTv = (TextView) convertView.findViewById(R.id.emailProfile);
        final ImageView profileIv = (ImageView) convertView.findViewById(R.id.imageProfile);
        final TextView enrollmentTv = (TextView) convertView.findViewById(R.id.enrolledProfile);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) convertView.findViewById(R.id.tool_bar);
        toolbar.setVisibility(View.GONE);
        LinearLayout buttonMenu = (LinearLayout)convertView.findViewById(R.id.button_menu);
        buttonMenu.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String username = bundle.getString("USERNAME");
                final String first_name = bundle.getString("FIRST_NAME");
                final String last_name = bundle.getString("LAST_NAME");
                final String location = bundle.getString("LOCATION");
                final String email = bundle.getString("EMAIL");
                final String date = bundle.getString("ENROLLMENT");
                final String profileIcon = bundle.getString("ICON");

                TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String usernameString = "Username: " + username;
                final SpannableStringBuilder usernameSpan = new SpannableStringBuilder(usernameString);
                usernameSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String firstnameString = "First Name: " + first_name;
                final SpannableStringBuilder firstnameSpan = new SpannableStringBuilder(firstnameString);
                firstnameSpan.setSpan(tfBold, 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String lastnameString = "Last Name: " + last_name;
                final SpannableStringBuilder lastnameSpan = new SpannableStringBuilder(lastnameString);
                lastnameSpan.setSpan(tfBold, 0, 11, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String locationString = "Location: " + location;
                final SpannableStringBuilder locationSpan = new SpannableStringBuilder(locationString);
                locationSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String emailString = "Email: " + email;
                final SpannableStringBuilder emailSpan = new SpannableStringBuilder(emailString);
                emailSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                emailSpan.setSpan(um.blueSpan, 7, emailSpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String dateString = "User Since: " + um.sdf.format(Timestamp.valueOf(date));
                final SpannableStringBuilder dateSpan = new SpannableStringBuilder(dateString);
                dateSpan.setSpan(tfBold, 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                if (profileIv != null && um.checkURL(profileIcon)) {
                    new Thread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                        @Override
                        public void run() {
                            final Bitmap bitmap = um.getBitmapFromURL(profileIcon, profileIv.getWidth(), profileIv.getHeight());
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profileIv.setImageBitmap(bitmap);
                                    profileIv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                    profileIv.getAnimation().setDuration(500);
                                    profileIv.animate().start();

                                    final UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(context);
                                    emailTv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            um.sendEmail(email);
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                } else {
                    assert profileIv != null;
                    profileIv.setImageResource(R.drawable.ic_person_black_36dp);
                }

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameTv.setText(usernameSpan);
                        firstnameTv.setText(firstnameSpan);
                        lastnameTv.setText(lastnameSpan);
                        locationTv.setText(locationSpan);
                        emailTv.setText(emailSpan);
                        enrollmentTv.setText(dateSpan);
                    }
                });
            }
        }).start();
        return convertView;
    }


}
