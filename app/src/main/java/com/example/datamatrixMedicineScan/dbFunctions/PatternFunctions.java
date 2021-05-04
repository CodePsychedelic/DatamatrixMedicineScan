package com.example.datamatrixMedicineScan.dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.example.datamatrixMedicineScan.dbHelper.DBMiddle;
import com.example.datamatrixMedicineScan.dbHelper.Pattern;
import com.example.datamatrixMedicineScan.dbHelper.Type;

public class PatternFunctions{
//context
private Context context;
//productDao is the database access object for products table
private Dao<Pattern, Integer> patternDao;
//productRuntimeExceptionDao is the database runtime exception dao for products table
private RuntimeExceptionDao<Pattern, Integer> patternRuntimeExceptionDao;
//helper is the DatabaseHelperMiddle object
private DBMiddle helper;
private Type type;


//constructor
public PatternFunctions(Context context){
	//initialize the context, helper, productDao and productRuntimeExceptionDao
	this.context=context;
	helper=new DBMiddle(context);
	helper.getWriteableDatabase().execSQL("PRAGMA foreign_keys=ON");
	try{
		patternDao=helper.getPatternDao();
		patternRuntimeExceptionDao=helper.getRuntimeExceptionPatternDao();

	}catch(SQLException e){e.printStackTrace();}
}



//select all from product function
public List<Pattern> selectAll(int option){
	try{
		if(option==1) return patternDao.queryForAll();
		else if(option==2) return patternRuntimeExceptionDao.queryForAll();
	}catch(SQLException e){e.printStackTrace();}
	return null;

}



//selectWhere function
public List<Pattern> selectWhere(HashMap<String,Object> parameters, int option){
	
	try{
		if(option==1) return patternDao.queryForFieldValues(parameters);
		else if(option==2) return patternRuntimeExceptionDao.queryForFieldValues(parameters);
	}catch(SQLException e){
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
}

public QueryBuilder<Pattern, Integer> qb(int option){
	if(option==1) return patternDao.queryBuilder();
	return patternRuntimeExceptionDao.queryBuilder();
}

}
