package io.anuke.home.entities.traits;

import io.anuke.home.Vars;
import io.anuke.ucore.ecs.Require;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;

@Require({PosTrait.class, EnemyTrait.class})
public class BossTrait extends Trait{
	public String name;
	
	public BossTrait(String name){
		this.name = name;
	}
	
	private BossTrait(){}

	@Override
	public void update(Spark spark){
		Spark target = spark.get(EnemyTrait.class).target;
		
		if(Vars.control == null) return;
		
		if(target == null){
			Vars.control.resetBoss(spark);
		}else{
			Vars.control.setBoss(spark);
		}
	}
}
