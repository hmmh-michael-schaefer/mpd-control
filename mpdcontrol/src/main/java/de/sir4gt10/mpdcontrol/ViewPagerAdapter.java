package de.sir4gt10.mpdcontrol;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import de.sir4gt10.mpdcontrol.helpers.MPDConnectionHandler;
import de.sir4gt10.mpdcontrol.helpers.SettingsHelper;
import de.sir4gt10.mpdcontrol.models.Fragment;
import de.sir4gt10.mpdcontrol.mpd.MPD;
import de.sir4gt10.mpdcontrol.settings.MainSettingsActivity;

/**
 * Created by michael.schaefer on 17.05.17.
 */

public class ViewPagerAdapter extends FragmentActivity implements ActionBar.TabListener
{
    public static final int ACTIVITY_SETTINGS = 5;

    private ViewPager viewPager = null;
    private TabPagerAdapter pagerAdapter = null;
    private Integer currentTab = 0;
    private boolean doubleBackToExitPressedOnce = false;


    public boolean isTablet()
    {
        if (pagerAdapter != null) return pagerAdapter.isTablet();
        return false;
    }

    public boolean isPortrait()
    {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart()
    {
        super.onStart();
        MainApplication app = (MainApplication) getApplicationContext();
        app.setActivity(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        MainApplication app = (MainApplication) getApplicationContext();
        app.unsetActivity(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;

        MainApplication app = (MainApplication) getApplicationContext();
        MPDConnectionHandler.startReceiver(this, app.getSettingsHelper());

        startService(new Intent(this, WearService.class));
    }

    @Override
    protected void onPause()
    {
        MPDConnectionHandler.stopReceiver(this);

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable("currentTab", currentTab);
        super.onSaveInstanceState(outState);
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(pagerAdapter.getMainLayoutID());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        pagerAdapter = new TabPagerAdapter(this);

        if (!isTablet()) setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        // Toggle actionBar TabBar
        Boolean enableTabBar = true;

        if (enableTabBar) {
            viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
            {
                @Override
                public void onPageSelected(int position)
                {
                    getActionBar().setSelectedNavigationItem(position);
                }
            });

            for (int i = 0; i < pagerAdapter.getCount(); i++)
            {
                ActionBar.Tab tab = actionBar.newTab();
                tab.setText(pagerAdapter.getTitle(i));
                tab.setTabListener(this);
                actionBar.addTab(tab);
            }

            if (savedInstanceState != null) {
                currentTab = (Integer) savedInstanceState.getSerializable("currentTab");
                getActionBar().setSelectedNavigationItem(currentTab);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        if (viewPager != null)
        {
            viewPager.setCurrentItem(tab.getPosition());
            viewPager.requestLayout();

            currentTab = tab.getPosition();
            invalidateOptionsMenu();

            int id = pagerAdapter.getID(currentTab);
            if (id != -1)
            {
                android.support.v4.app.Fragment f =  getSupportFragmentManager().findFragmentById(id);
                if (f instanceof Fragment)
                {
                    Fragment fragment = (Fragment) f;
                    if (fragment != null) fragment.onDisplay();
                }
            }
        }
    }

    public int getLibraryFragmentPosition()
    {
        return pagerAdapter.getPosition(R.id.tab_library);
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed()
    {
        if (restoreBackFragment()) return;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exitOnlyByMenu = settings.getBoolean(SettingsHelper.APP_EXIT_ONLY_BY_MENU, false);

        if (exitOnlyByMenu) return;

        if (doubleBackToExitPressedOnce)
        {
            MainApplication app = (MainApplication) getApplication();
            app.terminateApplication();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.app_exit_press_back_twice_message, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable()
        {
            @Override public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public boolean restoreBackFragment()
    {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        /*
        getMenuInflater().inflate(R.menu.main, menu);

        int id = pagerAdapter.getID(currentTab);
		if (id != -1)
		{
			if (id == R.id.tab_playingplaylist || id == R.id.tab_playlist) getMenuInflater().inflate(R.menu.playlist, menu);
		}
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        final MainApplication app = (MainApplication) this.getApplication();
        final MPD mpd = app.oMPDAsyncHelper.oMPD;

        switch (item.getItemId())
        {
            case R.id.menu_settings:
                i = new Intent(this, MainSettingsActivity.class);
                startActivityForResult(i, ACTIVITY_SETTINGS);
                return true;

            case R.id.menu_exit:
                app.terminateApplication();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
