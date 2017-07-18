package io.anuke.home.entities.processors;

import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitProcessor;

public class HealthBarProcessor extends TraitProcessor{
	
	public HealthBarProcessor(){
		super(HealthBarTrait.class);
	}

	@Override
	public void update(Spark spark){
		spark.get(HealthBarTrait.class).drawer.accept(spark);
	}
}
