package com.example.datamatrixMedicineScan.dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="Categories")
public class Category{
@DatabaseField(generatedId=true)
private int id;
@DatabaseField
private String category;

public Category(){
    
}

public Category(String category){
    this.category=category;
}

public int getId(){
    return id;
}

public void setId(int id){
    this.id=id;
}

public String getCategory(){
    return category;
}

public void setCategory(String category){
    this.category=category;
}







}
