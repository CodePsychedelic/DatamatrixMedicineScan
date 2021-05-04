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

import com.example.datamatrixMedicineScan.dbHelper.DBMiddle;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.ProductAttributes;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;

public class PIFunctions{


//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<ProductAttributes,Integer> piDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<ProductAttributes,Integer> piRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private ProductAttributes pi;
	

	//constructor
	public PIFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		
		try{
			piDao=helper.getProductInformationDao();
			piRuntimeExceptionDao=helper.getRuntimeExceptionProductInformationDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}
	
	
	
	//select all from product function
	public List<ProductAttributes> selectAll(int option){
		try{
			if(option==1) return piDao.queryForAll();
			else if(option==2) return piRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	//selectRaw function
	public GenericRawResults<String[]> selectRaw(String raw,String arguments[],int option){
		try{
			if(option==1) return piDao.queryRaw(raw,arguments);
			else if(option==2) return piRuntimeExceptionDao.queryRaw(raw,arguments);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//selectWhere function
	public List<ProductAttributes> selectWhere(HashMap<String,Object> parameters, int option){
		
		try{
			if(option==1) return piDao.queryForFieldValues(parameters);
			else if(option==2) return piRuntimeExceptionDao.queryForFieldValues(parameters);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//create product function (insert)
	public boolean create(SerialNumber serial, Field property, String value, int option){
		pi=new ProductAttributes(serial,property,value);
		try{
			if(option==1){
				piDao.create(pi);
			}else if(option==2){
				piRuntimeExceptionDao.create(pi);
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		
		return true;
	}
	
	//update function
	public void update(ProductAttributes old, HashMap<String,Object> data, int option){

		ProductAttributes newPi=new ProductAttributes();
		
		
		newPi.setId(old.getId());
		newPi.setPropertyId(old.getPropertyId());
		newPi.setValue(old.getValue());
		newPi.setSerial(old.getSerialNumber());
		
		if(data.containsKey("id")) newPi.setId((Integer)data.get("id"));
		if(data.containsKey("property"))newPi.setPropertyId((Field)data.get("property"));
		if(data.containsKey("value")) newPi.setValue((String)data.get("value"));
		if(data.containsKey("serial")){
			//newPi.setSerial((GTIN)data.get("code"));
			newPi.setSerial((SerialNumber)data.get("serial"));
		}	
		
		
		try{
			if(option==1){
				piDao.update(newPi);
			}else if(option==2){
				piRuntimeExceptionDao.update(newPi);
			}
		}catch(SQLException e){e.printStackTrace();}
				
		
	}
	
	
	public QueryBuilder<ProductAttributes,Integer> qb(int option){
		QueryBuilder<ProductAttributes,Integer> qb=null;
		if(option==1){
			return (qb=piDao.queryBuilder());
		}else if(option==2){
			return (qb=piRuntimeExceptionDao.queryBuilder());
		}
		return null;
	}

	public void delete(ProductAttributes pif, int option){
		QueryBuilder<ProductAttributes,Integer> qb=null;
		if(option==1){
			try{
				piDao.delete(pif);
			}catch(SQLException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(option==2){
			piRuntimeExceptionDao.delete(pif);
		}
		
	}
	
	public DeleteBuilder<ProductAttributes,Integer> db(int option){
		if(option==1){
			return piDao.deleteBuilder();
		}else
			return piRuntimeExceptionDao.deleteBuilder();
	}


	public UpdateBuilder<ProductAttributes,Integer> ub(int option){
		if(option==1){
			return piDao.updateBuilder();
		}else
			return piRuntimeExceptionDao.updateBuilder();
	}


}
