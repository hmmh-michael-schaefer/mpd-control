package de.sir4gt10.mpdcontrol.adapters;

import android.content.Context;
import android.view.View;

import de.sir4gt10.mpdcontrol.mpd.Item;
import de.sir4gt10.mpdcontrol.view.holders.AbstractViewHolder;

import java.util.List;

public interface ArrayDataBinder 

{
    public AbstractViewHolder findInnerViews(View targetView);
    public int getLayoutId();
    public boolean isEnabled(int position, List<? extends Item> items, Object item);
    public void onDataBind(Context context, View targetView, AbstractViewHolder viewHolder, List<? extends Item> items, Object item, int position);
    public View onLayoutInflation(Context context, View targetView, List<? extends Item> items);
    
}
