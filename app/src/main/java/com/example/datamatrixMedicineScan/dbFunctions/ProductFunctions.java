package com.example.datamatrixMedicineScan.dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.example.datamatrixMedicineScan.dbHelper.Category;
import com.example.datamatrixMedicineScan.dbHelper.DBMiddle;
import com.example.datamatrixMedicineScan.dbHelper.GTIN;


public class ProductFunctions{


//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<GTIN,Integer> productDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<GTIN,Integer> productRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private GTIN product;
	

	//constructor
	public ProductFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		
		try{
			productDao=helper.getProductDao();
			productRuntimeExceptionDao=helper.getRuntimeExceptionProductDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}
	
	
	
	//select all from product function
	public List<GTIN> selectAll(int option){
		try{
			if(option==1) return productDao.queryForAll();
			else if(option==2) return productRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	//selectRaw function
	public GenericRawResults<String[]> selectRaw(String raw,String arguments[],int option){
		
		try{
			if(option==1) return productDao.queryRaw(raw,arguments);
			else if(option==2) return productRuntimeExceptionDao.queryRaw(raw,arguments);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//selectWhere function
	public List<GTIN> selectWhere(HashMap<String,Object> parameters,int option){
		
		try{
			if(option==1) return productDao.queryForFieldValues(parameters);
			else if(option==2) return productRuntimeExceptionDao.queryForFieldValues(parameters);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//create product function (insert)
	public boolean create(String code, Category category, int option){
		product=new GTIN(code,category);
		try{
			if(option==1){
				productDao.create(product);
			}else if(option==2){
				productRuntimeExceptionDao.create(product);
			
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		
		return true;
	}
	
	public boolean insert(GTIN product,int option){
		try{
			if(option==1){
				productDao.create(product);
			}else if(option==2){
				productRuntimeExceptionDao.create(product);
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		return true;
	}
	
	
	//update function
	public void update(GTIN old,HashMap<String,Object> data,int option){

		GTIN newProduct=new GTIN();

		newProduct.setId(old.getId());
		newProduct.setCode(old.getCode());
		newProduct.setProductCategory(old.getProductCategory());

		
		
		if(data.containsKey("id")) newProduct.setId((Integer)data.get("id"));
		if(data.containsKey("code"))newProduct.setCode(data.get("code").toString());
		if(data.containsKey("category")) newProduct.setProductCategory((Category)data.get("category"));
		
		
		
		try{
			if(option==1){
				productDao.update(newProduct);
			}else if(option==2){
				productRuntimeExceptionDao.update(newProduct);
			}
		}catch(SQLException e){e.printStackTrace();}
		
		
			
		
	}
	
	public List<GTIN> selectLike(String column,String value,int option){
		QueryBuilder<GTIN,Integer> qb=null;
		if(option==1){
			qb=productDao.queryBuilder();
		}else if(option==2){
			qb=productRuntimeExceptionDao.queryBuilder();
		}
		try{
			return	qb.where().like(column,value+"%").query();
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	public void delete(GTIN product,int option){
		if(option==1){
			try{
				productDao.delete(product);
			}catch(SQLException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(option==2){
			productRuntimeExceptionDao.delete(product);
		}
		
		
	}
	
	public QueryBuilder<GTIN,Integer> qb(int option){
		if(option==1){
			return productDao.queryBuilder();
		}else
			return productRuntimeExceptionDao.queryBuilder();
		
	}
	
	
	public DeleteBuilder<GTIN,Integer> db(int option){
		if(option==1){
			return productDao.deleteBuilder();
		}else
			return productRuntimeExceptionDao.deleteBuilder();
	}


	public UpdateBuilder<GTIN,Integer> ub(int option){
		if(option==1){
			return productDao.updateBuilder();
		}else
			return productRuntimeExceptionDao.updateBuilder();
	}



}
