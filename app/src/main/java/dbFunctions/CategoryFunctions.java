package dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import dbHelper.Category;
import dbHelper.DBMiddle;

public class CategoryFunctions{

//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<Category,Integer> categoryDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<Category,Integer> categoryRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private Category category;
	
	
	
	public CategoryFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		helper.getWriteableDatabase().execSQL("PRAGMA foreign_keys=ON");
		try{
			categoryDao=helper.getCategoryDao();
			categoryRuntimeExceptionDao=helper.getRuntimeExceptionCategoryDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}
	
	
	
	
	public List<Category> selectAll(int option){
		try{
			if(option==1) return categoryDao.queryForAll();
			else if(option==2) return categoryRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	//selectWhere function
	public List<Category> selectWhere(HashMap<String,Object> parameters, int option){
		
		try{
			if(option==1) return categoryDao.queryForFieldValues(parameters);
			else if(option==2) return categoryRuntimeExceptionDao.queryForFieldValues(parameters);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	public QueryBuilder<Category,Integer> qb(int option){
		if(option==1){
			return categoryDao.queryBuilder();
		}else{
			return categoryRuntimeExceptionDao.queryBuilder();
		}
	}
	
	public DeleteBuilder<Category,Integer> db(int option){
		if(option==1){
			return categoryDao.deleteBuilder();
		}else{
			return categoryRuntimeExceptionDao.deleteBuilder();
		}
	}
	


}
