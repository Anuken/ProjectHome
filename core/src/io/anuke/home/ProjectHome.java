package io.anuke.home;

import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.Core;

public class ProjectHome extends Core {
	
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
