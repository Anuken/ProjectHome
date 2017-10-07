package io.anuke.home.items;

import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.home.items.types.*;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.util.Angles;

public class Items{
	public static final Item 
	
	lightsoul = new Soul("lightsoul", "Soul of Light"){
		{
		
		}
	},
	densearmor = new Armor("densearmor", "Dense Armor"){{
		defensebuff = 3;
		speedbuff = -1;
	}},
	hellarmor = new Armor("hellarmor", "Hellstrider Armor"){{
		defensebuff = 1;
		attackbuff = 1;
		speedbuff = 4;
	}},
	ascendarmor = new Armor("ascendarmor", "Ascendant Armor"){{
		defensebuff = 2;
		attackbuff = 3;
	}},
	juggarmor = new Armor("juggernautarmor", "Armor of the Juggernaut"){{
		defensebuff = 5;
		speedbuff = -5;
		attackbuff = 1;
	}},
	reflectarmor = new Armor("reflectarmor", "Absorption Armor"){{
		defensebuff = -1;
		speedbuff = 4;
		attackbuff = 5;
	}},
	marblesword = new Sword("marblesword", "Marble Sword"){{
		damage = 8;
		speed = 11f;
	}},
	icesword = new Sword("icesword", "Cryoblade"){{
		damage = 8;
		speed = 10f;
		swingarc = 190f;
		chargetime = 45f;
		slash = "swingcryo";
		alt = "swingaltcryo";
	}},
	daggersword = new Sword("lancesword", "Crimson Dagger"){{
		damage = 4;
		speed = 4f;
		swingarc = 160f;
		slash = "swingdagger";
		alt = "swingaltdagger";
		altprojectile = Projectiles.daggershot;
		chargetime = 20;
		chargemult = 7;
		reach = 9;
	}},
	silversword = new Sword("silversword", "Silver Blade"){{
		damage = 13;
		speed = 20f;
		swingarc = 240f;
		slash = "swingsilver";
		alt = "swingaltsilver";
		reach = 13;
	}},
	phasesword = new Sword("phasesword", "Skyblade"){{
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
	}},
	tentasword = new Sword("tentasword", "Ruste"){{
		damage = 6;
		speed = 9f;
		chargemult = 7;
		slash = "swingtenta";
		alt = "swingalttenta";
		altprojectile = Projectiles.tentaslash;
		reach = 14;
		chargetime = 20;
	}},
	amberstaff = new Staff("amberstaff", "Amber Staff"){{
		damage = 4;
		speed = 12f;
	}},
	scorchstaff = new Staff("scorchstaff", "Scorched Staff"){{
			damage = 4;
			speed = 11f;
			shooteffect = "scorchblap";
			projectile = Projectiles.scorchshot;
		}
		
		@Override
		public void altAttack(Spark player){
			Angles.shotgun(7, 10, player.get(PlayerTrait.class).angle(player), f->{
				Projectile.create(projectile, player, damage*2, Angles.x(), Angles.y(), f);
			});
		}
	},
	aetherstaff = new Staff("aetherstaff", "Staff of the Aether"){{
		damage = 3;
		speed = 12f;
		shooteffect = "aetherblap";
		shots = 2;
		projectile = Projectiles.aethershot;
		shotspacing = 9;
	}},
	orbstaff = new Staff("orbstaff", "Orbcaster"){{
		damage = 7;
		speed = 17f;
		shooteffect = "orbblap";
		projectile = Projectiles.orbshot;
		chargetime = 120;
	}},
	planestaff = new Staff("planestaff", "Planestaff"){{
			damage = 10;
			speed = 30f;
			shooteffect = "planeblap";
			projectile = Projectiles.planeshot;
			chargetime = 130;
		}
		
		@Override
		public void onShoot(){
			Effects.shake(3, 2);
		}
		
		@Override
		public void altAttack(Spark player){
			Angles.shotgun(7, 10, player.get(PlayerTrait.class).angle(player), f->{
				Projectile.create(projectile, player, damage*2, Angles.x(), Angles.y(), f);
			});
		}
	},
	fusionstaff = new Staff("fusionstaff", "Staff of Fused Cores"){{
		damage = 4;
		speed = 14f;
		shots = 3;
		shooteffect = "fusionblap";
		shotspacing = 14;
		projectile = Projectiles.fusionshot;
	}};
}
