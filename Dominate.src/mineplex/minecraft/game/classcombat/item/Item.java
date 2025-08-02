package mineplex.minecraft.game.classcombat.item;

import mineplex.core.common.util.UtilGear;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.minecraft.game.classcombat.item.repository.ItemToken;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Item
  implements IItem, Listener
{
  protected ItemFactory Factory;
  private int _salesPackageId;
  private Material _type;
  private String _name;
  private String[] _desc;
  private int _amount;
  private boolean _free;
  private int _gemCost;
  private boolean _canDamage;
  
  public Item(ItemFactory factory, int salesPackageId, String name, String[] desc, Material type, int amount, boolean canDamage, int gemCost)
  {
    this.Factory = factory;
    this._salesPackageId = salesPackageId;
    this._name = name;
    this._desc = desc;
    this._type = type;
    this._amount = amount;
    this._gemCost = gemCost;
    this._canDamage = canDamage;
  }
  

  public Material GetType()
  {
    return this._type;
  }
  

  public int GetAmount()
  {
    return this._amount;
  }
  

  public int GetGemCost()
  {
    return this._gemCost;
  }
  

  public int GetSalesPackageId()
  {
    return this._salesPackageId;
  }
  

  public String GetName()
  {
    return this._name;
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!UtilGear.isMat(damager.getItemInHand(), GetType())) {
      return;
    }
    if (!this._canDamage) {
      event.SetCancelled("Item Damage Cancel");
    }
  }
  
  public boolean IsFree()
  {
    return this._free;
  }
  
  public void Update(ItemToken itemToken)
  {
    this._salesPackageId = itemToken.SalesPackage.GameSalesPackageId.intValue();
    this._gemCost = itemToken.SalesPackage.Gems.intValue();
    this._free = itemToken.SalesPackage.Free;
  }
  

  public String[] GetDesc()
  {
    return this._desc;
  }
}
