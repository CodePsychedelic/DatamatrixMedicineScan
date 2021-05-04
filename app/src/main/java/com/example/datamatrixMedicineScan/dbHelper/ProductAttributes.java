package com.example.datamatrixMedicineScan.dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ProductAttributes")
public class ProductAttributes{
	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(
		foreign=true,
		foreignColumnName="id",
		canBeNull=false,
		foreignAutoRefresh=true,
		index=true,
		columnDefinition="integer references SerialNumbers(id) on delete cascade on update cascade"
	)
	private SerialNumber serial;//should be foreing key to SerialNumbers->id
	@DatabaseField(
		foreign=true,
		foreignColumnName="id",
		canBeNull=false,
		foreignAutoRefresh=true,
		index=true,
		columnDefinition="integer references Fields(id) on delete cascade on update cascade"
		)
	private Field property;
	@DatabaseField 
	private String value;
	
	
	
	public ProductAttributes(){
		
	}
	
	public ProductAttributes(SerialNumber serial, Field property, String value){
		this.serial=serial;
		this.property=property;
		this.value=value;
	}
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id=id;
	}
	public SerialNumber getSerialNumber(){
		return serial;
	}
	public void setSerial(SerialNumber serial){
		this.serial=serial;
	}
	public Field getPropertyId(){
		return property;
	}
	public void setPropertyId(Field property){
		this.property=property;
	}
	public String getValue(){
		return value;
	}
	public void setValue(String value){
		this.value=value;
	}

	
	
	
	
	
}
