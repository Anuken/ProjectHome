package io.anuke.home.entities.ecs.types;

import io.anuke.home.entities.ecs.traits.PlayerTrait;
import io.anuke.home.items.Item;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.*;

public class Player extends Prototype{
	
	public Player(){
		
	}

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(), 
			new PlayerTrait(),
			new HealthTrait(100),
			new ColliderTrait(4),
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
						//TODO
						//weapon.weapontype.draw(this, weapon);
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
