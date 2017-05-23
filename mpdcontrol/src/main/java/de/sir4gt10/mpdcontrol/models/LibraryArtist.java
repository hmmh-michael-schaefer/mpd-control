package de.sir4gt10.mpdcontrol.models;

import java.util.ArrayList;
import java.util.List;

import de.sir4gt10.mpdcontrol.mpd.Album;
import de.sir4gt10.mpdcontrol.mpd.Artist;
import de.sir4gt10.mpdcontrol.mpd.Item;

public class LibraryArtist extends Artist 
{

	private int ID = -1;
	private int drawable = -1;
	private List<LibraryAlbum> albums = null;
	
	public LibraryArtist(Artist a) 
	{
		super(a);
		this.ID = -1;
		this.drawable = -1;
		this.albums = null;
	}
	
	public LibraryArtist(Artist a, int ID) 
	{
		super(a);
		this.ID = ID;
		this.drawable = -1;
		this.albums = null;
	}
		
	public int getID()
	{
		return ID;
	}
	
	public void setDrawable(int drawable)
	{
		this.drawable = drawable;
	}
	
	public int getDrawable()
	{
		return drawable;
	}

	public List<LibraryAlbum> getAlbums()
	{
		return albums;
	}
	
	public int getAlbumsCount()
	{
		if (albums == null) return 0;
		return albums.size();
	}
	
	public LibraryAlbum getAlbum(int position)
	{
		if (albums == null) return null;
		if (position < 0 || position >= albums.size()) return null;
		return albums.get(position);
	}
	
	public void initAlbums(List<? extends Item> items)
	{
		if (items == null) 
		{
			albums = null;
			return;
		}

		List<LibraryAlbum> albumsTmp = new ArrayList<LibraryAlbum>();
		int ID = 1;
		for (Item i : items)
    	{
			LibraryAlbum album = new LibraryAlbum((Album) i, this.ID + 1000*ID);
			album.setDrawable(this.drawable);
    		albumsTmp.add(album);
    		ID++;
    	}
		albums = albumsTmp;
	}

}
