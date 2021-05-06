package com.example.datamatrixMedicineScan.tools;

import android.content.Context;

// CUSTOM TEXT FIELD
// ---------------------------------------------------------------------------
class TextF extends androidx.appcompat.widget.AppCompatEditText{
    // NAME AND APPLICATION IDENTIFIER
    private String name;
    private String ai="-1";

    public TextF(Context context, String name){
        super(context);
        this.name=name;
    }

    private String getName(){
        return name;
    }

    private String getAI(){
        return ai;
    }

    private void setName(String name){
        this.name=name;
    }

    private void setAI(String ai){
        this.ai=ai;
    }
}
// ---------------------------------------------------------------------------
