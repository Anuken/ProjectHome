package io.anuke.home.entities.ecs.traits;

import static io.anuke.home.entities.Direction.*;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.entities.Direction;
import io.anuke.home.entities.ItemDrop;
import io.anuke.home.entities.ecs.types.Enemy;
import io.anuke.home.items.Item;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.util.*;

public class PlayerTrait extends Trait{
	public static final float hitdur = 30;
	public static final float basespeed = 1.8f;
	public static final float height = 4;
	
	public Direction direction = right;
	public float dashscl = 1f;
	public float dashcharge = 15f;
	
	public boolean oncheckpoint = false;
	public Item weapon;
	public int defense;
	public int attack;
	public float speed;
	public float hittime;
	
	public float walktime;
	public float walkspeed = 0.09f;
	
	@Override
	public void registerEvents(Prototype type){
		type.traitEvent(Death.class, spark->{
			Effects.effect("explosion", spark);
			Effects.sound("death", spark);
			spark.remove();
			spark.get(PlayerTrait.class).oncheckpoint = true;
			
			Vars.control.onDeath();
		});
		
		type.traitEvent(Damaged.class, (spark, source, damage)->{
			PlayerTrait player = spark.get(PlayerTrait.class);
			Effects.effect("blood", spark);
			Effects.sound("hurt", spark);
			player.hittime = hitdur;
		});
		
		type.traitEvent(CollisionFilter.class, (spark, other)->{
			return other.has(ProjectileTrait.class) && other.get(ProjectileTrait.class).source.getType() instanceof Enemy;
		});
	}
	
	@Override
	public void update(Spark spark){
		HealthTrait ht = spark.get(HealthTrait.class);
		PosTrait pos = spark.pos();
		
		Vector2 vector = Tmp.v1;
		
		float delta = Mathf.clamp(Mathf.delta(), 0, 2f);
		
		if(hittime > 0){
			hittime -= delta;
			hittime = Mathf.clamp(hittime, 0, hitdur);
		}
		
		if(Timers.get(this, "regen", 60)){
			ht.health ++;
			ht.clampHealth();
		}
		
		Tile tile = World.get(Mathf.scl2(pos.x, Vars.tilesize), Mathf.scl2(pos.y, Vars.tilesize));
		if(tile.wall == Blocks.checkpoint){
			if(!oncheckpoint){
				Effects.effect("checkpoint", tile.worldx(), tile.worldy());
				Vars.control.addCheckpoint(tile);
				ht.heal();
			}
			oncheckpoint = true;
		}else{
			oncheckpoint = false;
		}
		
		vector.set(0, 0);
		
		float speed = (this.speed+basespeed)*delta;
		
		if(Inputs.keyDown("dash")){
			dashscl -= delta/dashcharge;
			if(dashscl > 0.3f && Timers.get(this, "dasheffect", 3)){
				Effects.effect("dash", pos.x, pos.y+3);
			}
			dashscl = Mathf.clamp(dashscl);
			speed *= (1f+dashscl*2f);
		}else{
			dashscl += delta/dashcharge/2f;
		}
		
		dashscl = Mathf.clamp(dashscl);
		
		if(Inputs.keyDown("up")){
			vector.y += speed;
			direction = back;
		}
		
		if(Inputs.keyDown("down")){
			vector.y -= speed;
			direction = front;
		}
		
		if(Inputs.keyDown("left")){
			vector.x -= speed;
			direction = left;
		}
		
		if(Inputs.keyDown("right")){
			vector.x += speed;
			direction = right;
		}
		
		if(weapon != null){
			float angle = Angles.mouseAngle(pos.x, pos.y + height);
			direction = (angle < 45 || angle > 315 ? right : angle >= 45 && angle < 135 ? back : angle >= 135 && angle < 225 ? left : front);
		}
		
		vector.limit(speed);
		
		if(vector.isZero()){
			walktime = 0f;
		}else{
			walktime += walkspeed*delta*speed/basespeed;
		}
		
		spark.get(TileCollideTrait.class).move(spark, vector.x, vector.y);
		
		ItemDrop drop = closestDrop(spark);
		
		if(Inputs.keyUp("pickup") && drop != null && !Vars.ui.getInventory().isFull()){
			Vars.ui.getInventory().addItem(drop.stack);
			Effects.sound("pickup", spark);
			drop.disappear();
		}
		
		if(weapon != null && !Vars.ui.hasMouse() && !Vars.ui.getInventory().selectedItem()){
			//weapon.weapontype.update(this);
		}
	}
	
	public float angle(Spark spark){
		return Angles.mouseAngle(spark.pos().x, spark.pos().y+height);
	}
	
	ItemDrop closestDrop(Spark spark){
		//TODO
		return null;
		//return (ItemDrop)Entities.getClosest(Graphics.mouseWorld().x, Graphics.mouseWorld().y, 10f, 
		//		e->{return e instanceof ItemDrop && !((ItemDrop)e).taken();});
	}
}
