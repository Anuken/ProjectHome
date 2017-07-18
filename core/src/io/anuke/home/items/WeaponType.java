package io.anuke.home.items;

import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.modules.Module;

public class WeaponType{
	protected static Vector2 vector = Module.vector;
	public int damage = 1;
	public float speed;
	
	public String getStatString(){
		return "Damage: " + damage + 
				"\n[firebrick]Attack Speed: " + toFixed(60f/speed, 1);
	}
	
	public float getAngleOffset(){
		return 0f;
	}
	
	public void draw(Spark player, Item item){
		
	}
	
	public void update(Spark player){
		
	}
	
	private static String toFixed(double d, int decimalPlaces) {
	    if (decimalPlaces < 0 || decimalPlaces > 8) {
	        throw new IllegalArgumentException("Unsupported number of "
	                + "decimal places: " + decimalPlaces);
	    }
	    String s = "" + Math.round(d * Math.pow(10, decimalPlaces));
	    int len = s.length();
	    int decimalPosition = len - decimalPlaces;
	    StringBuilder result = new StringBuilder();
	    if (decimalPlaces == 0) {
	        return s;
	    } else if (decimalPosition > 0) {
	        // Insert a dot in the right place
	        result.append(s.substring(0, decimalPosition));
	        result.append(".");
	        result.append(s.substring(decimalPosition));
	    } else {
	        result.append("0.");
	        // Insert leading zeroes into the decimal part
	        while (decimalPosition++ < 0) {
	            result.append("0");
	        }
	        result.append(s);
	    }
	    return result.toString();
	}
}
