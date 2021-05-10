package com.example.datamatrixMedicineScan.util;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Set;

public class ActivityHelper extends android.app.Activity {

    private Context ctx;
    private HashMap<String,Class<?>> classMap;

    public ActivityHelper(Context context, HashMap<String,Class<?>> classMap){
        this.ctx = context;
        this.classMap = classMap;
    }

    // createActivity method -- shortcut for creating a new activity
    // --------------------------------------------------------------------------------------
    public Intent createActivity(String activity, HashMap<String,Object> extra){
        //Intent intent=new Intent(this,classMap.get(activity));	// fetch class from classMap (hashmap)
        Intent intent = new Intent(ctx, classMap.get(activity));
        // if extra not null, set it dynamically
        if(extra!=null){
            Set<String> keySet=extra.keySet();
            Object keys[]=keySet.toArray();
            for(int i=0;i<keys.length;i++){
                intent.putExtra(keys[i].toString(),extra.get(keys[i]).toString());
            }
        }

        //startActivity(intent);	// start new activity
        return intent;
    }
    // --------------------------------------------------------------------------------------

    // same method, using class not string
    // --------------------------------------------------------------------------------------
    public Intent createActivity(Class<?> activity, HashMap<String,Object> extra){
        Intent intent=new Intent(this,activity);
        if(extra!=null){
            Set<String> keySet=extra.keySet();
            Object keys[]=keySet.toArray();
            for(int i=0;i<keys.length;i++){
                intent.putExtra(keys[i].toString(),extra.get(keys[i]).toString());
            }
        }
        return intent;
    }
    // --------------------------------------------------------------------------------------
}
