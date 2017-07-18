package io.anuke.home.entities.types;

import io.anuke.home.Vars;
import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.items.Item;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.*;

public class Player extends Prototype{
	
	public Player(){
		event(Death.class, spark->{
			Effects.effect("explosion", spark);
			Effects.sound("death", spark);
			spark.remove();
			spark.get(PlayerTrait.class).oncheckpoint = true;
			
			Vars.control.onDeath();
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
			new RenderableTrait((trait, spark)->{
				trait.draw(b->{
					PlayerTrait player = spark.get(PlayerTrait.class);
					float walktime = player.walktime;
					String walk = "";
					
					if(walktime > 0){
						walk = "walk" + ((int)(walktime)%2+1);
					}
					
					Draw.grect("player-" + player.direction.name+walk, spark.pos().x, spark.pos().y, player.direction.flipped);
					
					b.layer = spark.pos().y;
				});
				
				trait.draw(b->{
					PlayerTrait player = spark.get(PlayerTrait.class);
					Item weapon = player.weapon;
					float angle = player.angle(spark) + (weapon == null ? 0f : weapon.weapontype.getAngleOffset());
					
					if(weapon != null){
						weapon.weapontype.draw(spark, weapon);
					}
					
					if(angle > 0 && angle < 180){
						b.layer = spark.pos().y+1;
					}else{
						b.layer = spark.pos().y-0.1f;
					}
				});
				
				trait.drawShadow(spark, 8, 0);
			})
		);
	}

}
