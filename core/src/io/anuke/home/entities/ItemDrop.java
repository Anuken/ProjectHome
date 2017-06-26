package io.anuke.home.entities;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.items.ItemStack;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.renderables.FuncRenderable;
import io.anuke.ucore.renderables.RenderableList;
import io.anuke.ucore.renderables.Sorter;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class ItemDrop extends SolidEntity{
	public final ItemStack stack;
	public static final float pickuprange = 65;
	
	private RenderableList list = new RenderableList();
	private float size = 8f;
	private boolean taken;
	private Vector2 velocity = new Vector2();
	
	public ItemDrop(ItemStack stack){
		this.stack = stack;
		
		new FuncRenderable(b->{
			float raise = Mathf.sin(Timers.time(), 20f, 2f)+2f;
			
			Player player = Vars.control.getPlayer();
			
			if(Vector2.dst(x, y, player.x, player.y) < pickuprange && 
					Mathf.near2d(x, y+5, Graphics.mouseWorld().x, Graphics.mouseWorld().y, 4f)){
				
				Draw.color("purple");
				Draw.circle(x, y+raise+4f, size/1.6f);
				Draw.color();
			}
			
			Draw.grect(stack.item.name + "-item", x, y + raise, size, size);
			
			b.layer = y-1;
		}).add(list);
		
		new FuncRenderable(b->{
			Draw.rect("shadow" + Mathf.clamp(Mathf.roundi(size/8f*6f, 2), 2, 10), x, y);
			b.layer = Sorter.shadow;
			b.sort(Sorter.tile);
		}).add(list);
	}
	
	@Override
	public void update(){
		float drag = 0.08f;
		
		x += velocity.x*delta;
		y += velocity.y*delta;
		velocity.scl(1f - drag*delta);
	}
	
	public ItemDrop randomVelocity(){
		vector.setToRandomDirection();
		velocity.set(vector);
		return this;
	}
	
	public ItemDrop setVelocity(float vx, float vy){
		velocity.set(vx, vy);
		return this;
	}
	
	public boolean taken(){
		return taken;
	}
	
	public void disappear(){
		taken = true;
		Timers.runFor(10, ()->{
			size -= 0.8f*delta;
		}, ()->{
			remove();
		});
	}
	
	@Override
	public void removed(){
		list.free();
	}
	
	@Override
	public boolean collides(SolidEntity other){
		return false;
	}
}
