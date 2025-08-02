package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract class FormBase extends Skill
{
	private EntityType _type;
	private ArrayList<ISkill> _formSkills = new ArrayList<ISkill>();
	private String[] _formSkillNames;
	
	private HashMap<Player, HashMap<ISkill, Integer>> _savedSkills = new HashMap<Player, HashMap<ISkill, Integer>>();
	
	public FormBase(SkillFactory skills, String name, ClassType classType,
			SkillType skillType, int cost, int levels, EntityType type, String[] formSkillNames) 
	{
		super(skills, name, classType, skillType, cost, levels);
		
		_type = type;
		_formSkillNames = formSkillNames;
	}
	
	@EventHandler
	public void Use(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		int level = GetLevel(player);
		if (level == 0)		return;
		
		if (!UtilGear.isWeapon(event.getItemDrop().getItemStack()))
			return;

		//Shift/Non-Shift for A/B
		if (!IsMorphed(player))
		{
			if (this.GetSkillType() == SkillType.PassiveA)
				if (player.isSneaking())
					return;
			
			if (this.GetSkillType() == SkillType.PassiveB)
				if (!player.isSneaking())
					return;
		}	
		
		event.setCancelled(true);
		
		if (_savedSkills.containsKey(player))
			Unmorph(player);
		else
			Morph(player);
	}

	public EntityType GetType()
	{
		return _type;
	}
	
	public void Morph(Player player)
	{
		SaveHumanSkills(player);

		//Set Skills
		ClientClass cc = Factory.ClassManager().Get(player);
		
		for (ISkill skill : GetFormSkills())
			cc.AddSkill(skill, GetLevel(player));	
				
		
		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You shapeshifted into " + F.skill(GetName() + " " + GetLevel(player))));
	}

	public void Unmorph(Player player)
	{
		//Reset Skills
		for (ISkill skill : GetFormSkills())
			skill.Reset(player);
		
		UnapplyMorph(player);
		RestoreHumanSkills(player);
		
		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You returned to " + F.skill("Human Form")));
	}
	
	public boolean IsMorphed(Player player)
	{
		return _savedSkills.containsKey(player);
	}
	
	public Collection<Player> GetMorphedUsers()
	{
		return _savedSkills.keySet();
	}
	
	public ArrayList<ISkill> GetFormSkills()
	{
		if (_formSkills.isEmpty())
			for (String name : _formSkillNames)
			{
				ISkill skill = Factory.GetSkill(name);
				
				if (skill != null)
					_formSkills.add(skill);
				
				else
					System.out.println("Invalid Skill [" + name + "] for [" + GetName() + "].");
			}
		
		
		return _formSkills;
	}

	public abstract void UnapplyMorph(Player player);

	private void SaveHumanSkills(Player player) 
	{
		ClientClass cc = Factory.ClassManager().Get(player);
		
		//Save Current
		_savedSkills.put(player, new HashMap<ISkill, Integer>());
		
		for (Entry<ISkill, Integer> skill : cc.GetSkills())
		{
			//Save Sword/Axe
			if (skill.getKey().GetSkillType() == SkillType.Sword || 
				skill.getKey().GetSkillType() == SkillType.Axe ||
				skill.getKey().GetSkillType() == SkillType.Class)
				_savedSkills.get(player).put(skill.getKey(), skill.getValue());
			
			//Save OTHER Morph
			if (this.GetSkillType() == SkillType.PassiveA)
				if (skill.getKey().GetSkillType() == SkillType.PassiveB)
					_savedSkills.get(player).put(skill.getKey(), skill.getValue());
			
			if (this.GetSkillType() == SkillType.PassiveB)
				if (skill.getKey().GetSkillType() == SkillType.PassiveA)
					_savedSkills.get(player).put(skill.getKey(), skill.getValue());
		}
			
		//Remove Saved
		for (ISkill skill : _savedSkills.get(player).keySet())
			cc.RemoveSkill(cc.GetSkillByType(skill.GetSkillType()));
	}
	
	private void RestoreHumanSkills(Player player) 
	{
		ClientClass cc = Factory.ClassManager().Get(player);
		
		//Remove Morph Skills
		cc.RemoveSkill(cc.GetSkillByType(SkillType.Sword));
		cc.RemoveSkill(cc.GetSkillByType(SkillType.Axe));
		
		//Restore Old
		if (!_savedSkills.containsKey(player))
			return;
		
		for (ISkill skill : _savedSkills.get(player).keySet())
			cc.AddSkill(skill, _savedSkills.get(player).get(skill));

		_savedSkills.remove(player);	
	}
	
	@Override
	public void Reset(Player player) 
	{
		_savedSkills.remove(player);
	}	
}
