package mineplex.minecraft.game.classcombat.itempack;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import org.bukkit.plugin.java.JavaPlugin;



public class ItemPackFactory
  extends MiniPlugin
{
  private NautHashMap<IPvpClass.ClassType, List<ItemPack>> _classItemPackMap = new NautHashMap();
  
  public ItemPackFactory(JavaPlugin plugin)
  {
    super("Item Pack Factory", plugin);
    
    AddItemPacks();
  }
  







  private void AddItemPacks()
  {
    for (IPvpClass.ClassType pvpClassType : ) {
      this._classItemPackMap.put(pvpClassType, new ArrayList());
    }
    AddAssassin();
    AddBrute();
    AddKnight();
    AddMage();
    AddRanger();
    
    AddGlobal();
  }
  
  private void AddGlobal() {}
  
  private void AddRanger() {}
  
  private void AddMage() {}
  
  private void AddKnight() {}
  
  private void AddBrute() {}
  
  private void AddAssassin() {}
}
