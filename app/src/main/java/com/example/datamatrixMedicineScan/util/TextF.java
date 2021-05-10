package com.example.datamatrixMedicineScan.util;

import android.content.Context;

// CUSTOM TEXT FIELD
// ---------------------------------------------------------------------------
public class TextF extends androidx.appcompat.widget.AppCompatEditText{
    // NAME AND APPLICATION IDENTIFIER
    private String name;
    private String ai="-1";

    public TextF(Context context, String name){
        super(context);
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public String getAI(){
        return ai;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setAI(String ai){
        this.ai=ai;
    }
}
// ---------------------------------------------------------------------------
