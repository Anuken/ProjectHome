package io.anuke.home.items;

import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
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
		speedbuff = -5;
		attackbuff = 1;
	}},
	reflectarmor = new Item("reflectarmor", "Absorption Armor", ItemType.armor){{
		defensebuff = -1;
		speedbuff = 4;
		attackbuff = 5;
	}},
	marblesword = new Item("marblesword", "Marble Sword", new WeaponTypes.Sword(){{
		damage = 8;
		speed = 11f;
	}}),
	icesword = new Item("icesword", "Cryoblade", new WeaponTypes.Sword(){{
		damage = 8;
		speed = 10f;
		swingarc = 190f;
		chargetime = 45f;
		slash = "swingcryo";
		alt = "swingaltcryo";
	}}),
	daggersword = new Item("lancesword", "Crimson Dagger", new WeaponTypes.Sword(){{
		damage = 4;
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
		damage = 13;
		speed = 20f;
		swingarc = 240f;
		slash = "swingsilver";
		alt = "swingaltsilver";
		reach = 13;
	}}),
	phasesword = new Item("phasesword", "Skyblade", new WeaponTypes.Sword(){{
		damage = 13;
		speed = 13f;
		swingarc = 250f;
		slash = "swingphase";
		alt = "swingaltphase";
		projectile = Projectiles.phaseslash;
		altprojectile = Projectiles.phaseslashalt;
		alpha = 0.3f;
		reach = 10;
		chargetime = 45;
	}}),
	tentasword = new Item("tentasword", "Ruste", new WeaponTypes.Sword(){{
		damage = 6;
		speed = 9f;
		chargemult = 7;
		slash = "swingtenta";
		alt = "swingalttenta";
		altprojectile = Projectiles.tentaslash;
		reach = 14;
		chargetime = 20;
	}}),
	amberstaff = new Item("amberstaff", "Amber Staff", new WeaponTypes.Staff(){{
		damage = 4;
		speed = 12f;
	}}),
	scorchstaff = new Item("scorchstaff", "Scorched Staff", new WeaponTypes.Staff(){{
			damage = 4;
			speed = 11f;
			shooteffect = "scorchblap";
			projectile = Projectiles.scorchshot;
		}
	
		public void altAttack(Spark player){
			Geometry.shotgun(7, 10, player.get(PlayerTrait.class).angle(player), f->{
				Projectile.create(projectile, player, damage*2, vector.x, vector.y, f);
			});
		}
	}),
	aetherstaff = new Item("aetherstaff", "Staff of the Aether", new WeaponTypes.Staff(){{
		damage = 3;
		speed = 12f;
		shooteffect = "aetherblap";
		shots = 2;
		projectile = Projectiles.aethershot;
		shotspacing = 9;
	}}),
	orbstaff = new Item("orbstaff", "Orbcaster", new WeaponTypes.Staff(){{
		damage = 7;
		speed = 17f;
		shooteffect = "orbblap";
		projectile = Projectiles.orbshot;
		chargetime = 120;
	}}),
	planestaff = new Item("planestaff", "Planestaff", new WeaponTypes.Staff(){{
			damage = 10;
			speed = 30f;
			shooteffect = "planeblap";
			projectile = Projectiles.planeshot;
			chargetime = 130;
		}
	
		public void shot(){
			Effects.shake(3, 2);
		}
		
		public void altAttack(Spark player){
			Geometry.shotgun(7, 10, player.get(PlayerTrait.class).angle(player), f->{
				Projectile.create(projectile, player, damage*2, vector.x, vector.y, f);
			});
		}
	}),
	fusionstaff = new Item("fusionstaff", "Staff of Fused Cores", ItemType.ranged_weapon, new WeaponTypes.Staff(){{
		damage = 4;
		speed = 14f;
		shots = 3;
		shooteffect = "fusionblap";
		shotspacing = 14;
		projectile = Projectiles.fusionshot;
	}});
}
