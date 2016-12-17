package com.groganlabs.mishmash;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by katie_000 on 12/16/2016.
 */

public class PackListAdapter extends BaseAdapter {

    String [] titles, desc;
    int[] gameCount;
    Context context;
    private static LayoutInflater inflater;

    public PackListAdapter(Activity act, String[] titles, String[] desc, int[] count) {
        this.titles = titles;
        this.desc = desc;
        gameCount = count;
        context = act;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder {
        String title;
        String desc;
        int count;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
