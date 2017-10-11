package io.anuke.home.entities.traits;

import static io.anuke.home.entities.Direction.*;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.entities.Direction;
import io.anuke.home.items.types.Soul;
import io.anuke.home.items.types.Weapon;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.processors.CollisionProcessor;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;
import io.anuke.ucore.ecs.extend.traits.TileCollideTrait;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class PlayerTrait extends Trait{
	public static final float hitdur = 30;
	public static final float basespeed = 1.6f;
	public static final float height = 4;
	
	public Direction direction = right;
	public float dashscl = 1f;
	public float dashcharge = 15f;
	
	public boolean oncheckpoint = false;
	public int defense;
	public int attack;
	public float speed;
	public float hittime;
	
	public Weapon weapon;
	public Soul soul;
	
	public float walktime;
	public float walkspeed = 0.09f;
	public float lightrange;
	
	@Override
	public void added(Spark spark){
		
	}
	
	@Override
	public void update(Spark spark){
		HealthTrait ht = spark.get(HealthTrait.class);
		PosTrait pos = spark.pos();
		
		lightrange = 100;
		
		Vector2 vector = Tmp.v1;
		
		float delta = Mathf.clamp(Timers.delta(), 0, 2f);
		
		if(hittime > 0){
			hittime -= delta;
			hittime = Mathf.clamp(hittime, 0, hitdur);
		}
		
		if(Timers.get(this, "regen", 60)){
			ht.health ++;
			ht.clampHealth();
		}
		
		Tile tile = World.get(Mathf.scl2(pos.x, Vars.tilesize), Mathf.scl2(pos.y, Vars.tilesize));
		if(tile != null && tile.wall == Blocks.checkpoint){
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
			walktime += walkspeed*delta*(this.speed+basespeed)/basespeed;
		}
		
		spark.get(TileCollideTrait.class).move(spark, vector.x, vector.y);
		
		Spark drop = closestDrop(spark);
		
		if(Inputs.keyUp("pickup") && drop != null && !Vars.ui.getInventory().isFull()){
			Vars.ui.getInventory().addItem(drop.get(DropTrait.class).stack);
			Effects.sound("pickup", spark);
			drop.get(DropTrait.class).disappear(drop);
		}
		
		if(weapon != null && !Vars.ui.hasMouse() && !Vars.ui.getInventory().selectedItem()){
			weapon.update(spark);
		}
		
		if(soul != null){
			soul.update(spark);
		}
	}
	
	public float getHitTime(){
		return hittime/hitdur;
	}
	
	public void reset(){
		direction = right;
		weapon = null;
		speed = defense = attack = 0;
	}
	
	public float angle(Spark spark){
		return Angles.mouseAngle(spark.pos().x, spark.pos().y+height);
	}
	
	Spark closestDrop(Spark spark){
		return spark.getBasis().getProcessor(CollisionProcessor.class)
				.getClosest(Graphics.mouseWorld().x, Graphics.mouseWorld().y, 10f, e->e.has(DropTrait.class));
	}
}
