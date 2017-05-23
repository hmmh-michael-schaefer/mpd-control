package de.sir4gt10.mpdcontrol.settings;

import de.sir4gt10.mpdcontrol.R;
import de.sir4gt10.mpdcontrol.models.SettingsFragment;

import android.os.Bundle;

public class LibrarySettingsFragment extends SettingsFragment
{
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_library);
	}
		
}
