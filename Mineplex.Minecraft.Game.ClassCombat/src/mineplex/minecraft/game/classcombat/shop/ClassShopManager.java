package mineplex.minecraft.game.classcombat.shop;

import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.itempack.ItemPackFactory;

public class ClassShopManager extends MiniPlugin
{
	private ClassManager _classManager;
	private SkillFactory _skillFactory;
	private ItemPackFactory _itemPackFactory;
	
	public ClassShopManager(JavaPlugin plugin, ClassManager classManager, SkillFactory skillFactory, ItemPackFactory itemPackFactory)
	{
		super("Class Shop Manager", plugin);
		
		_classManager = classManager;
		_skillFactory = skillFactory;
		_itemPackFactory = itemPackFactory;
	}
	
	public ClassManager GetClassManager()
	{
		return _classManager;
	}

	public SkillFactory GetSkillFactory()
	{
		return _skillFactory;
	}

	public ItemPackFactory GetItemPackFactory()
	{
		return _itemPackFactory;
	}
}
