package com.skyfree.flashalert.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfree.flashalert.R;
import com.skyfree.flashalert.db.Database;
import com.skyfree.flashalert.db.Pack;
import com.skyfree.flashalert.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by KienBeu on 5/16/2018.
 */

public class AppAdapter extends BaseAdapter {

    private Context mContext;
    private PackageManager packageManager;
    private ArrayList<ApplicationInfo> mListAppInfo;
    private Database mDb;
    private ArrayList<Pack> mListPack;

    public AppAdapter(Context mContext, PackageManager packageManager, ArrayList<ApplicationInfo> mListAppInfo) {
        this.mContext = mContext;
        this.packageManager = packageManager;
        this.mListAppInfo = mListAppInfo;
        mDb = new Database(mContext);
        mListPack = new ArrayList<>();
        mListPack = mDb.getListPack();
    }

    @Override
    public int getCount() {
        return mListAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRow = inflater.inflate(R.layout.item_more_app, null);

        ImageView mImgApp = (ImageView) mRow.findViewById(R.id.img_app_item);
        TextView mTvApp = (TextView) mRow.findViewById(R.id.tv_app_item);
        Switch mSwApp = (Switch) mRow.findViewById(R.id.sw_app_item);

        mImgApp.setImageDrawable(mListAppInfo.get(position).loadIcon(packageManager));
        mTvApp.setText(mListAppInfo.get(position).loadLabel(packageManager));

        for(int i = 0; i<mListPack.size(); i++){
            if(mListAppInfo.get(position).packageName.equals(mListPack.get(i).getPack())){
                mSwApp.setChecked(true);
                break;
            }
        }

        mSwApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDb.addPack(new Pack(mListAppInfo.get(position).packageName));
                }else {
                    mDb.deletePack(new Pack(mListAppInfo.get(position).packageName));
                }
            }
        });

        return mRow;
    }
}
