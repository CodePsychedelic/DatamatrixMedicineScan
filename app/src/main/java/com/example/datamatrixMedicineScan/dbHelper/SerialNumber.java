package com.example.datamatrixMedicineScan.dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="SerialNumbers")
public class SerialNumber{
@DatabaseField(generatedId=true)	
private int id;
@DatabaseField	
	private String serialNumber;
@DatabaseField(
		foreign=true,
		foreignColumnName="id",
		canBeNull=false,
		foreignAutoRefresh=true,
		index=true,
		columnDefinition="integer references GTIN(id) on delete cascade on update cascade"
	
		)
	private GTIN product;

	@DatabaseField(
			foreign=true,
			foreignColumnName="id",
			canBeNull=false,
			foreignAutoRefresh=true,
			index=true,
			columnDefinition="integer references Types(id) on delete cascade on update cascade"
			)
	private Type type;
	
	
	public SerialNumber(){}
	
	public SerialNumber(String serialNumber, GTIN product, Type type){
		this.serialNumber=serialNumber;
		this.product=product;
		this.type=type;
	}
	
	
	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id=id;
	}

	public String getSerialNumber(){
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber){
		this.serialNumber=serialNumber;
	}

	public GTIN getProduct(){
		return product;
	}

	public void setProduct(GTIN product){
		this.product=product;
	}
	
	
	public Type getType(){
		return type;
	}
	
	public void setType(Type type){
		this.type=type;
	}
	
	
}
