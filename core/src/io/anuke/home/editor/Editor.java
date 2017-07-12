package io.anuke.home.editor;

import io.anuke.ucore.modules.Core;

public class Editor extends Core{

	@Override
	public void init(){
		addModule(new EditorInput());
		addModule(Evar.control = new EditorControl());
		addModule(Evar.ui = new EditorUI());
	}

}
