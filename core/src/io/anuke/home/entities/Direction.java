package io.anuke.home.entities;

public enum Direction{
	back, front, left(true, ""), right(false, "");
	
	public boolean flipped;
	public String name;
	
	private Direction(){
		name = name();
	}
	
	private Direction(boolean flipped, String name){
		this.flipped = flipped;
		this.name = name;
	}
}
