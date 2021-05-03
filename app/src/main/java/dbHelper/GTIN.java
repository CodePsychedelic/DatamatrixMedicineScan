package dbHelper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="GTIN")
public class GTIN{
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String GTIN_code;
	@DatabaseField(
			foreign=true,
			foreignColumnName="id",
			canBeNull=false,
			foreignAutoRefresh=true,
			index=true,
			columnDefinition="integer references Categories(id) on delete cascade on update cascade"
		
			)
	private Category category;//should be foreing to Categories->id
	
	public GTIN(){}
	
	public GTIN(String code, Category productCategory){
		this.GTIN_code=code;
		this.category=productCategory;
	}
	
	
	public int getId(){
		return id;
	}
	
	public String getCode(){
		return GTIN_code;
	}
	
	public Category getProductCategory(){
		return category;
	}
	
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setCode(String code){
		this.GTIN_code=code;
	}
	
	public void setProductCategory(Category category){
		this.category=category;
	}
	
	
	
}
