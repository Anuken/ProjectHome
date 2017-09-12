package io.anuke.home.entities.types;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.ParticleTrait;
import io.anuke.home.entities.traits.ParticleTrait.Particle;
import io.anuke.home.entities.types.enemies.library.DarkEnemy;
import io.anuke.home.entities.types.enemies.library.Shade;
import io.anuke.home.entities.types.enemies.library.Sol;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.ProjectileTrait.ProjectileType;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

//TODO aaaaaaAAAAAAAAAAAAAAAAAAaaaa
public abstract class Projectiles extends ProjectileType{
	static final Color tent = new Color(0x500680ff);
	static final float pd = -0.3f;
	static final float ld = -10;
	
	public boolean light = false;
	public float lightsize = 15;
	
	public boolean dark = false;
	public boolean particles = false;
	public int particleAmount = 10;
	
	public static final Projectiles
	darkball = new Projectiles(){
		{
			dark = true;
			speed = 0.01f;
			lifetime = 110f;
		}
		
		public void draw(Spark b){
			float rad = b.life().ifract()*12f;
			float rad2 = Mathf.clamp(b.life().ifract()-0.5f)*24f;
			
			Draw.color(Color.DARK_GRAY);
			Draw.rect("circle", b.pos().x, b.pos().y, rad, rad);
			
			Draw.color(DarkEnemy.eyeColor);
			Draw.rect("circle", b.pos().x, b.pos().y, rad2, rad2);
			
			Draw.reset();
		}
		
		public void despawned(Spark spark){
			Projectile.create(darkshot, spark.projectile().source, spark.pos().x, spark.pos().y, spark.velocity().angle());
			Effects.shake(3f, 3f);
		}
	},
	darkshot = new Projectiles(){
		{
			dark = true;
			particles = true;
			speed = 1f;
			lifetime = 150f;
			particleAmount = 30;
			hitsize = 8;
		}
		
		public void draw(Spark b){
			float rad = 9f;
			
			ParticleTrait particles = b.get(ParticleTrait.class);
			
			Draw.color(DarkEnemy.eyeColor);
			
			for(Particle p : particles.particles){
				float prad = p.fract()*5f;
				Draw.rect("circle", p.x, p.y, prad, prad);
			}
			
			Draw.rect("circle", b.pos().x, b.pos().y, rad, rad, Timers.time()*2f);
			
			Draw.thick(2f);
			Draw.polygon(20, b.pos().x, b.pos().y, rad/2f + 3f, -Timers.time()*2f);
			
			Draw.reset();
			
			//Draw.spikes(b.pos().x, b.pos().y, rad/2f-1f, 3f, 4, Timers.time()*2f);
		}
		
		public void despawned(Spark spark){
			removed(spark);
		}
		
		public void removed(Spark spark){
			Effects.shake(2f, 3f);
			
			Angles.circle(8, f->{
				Angles.translation(f, 4f);
				Projectile.create(darksplit, spark.projectile().source, spark.pos().x + Angles.vector.x, spark.pos().y + Angles.vector.y, f);
			});
		}
	},
	darksplit = new Projectiles(){
		{
			dark = true;
			particles = true;
			speed = 1f;
			lifetime = 100f;
			hiteffect = despawneffect = "purpleshadecloud";
		}
		
		public void draw(Spark b){
			float rad = 10f*b.life().fract();
			
			ParticleTrait particles = b.get(ParticleTrait.class);
			
			Draw.color(DarkEnemy.eyeColor);
			
			for(Particle p : particles.particles){
				float prad = p.sfract()*5f*b.life().fract();
				Draw.rect("circle", p.x, p.y, prad, prad);
			}
			
			Draw.rect("circle", b.pos().x, b.pos().y, rad, rad, Timers.time()*2f);
			
			Draw.reset();
			
			//Draw.circle(b.pos().x, b.pos().y, rad/2f + 2f);
			
			//Draw.spikes(b.pos().x, b.pos().y, rad/2f-1f, 3f, 4, Timers.time()*2f);
		}
	},
	darkorb = new Projectiles(){
		{
			dark = true;
			//particles = true;
			speed = 1f;
			lifetime = 200f;
			despawneffect = hiteffect = "shadewave";
		}
		
		public void draw(Spark b){
			float rad = 5f;
			
			Draw.color(DarkEnemy.eyeColor);
			
			Draw.rect("circle", b.pos().x, b.pos().y, rad/1.3f, rad/1.3f, Timers.time()*2f);
			
			Draw.circle(b.pos().x, b.pos().y, rad);
			
			Draw.reset();
			
			//Draw.circle(b.pos().x, b.pos().y, rad/2f + 2f);
			
			//Draw.spikes(b.pos().x, b.pos().y, rad/2f-1f, 3f, 4, Timers.time()*2f);
		}
		
		public void update(Spark spark){
			spark.velocity().vector.rotate(Timers.delta()/2f);
			
			if(Timers.get(spark, "fire", 13)){
				Angles.circle(3, f->{
					Projectile.create(darksplit, spark.projectile().source, spark.pos().x, spark.pos().y, f + spark.velocity().angle());
				});
				
			}
		}
	},
	darkorb3 = new Projectiles(){
		{
			dark = true;
			//particles = true;
			speed = 1f;
			lifetime = 200f;
			despawneffect = hiteffect = "shadewave";
		}
		
		public void draw(Spark b){
			float rad = 5f;
			
			Draw.color(DarkEnemy.eyeColor);
			
			Draw.rect("circle", b.pos().x, b.pos().y, rad/1.3f, rad/1.3f, Timers.time()*2f);
			
			Draw.circle(b.pos().x, b.pos().y, rad);
			
			Draw.reset();
			
			//Draw.circle(b.pos().x, b.pos().y, rad/2f + 2f);
			
			//Draw.spikes(b.pos().x, b.pos().y, rad/2f-1f, 3f, 4, Timers.time()*2f);
		}
		
		public void update(Spark spark){
			spark.velocity().vector.rotate(-Timers.delta()/2f);
			
			if(Timers.get(spark, "fire", 13)){
				Angles.circle(3, f->{
					Projectile.create(darksplit, spark.projectile().source, spark.pos().x, spark.pos().y, f + spark.velocity().angle());
				});
				
			}
		}
	},
	darkorb2 = new Projectiles(){
		{
			dark = true;
			//particles = true;
			speed = 1f;
			lifetime = 200f;
			despawneffect = hiteffect = "shadewave";
		}
		
		public void draw(Spark b){
			float rad = 4f;
			
			Draw.color(DarkEnemy.eyeColor);
			
			Draw.rect("circle", b.pos().x, b.pos().y, rad/1.3f, rad/1.3f, Timers.time()*2f);
			
			Draw.circle(b.pos().x, b.pos().y, rad);
			
			Draw.reset();
			
			//Draw.circle(b.pos().x, b.pos().y, rad/2f + 2f);
			
			//Draw.spikes(b.pos().x, b.pos().y, rad/2f-1f, 3f, 4, Timers.time()*2f);
		}
	},
	lightshot = new Projectiles(){
		{
			speed = 1f;
			hiteffect = "yellowblap";
			lifetime = 110;
			light = true;
		}
		
		public void draw(Spark b){
			
			Draw.thick(2f);
			Draw.lineAngleCenter(b.pos().x, b.pos().y, b.velocity().angle(), 4f);
			Draw.thick(1f);
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle(), 4f);
		}
	},
	lightball = new Projectiles(){
		{
			speed = 1f;
			hiteffect = "yellowblap";
			lifetime = 110;
			light = true;
		}
		
		public void draw(Spark b){
			
			Draw.color(Sol.color);
			Draw.thick(1f);
			Draw.circle(b.pos().x, b.pos().y, 3);
			Draw.circle(b.pos().x, b.pos().y, 1f + Mathf.sin(Timers.time(), 10f, 1f));
			Draw.reset();
		}
	},
	lightflash = new Projectiles(){
		{
			speed = 0.14f;
			lifetime = 13f;
			damage = 0;
			
		}
		
		public void draw(Spark b){
			
			float ifract = b.life().ifract();
			
			Draw.color(Color.valueOf("ffda77"), Sol.color, ifract);
			
			Draw.thick(4f - ifract*4f);
			Draw.lineAngleCenter(b.pos().x, b.pos().y, b.velocity().angle(), 9f);
			Draw.thick(3f - ifract*3f);
			Draw.lineAngleCenter(b.pos().x, b.pos().y, b.velocity().angle(), 13f);
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle(), 7f);
			Draw.thick(2f - ifract*2f);
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle(), 11f);
			
			Draw.reset();
		}
	},
	yellowshot = new Projectiles(){
		{
			speed = 3f+pd;
			hiteffect = "yellowblap";
			//despawneffect = "tentablip";
			lifetime = 70+ld;
		}
		
		public void draw(Spark b){
			Draw.color(Color.YELLOW, Color.WHITE, b.life().ifract());
			Draw.polygon(4, b.pos().x, b.pos().y, 4, b.velocity().angle()-90);
			Draw.color();
		}
	},
	scorchshot = new Projectiles(){
		{
			speed = 2.6f+pd;
			hiteffect = "scorchblap";
			lifetime = 70+ld;
		}
		
		public void draw(Spark b){
			Draw.color(Color.CORAL, Color.ORANGE, b.life().ifract());
			Draw.polygon(3, b.pos().x, b.pos().y, 4, b.velocity().angle()-90);
			Draw.color();
		}
	},
	aethershot = new Projectiles(){
		{
			speed = 3f+pd;
			hiteffect = "aetherblap";
			lifetime = 70+ld;
		}
		
		public void draw(Spark b){
			Draw.color(Color.PURPLE, Color.MAGENTA, b.life().ifract());
			Draw.circle(b.pos().x, b.pos().y, 3);
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
		
		public void draw(Spark b){
			Draw.color(Color.WHITE, Color.PINK, b.life().ifract());
			Draw.circle(b.pos().x, b.pos().y, 8);
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
		
		public void draw(Spark b){
			Draw.thick(2f);
			Draw.color(Color.WHITE, Color.LIME, b.life().ifract());
			Draw.lineAngleCenter(b.pos().x, b.pos().y, b.velocity().angle(), 4);
			Draw.reset();
		}
	},
	fusionshot = new Projectiles(){
		{
			speed = 2.6f+pd;
			hiteffect = "fusionblap";
			lifetime = 50+ld;
		}
		
		public void draw(Spark b){
			Draw.color(Color.CORAL, Color.WHITE, b.life().ifract());
			Draw.thick(1f);
			Draw.polygon(3, b.pos().x, b.pos().y, 3, b.velocity().angle()-90);
			Draw.lineAngleCenter(b.pos().x, b.pos().y, b.velocity().angle(), 7);
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
		
		public void draw(Spark b){
			Draw.color(Color.PURPLE, Color.BLACK, b.life().ifract());
			Draw.lineAngle(b.pos().x, b.pos().y, 0, b.velocity().angle()+30, 6);
			Draw.polygon(3, b.pos().x, b.pos().y, 4, b.velocity().angle()+180);
			Draw.reset();
		}
	},
	phaseslash = new Projectiles(){
		{
			speed = 0.8f;
			hitsize = 7;
			lifetime = 7;
		}
		
		public void draw(Spark b){
			Draw.color(Color.SKY);
			Draw.alpha(b.life().ifract());
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle(), 6);
			Draw.reset();
		}
	},
	phaseslashalt = new Projectiles(){
		{
			speed = 2f;
			hitsize = 11;
			lifetime = 7;
		}
		
		public void draw(Spark b){
			Draw.thick(1f);
			Draw.color(Color.SKY);
			Draw.alpha(b.life().ifract());
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle(), 4);
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle()-30, 4);
			Draw.lineAngle(b.pos().x, b.pos().y, b.velocity().angle()+30, 4);
			Draw.reset();
		}
	},
	golemshot = new Projectiles(){
		{
			speed = 1f;
			hiteffect = "golemflash";
			despawneffect = "shotshrink";
		}
		
		public void draw(Spark b){
			Draw.color(Color.SCARLET, Color.BLACK, b.life().ifract());
			Draw.circle(b.pos().x, b.pos().y, 3);
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
		
		public void draw(Spark b){
			Draw.color(Color.WHITE, Color.ORANGE, b.life().ifract());
			Draw.circle(b.pos().x, b.pos().y, 4);
			Draw.color();
		}
		
		public void despawned(Spark b){
			super.despawned(b);
			Angles.circle(8, f->{
				Projectile.create(golemsmallshot, b.projectile().source, b.pos().x, b.pos().y, f-90);
				//new Projectile(golemsmallshot, b.owner, f-90).set(b.pos().x, b.pos().y).add();
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
		
		public void draw(Spark b){
			Draw.color(Color.ORANGE, Color.WHITE, b.life().ifract());
			Draw.rect("circle", b.pos().x, b.pos().y, 4, 4);
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
		
		public void draw(Spark b){
			Draw.color(Color.ORANGE, Color.WHITE, b.life().ifract());
			Draw.circle(b.pos().x, b.pos().y, 2f);
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
		
		public void draw(Spark b){
			Draw.color(Color.ORANGE, Color.PURPLE, b.life().ifract());
			Draw.rect("circle", b.pos().x, b.pos().y, 3, 3);
			Draw.thick(2f);
			Draw.polysegment(30, 0, 12, b.pos().x, b.pos().y, 5, b.velocity().angle()-360/5f-90);
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
		
		public void draw(Spark b){
			Draw.color(tent, Color.BLACK, b.life().ifract());
			Draw.polygon(3, b.pos().x, b.pos().y, 4, b.velocity().angle()-90);
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
		
		public void draw(Spark b){
			Draw.color(Color.WHITE, tent, b.life().ifract());
			Draw.polygon(3, b.pos().x, b.pos().y, 4, b.velocity().angle()-90);
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
		
		public void draw(Spark b){
			Draw.color(Color.BLACK, tent, b.life().ifract());
			Draw.polygon(10, b.pos().x, b.pos().y, 4, b.velocity().angle()-90);
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
		
		public void draw(Spark b){
			Draw.color(Color.BLACK, tent, b.life().ifract());
			Draw.polygon(10, b.pos().x, b.pos().y, 8, b.velocity().angle()-90);
			Draw.color();
		}
		
		public void despawned(Spark b){
			super.despawned(b);
			Angles.circle(6, f->{
				Projectile.create(shadowshot, b.projectile().source, b.pos().x, b.pos().y, f-90);
				//new Projectile(shadowshot, b.owner, f-90).set(b.pos().x, b.pos().y).add();
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
		
		public void draw(Spark b){
			Draw.color(Color.WHITE, Color.SCARLET, b.life().ifract());
			Draw.polygon(3, b.pos().x, b.pos().y, 3, b.velocity().angle()-90);
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
		
		public void draw(Spark b){
			Draw.color(Color.PURPLE, Color.BLACK, b.life().ifract());
			Draw.rect("circle", b.pos().x, b.pos().y, 7f, 7f);
			Draw.color(Color.PURPLE);
			Draw.circle(b.pos().x, b.pos().y, 2);
			Draw.color();
		}
		
		public void despawned(Spark b){
			super.despawned(b);
			Angles.circle(6, f->{
				Projectile.create(tentashot, b.projectile().source, b.pos().x, b.pos().y, f-90);
				//new Projectile(tentashot, b.owner, f-90).set(b.pos().x, b.pos().y).add();
			});
		}
	};
	
	@Override
	public void draw(Spark b){
		
	}
	
	@Override
	public void removed(Spark spark){
		if(hiteffect != null)
			Effects.effect(hiteffect, dark ? Shade.eyeColor : Color.WHITE, spark.pos().x, spark.pos().y);
	}
	
	@Override
	public void despawned(Spark spark){
		if(despawneffect != null)
			Effects.effect(despawneffect, dark ? Shade.eyeColor : Color.WHITE, spark.pos().x, spark.pos().y);
	}

}
