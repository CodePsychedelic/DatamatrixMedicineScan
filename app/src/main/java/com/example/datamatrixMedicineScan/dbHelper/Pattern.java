package com.example.datamatrixMedicineScan.dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="Patterns")
public class Pattern{
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(
			foreign=true,
			foreignColumnName="id",
			canBeNull=false,
			foreignAutoRefresh=true,
			index=true,
			columnDefinition="integer references Fields(id) on delete cascade on update cascade"
		)
	private Field field;
	@DatabaseField
	private String patternIdentifier;
	@DatabaseField
	private String patternType;
	
	
	
	public Pattern(){
		
	}
	
	public Pattern(Field field,String pattern,String patternType){
		this.field=field;
		this.patternIdentifier=pattern;
		this.patternType=patternType;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id=id;
	}

	public Field getField(){
		return field;
	}

	public void setField(Field field){
		this.field=field;
	}

	public String getPatternIdentifier(){
		return patternIdentifier;
	}

	public void setPattern(String pattern){
		this.patternIdentifier=pattern;
	}
	
	public String getPatternType(){
		return patternType;
	}
	
	public void setPatternType(){
		this.patternType=patternType;
	}
	

}
