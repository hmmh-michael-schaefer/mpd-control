package de.sir4gt10.mpdcontrol.adapters;

import de.sir4gt10.mpdcontrol.R;
import de.sir4gt10.mpdcontrol.MainActivity;
import de.sir4gt10.mpdcontrol.cover.AlbumCoverDownloadListener;
import de.sir4gt10.mpdcontrol.cover.CoverInfo;
import de.sir4gt10.mpdcontrol.fragments.LibraryFragment;
import de.sir4gt10.mpdcontrol.helpers.CoverAsyncHelper;
import de.sir4gt10.mpdcontrol.helpers.SettingsHelper;
import de.sir4gt10.mpdcontrol.models.LibraryAlbum;
import de.sir4gt10.mpdcontrol.models.LibraryTrack;
import de.sir4gt10.mpdcontrol.mpd.Music;
import de.sir4gt10.mpdcontrol.view.holders.AlbumViewHolder;
import de.sir4gt10.mpdcontrol.view.holders.TrackViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LibraryAlbumAdapter extends BaseExpandableListAdapter
{
	
	private LibraryFragment fragment = null;
	private LibraryAlbum album = null;
	private CoverAsyncHelper coverHelper = null;
			
	///////////////////////////////////////////////////////////////////////////
	
    public LibraryAlbumAdapter(LibraryFragment fragment, LibraryAlbum album)
	{
    	this.fragment = fragment;
		this.album = album;
        this.coverHelper = new CoverAsyncHelper(fragment.getApplication(), fragment.getSettings());
        this.coverHelper.setCoverMaxSize(128);
        this.coverHelper.obtainMessage(CoverAsyncHelper.EVENT_COVER_NOT_FOUND).sendToTarget();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Object getGroup(int position) 
	{
		return null;
	}

	@Override
	public int getGroupCount()
	{
		return 1;
	}

	@Override
	public long getGroupId(int position)
	{
		return album.getID();
	}

	@Override
	public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent)
	{
		String info = album.getInfos(fragment.getActivity(), fragment.getSettings());
		if (!((MainActivity) fragment.getActivity()).isTablet()) info = null;
								
		if (convertView == null) 
        {
			AlbumViewHolder holder = new AlbumViewHolder();
        	convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.library_item_album, parent, false);
        	holder.albumName = (TextView) convertView.findViewById(R.id.album_name);
        	holder.albumInfo = (TextView) convertView.findViewById(R.id.album_info);
        	holder.albumCover = (ImageView) convertView.findViewById(R.id.albumCover);
        	holder.coverArtProgress = (ProgressBar) convertView.findViewById(R.id.albumCoverProgress);
        	convertView.setTag(holder);
        } 
        
		final AlbumViewHolder holder = (AlbumViewHolder) convertView.getTag();
        holder.albumName.setText(album.mainText());
        if (info != null && info.length() > 0) 
        {
            holder.albumInfo.setVisibility(View.VISIBLE);
            holder.albumInfo.setText(info);
        } 
        else 
        {
            holder.albumInfo.setVisibility(View.GONE);
        }
        	   
        convertView.setBackgroundResource(album.getDrawable());
        
        class MyAlbumCoverDownloadListener extends AlbumCoverDownloadListener
        {
			public MyAlbumCoverDownloadListener(Context context,ImageView coverArt, ProgressBar coverArtProgress, boolean bigCoverNotFound) 
			{
				super(context, coverArt, coverArtProgress, bigCoverNotFound);
			}
			
			@Override
			public void onCoverDownloaded(CoverInfo cover) 
			{
				super.onCoverDownloaded(cover);
				album.setDrawableCover(holder.albumCover.getDrawable());
			}
        }
        
        boolean enableCache = fragment.getSettings().getBoolean(SettingsHelper.COVER_USE_CACHE, true);
        if (album.isUnknown() || !enableCache) 
        { 
        	holder.albumCover.setVisibility(View.GONE);
        }
        else
        {
        	holder.albumCover.setVisibility(View.VISIBLE);
        	
        	if (album.getDrawableCover() == null)
	        {
	        	if (coverHelper.coverDownloadListenerCount() == 0)
	        	{
	        		MyAlbumCoverDownloadListener acd = new MyAlbumCoverDownloadListener(fragment.getActivity(), holder.albumCover, holder.coverArtProgress, false);
	        		coverHelper.addCoverDownloadListener(acd);
	        		coverHelper.downloadCover(album.getAlbumInfo());
	        	}
	        }
	        else
	        {
	        	coverHelper.freeCoverDownloadListeners();
	        	holder.albumCover.setImageDrawable(album.getDrawableCover());
	        }
        }
           
		return convertView;
	}			
		
	@Override
	public boolean hasStableIds() 
	{
		return true;
	}
				
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public Object getChild(int group, int position) 
	{
		return null;
	}

	@Override
	public int getChildrenCount(int group) 
	{
		return album.getTracksCount();
	}
	
	@Override
	public long getChildId(int group, int position) 
	{
		return album.getTrack(position).getID();
	}
	
	@Override
	public View getChildView(int group, int position, boolean isExpanded, View convertView, ViewGroup parent) 
	{
		LibraryTrack track = album.getTrack(position);
		Music song = track.getMusic();
        int trackNumber = song.getTrack();
        if (trackNumber < 0) trackNumber = 0;
        
		if (convertView == null) 
        {
			TrackViewHolder holder = new TrackViewHolder();
        	convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.library_item_track, parent, false);
        	holder.trackTitle = (TextView) convertView.findViewById(R.id.track_title);
        	holder.trackNumber = (TextView) convertView.findViewById(R.id.track_number);
        	holder.trackDuration = (TextView) convertView.findViewById(R.id.track_duration);
        	convertView.setTag(holder);
        } 
        
		TrackViewHolder holder = (TrackViewHolder) convertView.getTag();
        holder.trackTitle.setText(song.getTitle());
        holder.trackNumber.setText(trackNumber < 10 ? "0" + Integer.toString(trackNumber) : Integer .toString(trackNumber));
        holder.trackDuration.setText(song.getFormattedTime());
		
		convertView.setBackgroundResource(album.getDrawable());
		return convertView;		
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) 
	{
		return true;
	}

	///////////////////////////////////////////////////////////////////////////
		    
}

///////////////////////////////////////////////////////////////////////////

