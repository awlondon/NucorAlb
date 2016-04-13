package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by alexlondon on 4/2/16.
 */
public class GridAdapterImageTitle extends BaseAdapter {
    private Context mContext;
    private List<Integer> gridData;
    private LruCache<String,Bitmap> bitmapMemoryCache;
    private DatabaseHelper dh;

    private UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    public GridAdapterImageTitle(Context mContext, List<Integer> gridData) {
        this.mContext = mContext;
        this.gridData = gridData;
        this.dh = new DatabaseHelper(mContext);

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
        return gridData.size();
    }

    @Override
    public Object getItem(int position) {
        return gridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bundle item = dh.getInstitution(InstitutionTable.ID, String.valueOf(getItem(position)));

        ImageView mImageView = new ImageView(mContext);
        int dimen = (int) mContext.getResources().getDimension(R.dimen.logo_inst_wh);
        GridView.LayoutParams imageHWparams = new GridView.LayoutParams(dimen,dimen);
        mImageView.setLayoutParams(imageHWparams);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String mStringLogo = item.getString("LOGO");

        int resId = (int) (Math.random()*10000);
        if(mStringLogo!=null) {
            resId = mStringLogo.hashCode();
        }
        loadLogo(resId, R.dimen.logo_inst_wh, mImageView, mStringLogo, R.drawable.ic_account_balance_black_48dp);
        System.out.println("Returns mImageView");

        return mImageView;
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
            final AsyncDrawable asyncDrawable = new AsyncDrawable(Resources.getSystem(), BitmapFactory.decodeResource(mContext.getResources(),
                    universalMethods.PLACE_HOLDER_ALT), task);
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
            this.reqWidth = (int) mContext.getResources().getDimension(dimen);
            this.reqHeight = (int) mContext.getResources().getDimension(dimen);
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(imageUrl == null || !universalMethods.checkURL(imageUrl)) {
                return BitmapFactory.decodeResource(mContext.getResources(),
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
