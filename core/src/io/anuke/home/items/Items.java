package io.anuke.home.items;

import io.anuke.home.entities.Projectiles;

public class Items{
	public static final Item 
	
	marblesword = new Item("marblesword", "Marble Sword", new WeaponTypes.Sword(){{
		damage = 6;
		speed = 11f;
	}}),
	icesword = new Item("icesword", "Cryoblade", new WeaponTypes.Sword(){{
		damage = 6;
		speed = 11f;
		swingarc = 190f;
		slash = "swingcryo";
		alt = "swingaltcryo";
	}}),
	lancesword = new Item("lancesword", "Crimson Dagger", new WeaponTypes.Sword(){{
		damage = 6;
		speed = 4f;
		swingarc = 160f;
		slash = "swingdagger";
		alt = "swingaltdagger";
		altprojectile = Projectiles.daggershot;
		chargetime = 20;
		reach = 9;
	}}),
	silversword = new Item("silversword", "Silver Blade", new WeaponTypes.Sword(){{
		damage = 10;
		speed = 20f;
		swingarc = 240f;
		slash = "swingsilver";
		alt = "swingaltsilver";
		reach = 13;
	}}),
	phasesword = new Item("phasesword", "Skyblade", new WeaponTypes.Sword(){{
		damage = 9;
		speed = 14f;
		swingarc = 250f;
		slash = "swingphase";
		alt = "swingaltphase";
		projectile = Projectiles.phaseslash;
		altprojectile = Projectiles.phaseslashalt;
		alpha = 0.3f;
		reach = 10;
		chargetime = 50;
	}}),
	tentasword = new Item("tentasword", "The Rust", new WeaponTypes.Sword(){{
		damage = 6;
		speed = 11f;
		slash = "swingtenta";
		alt = "swingalttenta";
	}}),
	staff = new Item("staff", new WeaponTypes.Staff(){{
		damage = 3;
		speed = 6f;
	}});
}
