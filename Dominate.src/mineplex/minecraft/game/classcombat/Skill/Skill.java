package mineplex.minecraft.game.classcombat.Skill;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;






public abstract class Skill
  implements ISkill, Listener
{
  private String _name;
  private String[] _desc;
  private String[] _descFull;
  private IPvpClass.ClassType _classType;
  private ISkill.SkillType _skillType;
  private int _salesPackageId;
  private int _cost;
  private boolean _free;
  private List<Player> _users;
  public SkillFactory Factory;
  
  public Skill(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    this.Factory = skills;
    this._name = name;
    this._desc = new String[] { "<Skill Description>" };
    this._classType = classType;
    this._skillType = skillType;
    this._users = new ArrayList();
  }
  

  public String GetName()
  {
    return this._name;
  }
  
  public String GetName(int level)
  {
    if (level <= 1) {
      return GetName();
    }
    return this._name + " " + level;
  }
  
  public String GetName(String type)
  {
    return this._name + " (" + type + ")";
  }
  

  public Integer GetSalesPackageId()
  {
    return Integer.valueOf(this._salesPackageId);
  }
  

  public IPvpClass.ClassType GetClassType()
  {
    return this._classType;
  }
  

  public ISkill.SkillType GetSkillType()
  {
    return this._skillType;
  }
  

  public int GetCost()
  {
    return this._cost;
  }
  
  public int GetLevel(Entity ent)
  {
    if (!(ent instanceof Player)) {
      return 0;
    }
    Player player = (Player)ent;
    
    if (!this._users.contains(player)) {
      return 0;
    }
    return 1;
  }
  

  public String[] GetDesc()
  {
    if (this._descFull != null) {
      return this._descFull;
    }
    String recharge = GetRechargeString();
    String energy = GetEnergyString();
    
    if ((recharge == null) && (energy == null)) {
      this._descFull = this._desc;
    }
    ArrayList<String> descFull = new ArrayList();
    
    for (String line : this._desc) {
      descFull.add(line);
    }
    if ((energy != null) || (recharge != null)) {
      descFull.add("");
    }
    if (energy != null) {
      descFull.add(energy);
    }
    if (recharge != null) {
      descFull.add(recharge);
    }
    this._descFull = new String[descFull.size()];
    for (int i = 0; i < descFull.size(); i++) {
      this._descFull[i] = ((String)descFull.get(i));
    }
    return this._descFull;
  }
  
  public String GetEnergyString()
  {
    return null;
  }
  
  public String GetRechargeString()
  {
    return null;
  }
  

  public List<Player> GetUsers()
  {
    this._users.remove(null);
    return this._users;
  }
  

  public void AddUser(Player player)
  {
    this._users.add(player);
    OnPlayerAdd(player);
  }
  


  public void OnPlayerAdd(Player player) {}
  


  public void RemoveUser(Player player)
  {
    this._users.remove(player);
    Reset(player);
  }
  
  public void SetDesc(String[] desc)
  {
    this._desc = desc;
  }
  
  @EventHandler
  public final void Death(PlayerDeathEvent event)
  {
    Reset(event.getEntity());
  }
  
  @EventHandler
  public final void Quit(PlayerQuitEvent event)
  {
    Reset(event.getPlayer());
    this._users.remove(event.getPlayer());
  }
  

  public boolean IsFree()
  {
    return this._free;
  }
  
  public void Update(SkillToken skillToken)
  {
    this._salesPackageId = skillToken.SalesPackage.GameSalesPackageId.intValue();
    this._free = skillToken.SalesPackage.Free;
    this._cost = skillToken.SalesPackage.Gems.intValue();
  }
}
