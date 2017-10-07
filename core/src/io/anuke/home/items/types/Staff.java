package io.anuke.home.items.types;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.home.items.Item;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class Staff extends Weapon{
	protected Projectiles projectile = Projectiles.yellowshot;
	protected String shooteffect = "yellowblap";
	protected String alteffect = shooteffect;
	protected Color chargecolor = Color.ORANGE;
	
	protected int shots = 1;
	protected float shotspacing = 5f;
	protected float chargetime = 60;
	protected float accuracy = 0f;
	protected boolean holdable = true;

	float offset = 0f;
	float charge = 0;

	public Staff(String name, String formal) {
		super(name, formal);
		speed = 30f;
	}
	
	public void onShoot(){}

	void setVector(Spark player){
		Angles.translation(player.get(PlayerTrait.class).angle(player), 4f);
	}

	public void altAttack(Spark player){
		Effects.shake(3f, 3f, player);
		float basex = Angles.x(), basey = Angles.y();
		
		Angles.circle(20, f -> {
			Angles.translation(f, 10f);
			Projectile.create(projectile, player, damage * 2, basex + Angles.x(), basey + Angles.y(), f);
		});
	}

	/** sets Angles.vector to staff tip */
	public void setTip(Spark player){
		setVector(player);
		Angles.vector.add(player.pos().x, player.pos().y + PlayerTrait.height + 5);
	}

	public String getStatString(){
		return super.getStatString() + "\n[royal]Shots: " + (int) shots;
	}

	@Override
	public void draw(Spark player, Item item){
		setVector(player);

		offset = Mathf.lerp(offset, charge / chargetime * 4f, 0.3f * Timers.delta());

		Draw.rect(item.name, player.pos().x + Angles.x(), offset + player.pos().y + PlayerTrait.height + Angles.y() + 4);
		Draw.rect("hand", player.pos().x + Angles.x(), offset + player.pos().y + PlayerTrait.height + Angles.y());
		Draw.rect("hand", player.pos().x + Angles.x(), offset + player.pos().y + PlayerTrait.height + Angles.y() + 1);
		Draw.reset();

		if(charge > 0){
			Draw.color(Color.WHITE, chargecolor, charge / chargetime);
			Draw.polygon(3, player.pos().x + Angles.x() + 0.5f, player.pos().y + PlayerTrait.height + Angles.y() + 6 + offset, 3);
			if(charge >= chargetime){
				Draw.color(Color.WHITE, Color.SKY, Mathf.absin(Timers.time(), 5f, 1f));
				Draw.polygon(3, player.pos().x + Angles.x() + 0.5f, player.pos().y + PlayerTrait.height + Angles.y() + 6 + offset, 3);
			}
		}

		Draw.reset();
	}

	@Override
	public void update(Spark player){
		setVector(player);

		float x = player.pos().x + Angles.x();
		float y = player.pos().y + PlayerTrait.height + Angles.y() + 5;

		if(Inputs.buttonRelease(Buttons.RIGHT)){
			if(charge >= chargetime){
				Effects.effect(shooteffect, x, y + 2);
				setTip(player);
				altAttack(player);
				Effects.sound("ult", player);
			}
			charge = 0f;
		}

		if(Inputs.buttonDown(Buttons.RIGHT)){
			charge += Timers.delta();
			if(charge >= chargetime && holdable){
				Effects.effect(shooteffect, x, y + 2);
				setTip(player);
				altAttack(player);
				charge = 0f;
				Effects.sound("ult", player);
			}
		}else if(Inputs.buttonDown(Buttons.LEFT) && Timers.get(player, "weaponcooldown", speed)){
			Effects.effect(shooteffect, x, y + 2);
			onShoot();
			Effects.sound("shoot", player);
			Angles.shotgun(shots, shotspacing, Angles.mouseAngle(x, y), f -> {
				Projectile.create(projectile, player, damage, x, y, f + Mathf.range(accuracy));
			});

		}
	}

}
