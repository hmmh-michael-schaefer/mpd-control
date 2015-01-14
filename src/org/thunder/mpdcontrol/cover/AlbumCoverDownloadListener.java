package org.thunder.mpdcontrol.cover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.thunder.mpdcontrol.R;
import org.thunder.mpdcontrol.MainApplication;
import org.thunder.mpdcontrol.mpd.AlbumInfo;

public class AlbumCoverDownloadListener implements CoverDownloadListener 
{

	Context context;
    ImageView coverArt;
    ProgressBar coverArtProgress;
    boolean bigCoverNotFound;

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
            int noCoverDrawable;
            if (bigCoverNotFound) noCoverDrawable = R.drawable.no_cover_art_big;
            else noCoverDrawable = R.drawable.no_cover_art;
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
            coverArt.setImageDrawable(new CoverBitmapDrawable(context.getResources(), cover.getBitmap()[0]));
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
    
}
