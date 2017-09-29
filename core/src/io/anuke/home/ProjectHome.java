package io.anuke.home;

import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.ModuleCore;

public class ProjectHome extends ModuleCore {
	
	@Override
	public void init(){
		module(Vars.control = new Control());
		module(Vars.ui = new UI());
	}
	
	@Override
	public void update(){
		Inputs.update();
	}
	
}
