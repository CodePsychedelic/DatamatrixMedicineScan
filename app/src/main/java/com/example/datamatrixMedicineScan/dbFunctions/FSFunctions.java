package com.example.datamatrixMedicineScan.dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.example.datamatrixMedicineScan.dbHelper.DBMiddle;
import com.example.datamatrixMedicineScan.dbHelper.Field;

public class FSFunctions{

//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<Field,Integer> fsDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<Field,Integer> fsRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private Field fs;
	
	
	
	public FSFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		helper.getWriteableDatabase().execSQL("PRAGMA foreign_keys=ON");
		try{
			fsDao=helper.getFieldDao();
			fsRuntimeExceptionDao=helper.getRuntimeExceptionFieldDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}
	
	
	public List<Field> selectAll(int option){
		try{
			if(option==1) return fsDao.queryForAll();
			else if(option==2) return fsRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	
	public List<Field> selectWhere(HashMap<String,Object> parameters, int option){
		
		try{
			if(option==1) return fsDao.queryForFieldValues(parameters);
			else if(option==2) return fsRuntimeExceptionDao.queryForFieldValues(parameters);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public QueryBuilder<Field,Integer> qb(int option){
		if(option==1) return fsDao.queryBuilder();
		else return fsRuntimeExceptionDao.queryBuilder();
	}
	
	
}
