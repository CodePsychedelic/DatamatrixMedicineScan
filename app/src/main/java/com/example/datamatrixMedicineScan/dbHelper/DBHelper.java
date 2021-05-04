package com.example.datamatrixMedicineScan.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DBHelper extends OrmLiteSqliteOpenHelper{
	
	private Context context;
	private static final String DATABASE_NAME="Medicine.db";
	private static final int DATABASE_VERSION=34;
	private SQLiteDatabase database;

	public DBHelper(Context context){
		//super(context,databaseName,factory,databaseVersion);
		super(context,DATABASE_NAME,null,DATABASE_VERSION,com.example.datamatrixMedicineScan.R.raw.ormlite_config);
	
		this.context=context;
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource){
		// TODO Auto-generated method stub
		this.database=database;
		try{
			//database.execSQL("PRAGMA foreign_keys=ON;");
			//create category
			TableUtils.createTable(connectionSource, Category.class);
			//create GTIN and TYPE
			TableUtils.createTable(connectionSource, GTIN.class);
			TableUtils.createTable(connectionSource,Type.class);
			//create Serial numbers and fields
			TableUtils.createTable(connectionSource,SerialNumber.class);
			TableUtils.createTable(connectionSource, Field.class);
			//create attributes and patterns
			TableUtils.createTable(connectionSource,ProductAttributes.class);
			TableUtils.createTable(connectionSource, Pattern.class);
			DBMiddle hlp=new DBMiddle(context);
			hlp.dbSeed();
			
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource,int oldVersion,int newVersion){
		// TODO Auto-generated method stub
		try{
			
			TableUtils.dropTable(connectionSource, Field.class,true);
			TableUtils.dropTable(connectionSource,ProductAttributes.class,true);
			TableUtils.dropTable(connectionSource, Category.class,true);
			TableUtils.dropTable(connectionSource, GTIN.class,true);
			TableUtils.dropTable(connectionSource, Pattern.class,true);
			TableUtils.dropTable(connectionSource,Type.class,true);
			TableUtils.dropTable(connectionSource,SerialNumber.class,true);
			onCreate(database,connectionSource);
			
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	
	
}
