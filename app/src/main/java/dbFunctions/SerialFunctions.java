package dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import dbHelper.DBMiddle;
import dbHelper.SerialNumber;


public class SerialFunctions{
	//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<SerialNumber,Integer> serialDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<SerialNumber,Integer> serialRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private SerialNumber serial;


	//constructor
	public SerialFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		helper.getWriteableDatabase().execSQL("PRAGMA foreign_keys=ON");
		try{
			serialDao=helper.getSerialDao();
			serialRuntimeExceptionDao=helper.getRuntimeExceptionSerialDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}


	
	//select all from product function
	public List<SerialNumber> selectAll(int option){
		try{
			if(option==1) return serialDao.queryForAll();
			else if(option==2) return serialRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	
	
	
	
	public boolean insert(SerialNumber serial, int option){
		try{
			if(option==1){
				serialDao.create(serial);
			}else if(option==2){
				serialRuntimeExceptionDao.create(serial);
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		return true;
	}
	
	
	public void delete(SerialNumber serial, int option){
		if(option==1){
			try{
				serialDao.delete(serial);
			}catch(SQLException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(option==2){
			serialRuntimeExceptionDao.delete(serial);
		}
		
		
	}
	
	

	public QueryBuilder<SerialNumber,Integer> qb(int option){
		if(option==1) return serialDao.queryBuilder();
		return serialRuntimeExceptionDao.queryBuilder();
	}
	
	public DeleteBuilder<SerialNumber,Integer> db(int option){
		if(option==1){
			return serialDao.deleteBuilder();
		}else
			return serialRuntimeExceptionDao.deleteBuilder();
	}

	public UpdateBuilder<SerialNumber,Integer> ub(int option){
		if(option==1){
			return serialDao.updateBuilder();
		}else
			return serialRuntimeExceptionDao.updateBuilder();
	}
}
