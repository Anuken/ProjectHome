package io.anuke.home.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.home.Vars;
import io.anuke.home.items.Item;
import io.anuke.home.items.ItemStack;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.DestructibleEntity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Mathf;

public class Enemy extends Creature{
	static ObjectMap<Class<? extends Creature>, Array<Drop>> dropmap = new ObjectMap<>();
	static Array<Drop> empty = new Array<>();

	static final float timeout = 600;

	public DestructibleEntity target;

	public String deatheffect = "death";
	public String hiteffect = "hit";
	public String deathsound = "tentadie";

	public float height = 4;
	public float range = 230;
	public float speed = 0.5f;
	public float idletime = 0f;
	public boolean despawn = true;

	public void retarget(){
		
		if(targetValid()){
			idletime = 0;
			return;
		}else{
			target = null;
		}
		
		Player player = Vars.control.getPlayer();

		float dst = distanceTo(player);

		//optimization
		if(dst < range && !player.isDead()){
			target = player;
		}else{
			target = null;

			if(dst > 300 && despawn){
				idletime += delta;

				if(idletime >= timeout){
					Tile tile = World.get(Mathf.scl(x, Vars.tilesize), Mathf.scl(y, Vars.tilesize));
					
					if(tile != null && tile.wall == Blocks.air){
						tile.data = this;
						tile.wall = Blocks.spawner;
						heal();
						remove();
						reset();
					}else{
						idletime = 0f;
					}
				}
			}
		}

		//TODO uncomment this for multiplayer, if needed
		//target = (DestructibleEntity)Entities.getClosest(x, y, 100, e->{
		//	return e instanceof Player;
		//});
	}
	
	public void reset(){
		
	}

	protected boolean targetValid(){
		return target != null && !target.isDead() && distanceTo(target) < range;
	}

	public void move(){
	}

	public void moveToward(){
		vector.set(target.x - x, target.y - y);
		vector.setLength(speed * delta);
		move(vector.x, vector.y);
	}

	@Override
	public void onHit(SolidEntity entity){
		Effects.effect(hiteffect, entity);
	}

	@Override
	public void update(){
		retarget();

		if(targetValid()){
			move();
		}
	}

	@Override
	public boolean collides(SolidEntity other){
		return other instanceof Projectile && ((Projectile) other).owner instanceof Player;
	}
	
	@Override
	public void draw(){
		float offsety = -3f;
		float length = 9f;
		float height = 1f;
		float pad = 1f;
		
		Draw.color(new Color(0x3e2e50ff));
		Draw.crect("blank", x-length/2f-pad, y+offsety-pad, length+pad*2f, height+pad*2f);
		
		Draw.color(Color.BLACK);
		Draw.crect("blank", x-length/2f, y+offsety, length, height);
		
		Draw.color(Color.SCARLET);
		Draw.crect("blank", x-length/2f, y+offsety, length*healthfrac(), height);
		
		Draw.color();
	}

	public void onDeath(){
		Vars.control.addKill(this);
		for(int i = 0; i < 4; i++){
			Effects.effect("hit", x + Mathf.range(5), y + Mathf.range(5) + height);
		}

		Effects.effect(deatheffect, x, y + height);
		Effects.sound(deathsound, this);
		dropStuff();

		remove();
	}
	
	public static void setDrops(Class<? extends Enemy> type, Class<? extends Enemy> inherit){
		dropmap.put(type, dropmap.get(inherit));
	}

	/** List format: [ItemStack/Item], drop chance, (repeat). **/
	public static void setDrops(Class<? extends Enemy> type, Object... objects){
		Array<Drop> drops = new Array<Drop>();

		for(int i = 0; i < objects.length; i += 2){
			Drop drop = new Drop();

			if(objects[i] instanceof Item){
				drop.stack = new ItemStack((Item) objects[i], 1);
			}else{
				drop.stack = (ItemStack) objects[i];
			}
			
			if(objects[i + 1] instanceof Double){
				drop.chance = (double)(objects[i + 1])/100.0;
			}else if(objects[i + 1] instanceof Float){
				drop.chance = (float)(objects[i + 1])/100.0;
			}else{
				drop.chance = (int)(objects[i + 1])/100.0;
			}
			
			drops.add(drop);
		}

		dropmap.put(type, drops);
	}

	public static Array<Drop> getDrops(Class<? extends Enemy> type){
		return dropmap.get(type, empty);
	}

	void dropStuff(){
		Array<Drop> drops = dropmap.get(getClass(), empty);

		for(Drop drop : drops){
			if(Mathf.chance(drop.chance)){
				new ItemDrop(drop.stack.copy()).randomVelocity().set(x, y).add();
			}
		}
	}

	static class Drop{
		public double chance;
		public ItemStack stack;

		public String toString(){
			return stack.item.name + ": " + chance * 100 + "%";
		}
	}
}
