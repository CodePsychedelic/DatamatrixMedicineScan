package com.example.datamatrixMedicineScan;
/*package com.example.datamatrixscan;

import java.util.ArrayList;
import java.util.Enumeration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Connector{
private Schema schema;
private Context cnx;

public Connector(Context context){
	schema=new Schema(context);
	cnx=context;
}

/*
 * public long insertData(name:String,password:String)
 * 
 * example of inserting name and password in the database
 */
/*
public long insertData(String name,String password){
	// get SQLiteDatabase object from schema.getWriteableDatabase()
	SQLiteDatabase db=schema.getWritableDatabase();
	
	// Create a ContentValues object and set the values
	// name and password in accordance with the database schema columns
	// ================================================
	ContentValues contentValues=new ContentValues();
	contentValues.put(Schema.NAME,name);
	contentValues.put(Schema.PASSWORD,password);
	// ================================================
	
	// return the id of insert.If it is lower than zero there is a problem
	return db.insert(Schema.TABLE_NAME,null,contentValues);
	
}

public String selectData(){
	SQLiteDatabase db=schema.getWritableDatabase();
	
	String[] cols={schema.UID,schema.NAME,schema.PASSWORD};
	Cursor cursor=db.query(schema.TABLE_NAME,cols,null,null,null,null,null);
	String info="";
	while(cursor.moveToNext()){
		int uid=cursor.getInt(cursor.getColumnIndex(schema.UID));
		String name=cursor.getString(cursor.getColumnIndex(schema.NAME));
		String password=cursor
				.getString(cursor.getColumnIndex(schema.PASSWORD));
		
		info+=uid+" "+name+" "+password+"\n";
	}
	return info;
	
}

public String compare(String name){
	SQLiteDatabase db=schema.getWritableDatabase();
	
	String[] cols={schema.UID,schema.NAME,schema.PASSWORD};
	Cursor cursor=db.query(schema.TABLE_NAME,cols,schema.NAME+"=?",
			new String[]{name},null,null,null);
	
	String info="";
	while(cursor.moveToNext()){
		int uid=cursor.getInt(cursor.getColumnIndex(schema.UID));
		String uname=cursor.getString(cursor.getColumnIndex(schema.NAME));
		String password=cursor
				.getString(cursor.getColumnIndex(schema.PASSWORD));
		
		info+=uid+" "+uname+" "+password+"\n";
	}
	return info;
}

public int update(String id,String name,String password){
	SQLiteDatabase db=schema.getWritableDatabase();
	ContentValues contentValues=new ContentValues();
	contentValues.put(schema.NAME,name);
	contentValues.put(schema.PASSWORD,password);
	return db.update(schema.TABLE_NAME,contentValues,schema.UID+"=?",
			new String[]{id});
	
}

public int delete(Enumeration<String> fields,Enumeration<String> values){
	SQLiteDatabase db=schema.getWritableDatabase();
	ContentValues contentValues=new ContentValues();
	ArrayList<String> cols=new ArrayList<String>();
	ArrayList<String> vals=new ArrayList<String>();
	int i=0;
	while(fields.hasMoreElements()){
		
		String field=fields.nextElement();
		String value=values.nextElement();
		
		if(field.equals("id")){
			cols.add(schema.UID+"=?");
			vals.add(value);
			
		}else if(field.equals("name")){
			cols.add(schema.NAME+"=?");
			vals.add(value);
		}else if(field.equals("password")){
			cols.add(schema.PASSWORD+"=?");
			vals.add(value);
		}
		
	}
	String where="";
	String and="";
	
	for(i=0;i<cols.size();i++){
		where+=and+cols.get(i);
		and=" and ";
	}
	Object arguments[]=vals.toArray();
	String args[]=new String[arguments.length];
	for(i=0;i<arguments.length;i++){
		args[i]=(String)arguments[i];
	}
	return db.delete(schema.TABLE_NAME,where,args);
	
}

/*
 * 
 * static class Schema extends SQLiteOpenHelper serves as the Schema of our
 * database. onCreate and onUpgrade actions are performed here. also we define
 * our database name , tables names,columns names and the database version
 * 
 * we construct the queries for creation and deletion of the database
 */
/*
static class Schema extends SQLiteOpenHelper{

public static final String DATABASE_NAME="ScanProducts.db";
private static final String TABLE_NAME="ScannedProductsTable";
private static final String PID="_id";
private static final String PRODUCT_CODE="Product_Code";
private static final String PRODUCT_CATEGORY="Product_Category";
public static final int DATABASE_VERSION=5;

private static final String CREATE_SCANNED_PRODUCTS_TABLE="CREATE TABLE "
		+TABLE_NAME+"("+PID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+PRODUCT_CODE
		+" VARCHAR(255),"+PRODUCT_CATEGORY+" VARCHAR(255)"+");";
private static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;

private Context context;

// args: context,database_name,null,database_version
public Schema(Context context){
	super(context,DATABASE_NAME,null,DATABASE_VERSION);
	
	this.context=context;
	
}

// onCreate(db:SQLiteDatabase)
/*
 * called when the database is created for the first time creation of tables and
 * initial data inside tables should be put here
 */
/*
@Override
public void onCreate(SQLiteDatabase db){
	
	try{
		db.execSQL(CREATE_TABLE);
		
		Log.i("onCreate SAYS:","on create is CALLED");
	}catch(SQLException e){
	}
}

// onUpgrade(db:SQLiteDatabase,oldversion:int,newversion:int)
/*
 * called when the database needs to be upgraded.Use this method to drop
 * tables,add tables,or do anything else it needs to upgrade to the new schema
 * version a
 */
/*
@Override
public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	try{
		db.execSQL(DROP_TABLE);
		onCreate(db);
		Log.i("onUpgrade SAYS:","on upgrade is CALLED");
		
	}catch(SQLException e){
	}
}

}
}*/
