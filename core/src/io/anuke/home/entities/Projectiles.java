package io.anuke.home.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.BaseBulletType;
import io.anuke.ucore.util.Geometry;

public class Projectiles extends BaseBulletType<Projectile>{
	static final Color tent = new Color(0x500680ff);
	
	public static final Projectiles
	
	slash = new Projectiles(){
		{
			speed = 0f;
			hitsize = 6;
			lifetime = 7;
		}
	},
	phaseslash = new Projectiles(){
		{
			speed = 0.8f;
			hitsize = 7;
			lifetime = 7;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.SKY);
			Draw.alpha(b.ifract());
			Draw.lineAngle(b.x, b.y, b.angle(), 6);
			Draw.reset();
		}
	},
	phaseslashalt = new Projectiles(){
		{
			speed = 2f;
			hitsize = 11;
			lifetime = 7;
		}
		
		public void draw(Projectile b){
			Draw.thick(1f);
			Draw.color(Color.SKY);
			Draw.alpha(b.ifract());
			Draw.lineAngle(b.x, b.y, b.angle(), 4);
			Draw.lineAngle(b.x, b.y, b.angle()-30, 4);
			Draw.lineAngle(b.x, b.y, b.angle()+30, 4);
			Draw.reset();
		}
	},
	golemshot = new Projectiles(){
		{
			speed = 1f;
			hiteffect = "golemflash";
			despawneffect = "shotshrink";
		}
		
		public void draw(Projectile b){
			Draw.color(Color.SCARLET, Color.BLACK, b.ifract());
			Draw.circle(b.x, b.y, 3);
			Draw.color();
		}
	},
	golemsplitshot = new Projectiles(){
		{
			speed = 1.3f;
			hiteffect = "golemflash";
			despawneffect = "shotshrink";
			damage = 25;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.WHITE, Color.ORANGE, b.ifract());
			Draw.circle(b.x, b.y, 4);
			Draw.color();
		}
		
		public void despawned(Projectile b){
			super.despawned(b);
			Geometry.circle(8, f->{
				new Projectile(golemsmallshot, b.owner, f-90).set(b.x, b.y).add();
			});
		}
	},
	golemsmallshot = new Projectiles(){
		{
			speed = 1.3f;
			hiteffect = "golemflash";
			despawneffect = "gshotshrink";
			hitsize = 6;
			damage = 3;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.ORANGE, Color.WHITE, b.ifract());
			Draw.rect("circle", b.x, b.y, 4, 4);
			Draw.color();
		}
	},
	droneshot = new Projectiles(){
		{
			speed = 1.8f;
			hiteffect = "golemflash";
			despawneffect = "gshotshrink";
			hitsize = 6;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.ORANGE, Color.WHITE, b.ifract());
			Draw.circle(b.x, b.y, 2f);
			Draw.color();
		}
	},
	golemwave = new Projectiles(){
		{
			speed = 2.1f;
			hiteffect = "golemflash";
			despawneffect = "gshotshrink";
			hitsize = 6;
			damage = 6;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.ORANGE, Color.PURPLE, b.ifract());
			Draw.rect("circle", b.x, b.y, 3, 3);
			Draw.thick(2f);
			Draw.polysegment(30, 0, 12, b.x, b.y, 5, b.angle()-360/5f-90);
			Draw.reset();
		}
	},
	yellowshot = new Projectiles(){
		{
			speed = 3f;
			hiteffect = "yellowblap";
			//despawneffect = "tentablip";
			lifetime = 100;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.YELLOW, Color.WHITE, b.ifract());
			Draw.polygon(4, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	tentashot = new Projectiles(){
		{
			speed = 1.9f;
			hiteffect = "golemflash";
			despawneffect = "tentablip";
			lifetime = 140;
			damage = 5;
		}
		
		public void draw(Projectile b){
			Draw.color(tent, Color.BLACK, b.ifract());
			Draw.polygon(3, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	daggershot = new Projectiles(){
		{
			speed = 2.6f;
			hiteffect = "golemflash";
			despawneffect = "tentablip";
			lifetime = 100;
			damage = 5;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.WHITE, Color.SCARLET, b.ifract());
			Draw.polygon(3, b.x, b.y, 3, b.angle()-90);
			Draw.color();
		}
	},
	tentaball = new Projectiles(){
		{
			speed = 1.3f;
			hiteffect = "golemflash";
			despawneffect = "tentablip";
			lifetime = 90;
			damage = 15;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.PURPLE, Color.BLACK, b.ifract());
			Draw.rect("circle", b.x, b.y, 7f, 7f);
			Draw.color(Color.PURPLE);
			Draw.circle(b.x, b.y, 2);
			Draw.color();
		}
		
		public void despawned(Projectile b){
			super.despawned(b);
			Geometry.circle(6, f->{
				new Projectile(tentashot, b.owner, f-90).set(b.x, b.y).add();
			});
		}
	};;
	
	@Override
	public void draw(Projectile b){
		
	}

}
