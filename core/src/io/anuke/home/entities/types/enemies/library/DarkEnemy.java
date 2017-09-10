package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.editor.Editor;
import io.anuke.home.entities.traits.*;
import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.util.Mathf;

public abstract class DarkEnemy extends Enemy{
	float waketime = 60f;
	public static final Color eyeColor = new Color(0x882ddcff);
	
	public DarkEnemy(){
		range = 120;
		effectColor = eyeColor;
		deatheffect = "shadedeath";
		hiteffect = "shadehit";
	}
	
	@Override
	public void update(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(spark.health().dead){
			//enemy.time -= Mathf.delta();
			//enemy.time = Mathf.clamp(enemy.time, 0, waketime);
			return;
		}
		
		if(enemy.targetValid(spark) || Editor.active()){
			if(enemy.time <= 0.0001f){
				if(spark.has(ParticleTrait.class))
					spark.get(ParticleTrait.class).reset(spark);
			}
			enemy.time += Mathf.delta();
		}else{
			enemy.time -= Mathf.delta();
		}
		
		enemy.time = Mathf.clamp(enemy.time, 0, waketime);
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new DarkenTrait()).exclude(HealthBarTrait.class);
	}
}
