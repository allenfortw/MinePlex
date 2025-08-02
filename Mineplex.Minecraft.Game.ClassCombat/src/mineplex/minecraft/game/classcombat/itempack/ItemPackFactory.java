package mineplex.minecraft.game.classcombat.itempack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;

public class ItemPackFactory extends MiniPlugin
{
	private NautHashMap<ClassType, List<ItemPack>> _classItemPackMap = new NautHashMap<ClassType, List<ItemPack>>();
	
	public ItemPackFactory(JavaPlugin plugin)
	{
		super("Item Pack Factory", plugin);
		
		AddItemPacks();
	}
	
	/*
	public List<ItemPack> GetItemPacks(IPvpClass pvpClass)
	{
		
	}
	*/
	
	private void AddItemPacks()
	{
		for (ClassType pvpClassType : ClassType.values())
			_classItemPackMap.put(pvpClassType, new ArrayList<ItemPack>());	
		
		AddAssassin();
		AddBrute();
		AddKnight();
		AddMage();
		AddRanger();
		//AddShifter();
		AddGlobal();
	}

	private void AddGlobal()
	{
		/*
		for (ClassType pvpClassType : ClassType.values())
			_classItemPackMap.get(pvpClassType).add(itemPack);
			*/
	}

	private void AddRanger()
	{

	}

	private void AddMage()
	{
		// TODO Auto-generated method stub
		
	}

	private void AddKnight()
	{
		// TODO Auto-generated method stub
		
	}

	private void AddBrute()
	{
		// TODO Auto-generated method stub
		
	}

	private void AddAssassin()
	{
		// TODO Auto-generated method stub
		
	}
}
