package nautilus.game.core.arena.property;

import nautilus.game.core.arena.ITeamArena;

public class BlueShopPoints<ArenaType extends ITeamArena> extends PropertyBase<ArenaType>
{
  public BlueShopPoints()
  {
    super("blueshoppoints");
  }
  
  public boolean Parse(ArenaType arena, String value)
  {
    for (String vector : value.split(","))
    {
      arena.AddBlueGameShopPoint(ParseVector(vector.trim()), Float.parseFloat(vector.trim().split(" ")[3]) * 90.0F);
    }
    
    return true;
  }
}
