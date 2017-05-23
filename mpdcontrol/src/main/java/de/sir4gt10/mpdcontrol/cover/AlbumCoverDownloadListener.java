package de.sir4gt10.mpdcontrol.cover;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import de.sir4gt10.mpdcontrol.R;
import de.sir4gt10.mpdcontrol.MainApplication;
import de.sir4gt10.mpdcontrol.helpers.SettingsHelper;
import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;

public class AlbumCoverDownloadListener implements CoverDownloadListener 
{

	Context context;
    ImageView coverArt;
    ProgressBar coverArtProgress;
    boolean bigCoverNotFound;
    boolean blurCover;

    public AlbumCoverDownloadListener(Context context, ImageView coverArt) 
    {
        this.context = context;
        this.coverArt = coverArt;
        this.coverArt.setVisibility(View.VISIBLE);
        freeCoverDrawable();
    }

    public AlbumCoverDownloadListener(Context context, ImageView coverArt, ProgressBar coverArtProgress, boolean bigCoverNotFound)
    {
        this.context = context;
        this.coverArt = coverArt;
        this.bigCoverNotFound = bigCoverNotFound;
        this.coverArt.setVisibility(View.VISIBLE);
        this.coverArtProgress = coverArtProgress;
        this.coverArtProgress.setIndeterminate(true);
        this.coverArtProgress.setVisibility(ProgressBar.INVISIBLE);
        freeCoverDrawable();
    }

    public AlbumCoverDownloadListener(Context context, ImageView coverArt, ProgressBar coverArtProgress, boolean bigCoverNotFound, boolean blurCover)
    {
        this.context = context;
        this.coverArt = coverArt;
        this.bigCoverNotFound = bigCoverNotFound;
        this.blurCover = blurCover;
        this.coverArt.setVisibility(View.VISIBLE);
        this.coverArtProgress = coverArtProgress;
        this.coverArtProgress.setIndeterminate(true);
        this.coverArtProgress.setVisibility(ProgressBar.INVISIBLE);
        freeCoverDrawable();
    }

    public void detach() 
    {
        coverArtProgress = null;
        coverArt = null;
    }

    public void freeCoverDrawable() 
    {
        freeCoverDrawable(null);
    }

    private void freeCoverDrawable(Drawable oldDrawable) 
    {
        if (coverArt == null) return;
        
        final Drawable coverDrawable = oldDrawable == null ? coverArt.getDrawable() : oldDrawable;
        if (coverDrawable == null || !(coverDrawable instanceof CoverBitmapDrawable)) return;
        
        if (oldDrawable == null) 
        {
            int noCoverDrawable = 0;

            if (!blurCover) {
                if (bigCoverNotFound) noCoverDrawable = R.drawable.ic_headset_white_48dp;
                else noCoverDrawable = R.drawable.ic_headset_white_48dp;
            }
            coverArt.setImageResource(noCoverDrawable);
        }

        coverDrawable.setCallback(null);
        final Bitmap coverBitmap = ((BitmapDrawable) coverDrawable).getBitmap();
        if (coverBitmap != null) 
        {
            coverBitmap.recycle();
        }
    }

    private boolean isMatchingCover(CoverInfo coverInfo) 
    {
        return coverInfo != null && coverArt != null &&
                (coverArt.getTag() == null || coverArt.getTag().equals(coverInfo.getKey()));
    }

    @Override
    public void onCoverDownloaded(CoverInfo cover) 
    {
        if (!isMatchingCover(cover))  return;
        if (cover.getBitmap() == null) return;
  
        try 
        {
            if (coverArtProgress != null) 
            {
                coverArtProgress.setVisibility(ProgressBar.INVISIBLE);
            }
            freeCoverDrawable(coverArt.getDrawable());

            // @sir4gt10
            if (blurCover) {
                final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                String backgroundMode = settings.getString(SettingsHelper.COVER_BACKGROUND, "cover_blur");

                switch(backgroundMode) {
                    case "cover_blur":
                        Bitmap blurredCover = myblur(cover.getBitmap()[0], context);
                        coverArt.setImageDrawable(new CoverBitmapDrawable(context.getResources(), blurredCover));
                        break;
                    case "cover_pixel":
                        Bitmap pixelate = pixelate(cover.getBitmap()[0], 10);
                        coverArt.setImageDrawable(new CoverBitmapDrawable(context.getResources(), pixelate));
                        break;
                    case "cover_normal":
                        coverArt.setImageDrawable(new CoverBitmapDrawable(context.getResources(), cover.getBitmap()[0]));
                        break;
                    default:
                        // no backrgound cover
                        coverArt.setImageResource(android.R.color.transparent);
                }
            } else {
                coverArt.setImageDrawable(new CoverBitmapDrawable(context.getResources(), cover.getBitmap()[0]));
            }
            cover.setBitmap(null);
        } 
        catch (Exception e) 
        {
            Log.w(MainApplication.TAG, AlbumCoverDownloadListener.class.getSimpleName() + " " + e);
        }
    }

    @Override
    public void onCoverDownloadStarted(CoverInfo cover) 
    {
        if (!isMatchingCover(cover))  return;
        if (coverArtProgress != null) 
        {
            this.coverArtProgress.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    public void onCoverNotFound(CoverInfo cover) 
    {
        if (!isMatchingCover(cover)) return;
        cover.setBitmap(null);
        if (coverArtProgress != null)
        {
        	coverArtProgress.setVisibility(ProgressBar.INVISIBLE);
        }
        freeCoverDrawable();
    }

    @Override
    public void tagAlbumCover(AlbumInfo albumInfo) 
    {
        if (coverArt != null && albumInfo != null) 
        {
            coverArt.setTag(albumInfo.getKey());
        }
    }

    public static Bitmap pixelate(Bitmap image, Integer factor) {
        image = Bitmap.createScaledBitmap(image, image.getWidth()/factor, image.getHeight()/factor, false);
        image = Bitmap.createScaledBitmap(image, image.getWidth()*factor, image.getHeight()*factor, false);

        return image;
    }

    public static Bitmap myblur(Bitmap image, Context context) {
        final float BITMAP_SCALE = 0.4f;
        final float BLUR_RADIUS = 7f;
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}
