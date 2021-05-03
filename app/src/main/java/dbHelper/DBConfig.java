package dbHelper;


import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;


public class DBConfig extends OrmLiteConfigUtil{

	private static final Class<?> classes[]=new Class<?>[]{
		GTIN.class,
		Category.class,
		ProductAttributes.class,
		Field.class,
		Pattern.class,
		Type.class,
		SerialNumber.class
	};
	public static void main(String[] args){
		// TODO Auto-generated method stub
		try{
			writeConfigFile("ormlite_config.txt",classes);
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
