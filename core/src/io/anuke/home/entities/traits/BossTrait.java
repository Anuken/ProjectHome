package io.anuke.home.entities.traits;

import io.anuke.ucore.ecs.Trait;

public class BossTrait extends Trait{
	public String name;
	
	public BossTrait(String name){
		this.name = name;
	}
	
	private BossTrait(){}
}
