package io.anuke.home.entities.ecs.types;

import io.anuke.home.entities.ecs.traits.LootTrait;
import io.anuke.home.items.Items;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.ColliderTrait;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;

/**Base enemy prototype. Extend to use.*/
public abstract class Enemy extends Prototype{
	
	@Override
	public TraitList typeTraits(){
		return new TraitList(
			new LootTrait(Items.aetherstaff, 1)
		);
	}
	
	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(),
			new ColliderTrait(),
			new HealthTrait()
		);
	}
}
