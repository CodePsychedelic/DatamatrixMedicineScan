package com.example.datamatrixMedicineScan.tools;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

import com.example.datamatrixMedicineScan.dbFunctions.CategoryFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.FSFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PIFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PatternFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.ProductFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.SerialFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.TypeFunctions;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.ProductAttributes;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;

public class Tools{
	public static ProductFunctions pf;
	public static TypeFunctions tf;
	public static SerialFunctions sf;
	public static FSFunctions fs;
	public static PatternFunctions paf;
	public static CategoryFunctions cf;
	public static PIFunctions pif;
	
	public static void initializeTools(Context context){
		pf=new ProductFunctions(context);
		tf=new TypeFunctions(context);
		sf=new SerialFunctions(context);
		fs=new FSFunctions(context);
		paf=new PatternFunctions(context);
		cf=new CategoryFunctions(context);
		pif=new PIFunctions(context);
		
	}
	
	
	public static void reallocFields(DeleteBuilder<ProductAttributes,Integer> padb, List<Field> fields, List<Field> newFields, SerialNumber code) throws SQLException{
		for(int i=0;i<fields.size();i++){
			padb.where().eq("property_id",fields.get(i).getId()).and().eq("serial_id",code.getId());
			padb.delete();
		}
		for(int i=0;i<newFields.size();i++){
			//SerialNumber sn=Tools.sf.qb(2).where().eq("serialNumber",code).and().eq("product_id",productId).queryForFirst();
			Tools.pif.create(code,newFields.get(i),"-",2);
		}
		
	}
	
	
}
