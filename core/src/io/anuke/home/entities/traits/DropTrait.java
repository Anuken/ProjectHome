package io.anuke.home.entities.traits;

import io.anuke.home.items.ItemStack;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;

public class DropTrait extends Trait{
	public ItemStack stack;
	public boolean taken = false;
	public float size = 8f;
	
	@Override
	public void registerEvents(Prototype type){
		type.traitEvent(CollisionFilter.class, (a, b)->{
			return false;
		});
	}
	
	public void disappear(Spark spark){
		taken = true;
		
		Timers.runFor(10, ()->{
			size -= 0.8f*Timers.delta();
		}, ()->{
			spark.remove();
		});
	}
}
