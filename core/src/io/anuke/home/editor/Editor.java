package io.anuke.home.editor;

import io.anuke.ucore.modules.ModuleCore;

public class Editor extends ModuleCore{

	@Override
	public void init(){
		module(new EditorInput());
		module(Evar.control = new EditorControl());
		module(Evar.ui = new EditorUI());
	}
	
	public static boolean active(){
		return Evar.control != null;
	}

}
