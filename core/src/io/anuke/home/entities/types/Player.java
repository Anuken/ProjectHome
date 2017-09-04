package io.anuke.home.entities.types;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.Vars;
import io.anuke.home.effect.Shaders.Distort;
import io.anuke.home.effect.Shaders.Outline;
import io.anuke.home.entities.Direction;
import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.items.Item;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.processors.CollisionProcessor;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.function.Callable;

public class Player extends Prototype{
	
	public Player(){
		event(Death.class, spark->{
			Effects.effect("explosion", spark);
			Effects.sound("death", spark);
			spark.remove();
			spark.get(PlayerTrait.class).oncheckpoint = true;
			
			Vars.control.onDeath();
			
			Basis.instance().getProcessor(CollisionProcessor.class)
			.getNearby(spark.pos().x, spark.pos().y, 300, e->{
				if(e.getType() instanceof Projectile){
					Effects.effect("shotshrink", e);
					e.remove();
				}
			});
		});
		
		event(Damaged.class, (spark, source, damage)->{
			PlayerTrait player = spark.get(PlayerTrait.class);
			Effects.effect("blood", spark);
			Effects.sound("hurt", spark);
			player.hittime = PlayerTrait.hitdur;
		});
		
		event(CollisionFilter.class, (spark, other)->{
			return other.has(ProjectileTrait.class) && other.get(ProjectileTrait.class).source.getType() instanceof Enemy;
		});
	}

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(), 
			new PlayerTrait(),
			new HealthTrait(100),
			new ColliderTrait(4),
			new TileCollideTrait(0.5f, 1.5f, 4, 3),
			new FacetTrait((trait, spark)->{
				
				trait.draw(b->{
					
					PlayerTrait player = spark.get(PlayerTrait.class);
					Item weapon = player.weapon;
					float angle = player.angle(spark) + (weapon == null ? 0f : weapon.weapontype.getAngleOffset());
					
					Callable drawWeapon = ()->{
						if(weapon != null){
							weapon.weapontype.draw(spark, weapon);
						}else{
							int ox = player.direction == Direction.left ? -1 : 0;
							Draw.rect("hand", spark.pos().x - 2 + ox, spark.pos().y + 2);
							Draw.rect("hand", spark.pos().x + 2 + ox, spark.pos().y + 2);
						}
						
						if(angle > 0 && angle < 180 && weapon != null){
							b.layer = spark.pos().y+1;
						}else{
							b.layer = spark.pos().y-0.1f;
						}
					};
					
					boolean drawBefore = angle > 0 && angle < 180 && weapon != null;
					
					
					Draw.getShader(Outline.class).color = Color.PURPLE;
					Draw.beginShaders(Distort.class);
					
					if(drawBefore){
						drawWeapon.run();
					}
					
					float walktime = player.walktime;
					String walk = "";
					
					if(walktime > 0){
						walk = "walk" + ((int)(walktime)%2+1);
					}
					
					Draw.grect("player-" + player.direction.name+walk, spark.pos().x, spark.pos().y, player.direction.flipped);
					
					b.layer = spark.pos().y;
					
					if(!drawBefore){
						drawWeapon.run();
					}
					
					Draw.endShaders();
					
				});
				
				trait.drawShadow(spark, 8, 0);
			})
		);
	}

}
