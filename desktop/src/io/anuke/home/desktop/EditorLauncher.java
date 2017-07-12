package io.anuke.home.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.anuke.home.editor.Editor;

public class EditorLauncher{
	
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Project Home Editor");
		config.setWindowedMode(800, 600);
		config.setMaximized(true);
		new Lwjgl3Application(new Editor(), config);
	}
}
