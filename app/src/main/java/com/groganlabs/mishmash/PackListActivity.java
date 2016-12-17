package com.groganlabs.mishmash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class PackListActivity extends AppCompatActivity {
    ListView packList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_list);

        packList = (ListView) findViewById(R.id.packList);

    }
}
