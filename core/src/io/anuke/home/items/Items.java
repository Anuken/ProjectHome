package io.anuke.home.items;

import io.anuke.home.entities.Player;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;

public class Items{
	public static final Item 
	
	densearmor = new Item("densearmor", "Dense Armor", ItemType.armor){{
		defensebuff = 3;
		speedbuff = -1;
	}},
	hellarmor = new Item("hellarmor", "Hellstrider Armor", ItemType.armor){{
		defensebuff = 1;
		attackbuff = 1;
		speedbuff = 4;
	}},
	ascendarmor = new Item("ascendarmor", "Ascendant Armor", ItemType.armor){{
		defensebuff = 2;
		attackbuff = 3;
	}},
	juggarmor = new Item("juggernautarmor", "Armor of the Juggernaut", ItemType.armor){{
		defensebuff = 5;
		speedbuff = -7;
	}},
	reflectarmor = new Item("reflectarmor", "Absorption Armor", ItemType.armor){{
		defensebuff = -1;
		speedbuff = 4;
		attackbuff = 5;
	}},
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
		damage = 3;
		speed = 4f;
		swingarc = 160f;
		slash = "swingdagger";
		alt = "swingaltdagger";
		altprojectile = Projectiles.daggershot;
		chargetime = 20;
		chargemult = 7;
		reach = 9;
	}}),
	silversword = new Item("silversword", "Silver Blade", new WeaponTypes.Sword(){{
		damage = 11;
		speed = 20f;
		swingarc = 240f;
		slash = "swingsilver";
		alt = "swingaltsilver";
		reach = 13;
	}}),
	phasesword = new Item("phasesword", "Skyblade", new WeaponTypes.Sword(){{
		damage = 11;
		speed = 13f;
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
		damage = 4;
		speed = 9f;
		chargemult = 3;
		slash = "swingtenta";
		alt = "swingalttenta";
		altprojectile = Projectiles.tentaslash;
		reach = 14;
		chargetime = 20;
	}}),
	amberstaff = new Item("amberstaff", new WeaponTypes.Staff(){{
		damage = 3;
		speed = 6f;
	}}),
	scorchstaff = new Item("scorchstaff", new WeaponTypes.Staff(){{
			damage = 4;
			speed = 8f;
			shooteffect = "scorchblap";
			projectile = Projectiles.scorchshot;
		}
	
		public void altAttack(Player player){
			Geometry.shotgun(7, 10, player.angle(), f->{
				player.shoot(Projectiles.scorchshot, damage*2, vector.x, vector.y, f);
			});
		}
	}),
	aetherstaff = new Item("aetherstaff", new WeaponTypes.Staff(){{
		damage = 6;
		speed = 6f;
		shooteffect = "aetherblap";
		shots = 2;
		projectile = Projectiles.aethershot;
		shotspacing = 7;
	}}),
	orbstaff = new Item("orbstaff", new WeaponTypes.Staff(){{
		damage = 12;
		speed = 18f;
		shooteffect = "orbblap";
		projectile = Projectiles.orbshot;
		chargetime = 100;
	}}),
	planestaff = new Item("planestaff", new WeaponTypes.Staff(){{
			damage = 30;
			speed = 20f;
			shooteffect = "planeblap";
			projectile = Projectiles.planeshot;
		}
	
		public void shot(){
			Effects.shake(2, 1);
		}
	}),
	fusionstaff = new Item("fusionstaff", new WeaponTypes.Staff(){{
		damage = 10;
		speed = 6f;
		shots = 3;
		shooteffect = "fusionblap";
		shotspacing = 9;
		projectile = Projectiles.fusionshot;
	}});
}
