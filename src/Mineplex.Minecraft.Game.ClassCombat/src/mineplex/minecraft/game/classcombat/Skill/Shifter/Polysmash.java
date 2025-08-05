package mineplex.minecraft.game.classcombat.Skill.Shifter;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;

public class Polysmash extends SkillActive
{
	private HashSet<Player>	_used = new HashSet<Player>();

	public Polysmash(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy, int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType,
				cost, levels,
				energy, energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);

		SetDesc(new String[] 
				{
				"Turn target enemy into a sheep",
				"for 6 seconds. While in sheep form,",
				"players have Slow and Silence."
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (_used.contains(player))
			return false;

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
	}

	@EventHandler
	public void Miss(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		_used.clear();
	}

	public boolean CanUse(Player player)
	{
		int level = GetLevel(player);
		if (level == 0)
			return false;

		//Check Material
		if (!_itemSet.contains(player.getItemInHand().getType()))
			return false;

		//Check Allowed
		SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
		UtilServer.getServer().getPluginManager().callEvent(trigger);
		if (trigger.IsCancelled())
			return false;

		//Check Energy/Recharge
		if (!EnergyRechargeCheck(player, level))
			return false;

		//Allow
		return true;
	}

	@EventHandler
	public void Hit(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();

		//Level
		int level = GetLevel(player);
		if (level == 0)			return;

		if (!CanUse(player))
			return;

		if (event.getRightClicked() == null)
			return;

		if (!(event.getRightClicked() instanceof LivingEntity))
			return;

		LivingEntity ent = (LivingEntity)event.getRightClicked();
		
		if (UtilMath.offset(player, ent) > 3)
		{
			UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
			return;
		}

		//Set Used
		_used.add(player);

		//Condition
		Factory.Condition().Factory().Slow(GetName(), ent, player, 5, 0, false, true, false);
		Factory.Condition().Factory().Silence(GetName(), ent, player, 5, false, true);

		//Effect
		ent.getWorld().playSound(ent.getLocation(), Sound.SHEEP_IDLE, 2f, 1f);
		ent.getWorld().playSound(ent.getLocation(), Sound.SHEEP_IDLE, 2f, 1f);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName()) + "."));
		UtilPlayer.message(ent, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
	}

	@Override
	public void Reset(Player player) 
	{
		_used.remove(player);
	}
}
