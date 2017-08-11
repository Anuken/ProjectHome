package io.anuke.home.entities.types;

import io.anuke.home.entities.traits.TriggerTrait;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;

public class Trigger extends Prototype{
	
	//TODO
	@Override
	public TraitList traits(){
		return new TraitList(new TriggerTrait());
	}

}
