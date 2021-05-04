package com.example.datamatrixMedicineScan.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DBMiddle{
	private Context context;
	private DBHelper helper;

	private Dao<GTIN,Integer> productDao=null;
	private Dao<Category,Integer> categoryDao=null;
	private Dao<ProductAttributes,Integer> piDao=null;
	private Dao<Field,Integer> fsDao=null;
	private Dao<Pattern,Integer> patternDao=null;
	private Dao<Type,Integer> typeDao=null;
	private Dao<SerialNumber,Integer> serialDao=null;
	
	private RuntimeExceptionDao<GTIN,Integer> productRuntimeExceptionDao=null;
	private RuntimeExceptionDao<Category,Integer> categoryRuntimeExceptionDao=null;
	private RuntimeExceptionDao<ProductAttributes,Integer> piRuntimeExceptionDao=null;
	private RuntimeExceptionDao<Field,Integer> fsRuntimeExceptionDao=null;
	private RuntimeExceptionDao<Pattern,Integer> patternRuntimeExceptionDao=null;
	private RuntimeExceptionDao<Type,Integer> typeRuntimeExceptionDao=null;
	private RuntimeExceptionDao<SerialNumber,Integer> serialRuntimeExceptionDao=null;
	private RuntimeExceptionDao<ProductAttributes,Integer> productAttributesRuntimeExceptionDao=null;
	public DBMiddle(Context context){
		this.context=context;
		helper=OpenHelperManager.getHelper(context, DBHelper.class);
		
	}

	
	
	public Dao<GTIN,Integer> getProductDao() throws SQLException{
		if(productDao==null) productDao=helper.getDao(GTIN.class);
		return productDao;
	}
	
	public Dao<Category,Integer> getCategoryDao() throws SQLException{
		if(categoryDao==null) categoryDao=helper.getDao(Category.class);
		return categoryDao;
	}

	public Dao<ProductAttributes,Integer> getProductInformationDao() throws SQLException{
		if(piDao==null) piDao=helper.getDao(ProductAttributes.class);
		return piDao;
	}
	
	public Dao<Field,Integer> getFieldDao() throws SQLException{
		if(fsDao==null) fsDao=helper.getDao(Field.class);
		return fsDao;
	}
	
	public Dao<Pattern,Integer> getPatternDao() throws SQLException{
		if(patternDao==null) patternDao=helper.getDao(Pattern.class);
		return patternDao;
	}
	public Dao<Type,Integer> getTypeDao() throws SQLException{
		if(typeDao==null) typeDao=helper.getDao(Type.class);
		return typeDao;
	}
	public Dao<SerialNumber,Integer> getSerialDao() throws SQLException{
		if(serialDao==null) serialDao=helper.getDao(SerialNumber.class);
		return serialDao;
	}
	
	

	public RuntimeExceptionDao<GTIN,Integer> getRuntimeExceptionProductDao() throws SQLException{
		if(productRuntimeExceptionDao==null) productRuntimeExceptionDao=helper.getRuntimeExceptionDao(GTIN.class);
		return productRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<Category,Integer> getRuntimeExceptionCategoryDao() throws SQLException{
		if(categoryRuntimeExceptionDao==null) categoryRuntimeExceptionDao=helper.getRuntimeExceptionDao(Category.class);
		return categoryRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<ProductAttributes,Integer> getRuntimeExceptionProductInformationDao() throws SQLException{
		if(piRuntimeExceptionDao==null) piRuntimeExceptionDao=helper.getRuntimeExceptionDao(ProductAttributes.class);
		return piRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<Field,Integer> getRuntimeExceptionFieldDao() throws SQLException{
		if(fsRuntimeExceptionDao==null) fsRuntimeExceptionDao=helper.getRuntimeExceptionDao(Field.class);
		return fsRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<Pattern,Integer> getRuntimeExceptionPatternDao() throws SQLException{
		if(patternRuntimeExceptionDao==null) patternRuntimeExceptionDao=helper.getRuntimeExceptionDao(Pattern.class);
		return patternRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<Type,Integer> getRuntimeExceptionTypeDao() throws SQLException{
		if(typeRuntimeExceptionDao==null) typeRuntimeExceptionDao=helper.getRuntimeExceptionDao(Type.class);
		return typeRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<SerialNumber,Integer> getRuntimeExceptionSerialDao() throws SQLException{
		if(serialRuntimeExceptionDao==null) serialRuntimeExceptionDao=helper.getRuntimeExceptionDao(SerialNumber.class);
		return serialRuntimeExceptionDao;
	}
	
	public SQLiteDatabase getReadableDatabase(){
		return helper.getReadableDatabase();
	}
	
	public SQLiteDatabase getWriteableDatabase(){
		return helper.getWritableDatabase();
	}
	
	
	
	public void clearAll(){
		try{
			//helper.onUpgrade(helper.getWritableDatabase(),helper.getConnectionSource(),1,2);
			//helper.onCreate(helper.getWritableDatabase(),helper.getConnectionSource());
			TableUtils.dropTable(helper.getConnectionSource(), Pattern.class,true);
			TableUtils.dropTable(helper.getConnectionSource(), Field.class,true);
			TableUtils.dropTable(helper.getConnectionSource(),ProductAttributes.class,true);
			TableUtils.dropTable(helper.getConnectionSource(),SerialNumber.class,true);
			TableUtils.dropTable(helper.getConnectionSource(), GTIN.class,true);
			TableUtils.dropTable(helper.getConnectionSource(),Type.class,true);
			TableUtils.dropTable(helper.getConnectionSource(), Category.class,true);
			
			TableUtils.createTable(helper.getConnectionSource(), Category.class);
			
			TableUtils.createTable(helper.getConnectionSource(),Type.class);
			TableUtils.createTable(helper.getConnectionSource(), GTIN.class);
			
			TableUtils.createTable(helper.getConnectionSource(), Field.class);
			TableUtils.createTable(helper.getConnectionSource(),SerialNumber.class);
			
			TableUtils.createTable(helper.getConnectionSource(),ProductAttributes.class);
			TableUtils.createTable(helper.getConnectionSource(), Pattern.class);
		
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void dbSeed(){
		
		try{
			categoryRuntimeExceptionDao=getRuntimeExceptionCategoryDao();
			fsRuntimeExceptionDao=getRuntimeExceptionFieldDao();
			productRuntimeExceptionDao=getRuntimeExceptionProductDao();
			piRuntimeExceptionDao=getRuntimeExceptionProductInformationDao();
			patternRuntimeExceptionDao=getRuntimeExceptionPatternDao();
			typeRuntimeExceptionDao=getRuntimeExceptionTypeDao();
			productAttributesRuntimeExceptionDao=getRuntimeExceptionProductInformationDao();
			serialRuntimeExceptionDao=getRuntimeExceptionSerialDao();


			// empty database
			// ---------------------------------------------------------------
			//empty pattern and attributes
			patternRuntimeExceptionDao.deleteBuilder().delete();
			piRuntimeExceptionDao.deleteBuilder().delete();

			//empty fields and serial numbers
			fsRuntimeExceptionDao.deleteBuilder().delete();
			serialRuntimeExceptionDao.deleteBuilder().delete();

			//empty type and gtin
			typeRuntimeExceptionDao.deleteBuilder().delete();
			productRuntimeExceptionDao.deleteBuilder().delete();

			//empty category
			categoryRuntimeExceptionDao.deleteBuilder().delete();
			// ---------------------------------------------------------------

			
			//initial categories
			//========================================
			Category antipyretic=new Category("Antipyretic");
			Category nasal=new Category("Nasal");
			//Category stamina=new Category("Stamina");
			Category inflamation=new Category("Inflamation");
			
			Category allC=new Category("all");
			//========================================
			
			
		
			//initial types for each CATEGORY
			//=========================================
			Type a_pills=new Type("pills",antipyretic);
			Type a_syrup=new Type("liquid medicine",antipyretic);
			
			Type n_pills=new Type("pills",nasal);
			Type n_spray=new Type("spray",nasal);
			
			//Type s_bar=new Type("stamina bars",stamina);
			//Type s_drink=new Type("stamina drink",stamina);
			//Type s_pills=new Type("stamina pills",stamina);
			
			Type i_pills=new Type("pills",inflamation);
			Type i_spray=new Type("spray",inflamation);
			Type i_gel=new Type("ointment",inflamation);
		
			Type allT=new Type("all",allC);
			//=========================================
			
	
			// fields creation
			// ---------------------------------------------------------------
			Field GTINLabel=new Field(allT,881,"GTINLabel","Global Trade Item Number:","Label","-");
			Field medicineGTINText=new Field(allT,991,"GTIN","-","EditText","String");
			
			Field medicineExpDateLabel=new Field(allT,882,"MedicineExpDateLabel","Expiration Date:","Label","-");
			Field medicineExpDateText=new Field(allT,992,"MedicineExpDate","-","EditText","String");
			
			Field medicineSNLabel=new Field(allT,883,"MedicineSNLabel","Serial Number:","Label","-");
			Field medicineSNText=new Field(allT,993,"MedicineSN","-","EditText","String");
			
			Field medicineBatchLabel=new Field(allT,884,"MedicineBatchLabel","Batch (Lot) number:","Label","-");
			Field medicineBatchText=new Field(allT,994,"MedicineBatch","-","EditText","String");
			
			Field medicineTypeLabel=new Field(allT,885,"MedicineNameLabel","Medicine Name:","Label","-");
			Field medicineNameText=new Field(allT,995,"MedicineName","-","EditText","String");
			// ---------------------------------------------------------------


			// application identifiers init
			// ---------------------------------------------------------------
			Pattern m_gtinAI=new Pattern(medicineGTINText,"01","n14");
			Pattern m_expDateAI=new Pattern(medicineExpDateText,"15","n6");	// need to correct this
			Pattern m_serialAI=new Pattern(medicineSNText,"21","n1-20");
			Pattern m_batchAI=new Pattern(medicineBatchText,"10","s1-20");
			Pattern m_nameAI=new Pattern(medicineNameText,"90","s3-20");		// this is a custom field -- not correct by the standard
			// ---------------------------------------------------------------

			// Antipyretic pills custom fields
			// ---------------------------------------------------------------
			Field a_pillsRecomendedDosologyLabel=new Field(a_pills,111,"AntipireticPillsNumberLabel","Number of pills per day:","Label","-");
			Field a_pillsRecomendedDosologyText=new Field(a_pills,11,"AntipyreticPillsPerDay","-","EditText","Integer");
			Field a_pillsNumberInPackageLabel=new Field(a_pills,112,"AntipireticPillsTabletsLabel","Tablets in Pack:","Label","-");
			Field a_pillsNumberInPackageText=new Field(a_pills,12,"AntipyreticPillsNumber","-","EditText","Integer");
			Field a_pillsButton=new Field(a_pills,13,"pillsSubmitButton","Submit Antipyretic Fields","Button","-");
			// ---------------------------------------------------------------

			// Antipyretic liquid medicine custom fields
			// ---------------------------------------------------------------
			Field a_syrupRecDosologyLabel=new Field(a_syrup,221,"AntipyreticSyrupDosolLabel","Spoons of syrup","Label","-");
			Field a_syrupRecDosologyText=new Field(a_syrup,21,"AntipyreticSyrupDosol","-","EditText","Integer");
			Field a_syrupQuantityLabel=new Field(a_syrup,222,"AntipyreticSyrupQuantityLabel","Quantity of syrup (ml)","Label","-");
			Field a_syrupQuantityText=new Field(a_syrup,22,"AntipyreticSyrupQuantity","-","EditText","Integer");
			Field a_syrupButton=new Field(a_syrup,23,"antipyreticSyrupButton","Submit antipyretic syrup","Button","-");
			// ---------------------------------------------------------------

			// Nasal pills medicine custom fields
			// ---------------------------------------------------------------
			Field n_pillsRecomendedDosologyLabel=new Field(n_pills,331,"NasalPillsNumberLabel","Recomended Dosol(per day)","Label","-");
			Field n_pillsRecomendedDosologyText=new Field(n_pills,31,"NasalPillsDosology","-","EditText","Integer");
			Field n_pillsNumberInPackageLabel=new Field(n_pills,332,"NasalPillsNumberInPackageLabel","Number in package","Label","-");
			Field n_pillsNumberInPackageText=new Field(n_pills,32,"NasalPillsNumberInPackage","-","EditText","Integer");
			Field n_pillsButton=new Field(n_pills,33,"nasalPillsButton","Submit Nasal Medicine","Button","-");
			// ---------------------------------------------------------------

			// Nasal spray medicine custom fields
			// ---------------------------------------------------------------
			Field n_sprayRecomendedDosologyLabel=new Field(n_spray,441,"NasalSprayDosologyLabel","Recomended Sprays(per day)","Label","-");
			Field n_sprayRecomendedDosologyText=new Field(n_spray,41,"NasalSprayDosology","-","EditText","Integer");
			Field n_sprayQuantityLabel=new Field(n_spray,442,"NasalSprayQuantityLabel","Quantity in ml","Label","-");
			Field n_sprayQuantityText=new Field(n_spray,42,"NasalSprayQuantity","-","EditText","Integer");
			Field n_sprayButton=new Field(n_spray,43,"nasalSprayButton","Submit Nasal Spray Medicine","Button","-");
			// ---------------------------------------------------------------


			Field i_pillsRecomendedDosologyLabel=new Field(i_pills,551,"InflamationPillsDosologyLabel","Recomended pills per day:","Label","-");
			Field i_pillsRecomendedDosologyText=new Field(i_pills,51,"InfalamtionPillsDosology","-","EditText","Integer");
			Field i_pillsQuantityLabel=new Field(i_pills,552,"InflamationPillsQuantityLabel","Tablets in package:","Label","-");
			Field i_pillsQuantityText=new Field(i_pills,52,"InflamationPillsQuantity","-","EditText","Integer");
			Field i_pillsButton=new Field(i_pills,53,"InfamationPillsButton","Submit inflamation Pills","Button","-");
			
			
			Field i_sprayRecomendedDosologyLabel=new Field(i_spray,661,"InflamationSprayDosologyLabel","Recomended application times per day:","Label","-");
			Field i_sprayRecomendedDosologyText=new Field(i_spray,61,"InfalamtionSprayDosology","-","EditText","Integer");
			Field i_sprayQuantityLabel=new Field(i_spray,662,"InflamationSprayQuantityLabel","Quantity of spray (ml):","Label","-");
			Field i_sprayQuantityText=new Field(i_spray,62,"InflamationSprayQuantity","-","EditText","Integer");
			Field i_sprayButton=new Field(i_spray,63,"InfamationSprayButton","Submit inflamation spray","Button","-");
			
			
			
			Field i_gellRecomendedDosologyLabel=new Field(i_gel,771,"InflamationGellDosologyLabel","Recomended application times per day:","Label","-");
			Field i_gellRecomendedDosologyText=new Field(i_gel,71,"InfalamtionGellDosology","-","EditText","Integer");
			Field i_gellQuantityLabel=new Field(i_gel,772,"InflamationGellQuantityLabel","Quantity of gel (grams):","Label","-");
			Field i_gellQuantityText=new Field(i_gel,72,"InflamationGelQuantity","-","EditText","Integer");
			Field i_gellButton=new Field(i_gel,73,"InfamationGellButton","Submit inflamation Gell","Button","-");
			
			
			
			/*Type i_pills=new Type("pills",inflamation);
			Type i_spray=new Type("spray",inflamation);
			Type i_gel=new Type("gell",inflamation);
			*/
			
			
			
			

			categoryRuntimeExceptionDao.create(antipyretic);
			categoryRuntimeExceptionDao.create(nasal);
			//categoryRuntimeExceptionDao.create(stamina);
			categoryRuntimeExceptionDao.create(inflamation);
			categoryRuntimeExceptionDao.create(allC);
			
			typeRuntimeExceptionDao.create(a_pills);
			typeRuntimeExceptionDao.create(a_syrup);
			typeRuntimeExceptionDao.create(n_pills);
			typeRuntimeExceptionDao.create(n_spray);
			//typeRuntimeExceptionDao.create(s_bar);
			//typeRuntimeExceptionDao.create(s_drink);
			//typeRuntimeExceptionDao.create(s_pills);
			
			typeRuntimeExceptionDao.create(i_pills);
			typeRuntimeExceptionDao.create(i_spray);
			typeRuntimeExceptionDao.create(i_gel);
			
			typeRuntimeExceptionDao.create(allT);
			
			
		
			
			//global fields
			//==================================================
			fsRuntimeExceptionDao.create(GTINLabel);
			fsRuntimeExceptionDao.create(medicineGTINText);
			
			fsRuntimeExceptionDao.create(medicineExpDateLabel);
			fsRuntimeExceptionDao.create(medicineExpDateText);
			
			fsRuntimeExceptionDao.create(medicineSNLabel);
			fsRuntimeExceptionDao.create(medicineSNText);
			
			fsRuntimeExceptionDao.create(medicineBatchLabel);
			fsRuntimeExceptionDao.create(medicineBatchText);
			
			fsRuntimeExceptionDao.create(medicineTypeLabel);
			fsRuntimeExceptionDao.create(medicineNameText);
			//==================================================
			
			
			//extra fields
			//==================================================
			fsRuntimeExceptionDao.create(a_pillsRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(a_pillsRecomendedDosologyText);	
			fsRuntimeExceptionDao.create(a_pillsNumberInPackageLabel);
			fsRuntimeExceptionDao.create(a_pillsNumberInPackageText);
			fsRuntimeExceptionDao.create(a_pillsButton);
			
			fsRuntimeExceptionDao.create(a_syrupRecDosologyLabel);
			fsRuntimeExceptionDao.create(a_syrupRecDosologyText);
			fsRuntimeExceptionDao.create(a_syrupQuantityLabel);
			fsRuntimeExceptionDao.create(a_syrupQuantityText);
			fsRuntimeExceptionDao.create(a_syrupButton);
			
			
			fsRuntimeExceptionDao.create(n_pillsRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(n_pillsRecomendedDosologyText);
			fsRuntimeExceptionDao.create(n_pillsNumberInPackageLabel);
			fsRuntimeExceptionDao.create(n_pillsNumberInPackageText);
			fsRuntimeExceptionDao.create(n_pillsButton);
			
			fsRuntimeExceptionDao.create(n_sprayRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(n_sprayRecomendedDosologyText);
			fsRuntimeExceptionDao.create(n_sprayQuantityLabel);
			fsRuntimeExceptionDao.create(n_sprayQuantityText);
			fsRuntimeExceptionDao.create(n_sprayButton);
			
			
			fsRuntimeExceptionDao.create(i_pillsRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(i_pillsRecomendedDosologyText);
			fsRuntimeExceptionDao.create(i_pillsQuantityLabel);
			fsRuntimeExceptionDao.create(i_pillsQuantityText);	
			fsRuntimeExceptionDao.create(i_pillsButton);
			
			fsRuntimeExceptionDao.create(i_sprayRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(i_sprayRecomendedDosologyText);
			fsRuntimeExceptionDao.create(i_sprayQuantityLabel);
			fsRuntimeExceptionDao.create(i_sprayQuantityText);
			fsRuntimeExceptionDao.create(i_sprayButton);
			
			
			fsRuntimeExceptionDao.create(i_gellRecomendedDosologyLabel);
			fsRuntimeExceptionDao.create(i_gellRecomendedDosologyText);
			fsRuntimeExceptionDao.create(i_gellQuantityLabel);
			fsRuntimeExceptionDao.create(i_gellQuantityText);
			fsRuntimeExceptionDao.create(i_gellButton);
			
			patternRuntimeExceptionDao.create(m_gtinAI);
			patternRuntimeExceptionDao.create(m_serialAI);
			patternRuntimeExceptionDao.create(m_expDateAI);
			patternRuntimeExceptionDao.create(m_batchAI);
			patternRuntimeExceptionDao.create(m_nameAI);
			
			
	
			//product init.
			int prInit=0;
			if(prInit==1){
				GTIN gtin=new GTIN("12345678901234",antipyretic);
				productRuntimeExceptionDao.create(gtin);
				
				SerialNumber sn=new SerialNumber("001",gtin,i_gel);
				serialRuntimeExceptionDao.create(sn);
				
				ProductAttributes pa=new ProductAttributes(sn,medicineGTINText,"12345678901234");
				productAttributesRuntimeExceptionDao.create(pa);
				pa=new ProductAttributes(sn,medicineSNText,"001");
				productAttributesRuntimeExceptionDao.create(pa);
				pa=new ProductAttributes(sn,medicineExpDateText,"050792");
				productAttributesRuntimeExceptionDao.create(pa);
				pa=new ProductAttributes(sn,medicineBatchText,"5ab");
				productAttributesRuntimeExceptionDao.create(pa);
				pa=new ProductAttributes(sn,medicineNameText,"Panadol Extra");
				productAttributesRuntimeExceptionDao.create(pa);
				
				
				
				
			}
			
			
			
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}
