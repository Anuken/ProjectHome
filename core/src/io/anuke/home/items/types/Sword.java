package io.anuke.home.items.types;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.home.items.Item;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class Sword extends Weapon{
	//config data
	protected Projectiles projectile = Projectiles.slash;
	protected Projectiles altprojectile = Projectiles.slash;
	protected String slash = "swing";
	protected String alt = "swingalt";
	
	protected float swingarc = 170f;
	protected float swingang = 0f;
	protected float swingtime = 4;
	protected float alpha = 0.4f, chargealpha = 0.07f;
	protected float swingoffset = 1;
	protected float reach = 11;
	protected int chargemult = 6;

	protected float chargetime = 60f;
	
	//state data
	boolean swing = false;
	float charge = 0f;

	public Sword(String name, String formal) {
		super(name, formal);
		speed = 40f;
	}

	public String getStatString(){
		return super.getStatString() + "\n[royal]Reach: " + (int) reach + "\n[purple]Arc: " + (int) swingarc;
	}

	float maxswing(){
		return -swingoffset * 2f;
	}

	float target(){
		return(!swing ? 0 : maxswing());
	}

	float current(){
		return(swing ? 0 : maxswing());
	}

	@Override
	public float getAngleOffset(){
		return swingang + swingoffset;
	}

	@Override
	public void draw(Spark player, Item item){
		float angle = player.get(PlayerTrait.class).angle(player);

		Angles.translation(1.5f, angle + swingang + swingoffset);

		Draw.borect(item.name, player.pos().x, player.pos().y + PlayerTrait.height, angle - 90 + swingang + swingoffset);

		Angles.vector.setLength(2f);
		Draw.rect("hand", player.pos().x + Angles.x(), player.pos().y + PlayerTrait.height + Angles.y());

		Angles.vector.setLength(1f);
		Draw.rect("hand", player.pos().x + Angles.x(), player.pos().y + PlayerTrait.height + Angles.y());

		Angles.translation(angle + swingang + swingoffset, 5f);

		if(charge > 0){
			Draw.color(Color.WHITE, Color.ORANGE, charge / chargetime);
			Draw.lineAngle(player.pos().x + Angles.x(), player.pos().y + PlayerTrait.height + Angles.y(), Angles.vector.angle(), reach - 3f);
		}

		if(charge >= chargetime){
			Draw.color(Color.WHITE, Color.SKY, Mathf.absin(Timers.time(), 5f, 1f));
			Draw.polygon(3, player.pos().x + Angles.x(), player.pos().y + PlayerTrait.height + Angles.y(), 3, Angles.vector.angle() - 30 - 180);
		}

		Draw.color();
	}

	@Override
	public void update(Spark player){
		if(swingoffset > 0){
			swingang = 0;
			for(int i = 0; i < swingtime; i++){
				swingang = Mathf.lerp(swingang, swingarc, alpha);
			}
			swingoffset = -swingang / 2f;
		}

		if(Inputs.buttonRelease(Buttons.RIGHT)){
			if(charge >= chargetime){
				swing = !swing;
				slash(player, altprojectile, alt, damage * chargemult);
				Effects.sound("slash2", player);
				Effects.shake(4, 4, player);
			}else{
				swing = !swing;
				Timers.runFor(8, () -> {
					swingang = Mathf.lerp(swingang, current(), 0.2f * Timers.delta());
				}, () -> {
					swingang = current();
				});
			}
			charge = 0;
		}

		if(Inputs.buttonDown(Buttons.RIGHT)){
			swingang = Mathf.lerp(swingang, target(), chargealpha * Timers.delta());
			if(charge <= chargetime)
				charge += Timers.delta();
		}else if(Inputs.buttonDown(Buttons.LEFT) && Timers.get(player, "weaponcooldown", speed)){
			slash(player, projectile, slash, damage);
			Effects.sound("slash", player);
			Effects.shake(1f, 1f, player);
		}
	}

	void slash(Spark player, Projectiles projectile, String effect, int damage){

		float delta = Timers.delta();
		Vector2 vector = Angles.vector;
		float height = PlayerTrait.height;
		float x = player.pos().x, y = player.pos().y;

		int sdir = Mathf.sign(swing);
		float target = swingarc / 2f + swingarc / 2f * sdir;

		Timers.runFor(swingtime, () -> {
			swingang = Mathf.lerp(swingang, target, alpha * delta);

			if(Timers.get(this, "swings", 0.99f)){
				float angle = player.get(PlayerTrait.class).angle(player);

				float wang = angle + swingang + swingoffset;

				vector.set(1, 1).setAngle(wang).setLength(reach);

				Effects.effect(effect, x + Angles.x(), y + Angles.y() + height);

				Projectile.create(projectile, player, damage, x + Angles.x(), y + Angles.y() + height, wang);

				vector.setAngle(angle + Mathf.lerp(swingang, target, alpha * 0.5f) + swingoffset);

				Effects.effect(effect, x + Angles.x(), y + Angles.y() + height);
			}
		}, () -> {
			swingang = !swing ? 0f : -2f * swingoffset;
			swing = !swing;
		});
	}
}
