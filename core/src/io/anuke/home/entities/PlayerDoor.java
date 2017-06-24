package io.anuke.home.entities;

import io.anuke.home.Vars;
import io.anuke.ucore.util.Timers;

public class PlayerDoor extends Door{
	
	public PlayerDoor(int tilex, int tiley){
		super(true, tilex, tiley);
	}
	
	@Override
	public void update(){
		if(Timers.get(this, "wallupdate", 30)){
			Player p = Vars.control.player;
			
			if((front && p.y < tiley*Vars.tilesize+6) || (!front && p.y > (tiley+height)*Vars.tilesize+6)){
				hideBlocks();
			}else{
				showBlocks();
			}
		}
	}
}
