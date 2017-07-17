package io.anuke.home.entities;

import static io.anuke.home.entities.Direction.*;

import io.anuke.home.Vars;
import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.home.items.Item;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Player extends Creature{
	public static final float hitdur = 30;
	public static final float basespeed = 1.8f;
	public static final float height = 4;
	
	private Direction direction = right;
	private float hittime;
	private boolean oncheckpoint = false;
	private float dashscl = 1f;
	private float dashcharge = 15f;
	
	public Item weapon;
	public int defense;
	public int attack;
	public float speed;
	
	private float walktime;
	private float walkspeed = 0.09f;
	
	
	public Player(){
		setMaxHealth(100);
		hitsize = 4;
		hitoffsety = 4;
	}
	
	public void reset(){
		direction = right;
		weapon = null;
		speed = defense = attack = 0;
	}
	
	public float getHitTime(){
		return hittime/hitdur;
	}
	
	@Override
	public void drawRenderables(){
		draw(b->{
			String walk = "";
			
			if(walktime > 0){
				walk = "walk" + ((int)(walktime)%2+1);
			}
			
			Draw.grect("player-" + direction.name+walk, x, y, direction.flipped);
			
			b.layer = y;
		});
		
		draw(b->{
			float angle = angle() + (weapon == null ? 0f : weapon.weapontype.getAngleOffset());
			
			drawWeapons();
			
			if(angle > 0 && angle < 180){
				b.layer = y+1;
			}else{
				b.layer = y-0.1f;
			}
		});
		
		drawShadow(8, 0);
	}
	
	void drawWeapons(){
		if(weapon == null) return;
		
		weapon.weapontype.draw(this, weapon);
	}
	
	boolean side(){
		return angle() > 90 && angle() < 270;
	}
	
	void combat(){
		if(weapon == null) return;
		
		if(!Vars.ui.hasMouse() && !Vars.ui.getInventory().selectedItem()){
			weapon.weapontype.update(this);
		}
	}
	
	public float angle(){
		return Angles.mouseAngle(x, y+height);
	}
	
	ItemDrop closestDrop(){
		return (ItemDrop)Entities.getClosest(Graphics.mouseWorld().x, Graphics.mouseWorld().y, 10f, 
				e->{return e instanceof ItemDrop && !((ItemDrop)e).taken();});
	}
	
	@Override
	public void damage(int amount){
		int out = Mathf.clamp(amount-defense, 2, 1000);
		super.damage(out);
	}
	
	@Override
	public void shoot(Projectiles type, int damage, float x, float y, float angle){
		Projectile p = new Projectile(type, this, angle).set(x, y).add();
		p.damage = damage+attack;
	}
	
	@Override
	public void onDeath(){
		clampHealth();
		Effects.effect("explosion", this);
		Effects.sound("death", this);
		remove();
		oncheckpoint = true;
		
		Vars.control.onDeath();
	}
	
	@Override
	public void onHit(SolidEntity entity){
		Effects.effect("blood", entity);
		Effects.sound("hurt", this);
		hittime = hitdur;
		clampHealth();
	}
	
	@Override
	public void update(){
		delta = Mathf.clamp(delta, 0, 2f);
		
		if(hittime > 0){
			hittime -= delta;
			hittime = Mathf.clamp(hittime, 0, hitdur);
		}
		
		if(Timers.get(this, "regen", 60)){
			health ++;
			clampHealth();
		}
		
		Tile tile = World.get(Mathf.scl2(x, Vars.tilesize), Mathf.scl2(y, Vars.tilesize));
		if(tile.wall == Blocks.checkpoint){
			if(!oncheckpoint){
				Effects.effect("checkpoint", tile.worldx(), tile.worldy());
				Vars.control.addCheckpoint(tile);
				heal();
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
				Effects.effect("dash", x, y+3);
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
			float angle = angle();
			direction = (angle < 45 || angle > 315 ? right : angle >= 45 && angle < 135 ? back : angle >= 135 && angle < 225 ? left : front);
		}
		
		vector.limit(speed);
		
		if(vector.isZero()){
			walktime = 0f;
		}else{
			walktime += walkspeed*delta*this.speed/basespeed;
		}
		
		move(vector.x, vector.y);
		
		
		ItemDrop drop = closestDrop();
		
		if(Inputs.keyUp("pickup") && drop != null && !Vars.ui.getInventory().isFull()){
			Vars.ui.getInventory().addItem(drop.stack);
			Effects.sound("pickup", this);
			drop.disappear();
		}
		
		combat();
	}
	
	@Override
	public boolean collides(SolidEntity other){
		return other instanceof Projectile && ((Projectile)other).owner instanceof Enemy;
	}
	
}
