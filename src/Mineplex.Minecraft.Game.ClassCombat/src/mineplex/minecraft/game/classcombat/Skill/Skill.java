package mineplex.minecraft.game.classcombat.Skill;

import java.util.ArrayList;
import java.util.List;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class Skill implements ISkill, Listener
{	
	//Properties
	private String _name;
	private String[] _desc;
	private String[] _descFull;

	private ClassType _classType;
	private SkillType _skillType;	

	private int _salesPackageId;
	private int _cost;
	
	private boolean _free;
	private List<Player> _users;
	
	public SkillFactory Factory;

	public Skill(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels)
	{
		Factory = skills;
		_name = name;
		_desc = new String[] { "<Skill Description>" };
		_classType = classType;
		_skillType = skillType;
		_users = new ArrayList<Player>();
	}

	@Override
	public String GetName() 
	{
		return _name;
	}
	
	public String GetName(int level) 
	{
		if (level <= 1)
			return GetName();
		
		return _name + " " + level;
	}

	public String GetName(String type) 
	{
		return _name + " (" + type + ")";
	}

	@Override
	public Integer GetSalesPackageId()
	{
		return _salesPackageId;
	}

	@Override
	public ClassType GetClassType() 
	{
		return _classType;
	}

	@Override
	public SkillType GetSkillType()
	{
		return _skillType;
	}

	@Override
	public int GetCost()
	{
		return _cost;
	}
	
	public int GetLevel(Entity ent)
	{
		if (!(ent instanceof Player))
			return 0;

		Player player = (Player)ent;

		if (!_users.contains(player))
			return 0;

		return 1;
	}

	@Override
	public String[] GetDesc()
	{
		if (_descFull != null)
			return _descFull;

		String recharge = GetRechargeString();
		String energy = GetEnergyString();

		if (recharge == null && energy == null)
			_descFull = _desc;

		ArrayList<String> descFull = new ArrayList<String>();

		for (String line : _desc)
			descFull.add(line);

		if (energy != null || recharge != null)
			descFull.add("");

		if (energy != null)
			descFull.add(energy);

		if (recharge != null)
			descFull.add(recharge);

		_descFull = new String[descFull.size()];
		for (int i=0 ; i<descFull.size() ; i++)
			_descFull[i] = descFull.get(i);

		return _descFull;
	}

	public String GetEnergyString() 
	{
		return null;
	}

	public String GetRechargeString() 
	{
		return null;
	}

	@Override
	public List<Player> GetUsers()
	{
		_users.remove(null);
		return _users;
	}

	@Override
	public void AddUser(Player player)
	{
		_users.add(player);
		OnPlayerAdd(player);
	}

	public void OnPlayerAdd(Player player)
	{
		//Null Default
	}

	@Override
	public void RemoveUser(Player player)
	{
		_users.remove(player);
		Reset(player);
	}

	public void SetDesc(String[] desc)
	{
		_desc = desc;
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
		_users.remove(event.getPlayer());
	}

	@Override
	public boolean IsFree()
	{
		return _free;
	}

	public void Update(SkillToken skillToken) 
	{
		_salesPackageId = skillToken.SalesPackage.GameSalesPackageId;
		_free = skillToken.SalesPackage.Free;
		_cost = skillToken.SalesPackage.Gems;
	}
}
