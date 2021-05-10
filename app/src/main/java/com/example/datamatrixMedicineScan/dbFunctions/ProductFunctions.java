package com.example.datamatrixMedicineScan.dbFunctions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import com.example.datamatrixMedicineScan.UI.ProductsListActivity;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.ProductAttributes;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;
import com.example.datamatrixMedicineScan.util.Error;
import com.example.datamatrixMedicineScan.util.TextF;
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

import org.javatuples.Triplet;


public class ProductFunctions {


	//context
	private Context context;
	//productDao is the database access object for products table
	private Dao<GTIN, Integer> productDao;
	//productRuntimeExceptionDao is the database runtime exception dao for products table
	private RuntimeExceptionDao<GTIN, Integer> productRuntimeExceptionDao;
	//helper is the DatabaseHelperMiddle object
	private DBMiddle helper;
	private GTIN product;


	//constructor
	public ProductFunctions(Context context) {
		//initialize the context, helper, productDao and productRuntimeExceptionDao
		this.context = context;
		helper = new DBMiddle(context);

		try {
			productDao = helper.getProductDao();
			productRuntimeExceptionDao = helper.getRuntimeExceptionProductDao();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	//select all from product function
	public List<GTIN> selectAll(int option) {
		try {
			if (option == 1) return productDao.queryForAll();
			else if (option == 2) return productRuntimeExceptionDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}


	//selectRaw function
	public GenericRawResults<String[]> selectRaw(String raw, String arguments[], int option) {

		try {
			if (option == 1) return productDao.queryRaw(raw, arguments);
			else if (option == 2) return productRuntimeExceptionDao.queryRaw(raw, arguments);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//selectWhere function
	public List<GTIN> selectWhere(HashMap<String, Object> parameters, int option) {

		try {
			if (option == 1) return productDao.queryForFieldValues(parameters);
			else if (option == 2) return productRuntimeExceptionDao.queryForFieldValues(parameters);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//create product function (insert)
	public boolean create(String code, Category category, int option) {
		product = new GTIN(code, category);
		try {
			if (option == 1) {
				productDao.create(product);
			} else if (option == 2) {
				productRuntimeExceptionDao.create(product);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insert(GTIN product, int option) {
		try {
			if (option == 1) {
				productDao.create(product);
			} else if (option == 2) {
				productRuntimeExceptionDao.create(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	//update function
	public void update(GTIN old, HashMap<String, Object> data, int option) {

		GTIN newProduct = new GTIN();

		newProduct.setId(old.getId());
		newProduct.setCode(old.getCode());
		newProduct.setProductCategory(old.getProductCategory());


		if (data.containsKey("id")) newProduct.setId((Integer) data.get("id"));
		if (data.containsKey("code")) newProduct.setCode(data.get("code").toString());
		if (data.containsKey("category"))
			newProduct.setProductCategory((Category) data.get("category"));


		try {
			if (option == 1) {
				productDao.update(newProduct);
			} else if (option == 2) {
				productRuntimeExceptionDao.update(newProduct);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public List<GTIN> selectLike(String column, String value, int option) {
		QueryBuilder<GTIN, Integer> qb = null;
		if (option == 1) {
			qb = productDao.queryBuilder();
		} else if (option == 2) {
			qb = productRuntimeExceptionDao.queryBuilder();
		}
		try {
			return qb.where().like(column, value + "%").query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}


	public void delete(GTIN product, int option) {
		if (option == 1) {
			try {
				productDao.delete(product);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (option == 2) {
			productRuntimeExceptionDao.delete(product);
		}


	}

	public QueryBuilder<GTIN, Integer> qb(int option) {
		if (option == 1) {
			return productDao.queryBuilder();
		} else
			return productRuntimeExceptionDao.queryBuilder();

	}


	public DeleteBuilder<GTIN, Integer> db(int option) {
		if (option == 1) {
			return productDao.deleteBuilder();
		} else
			return productRuntimeExceptionDao.deleteBuilder();
	}


	public UpdateBuilder<GTIN, Integer> ub(int option) {
		if (option == 1) {
			return productDao.updateBuilder();
		} else
			return productRuntimeExceptionDao.updateBuilder();
	}


	// GET GTIN ID BY CODE
	// ----------------------------------------------------------------------------
	public int getProductGtinId(String gtinCode) {
		ProductFunctions pf = new ProductFunctions(this.context);
		try {
			GTIN gtin = pf.qb(2).where().eq("GTIN_code", gtinCode).queryForFirst();
			return gtin.getId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	// ----------------------------------------------------------------------------

	// GET SERIAL ID BY SERIAL NUMBER
	// ----------------------------------------------------------------------------
	public int getProductSerialId(String gtinCode, String serialNumber) {
		SerialFunctions sf = new SerialFunctions(this.context);
		try {
			int pid = getProductGtinId(gtinCode);
			SerialNumber serial = sf.qb(2).where()
					.eq("serialNumber", serialNumber)
					.and()
					.eq("product_id", pid).queryForFirst();
			return serial.getId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	// ----------------------------------------------------------------------------


	// SO MUCH SCIENCE
	// update the product information -- user input will be checked
	// ----------------------------------------------------------------------------
	public Triplet<String, String, Class> updateProductInformation(View root, List<ProductAttributes> initialValues, String gtinCode, String serialNumber) {

		int id = 1;
		View v;


		String newGTIN = "";                                            // updated GTIN
		String newSerial = "";                                        // updated serial
		HashMap<String, String[]> valuesMapping = new HashMap<String, String[]>();    // valuesMapping is like (field->[oldValue,newValue])

		// check all views -- they will be sequential
		while ((v = root.findViewById(id++)) != null) {

			// if the view is TextF
			if (v instanceof TextF) {
				// get it
				TextF field = ((TextF) v);
				Log.d("textField " + ((TextF) v).getAI() + " " + ((TextF) v).getName() + ":", ((TextF) v).getText().toString());


				// AI FIELDS CHECK
				// ------------------------------------------------------------------------------
				// check for errors using field
				Error e = Error.checkRules(field, field.getAI());

				String title = "";
				String msg = "";
				if (e.getErrorCode().equals("-1")) {            // NUMERIC VIOLATION
					title = "numeric";
					msg = "The type of data on the field: " + e.getField().getName() + " should be numeric";
				} else if (e.getErrorCode().equals("-21")) {    // GTIN LENGTH VIOLATION
					String ai = e.getField().getAI();            // get the application identifier of problem field
					String tokens[] = ai.split(";");    // split it -- paternIdentifier;paternType

					title = "range";
					msg = "The information for the field: " + e.getField().getName() + " should follow: " + tokens[1];
				} else if (e.getErrorCode().equals("-22")) {    // MINIMUM LENGTH VIOLATION
					String ai = e.getField().getAI();            // get the application identifier of problem field
					String tokens[] = ai.split(";");    // split

					title = "range";
					msg = "The information for the field: " + e.getField().getName() + " should follow: " + tokens[1];
				} else if (e.getErrorCode().equals("-23")) {    // MAXIMUM LENGTH VIOLATION
					String ai = e.getField().getAI();
					String tokens[] = ai.split(";");

					title = "range";
					msg = "The information for the field: " + e.getField().getName() + " should follow: " + tokens[1];
				}

				if (title.length() > 0 && msg.length() > 0) {
					//	printMessage(title,msg,null);
					Triplet<String, String, Class> eMsg = new Triplet<>(title, msg, null);
					return eMsg;
				}
				// ------------------------------------------------------------------------------

				// UPDATE ACCORDINGLY
				// ------------------------------------------------------------------------------
				if (field.getName().equals("serialNumber")) {
					newSerial = field.getText().toString();
				} else if (field.getName().equals("productGTIN")) {
					newGTIN = field.getText().toString();
				} else {
					// else put the other data in the values hashmap.
					// like fieldName->fieldValue. Old value will be fixed below
					valuesMapping.put(field.getName(), new String[]{"", field.getText().toString()});
				}
				// ------------------------------------------------------------------------------

			}
		}


		// FIX OLD VALUES FOREACH FIELD -- INITIALVALUES HAVE THEM ORDERED
		// -------------------------------------------------------------------------------------
		for (int i = 0; i < initialValues.size(); i++) {

			// table will hold [oldvalue,newvalue] pair of each field - WITH INIT VALUES ORDER
			String table[] = valuesMapping.get(initialValues.get(i).getPropertyId().getFieldName());

			// init old value position accordingly
			table[0] = initialValues.get(i).getValue();

			//put the table back
			valuesMapping.put(initialValues.get(i).getPropertyId().getFieldName(), table);

			// log
			Log.d("Report" + initialValues.get(i).getPropertyId().getFieldName() + ":",
					"Old value:" + table[0] + " ,New Value:" + table[1]);
		}
		// -------------------------------------------------------------------------------------


		// get functions
		ProductFunctions pf = this;
		SerialFunctions sf = new SerialFunctions(this.context);
		PIFunctions pif = new PIFunctions(this.context);

		// get the product with the old code
		GTIN updatedGTIN = null;
		SerialNumber updatedSerial = null;

		try {
			// get the old gtin object (product) from the database
			GTIN oldProduct = pf.qb(2).where()
					.eq("GTIN_code", gtinCode).
							queryForFirst();

			// get the old serial number from the database
			// based on serial number value and gtin id
			SerialNumber oldSn = sf.qb(2).where().
					eq("serialNumber", serialNumber).
					and().
					eq("product_id", oldProduct.getId()).
					queryForFirst();


			// check if updated GTIN already exist in the database
			GTIN existsProduct = pf.qb(2).
					where().eq("GTIN_code", newGTIN).
					queryForFirst();


			// CASE OF EXISTING PRODUCT GLOBAL TRADE INFORMATION NUMBER (GTIN)
			// ===============================================================

			// if product with new GTIN code exists
			if (existsProduct != null) {
				// check if serial number with new GTIN combination exists
				SerialNumber existsSn = sf.qb(2).where().
						eq("serialNumber", newSerial).
						and().
						eq("product_id", existsProduct.getId()).
						queryForFirst();

				// updatedGTIN=existsProduct;

				// CASE THAT GTIN GROUP AND SERIAL NUMBER EXISTS
				if (existsSn != null) {
					// if the existing product gtin code is not the same
					// with the old product gtin code
					// and the existing serial is not the same with the old serial
					// then we cannot use the comp

					// CASE: THE DIFFERENCE IS ON SNs
					if (existsProduct.getCode().equals(oldProduct.getCode())
							&&
							!existsSn.getSerialNumber().equals(oldSn.getSerialNumber())
					) {
						//printMessage("already exists","There already exists a product with the same serial number",null);
						Triplet<String, String, Class> eMsg = new Triplet<>("already exists", "There already exists a product with the same serial number", null);
						return eMsg;
					}
					// CASE: THE DIFFERENCE IS ON GTINs
					else if (!(existsProduct.getCode().equals(oldProduct.getCode()))) {
						//printMessage("already exists","GTIN group already exists",null);
						Triplet<String, String, Class> eMsg = new Triplet<>("already exists", "GTIN group already exists", null);
						return eMsg;
					}

					// if no problem --> the updated serial is the old one
					updatedSerial = oldSn;
				}
				// CASE: NON EXISTENT SERIAL
				else {
					// if the serial that we want to give is not existent then we update
					// it according to the GTIN of existing product

					if (existsProduct.getCode().equals(oldProduct.getCode())) {
						//update in current gtin
						UpdateBuilder<SerialNumber, Integer> ub = sf.ub(2);
						ub.where().
								eq("serialNumber", serialNumber).
								and().
								eq("product_id", oldProduct.getId());

						ub.updateColumnValue("serialNumber", newSerial);
						ub.update();

						updatedSerial = sf.qb(2).
								where().
								eq("serialNumber", newSerial).
								and().
								eq("product_id", oldProduct.getId()).
								queryForFirst();

					}
					// MOVE GROUPS
					else {
						// gtin exists not the same -- IN A DIFFERENT GROUP
						// update the gtin pointer in old serial
						// update the old serial to new serial...
						// check the remaining serial to old gtin.

						// PRODUCTS ON THE SAME CATEGORY
						if (existsProduct.getProductCategory().getId() == oldProduct.getProductCategory().getId()) {

							// set the update builder to point
							// the serial number of product with old GTIN
							//============================================
							UpdateBuilder<SerialNumber, Integer> ub = sf.ub(2);
							ub.where().
									eq("serialNumber", serialNumber).
									and().
									eq("product_id", oldProduct.getId());
							//============================================

							//update the serial number value and gtin pointer
							//===============================================
							ub.updateColumnValue("product_id", existsProduct.getId());    // GTIN ID
							ub.updateColumnValue("serialNumber", newSerial);            // update serial
							//===============================================

							// DO THE UPDATE
							ub.update();


							// check if there are remaining products under the old GTIN GROUP. If not, delete the group
							List<SerialNumber> slist = sf.qb(2).where().eq("product_id", oldProduct.getId()).query();
							Log.d("LIST SIZE", slist.size() + "");
							if (slist.size() == 0) {
								DeleteBuilder<GTIN, Integer> pdb = pf.db(2);
								pdb.where().eq("GTIN_code", gtinCode);
								pdb.delete();
							}

							updatedSerial = sf.qb(2).
									where().
									eq("serialNumber", newSerial).
									and().
									eq("product_id", existsProduct.getId()).
									queryForFirst();

						} else {
							//printMessage("category","clone OR same category change",null);
							Triplet<String, String, Class> eMsg = new Triplet<>("category", "clone OR same category change", null);

							return eMsg;
						}
					}
				}
			}
			//===============================================================

			//CASE OF NON EXISTENT PRODUCT GLOBAL TRADE INFORMATION NUMBER (PRODUCT GTIN)
			//===============================================================
			else {
				// if we do not have a product with the new GTIN
				// we can create it and insert the serial number

				// create the new GTIN
				// update the GTIN pointer in old serial to new GTIN
				// update the old serial to new serial
				// check list of old gtin to see if we need to delete


				GTIN ngtin = new GTIN(newGTIN, oldProduct.getProductCategory());                            // create new gtin group using the existing category
				pf.insert(ngtin, 2);                                                                // insert the new gtin product
				ngtin = pf.qb(2).where().eq("GTIN_code", newGTIN).queryForFirst();        // get it from db


				// update the old product serial and gtin
				// ---------------------------------------------------------------
				UpdateBuilder<SerialNumber, Integer> ub = sf.ub(2);
				ub.where().
						eq("serialNumber", serialNumber).
						and().
						eq("product_id", oldProduct.getId());

				ub.updateColumnValue("serialNumber", newSerial);
				ub.updateColumnValue("product_id", ngtin.getId());

				ub.update();
				// ---------------------------------------------------------------


				// check remaining
				// ---------------------------------------------------------------
				List<SerialNumber> slist = sf.qb(2).where().eq("product_id", oldProduct.getId()).query();
				Log.d("LIST SIZE:", slist.size() + "");
				if (slist.size() == 0) {
					DeleteBuilder<GTIN, Integer> pdb = pf.db(2);
					pdb.where().eq("GTIN_code", gtinCode);
					pdb.delete();
				}
				// ---------------------------------------------------------------

				updatedGTIN = pf.qb(2).where().
						eq("GTIN_code", newGTIN).
						and().
						eq("category_id", ngtin.getProductCategory().getId()).
						queryForFirst();

				//updatedSerial=newSerialNumber;
				updatedSerial = sf.qb(2).where().
						eq("serialNumber", newSerial).
						and().
						eq("product_id", ngtin.getId()).
						queryForFirst();
			}

			//===============================================================

		} catch (SQLException e) {
			e.printStackTrace();
		}


		// time for updating
		// --------------------------------------------------------------------------------------
		Object[] keys = valuesMapping.keySet().toArray();    // get fields
		try {
			// foreach field
			for (int i = 0; i < keys.length; i++) {
				String val[] = valuesMapping.get(keys[i]);                // get values table
				HashMap<String, Object> newValues = new HashMap<String, Object>();


				// get field functions and fetch each field from the db
				FSFunctions fs = new FSFunctions(this.context);
				Field field = fs.qb(2).where().eq("fieldName", keys[i].toString()).queryForFirst();
				Log.d("Field", field.getFieldName());

				// get the product attributes (values of each field)
				ProductAttributes oldS = pif.qb(2).where().
						eq("serial_id", updatedSerial.getId()).    // we will always have an updated serial (worst case the old one)
						and().
						eq("property_id", field).                // get the field by its id
						and().
						eq("value", val[0]).queryForFirst();    // and its value = old value

				if (oldS == null) {
					// bad news
					Log.d("not", "existent");
					Log.d("info", updatedSerial.getId() + " " + field.getFieldName() + " " + val[0]);
				} else {
					// create an update block and do update
					newValues.put("property", field);
					newValues.put("value", val[1]);
					newValues.put("serial", updatedSerial);
					pif.update(oldS, newValues, 2);
				}

			}

			/*
			// inform dialog
			AlertDialog alertDialog=new AlertDialog.Builder(this.context).create();
			alertDialog.setTitle("Success");
			alertDialog.setMessage("The product was updated successfully");
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
					"OK",
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog,int which){
							// TODO Auto-generated method stub
							dialog.dismiss();
							createActivity(ProductsListActivity.class);
						}
					});

			alertDialog.show();

			 */


		} catch (SQLException e) {
			e.printStackTrace();
		}

		// --------------------------------------------------------------------------------------
		return null;

	}



	public boolean deleteProductInformation(String gtinCode, String serialNumber){
		// we want product and serial functions
		ProductFunctions pf=new ProductFunctions(this.context);
		SerialFunctions sf=new SerialFunctions(this.context);

		try{
			// get product by GTIN code
			GTIN product=pf.qb(2).where().eq("GTIN_code",gtinCode).queryForFirst();

			// serial delete builder -- delete product with serialnumber and GTIN (using product_id foreign key)
			DeleteBuilder <SerialNumber,Integer> db=sf.db(2);
			db.where().eq("serialNumber",serialNumber).and().eq("product_id",product.getId());
			db.delete();

			// we need to check if there are no products remaining under current GTIN
			// ----------------------------------------------------------------------------
			List<SerialNumber> productSerialList=sf.qb(2).where().eq("product_id",product.getId()).query();

			// if no products remaining -- delete the GTIN using product delete builder
			if(productSerialList.size()==0){
				DeleteBuilder <GTIN,Integer> pdb=pf.db(2);
				pdb.where().eq("GTIN_code",gtinCode);
				pdb.delete();
			}
			// ----------------------------------------------------------------------------


			return true;

		}catch(SQLException e){e.printStackTrace();}
		return false;
	}
	// ----------------------------------------------------------------------------

}
