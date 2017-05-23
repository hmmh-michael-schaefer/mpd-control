package de.sir4gt10.mpdcontrol.settings;

import java.util.List;

import de.sir4gt10.mpdcontrol.R;
import de.sir4gt10.mpdcontrol.TabPagerAdapter;
import de.sir4gt10.mpdcontrol.helpers.SettingsHelper;
import de.sir4gt10.mpdcontrol.models.SettingsActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class MainSettingsActivity extends SettingsActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		TabPagerAdapter adapter = new TabPagerAdapter(this);
		if (!adapter.isTablet()) setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
		if (SettingsHelper.isSimplePreferences(this))
		{
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MainSettingsFragment())
				.commit();
		}
	}
	
	@Override
	public void onBuildHeaders(List<Header> target) 
	{
		super.onBuildHeaders(target);
		if (!SettingsHelper.isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.settings_header_main, target);
		}
	}
		
}
