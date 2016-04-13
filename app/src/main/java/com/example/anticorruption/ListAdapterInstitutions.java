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
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
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
 * Created by alexlondon on 3/20/16.
 */
public class ListAdapterInstitutions extends BaseAdapter {
    private Context context;
    private List<Bundle> listData;

    private String query;

    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables();

    private LruCache<String, Bitmap> bitmapMemoryCache;

    Bitmap placeHolder;


    public ListAdapterInstitutions(Context context, List<Bundle> listData, String query) {
        this.context = context;
        this.listData = listData;
        this.query = query;
        placeHolder = BitmapFactory.decodeResource(context.getResources(),
                um.PLACE_HOLDER);
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
        final ViewHolder holder;

        Bundle tempBundle = listData.get(position);
        final Bundle item = dh.getInstitution(InstitutionTable.ID, tempBundle.getString("ID"));

        Institution mInstitution = new Institution(item);

        System.out.println("Feeding an institution view...");

//        String institution = item.getString("INSTITUTION");
//        String address = item.getString("ADDRESS");
//        String city = item.getString("CITY");

        String logo;
        if (mInstitution.getLogo() != null && !mInstitution.getLogo().equals("null"))
            logo = item.getString("LOGO");
        else
            logo = null;

        if (convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.institution_summary, null);

            holder = new ViewHolder(convertView);
            holder.institutionTv = (TextView) convertView.findViewById(R.id.institution);
            holder.addressTv = (TextView) convertView.findViewById(R.id.address);
            holder.cityTv = (TextView) convertView.findViewById(R.id.city);
            holder.logoIv = (ImageView) convertView.findViewById(R.id.logo);
            holder.positiveTv = (TextView) convertView.findViewById(R.id.positive);
            holder.negativeTv = (TextView) convertView.findViewById(R.id.negative);
            holder.titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);

            holder.position = position;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.titleBar.setVisibility(View.GONE);

//        float positive = Float.valueOf(item.getString("POSITIVE"));
//        float negative = Float.valueOf(item.getString("NEGATIVE"));

        CustomTypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

        String addressString = "Address: " + mInstitution.getAddress();
        SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
        addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String cityString = "City: " + mInstitution.getCity();
        SpannableStringBuilder citySpan = new SpannableStringBuilder(cityString);
        citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String posString = um.getPercentage("pos", mInstitution.getPositive(), mInstitution.getNegative()) + "%";

        String negString = um.getPercentage("neg", mInstitution.getPositive(), mInstitution.getNegative()) + "%";

        int resId = (int) (Math.random()*10000);
        if(logo != null) {
            resId = logo.hashCode();
        }
        loadLogo(resId, R.dimen.logo_inst_wh, holder.logoIv, logo);

        holder.institutionTv.setText(mInstitution.getName());
        holder.addressTv.setText(addressSpan);
        holder.cityTv.setText(citySpan);
        holder.positiveTv.setText(posString);
        holder.negativeTv.setText(negString);

        System.out.println("Finished feeding an institution view...");
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

    public void loadLogo(int resId, int dimen, ImageView imageView, String logoURL) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(dimen, imageView, logoURL);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(Resources.getSystem(), placeHolder,task);
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
        private String logoURL;
        private int reqWidth;
        private int reqHeight;
        private int data = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public ThumbnailTask(int dimen, ImageView imageView, String logoURL) {
            this.logoURL = logoURL;
            this.reqWidth = (int) context.getResources().getDimension(dimen);
            this.reqHeight = (int) context.getResources().getDimension(dimen);
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(logoURL == null || !um.checkURL(logoURL)) {
                System.out.println("Att: Logo URL not working!");
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account_balance_black_48dp);
            } else {
                if (!isBitmapInMemoryCache(logoURL)) {
                    addBitmapToMemoryCache(logoURL, um.getBitmapFromURL(logoURL, reqWidth, reqHeight));
                }
                return getBitmapFromMemoryCache(logoURL);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView institutionTv;
        public TextView addressTv;
        public TextView cityTv;
        public ImageView logoIv;
        public TextView positiveTv;
        public TextView negativeTv;
        public RelativeLayout titleBar;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}


