package com.example.datamatrixMedicineScan.dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="Fields")
public class Field{
	@DatabaseField(generatedId=true)
	private int id;
	
	
	@DatabaseField(
	foreign=true,
	foreignColumnName="id",
	canBeNull=false,
	foreignAutoRefresh=true,
	index=true,
	columnDefinition="integer references Types(id) on delete cascade on update cascade"

	)
	private Type type;//should be foreign key to Types->id 
	@DatabaseField
	private int fieldId;
	@DatabaseField
	private String fieldName;//
	@DatabaseField
	private String fieldValue;
	@DatabaseField
	private String fieldClass;
	@DatabaseField
	private String fieldContent;
	
	
	public Type getType(){
		return type;
	}
	public void setType(Type type){
		this.type=type;
	}


	
	public Field(){}
	public Field(Type type,int fieldId,String fieldName,String fieldValue,String fieldType,String fieldContent){
		this.fieldId=fieldId;
		this.type=type;
		this.fieldName=fieldName;
		this.fieldValue=fieldValue;//
		this.fieldClass=fieldType;
		this.fieldContent=fieldContent;
	}
	

	public void setId(int id){
		this.id=id;
	}

	
	public void setFieldName(String fieldName){
		this.fieldName=fieldName;
	}
	
	public void setFieldId(int fieldId){
		this.fieldId=fieldId;
	}
	
	public void setFieldValue(String fieldValue){
		this.fieldValue=fieldValue;
	}
	
	public void setFieldType(String fieldType){
		this.fieldClass=fieldType;
	}
	
	public void setFieldContent(String fieldContent){
		this.fieldContent=fieldContent;
	}
	
	
	
	

	public int getId(){
		return id;
	}
	
	public int getFieldId(){
		return fieldId;
	}
	
	public String getFieldName(){
		return fieldName;
	}
	public String getFieldValue(){
		return fieldValue;
	}
	
	public String getFieldType(){
		return fieldClass;
	}
	

	public String getFieldContent(){
		return fieldContent;
	}
	
	
	

}
