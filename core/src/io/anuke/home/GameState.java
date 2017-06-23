package io.anuke.home;

public class GameState{
	private static State state = State.playing;
	
	public static boolean is(State other){
		return state == other;
	}
	
	public static void set(State other){
		state = other;
	}
	
	public static enum State{
		playing, menu, paused;
	}
}
