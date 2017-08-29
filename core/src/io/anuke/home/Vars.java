package io.anuke.home;

public class Vars{
	public static boolean debug = true;
	
	public static Control control;
	public static UI ui;
	
	public static final int tilesize = 12;
	
	public static String[] tutorialText = {
		"[yellow]Controls:",
		"[green][[WASD][] to move, [green][[Q][] to pick up items.",
		"[green][[Right-click][] to charge your attack, [green][left-click][] to attack normally.",
		"[green][[L-Shift][] to dash.",
		"",
		"[orange]Checkpoints are indicated by floating golden triangles.",
		"Stand on them to respawn there when you die.",
		"Every death, the monsters that you've killed after",
		"the checkpoint will respawn."
	};
	
	public static String[] aboutText = {
		"Made by [crimson]Anuke[] in 7 days for RemakeJam.",
		"",
		"Tools used:",
		"- [lime]BFXR[] for sound effects",
		"- [royal]jukedeck.com[] for music"
	};
}
