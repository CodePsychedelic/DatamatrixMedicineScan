package com.example.datamatrixMedicineScan.util;


import android.util.Log;

// ERROR CUSTOM CLASS
// ---------------------------------------------------------------------------
public class Error{
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


    //checkRules function
    //returns an Error object defining the error Field and type
    //Error Types are: 	-1  for string value in numeric field
    //					-21 for length violation
    //					-22 for minimum length violation
    //					-23 for maximum length violation
    //will return error 0 in case of no error
    // ----------------------------------------------------------------------------
    public static Error checkRules(TextF field,String ai){
        //if the ai of field is not default value (-1)
        if(!ai.equals("-1")){

            // split the ai string with the ';' delimiter
            // the ai string is defined by the format: ai_Identifier;ai_Rule
            // an ai string with range is "01;s1-20
            // tokens[0]=>AI, tokens[1]=rule
            String tokens[]=ai.split(";");

            // initialize variables
            // identifier will be used for field data validation (numeric or string)
            // endRagne is set to -1 and will be the maximum length if the field has one
            String identifier="";
            String endRange="-1";
            Log.d("Data:","AI:"+tokens[0]+" Rule:"+tokens[1]);

            if(tokens[1].contains("-")){
                // if the ai rule contains range then split it by '-' character
                // and store it to ruleParts.
                // set the identifier to ruleParts[0] and
                // the endRange to ruleParts[1] ( format: [n|s]MIR-MAR)
                // ruleParts[0] = [n|s]MIR , ruleParts[1] = MAR
                String ruleParts[]=tokens[1].split("-");
                identifier=ruleParts[0];
                endRange=ruleParts[1];

            }else{
                // if the ai rule does not contain range then set the identifier
                // equal to the rule => identifier = [n|s]LEN
                identifier=tokens[1];
            }

            // initialize min and maxRange variables
            int minRange=0;
            int maxRange=-1;

            // the typeCharacter is the first character of identifier
            String typeCharacter=identifier.substring(0,1);

            // replcace the typeCharacter in identifier section with ""
            // then parse the number to the minRange
            identifier=identifier.replace(typeCharacter,"");
            minRange=Integer.parseInt(identifier);

            //get the field value
            String text=field.getText().toString();

            // if the endRagne changes from "-1" then parse it to maxRange
            if(!endRange.equals("-1")) maxRange=Integer.parseInt(endRange);


            // NUMERIC ERROR
            // if the typeCharacter is n for number
            // check if all characters are numeric
            // if not return an error
            if(typeCharacter.equals("n")){
                char []t=text.toCharArray();
                for(char c:t){
                    if(c<48 || c>57){
                        Error e=new Error(field,"-1");
                        return e;
                    }
                }
            }

            // get length of input text
            int len=text.length();

            // LENGTH ERROR -- ONLY IF NO MIN RAGNE
            // if maxRange remains inited to -1
            // return len error (not equal)
            if(maxRange<0){
                if(len!=minRange){
                    return new Error(field,"-21");
                }
            }else{

                // MIN RANGE ERROR
                //if length of text is less than min ragne return less than error
                if(len<minRange){
                    return new Error(field,"-22");
                }
                // MAX RANGE ERROR
                //else if text length is greater than max range return greater than error
                if(len>maxRange){
                    return new Error(field,"-23");
                }
            }






        }
        //return error zero.
        return new Error(field,"0");
    }
    // ----------------------------------------------------------------------------

}
// ---------------------------------------------------------------------------
