package com.groganlabs.mishmash;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by katie_000 on 12/16/2016.
 */

public class PackListAdapter extends BaseAdapter {

    Pack[] packs;
    Context context;
    private static LayoutInflater inflater;

    public PackListAdapter(Activity act, Pack[] packs) {
        this.packs = packs;
        context = act;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return packs.length;
    }

    @Override
    public Object getItem(int position) {
        return packs[position];
    }

    @Override
    public long getItemId(int position) {
        return packs[position].id;
    }

    public class Holder {
        TextView title;
        TextView desc;
        TextView count;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder hold = new Holder();
        View row = inflater.inflate(R.layout.pack, null);
        hold.title = (TextView) row.findViewById(R.id.packTitle);
        hold.desc = (TextView) row.findViewById(R.id.packDesc);
        hold.count = (TextView) row.findViewById(R.id.numGames);

        hold.title.setText(packs[position].title);
        hold.desc.setText(packs[position].description);
        hold.count.setText("Number of Games: " + packs[position].gameCount);

        // if pack not purchased,
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find out if the pack's been purchased
                // if not, start the purchase process
                // mHelper.launchPurchaseFlow(this, SKU, requestCode, purchaseFinishedListener, payloadString);
            }
        });
        return row;
    }
}
