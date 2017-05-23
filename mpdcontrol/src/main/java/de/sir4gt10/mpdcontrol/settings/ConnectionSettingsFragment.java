package de.sir4gt10.mpdcontrol.settings;

import de.sir4gt10.mpdcontrol.R;
import de.sir4gt10.mpdcontrol.helpers.PreferencesHelper;
import de.sir4gt10.mpdcontrol.helpers.SettingsHelper;
import de.sir4gt10.mpdcontrol.models.SettingsFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

public class ConnectionSettingsFragment extends SettingsFragment
{
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		PreferenceScreen p = getPreferenceManager().createPreferenceScreen(getActivity());
		init(p, getActivity());
		setPreferenceScreen(p);
	}
	
	protected static void init(Preference pref, Context context)
	{
		Activity a = (Activity) context;
		String ssid = a.getIntent().getStringExtra("SSID");
		if (ssid != null) 
		{
			a.setTitle(R.string.settings_connections_wifi);
			
        }
		else 
		{
			a.setTitle(R.string.settings_connections_default);
        }
		
		EditTextPreference prefHost = new EditTextPreference(context);
        prefHost.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        prefHost.setDialogTitle(R.string.settings_connection_host_desc);
        prefHost.setTitle(R.string.settings_connection_host);
        prefHost.setSummary(R.string.settings_connection_host_desc);
        prefHost.setDefaultValue("");
        prefHost.setKey(SettingsHelper.getServerStringWithSSID(SettingsHelper.SERVER_HOSTNAME, ssid));
        PreferencesHelper.bindPreferenceSummaryToValue(prefHost);
        addPreference(pref, prefHost);
        
        EditTextPreference prefPort = new EditTextPreference(context);
        prefPort.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        prefPort.setDialogTitle(R.string.settings_connection_port_desc);
        prefPort.setTitle(R.string.settings_connection_port);
        prefPort.setSummary(R.string.settings_connection_port_desc);
        prefPort.setDefaultValue("6600");
        prefPort.setKey(SettingsHelper.getServerStringWithSSID(SettingsHelper.SERVER_PORT, ssid));
        addPreference(pref, prefPort);
        
        EditTextPreference prefPassword = new EditTextPreference(context);
        prefPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        prefPassword.setDialogTitle(R.string.settings_connection_pass_desc);
        prefPassword.setTitle(R.string.settings_connection_pass);
        prefPassword.setSummary(R.string.settings_connection_pass_desc);
        prefPassword.setDefaultValue("");
        prefPassword.setKey(SettingsHelper.getServerStringWithSSID(SettingsHelper.SERVER_PASSWORD, ssid));
        addPreference(pref, prefPassword);
	}
	
	public static void addPreference(Preference parent, Preference child)
	{
		if (parent.getClass() == PreferenceScreen.class)
		{
			PreferenceScreen p = (PreferenceScreen) parent;
			p.addPreference(child);
		}
		else if (parent.getClass() == PreferenceCategory.class)
		{
			PreferenceCategory p = (PreferenceCategory) parent;
			p.addPreference(child);
		}
	}
	
}