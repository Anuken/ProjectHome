package io.anuke.home.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.BaseBulletType;
import io.anuke.ucore.util.Geometry;

public class Projectiles extends BaseBulletType<Projectile>{
	static final Color tent = new Color(0x500680ff);
	static final float pd = -0.3f;
	static final float ld = -10;
	
	public static final Projectiles
	yellowshot = new Projectiles(){
		{
			speed = 3f+pd;
			hiteffect = "yellowblap";
			//despawneffect = "tentablip";
			lifetime = 70+ld;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.YELLOW, Color.WHITE, b.ifract());
			Draw.polygon(4, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	scorchshot = new Projectiles(){
		{
			speed = 2.6f+pd;
			hiteffect = "scorchblap";
			lifetime = 70+ld;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.CORAL, Color.ORANGE, b.ifract());
			Draw.polygon(3, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	aethershot = new Projectiles(){
		{
			speed = 3f+pd;
			hiteffect = "aetherblap";
			lifetime = 70+ld;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.PURPLE, Color.MAGENTA, b.ifract());
			Draw.circle(b.x, b.y, 3);
			Draw.color();
		}
	},
	orbshot = new Projectiles(){
		{
			speed = 2f+pd;
			hiteffect = "orbshrink";
			despawneffect = "orbshrink";
			lifetime = 70+ld;
			hitsize = 14;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.WHITE, Color.PINK, b.ifract());
			Draw.circle(b.x, b.y, 8);
			Draw.color();
		}
	},
	planeshot = new Projectiles(){
		{
			speed = 4.2f+pd;
			hiteffect = "planeblap";
			despawneffect = "planeblap";
			lifetime = 40+ld;
		}
		
		public void draw(Projectile b){
			Draw.thick(2f);
			Draw.color(Color.WHITE, Color.LIME, b.ifract());
			Draw.lineAngleCenter(b.x, b.y, b.angle(), 4);
			Draw.reset();
		}
	},
	fusionshot = new Projectiles(){
		{
			speed = 2.6f+pd;
			hiteffect = "fusionblap";
			lifetime = 50+ld;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.CORAL, Color.WHITE, b.ifract());
			Draw.thick(1f);
			Draw.polygon(3, b.x, b.y, 3, b.angle()-90);
			Draw.lineAngleCenter(b.x, b.y, b.angle(), 7);
			Draw.color();
		}
	},
	slash = new Projectiles(){
		{
			speed = 0f;
			hitsize = 6;
			lifetime = 7;
		}
	},
	tentaslash = new Projectiles(){
		{
			speed = 2f;
			hitsize = 9;
			lifetime = 7;
			despawneffect = "tentaflash";
		}
		
		public void draw(Projectile b){
			Draw.color(Color.PURPLE, Color.BLACK, b.ifract());
			Draw.lineAngle(b.x, b.y, 0, b.angle()+30, 6);
			Draw.polygon(3, b.x, b.y, 4, b.angle()+180);
			Draw.reset();
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
			damage = 10;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.ORANGE, Color.PURPLE, b.ifract());
			Draw.rect("circle", b.x, b.y, 3, 3);
			Draw.thick(2f);
			Draw.polysegment(30, 0, 12, b.x, b.y, 5, b.angle()-360/5f-90);
			Draw.reset();
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
	tentashot2 = new Projectiles(){
		{
			speed = 1.9f;
			hiteffect = "golemflash";
			despawneffect = "tentablip";
			lifetime = 140;
			damage = 5;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.WHITE, tent, b.ifract());
			Draw.polygon(3, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	shadowshot = new Projectiles(){
		{
			speed = 1.7f;
			hiteffect = "tentablip";
			despawneffect = "tentablip";
			lifetime = 160;
			damage = 5;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.BLACK, tent, b.ifract());
			Draw.polygon(10, b.x, b.y, 4, b.angle()-90);
			Draw.color();
		}
	},
	shadowsplit = new Projectiles(){
		{
			speed = 1.7f;
			hiteffect = "tentablip";
			despawneffect = "tentablip";
			lifetime = 90;
			damage = 12;
		}
		
		public void draw(Projectile b){
			Draw.color(Color.BLACK, tent, b.ifract());
			Draw.polygon(10, b.x, b.y, 8, b.angle()-90);
			Draw.color();
		}
		
		public void despawned(Projectile b){
			super.despawned(b);
			Geometry.circle(6, f->{
				new Projectile(shadowshot, b.owner, f-90).set(b.x, b.y).add();
			});
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
	};
	
	@Override
	public void draw(Projectile b){
		
	}

}
