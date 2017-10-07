package io.anuke.home.items;

public class Item{
	public final String name;
	public String typeName;
	public String formalName;
	public String description = null;
	public boolean stackable = false;
	public int attackbuff = 0, defensebuff = 0, speedbuff = 0;
	
	public Item(String name, String formalName, String typeName){
		this.name = name;
		this.formalName = formalName;
		this.typeName = typeName;
	}
	
	public String getStats(){
		String out = "";
		
		if(Math.abs(attackbuff) > 0){
			out += "\n" + parse(attackbuff) + " Attack";
		}
		
		if(Math.abs(defensebuff) > 0){
			out += "\n" + parse(defensebuff) + " Defense";
		}
		
		if(Math.abs(speedbuff) > 0){
			out += "\n" + parse(speedbuff) + " Speed";
		}
		
		return out;
	}
	
	private String parse(int i){
		if(i < 0){
			return "[crimson]- " + -i;
		}else{
			return "[lime]+ " + i;
		}
	}
}
