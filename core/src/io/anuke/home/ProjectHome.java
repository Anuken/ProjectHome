package io.anuke.home;

import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.ModuleCore;

public class ProjectHome extends ModuleCore {
	
	@Override
	public void init(){
		add(Vars.control = new Control());
		add(Vars.ui = new UI());
	}
	
	@Override
	public void update(){
		Inputs.update();
	}
	
}
