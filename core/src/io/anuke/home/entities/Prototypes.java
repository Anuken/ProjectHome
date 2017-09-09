package io.anuke.home.entities;

import io.anuke.home.entities.types.*;
import io.anuke.home.entities.types.enemies.corruption.*;
import io.anuke.home.entities.types.enemies.library.*;
import io.anuke.ucore.ecs.Prototype;

public class Prototypes{
	public static final Prototype
	
	player = new Player(),
	projectile = new Projectile(),
	itemdrop = new ItemDrop(),
	marblegolem = new MarbleGolem(),
	marbledrone = new MarbleDrone(),
	marbleobelisk = new MarbleObelisk(),
	darkeffigy = new DarkEffigy(),
	tentafly = new Tentafly(),
	tentacolumn = new Tentacolumn(),
	tentawarrior = new Tentawarrior(),
	tentapod = new Tentapod(),
	wisp = new Wisp(),
	shade = new Shade(),
	crawler = new Crawler(),
	goldfrog = new GoldFrog(),
	effect = new Effect();
}
