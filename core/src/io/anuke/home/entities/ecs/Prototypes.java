package io.anuke.home.entities.ecs;

import io.anuke.home.entities.ecs.types.ItemDrop;
import io.anuke.home.entities.ecs.types.Player;
import io.anuke.home.entities.ecs.types.Projectile;
import io.anuke.home.entities.ecs.types.enemies.*;
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
	tentapod = new Tentapod();
}
