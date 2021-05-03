package dbFunctions;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import dbHelper.Category;
import dbHelper.DBMiddle;
import dbHelper.Type;

public class TypeFunctions{



//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<Type,Integer> typeDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<Type,Integer> typeRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private Type type;
	

	//constructor
	public TypeFunctions(Context context){
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context=context;
		helper=new DBMiddle(context);
		helper.getWriteableDatabase().execSQL("PRAGMA foreign_keys=ON");
		try{
			typeDao=helper.getTypeDao();
			typeRuntimeExceptionDao=helper.getRuntimeExceptionTypeDao();
			
		}catch(SQLException e){e.printStackTrace();}
	}
	
	
	
	//select all from product function
	public List<Type> selectAll(int option){
		try{
			if(option==1) return typeDao.queryForAll();
			else if(option==2) return typeRuntimeExceptionDao.queryForAll();
		}catch(SQLException e){e.printStackTrace();}
		return null;
		
	}
	
	
	//selectRaw function
	public GenericRawResults<String[]> selectRaw(String raw,String arguments[],int option){
		try{
			if(option==1) return typeDao.queryRaw(raw,arguments);
			else if(option==2) return typeRuntimeExceptionDao.queryRaw(raw,arguments);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//selectWhere function
	public List<Type> selectWhere(HashMap<String,Object> parameters, int option){
		
		try{
			if(option==1) return typeDao.queryForFieldValues(parameters);
			else if(option==2) return typeRuntimeExceptionDao.queryForFieldValues(parameters);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	//create product function (insert)
	public boolean create(String name, Category category, int option){
		type=new Type(name,category);
		try{
			if(option==1){
				typeDao.create(type);
			}else if(option==2){
				typeRuntimeExceptionDao.create(type);
			
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		
		return true;
	}
	
	public boolean insert(Type type, int option){
		try{
			if(option==1){
				typeDao.create(type);
			}else if(option==2){
				typeRuntimeExceptionDao.create(type);
			}
		}catch(SQLException e){e.printStackTrace();return false;}
		return true;
	}
	
	/*
	//update function
	public void update(Product old,HashMap<String,Object> data,int option){

		Product newProduct=new Product();

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
		
		
			
		
	}*/
	/*
	public List<Product> selectLike(String column,String value,int option){
		QueryBuilder<Product,Integer> qb=null;
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
	
	
	public void delete(Product product,int option){
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
*/

	
	public QueryBuilder<Type,Integer> qb(int option){
		if(option==1){
			return typeDao.queryBuilder();
		}else
			return typeRuntimeExceptionDao.queryBuilder();
		
	}






}
