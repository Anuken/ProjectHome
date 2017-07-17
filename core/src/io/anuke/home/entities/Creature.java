package io.anuke.home.entities;

import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.DestructibleEntity;
import io.anuke.ucore.renderables.FuncRenderable;
import io.anuke.ucore.renderables.FuncRenderable.DrawFunc;
import io.anuke.ucore.renderables.RenderableList;
import io.anuke.ucore.renderables.Sorter;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public abstract class Creature extends DestructibleEntity{
	protected RenderableList list = new RenderableList();
	boolean drawn = false;
	
	public int frames(float scale, int amount){
		return Mathf.scl(Timers.time(), scale)%amount+1;
	}

	public void shadow(int size){
		drawShadow(size, 0);
	}
	
	public void drawShadow(int size, float offsety){
		
		String shadow = "shadow"
				+ (int) (size * 0.8f / 2f + Math.pow(size, 1.5f) / 200f) * 2;
		
		draw(p->{
			p.provider = Sorter.tile;
			p.layer = Sorter.shadow;
			Draw.rect(shadow, x, y+offsety);
		});
	}
	
	public void draw(DrawFunc d){
		list.add(new FuncRenderable(0, Sorter.object, d));
	}
	
	public void shoot(Projectiles type, float x, float y, float angle){
		new Projectile(type, this, angle).set(x, y).add();
	}
	
	public void shoot(Projectiles type, int damage, float x, float y, float angle){
		Projectile p = new Projectile(type, this, angle).set(x, y).add();
		p.damage = damage;
	}
	
	protected void shoot(Projectiles type, float angle){
		shoot(type, x, y, angle);
	}
	
	public void drawRenderables(){
		
	}
	
	@Override
	public void added(){
		if(!drawn){
			drawRenderables();
		}
		drawn = true;
	}
	
	@Override
	public void removed(){
		list.free();
		drawn = false;
	}
	
}
