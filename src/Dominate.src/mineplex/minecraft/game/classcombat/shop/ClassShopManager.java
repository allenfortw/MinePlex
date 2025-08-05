package mineplex.minecraft.game.classcombat.shop;

import mineplex.core.MiniPlugin;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.itempack.ItemPackFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassShopManager
  extends MiniPlugin
{
  private ClassManager _classManager;
  private SkillFactory _skillFactory;
  private ItemPackFactory _itemPackFactory;
  
  public ClassShopManager(JavaPlugin plugin, ClassManager classManager, SkillFactory skillFactory, ItemPackFactory itemPackFactory)
  {
    super("Class Shop Manager", plugin);
    
    this._classManager = classManager;
    this._skillFactory = skillFactory;
    this._itemPackFactory = itemPackFactory;
  }
  
  public ClassManager GetClassManager()
  {
    return this._classManager;
  }
  
  public SkillFactory GetSkillFactory()
  {
    return this._skillFactory;
  }
  
  public ItemPackFactory GetItemPackFactory()
  {
    return this._itemPackFactory;
  }
}
