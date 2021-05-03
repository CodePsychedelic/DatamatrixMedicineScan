package dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="Types")
public class Type{

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String type;
	@DatabaseField(
			foreign=true,
			foreignColumnName="id",
			canBeNull=false,
			foreignAutoRefresh=true,
			index=true,
			columnDefinition="integer references Categories(id) on delete cascade on update cascade"

			)
	private Category category;
	
	
	
	public Type(){}
	
	public Type(String type, Category category){
	
	
		this.type=type;
		this.category=category;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id=id;
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type=type;
	}

	public Category getCategory(){
		return category;
	}

	public void setCategory(Category category){
		this.category=category;
	}
	
	
	
	
	
	

}
