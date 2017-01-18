package com.groganlabs.mishmash;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class PackListActivity extends AppCompatActivity {
    ListView packList;
    String[] titles;
    String[] descriptions;
    boolean[] purchased;
    int[] counts;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pack_list);
        packList = (ListView) findViewById(R.id.packList);

        // get arrays from resource - pack titles, descriptions, gamecounts
        //Resources res = getResources();
        //titles = res.getStringArray(R.array.availablePackNames);
        //descriptions = res.getStringArray(R.array.availablePackDescs);
        //counts = res.getIntArray(R.array.availablePackCounts);

        MishMashDB dbHelper = new MishMashDB(this, MishMashDB.DB_NAME, null, MishMashDB.latestVersion);

        Cursor cur = dbHelper.getPacks();
        size = cur.getCount();
        Pack[] packs = new Pack[size];
        titles = new String[size];
        descriptions = new String[size];
        counts = new int[size];
        purchased = new boolean[size];

        cur.moveToFirst();
        for(int ii = 0; ii < size; ii++) {
            packs[ii] = new Pack();
            packs[ii].title = cur.getString(0);
            packs[ii].description = cur.getString(1);
            packs[ii].gameCount = cur.getInt(2);
            if(cur.getInt(4) == 0)
                packs[ii].purchased = false;
            else
                packs[ii].purchased = true;

            cur.moveToNext();
        }

        packList.setAdapter(new PackListAdapter(this, packs));

    }
}
