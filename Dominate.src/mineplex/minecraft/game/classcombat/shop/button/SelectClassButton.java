package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPage;
import org.bukkit.entity.Player;

public class SelectClassButton
  implements IButton
{
  ArmorPage _page;
  private IPvpClass _pvpClass;
  
  public SelectClassButton(ArmorPage page, IPvpClass pvpClass)
  {
    this._page = page;
    this._pvpClass = pvpClass;
  }
  

  public void Clicked(Player player)
  {
    this._page.SelectClass(player, this._pvpClass);
  }
}
