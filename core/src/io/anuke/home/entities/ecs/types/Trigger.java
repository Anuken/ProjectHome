package io.anuke.home.entities.ecs.types;

import io.anuke.home.entities.ecs.traits.TriggerTrait;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;

public class Trigger extends Prototype{

	@Override
	public TraitList traits(){
		return new TraitList(new TriggerTrait());
	}

}
