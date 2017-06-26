package io.anuke.home.ui;

import java.util.Arrays;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import io.anuke.home.Vars;
import io.anuke.home.entities.ItemDrop;
import io.anuke.home.entities.Player;
import io.anuke.home.items.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.style.TextureRegionDrawable;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.Label;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Strings;
import io.anuke.ucore.util.Timers;

public class Inventory extends Table{
	int slotw = 4, sloth = 4;
	ItemStack[] stacks = new ItemStack[slotw * sloth];
	ItemType[] filters = new ItemType[slotw * sloth];
	ItemStack selected;
	ItemTooltip tooltip;
	boolean deselecting;
	int slotweapon = 0;

	final float slotsize = 56;
	final float itemsize = 32;

	public Inventory() {
		filters[0] = ItemType.weapon;
		filters[1] = ItemType.weapon;
		filters[2] = ItemType.weapon;
		filters[3] = ItemType.armor;
		
		clearItems();
		setup();
	}
	
	public void clearItems(){
		Arrays.fill(stacks, null);
		selected = null;
		
		addItem(new ItemStack(Items.marblesword));
		addItem(new ItemStack(Items.amberstaff));
		
		if(Vars.debug){
			addItem(new ItemStack(Items.tentasword));
			addItem(new ItemStack(Items.silversword));
			addItem(new ItemStack(Items.orbstaff));
			addItem(new ItemStack(Items.planestaff));
			addItem(new ItemStack(Items.fusionstaff));
		}
	}
	
	public boolean selectedItem(){
		return selected != null || deselecting;
	}
	
	void setup(){
		tooltip = new ItemTooltip();
		tooltip.setVisible(false);
		
		background("inventory");
		
		Table slots = new Table();
		
		add("Inventory", Color.DARK_GRAY).padBottom(-32).left();
		row();
		add("Inventory").left().padBottom(4);
		row();
		add(slots);
		
		pad(10);
		
		for(int y = 0; y < sloth; y++){
			for(int x = 0; x < slotw; x++){
				Slot slot = new Slot(y * slotw + x);
				slots.add(slot).size(slotsize);
			}
			slots.row();
		}
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		if(Inputs.scrolled()){
			slotweapon -= Inputs.scroll();
			slotweapon = Mathf.clamp(slotweapon, 0, 2);
		}
		
		if(Inputs.keyUp("weapon1"))
			slotweapon = 0;
		
		if(Inputs.keyUp("weapon2"))
			slotweapon = 1;
		
		if(Inputs.keyUp("weapon3"))
			slotweapon = 2;
		
		Player player = Vars.control.getPlayer();
		
		player.weapon = (stacks[slotweapon] == null ? null : stacks[slotweapon].item);
		
		if(stacks[3] != null){
			Item item = stacks[3].item;
			player.speed = item.speedbuff/10f;
			player.attack = item.attackbuff;
			player.defense = item.defensebuff;
		}else{
			player.speed = player.attack = player.defense = 0;
		}
		
		Element e = getScene().hit(Graphics.mouse().x, Graphics.mouse().y, true);

		if(e != null && e instanceof Slot && stacks[((Slot) e).index] != null){
			Slot slot = (Slot) e;
			
			if(tooltip.getScene() == null)
				getScene().add(tooltip);
			
			tooltip.setVisible(true);
			tooltip.setItem(stacks[slot.index].item);
			tooltip.setPosition(Graphics.mouse().x, Graphics.mouse().y);

		}else{
			tooltip.setVisible(false);
		}
		
		Element any = getScene().hit(Graphics.mouse().x, Graphics.mouse().y, true);
		
		if(Inputs.buttonUp(Buttons.LEFT) && selected != null && any == null){
			new ItemDrop(selected).randomVelocity()
			.set(player.x, player.y).add();
			
			selected = null;
			deselecting = true;
			
			Timers.run(10, ()->{
				deselecting = false;
			});
		}
	}

	@Override
	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);

		if(selected != null){
			Draw.color(0, 0, 0, 0.2f);
			Draw.rect(selected.item.name + "-item", Graphics.mouse().x, Graphics.mouse().y-4, itemsize, itemsize);
			Draw.color();
			Draw.rect(selected.item.name + "-item", Graphics.mouse().x, Graphics.mouse().y, itemsize, itemsize);
		}
	}
	
	public ItemStack getItem(int index){
		return stacks[index];
	}

	public boolean canPlace(ItemStack stack, int slot){
		return filters[slot] == null || filters[slot] == stack.item.type;
	}
	
	public boolean isFull(){
		for(int i = 0; i < stacks.length; i ++){
			if(stacks[i] == null) return false;
		}
		return true;
	}

	public boolean addItem(ItemStack add){
		for(int i = 0; i < stacks.length; i++){
			ItemStack stack = stacks[i];

			if(!canPlace(add, i)){
				continue;
			}

			if(stack == null){
				stacks[i] = add;
				return true;
			}else if(stack.item == add.item && stack.item.type.stackable){
				stacks[i].amount += add.amount;
				return true;
			}
		}
		return false;
	}

	class ItemTooltip extends Table{
		Label label;
		Image image;
		Item item;
		float imgscl = 4f;

		public ItemTooltip() {
			background("tooltip");
			setTouchable(Touchable.disabled);
			
			padRight(30);
			padBottom(12);
			
			Table text = new Table();

			setTransform(true);
			label = new Label("");
			label.setAlignment(Align.left);
			image = new Image();
			
			text.add(label).left();
			add(image).top();
			add(text);
		}

		public void setItem(Item item){
			if(this.item == item) return;
			
			String desc = (item.description == null) ? "" : ("\n"+item.description);
			String stat = item.getStats();
			
			label.setText(
				"[yellow]"+item.formalName+"\n"+
				"[DARK_GRAY]" + Strings.capitalize(item.type.name()) + 
				"[orange]" + stat+
				"[purple]" + desc
			);
			
			//namel.setText(Strings.capitalize(item.name));
			//descl.setText(item.description == null ? "                         " : item.description);
			//typel.setText(Strings.capitalize(item.type.name()));
			
			getCell(image).pad(4);
			
			if(Draw.hasRegion(item.name)){
				image.setDrawable(new TextureRegionDrawable(Draw.region(item.name)));
				getCell(image).size(9*imgscl, 15*imgscl);
			}else{
				image.setDrawable(new TextureRegionDrawable(Draw.region(item.name + "-item")));
				getCell(image).size(8*imgscl, 8*imgscl).pad(6).padTop(16);
			}
			
			pack();
		}
	}

	class Slot extends Element{
		final int index;

		public Slot(int index) {
			this.index = index;

			clicked(() -> {
				ItemStack stack = stacks[index];

				if(selected == null && stack != null){
					selected = stacks[index];
					stacks[index] = null;
				}else if(selected != null && stack == null && canPlace(selected, index)){
					stacks[index] = selected;
					selected = null;
				}else if(selected != null && stack != null && canPlace(selected, index)){
					stacks[index] = selected;
					selected = stack;
				}
			});
		}

		@Override
		public void draw(){
			Draw.patch(slotweapon == index ? "slot-select" : "slot", x, y, width, height);

			ItemStack stack = stacks[index];

			if(stack != null){
				Draw.color(0, 0, 0, 0.2f);
				Draw.rect(stack.item.name + "-item", x + width / 2, y + height / 2-4, itemsize, itemsize);
				Draw.color();
				Draw.rect(stack.item.name + "-item", x + width / 2, y + height / 2, itemsize, itemsize);
			}else if(filters[index] != null){
				Draw.rect("icon-" + filters[index].name(), x + width / 2, y + height / 2, itemsize, itemsize);
			}
		}
	}
}
