package org.chatminou.mpdcontrol.cover.provider;

import static org.chatminou.mpdcontrol.cover.CoverManager.getCoverFileName;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.chatminou.mpdcontrol.mpd.AlbumInfo;
import org.chatminou.mpdcontrol.MainApplication;
import org.chatminou.mpdcontrol.cover.ICoverRetriever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CachedCover implements ICoverRetriever 
{

    private MainApplication application;
    private static final String FOLDER_SUFFIX = "/covers/";

    public CachedCover(MainApplication context) {
        if (context == null)
            throw new RuntimeException("Context cannot be null");
        application = context;
    }

    public void clear() {
        final String cacheFolderPath = getAbsoluteCoverFolderPath();
        if (cacheFolderPath == null)
            return;
        final File cacheFolder = new File(cacheFolderPath);
        if (!cacheFolder.exists()) {
            return;
        }
        File[] files = cacheFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                // No need to take care of subfolders, there won't be any.
                // (Or at least any that MPDroid cares about)
                f.delete();
            }
        }
    }

    public void delete(AlbumInfo albumInfo) {
        final String cacheFolderPath = getAbsoluteCoverFolderPath();
        if (cacheFolderPath == null)
            return;
        final File cacheFolder = new File(cacheFolderPath);
        if (!cacheFolder.exists()) {
            return;
        }
        File[] files = cacheFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                // No need to take care of subfolders, there won't be any.
                // (Or at least any that MPDroid cares about)
                if (getCoverFileName(albumInfo).equals(f.getName())) {
                    Log.d(MainApplication.TAG, CachedCover.class.getSimpleName() + ": Deleting cover : " + f.getName());
                    f.delete();
                }
            }
        }
    }

    public String getAbsoluteCoverFolderPath() {
        final File cacheDir = application.getExternalCacheDir();
        if (cacheDir == null)
            return null;
        return cacheDir.getAbsolutePath() + FOLDER_SUFFIX;
    }

    public String getAbsolutePathForSong(AlbumInfo albumInfo) {
        final File cacheDir = application.getExternalCacheDir();
        if (cacheDir == null)
            return null;
        return getAbsoluteCoverFolderPath() + getCoverFileName(albumInfo);
    }

    public long getCacheUsage() {
        long size = 0;
        final String cacheDir = getAbsoluteCoverFolderPath();
        if (null != cacheDir && 0 != cacheDir.length()) {
            File[] files = new File(cacheDir).listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    }
                }
            }
        }
        return size;
    }

    @Override
    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception {
        final String storageState = Environment.getExternalStorageState();
        // If there is no external storage available, don't bother
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)
                || Environment.MEDIA_MOUNTED.equals(storageState)) {
            final String url = getAbsolutePathForSong(albumInfo);
            if (new File(url).exists())
                return new String[] {
                        url
                };
        }
        return null;
    }

    @Override
    public String getName() {
        return "SD Card Cache";
    }

    @Override
    public boolean isCoverLocal() {
        return true;
    }

    public void save(AlbumInfo albumInfo, Bitmap cover) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // External storage is not there or read only, don't do anything
        	Log.e(MainApplication.TAG, "No writable external storage, not saving cover to cache");
            return;
        }
        FileOutputStream out = null;
        try {
            new File(getAbsoluteCoverFolderPath()).mkdirs();
            out = new FileOutputStream(getAbsolutePathForSong(albumInfo));
            cover.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            Log.e(MainApplication.TAG, "Cache cover write failure : " + e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(MainApplication.TAG, "Cannot close cover stream : " + e);
                }
            }
        }
    }

}
