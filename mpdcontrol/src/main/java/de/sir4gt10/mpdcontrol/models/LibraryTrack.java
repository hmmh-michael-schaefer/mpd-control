package de.sir4gt10.mpdcontrol.models;

import de.sir4gt10.mpdcontrol.mpd.Music;

public class LibraryTrack
{

	private int ID = -1;
	private Music music = null;
	
	public LibraryTrack(Music m) 
	{
		this.music = m;
		this.ID = -1;
	}
	
	public LibraryTrack(Music m, int ID) 
	{
		this.music = m;
		this.ID = ID;
	}
		
	public int getID()
	{
		return ID;
	}
		
	public Music getMusic()
	{
		return music;
	}

}
