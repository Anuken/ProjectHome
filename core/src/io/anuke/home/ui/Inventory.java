package io.anuke.home.ui;

import java.util.Arrays;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import io.anuke.home.Vars;
import io.anuke.home.entities.traits.InventoryTrait;
import io.anuke.home.entities.traits.PlayerTrait;
import io.anuke.home.entities.types.ItemDrop;
import io.anuke.home.items.*;
import io.anuke.home.items.types.Armor;
import io.anuke.home.items.types.Soul;
import io.anuke.home.items.types.Weapon;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.style.TextureRegionDrawable;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.Label;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Strings;

public class Inventory extends Table{
	int slotw = 4, sloth = 4;
	Slot[] slots = new Slot[4];
	ItemStack[] stacks = new ItemStack[slotw * sloth];
	ItemFilter filter = new ItemFilter(Weapon.class, Weapon.class, Soul.class, Armor.class);
	ItemStack selected;
	ItemTooltip tooltip;
	boolean deselecting;
	int slotweapon = 0;
	InventoryTrait trait;

	final float slotsize = 56;
	final float itemsize = 32;

	public Inventory(Spark player) {
		trait = player.get(InventoryTrait.class);
		
		trait.melee.add(Items.marblesword);
		trait.melee.add(Items.silversword);
		trait.melee.add(Items.icesword);
		
		clearItems();
		setup();
	}
	
	public void clearItems(){
		Arrays.fill(stacks, null);
		selected = null;
		
		addItem(new ItemStack(Items.marblesword));
		addItem(new ItemStack(Items.fusionstaff));
		addItem(new ItemStack(Items.lightsoul));
		addItem(new ItemStack(Items.densearmor));
	}
	
	public boolean selectedItem(){
		return selected != null || deselecting;
	}
	
	void setup(){
		tooltip = new ItemTooltip();
		tooltip.setVisible(false);
		
		pad(8);
		
		for(int i = 0; i < slots.length; i ++){
			Slot slot = new Slot(i);
			add(slot).size(slotsize).pad(10);
		}
		
		stacks[0] = new ItemStack(Items.marblesword);
		
		/*
		Table weapons = new Table();
		
		for(int i = 0; i < trait.capacity; i ++){
			Table slot = new Table("slot");
			
			weapons.add(slot).size(64).pad(8);
			weapons.row();
		}
		
		add(weapons);
		/*
		background("inventory");
		
		Table slots = new Table();
		
		//add("Inventory", Color.DARK_GRAY).padBottom(-32).left();
		//row();
		//add("Inventory").left().padBottom(4);
		//row();
		add(slots);
		
		pad(10);
		
		for(int y = 0; y < sloth; y++){
			for(int x = 0; x < slotw; x++){
				Slot slot = new Slot(y * slotw + x);
				slots.add(slot).size(slotsize);
			}
			slots.row();
		}
		*/
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		if(Inputs.scrolled()){
			slotweapon -= Inputs.scroll();
			slotweapon = Mathf.clamp(slotweapon, 0, 1);
		}
		
		if(Inputs.keyUp("weaponswitch")){
			slotweapon = slotweapon == 0 ? 1 : 0;
		}
		
		if(Inputs.keyUp("weapon1"))
			slotweapon = 0;
		
		if(Inputs.keyUp("weapon2"))
			slotweapon = 1;
		
		Spark spark = Vars.control.getPlayer();
		PlayerTrait player = spark.get(PlayerTrait.class);
		
		player.weapon = (stacks[slotweapon] == null ? null : (Weapon)stacks[slotweapon].item);
		
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
			ItemDrop.create(selected, spark.pos().x, spark.pos().y);
			
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
		return filter.accept(slot, stack.item);
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
			}else if(stack.item == add.item && stack.item.stackable){
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
				"[DARK_GRAY]" + Strings.capitalize(item.typeName) + 
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
				getCell(image).size(8*imgscl, 8*imgscl).pad(6).padBottom(16);
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
			Draw.patch(slotweapon == index ? "slot-select" : "blank", x, y, width, height);

			ItemStack stack = stacks[index];

			if(stack != null){
				Draw.color(0, 0, 0, 0.2f);
				Draw.rect(stack.item.name + "-item", x + width / 2, y + height / 2-4, itemsize, itemsize);
				Draw.color();
				Draw.rect(stack.item.name + "-item", x + width / 2, y + height / 2, itemsize, itemsize);
			}else if(filter.getType(index) != null){
				Draw.rect("icon-" + ClassReflection.getSimpleName(filter.getType(index)).toLowerCase(), x + width / 2, y + height / 2, itemsize, itemsize);
			}
		}
	}
}
