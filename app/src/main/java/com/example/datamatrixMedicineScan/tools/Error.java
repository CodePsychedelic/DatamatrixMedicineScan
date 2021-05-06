package com.example.datamatrixMedicineScan.tools;


import com.example.datamatrixMedicineScan.tools.TextF;

// ERROR CUSTOM CLASS
// ---------------------------------------------------------------------------
class Error{
    // FOR A TEXTFIELD WITH ERROR CODE
    private TextF field;
    private String errorCode;

    public Error(TextF field, String errorCode){
        this.field=field;
        this.errorCode=errorCode;
    }

    public void setField(TextF field){
        this.field=field;
    }
    public TextF getField(){
        return field;
    }
    public void setErrorCode(String errorCode){
        this.errorCode=errorCode;
    }
    public String getErrorCode(){
        return errorCode;
    }
}
// ---------------------------------------------------------------------------
