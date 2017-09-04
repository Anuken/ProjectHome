package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;

public class Wisp extends Enemy{
	
	@Override
	public void move(Spark spark){
		
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		trait.draw(spark, 12, ()->{
			
		});
	}
}
