package io.anuke.home.items;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.Player;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.*;

public class WeaponTypes{
	
	public static class Staff extends WeaponType{
		Projectiles projectile = Projectiles.yellowshot;
		String shooteffect = "yellowblap";
		String alteffect = shooteffect;
		Color chargecolor = Color.ORANGE;
		int shots = 1;
		float shotspacing = 5f;
		float chargetime = 60;
		float accuracy = 0f;
		boolean holdable = true;
		
		float offset = 0f;
		float charge = 0;
		
		{
			speed = 30f;
		}
		
		void setVector(Player player){
			vector.set(1, 1).setAngle(player.angle()).setLength(4);
		}
		
		public void altAttack(Player player){
			Effects.shake(3f, 3f);
			Geometry.circle(20, f->{
				Angles.translation(f, 10f);
				player.shoot(projectile, damage*2, vector.x + Angles.vector.x, vector.y + Angles.vector.y, f);
			});
		}
		
		public void shot(){
			
		}
		
		/**sets vector to staff tip*/
		public void setTip(Player player){
			setVector(player);
			vector.add(player.x, player.y+player.height+5);
		}
		
		public String getStatString(){
			return super.getStatString() + "\n[royal]Shots: " + (int)shots;
		}
		
		@Override
		public void draw(Player player, Item item){
			setVector(player);
			
			offset = Mathf.lerp(offset, charge/chargetime*4f, 0.3f*Mathf.delta());
			
			Draw.rect(item.name, player.x+vector.x, offset+player.y+player.height+vector.y+4);
			Draw.rect("hand", player.x+vector.x, offset+player.y+player.height+vector.y);
			Draw.rect("hand", player.x+vector.x, offset+player.y+player.height+vector.y+1);
			Draw.reset();
			
			if(charge > 0){
				Draw.color(Color.WHITE, chargecolor, charge/chargetime);
				Draw.polygon(3, player.x+vector.x+0.5f, player.y+player.height+vector.y+6+offset, 3);
				if(charge >= chargetime){
					Draw.color(Color.WHITE, Color.SKY, Mathf.absin(Timers.time(), 5f, 1f));
					Draw.polygon(3, player.x+vector.x+0.5f, player.y+player.height+vector.y+6+offset, 3);
				}
			}
			
			Draw.reset();
		}
		
		@Override
		public void update(Player player){
			setVector(player);
			
			float x = player.x + vector.x;
			float y = player.y + player.height + vector.y + 5;
			
			if(Inputs.buttonRelease(Buttons.RIGHT)){
				if(charge >= chargetime){
					Effects.effect(shooteffect, x, y+2);
					setTip(player);
					altAttack(player);
					Effects.sound("ult", player);
				}
				charge = 0f;
			}
			
			if(Inputs.buttonDown(Buttons.RIGHT)){
				charge += Mathf.delta();
				if(charge >= chargetime && holdable){
					Effects.effect(shooteffect, x, y+2);
					setTip(player);
					altAttack(player);
					charge = 0f;
					Effects.sound("ult", player);
				}
			}else if(Inputs.buttonDown(Buttons.LEFT) && Timers.get(player, "weaponcooldown", speed)){
				Effects.effect(shooteffect, x, y+2);
				shot();
				Effects.sound("shoot", player);
				Geometry.shotgun(shots, shotspacing, Angles.mouseAngle(x, y), f->{
					player.shoot(projectile, damage, x, y, f + Mathf.range(accuracy));
				});
				
			}
		}
	}
	
	public static class Sword extends WeaponType{
		Projectiles projectile = Projectiles.slash;
		Projectiles altprojectile = Projectiles.slash;
		String slash = "swing";
		String alt = "swingalt";
		boolean swing = false;
		float swingarc = 170f;
		float swingang = 0f;
		float swingtime = 4;
		float alpha = 0.4f, chargealpha = 0.07f;
		float swingoffset = 1;//122/2;
		float reach = 11;
		int chargemult = 6;
		
		float charge = 0f;
		float chargetime = 60f;
		
		{
			speed = 40f;
		}
		
		public String getStatString(){
			return super.getStatString() + "\n[royal]Reach: " + (int)reach + "\n[purple]Arc: " + (int)swingarc;
		}
		
		float maxswing(){
			return -swingoffset*2f;
		}
		
		float target(){
			return (!swing ? 0 : maxswing());
		}
		
		float current(){
			return (swing ? 0 : maxswing());
		}
		
		@Override
		public float getAngleOffset(){
			return swingang+swingoffset;
		}
		
		@Override
		public void draw(Player player, Item item){
			
			vector.set(1,1).setAngle(player.angle()+swingang+swingoffset);
			
			Draw.borect(item.name, player.x, player.y+player.height, player.angle()-90+swingang+swingoffset);
			
			vector.setLength(2f);
			Draw.rect("hand", player.x + vector.x, player.y+player.height+vector.y);
			vector.setLength(1f);
			Draw.rect("hand", player.x + vector.x, player.y+player.height+vector.y);
			
			vector.setAngle(player.angle()+swingang+swingoffset).setLength(5f);
			
			if(charge > 0){
				Draw.color(Color.WHITE, Color.ORANGE, charge/chargetime);
				Draw.lineAngle(player.x+vector.x, player.y+player.height+vector.y, vector.angle(), reach-3f);
				//Draw.polygon(3, player.x+vector.x, player.y+player.height+vector.y, 1.4f, vector.angle()-30-180);
			}
			
			if(charge >= chargetime){
				Draw.color(Color.WHITE, Color.SKY, Mathf.absin(Timers.time(), 5f, 1f));
				Draw.polygon(3, player.x+vector.x, player.y+player.height+vector.y, 3, vector.angle()-30-180);
			}
			
			Draw.color();
		}
		
		@Override
		public void update(Player player){
			if(swingoffset > 0){
				swingang = 0;
				for(int i = 0; i < swingtime; i ++){
					swingang = Mathf.lerp(swingang, swingarc, alpha);
				}
				swingoffset = -swingang/2f;
			}
			
			if(Inputs.buttonRelease(Buttons.RIGHT)){
				if(charge >= chargetime){
					swing = !swing;
					slash(player, altprojectile, alt, damage*chargemult);
					Effects.sound("slash2", player);
					Effects.shake(4, 4);
				}else{
					swing = !swing;
					Timers.runFor(8, ()->{
						swingang = Mathf.lerp(swingang, current(), 0.2f*Entity.delta);
					}, ()->{
						swingang = current();
					});
				}
				charge = 0;
			}
			
			if(Inputs.buttonDown(Buttons.RIGHT)){
				swingang = Mathf.lerp(swingang, target(), chargealpha*Entity.delta);
				if(charge <= chargetime)
					charge += Entity.delta;
			}else if(Inputs.buttonDown(Buttons.LEFT) && Timers.get(player, "weaponcooldown", speed)){
				slash(player, projectile, slash, damage);
				Effects.sound("slash", player);
				Effects.shake(1f, 1f);
			}
		}
		
		void slash(Player player, Projectiles projectile, String effect, int damage){
			float delta = Entity.delta;
			Vector2 vector = Module.vector;
			float height = player.height;
			float x = player.x, y = player.y;
			
			int sdir = Mathf.sign(swing);
			float target = swingarc/2f+swingarc/2f*sdir;
			
			Timers.runFor(swingtime, ()->{
				swingang = Mathf.lerp(swingang, target, alpha*delta);
				
				if(Timers.get(this, "swings", 0.99f)){
					
					float wang = player.angle()+swingang+swingoffset;
					
					vector.set(1, 1).setAngle(wang).setLength(reach);
					
					Effects.effect(effect, x + vector.x, y + vector.y + height);
					
					player.shoot(projectile, damage, x + vector.x, y + vector.y + height, wang);
					
					vector.setAngle(player.angle()+Mathf.lerp(swingang, target, alpha*0.5f)+swingoffset);
					
					Effects.effect(effect, x + vector.x, y + vector.y + height);
				}
			}, ()->{
				swingang = !swing ? 0f : -2f*swingoffset;
				swing = !swing;
			});
		}
	}
}
