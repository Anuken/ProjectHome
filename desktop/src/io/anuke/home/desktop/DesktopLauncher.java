package io.anuke.home.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.anuke.home.ProjectHome;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("ProjectHome");
		config.setWindowedMode(800, 600);
		config.setMaximized(true);
		config.useVsync(false);
		new Lwjgl3Application(new ProjectHome(), config);
	}
}
