package de.sir4gt10.mpdcontrol.settings;

import de.sir4gt10.mpdcontrol.TabPagerAdapter;
import de.sir4gt10.mpdcontrol.models.SettingsActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class ConnectionSettingsActivity extends SettingsActivity
{
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
			
		TabPagerAdapter adapter = new TabPagerAdapter(this);
		if (!adapter.isTablet()) setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new ConnectionSettingsFragment())
			.commit();
	}
	
}
