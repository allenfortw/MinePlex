package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Agility extends SkillActive
{
	private HashSet<Player>	_active = new HashSet<Player>();

	public Agility(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Sprint with great agility, gaining",
				"Speed I for 8 seconds. You are",
				"immune to attacks while sprinting.",
				"",
				"Agility ends if you interact."	
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		Factory.Condition().Factory().Speed(GetName(), player, player, 8, 0, false, true, true);
		_active.add(player);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));

		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void End(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (event.getAction() == Action.PHYSICAL)
			return;

		if (!_active.contains(player))
			return;

		//Remove
		_active.remove(player);
		player.removePotionEffect(PotionEffectType.SPEED);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();

		if (damagee == null)
			return;

		if (!damagee.isSprinting())
			return;

		if (!_active.contains(damagee))
			return;

		//Cancel
		event.SetCancelled(GetName());

		//Inform
		UtilPlayer.message(event.GetDamagerPlayer(true), F.main(GetClassType().name(), 
				F.name(damagee.getName()) + " is using " + F.skill(GetName(GetLevel(damagee))) + "."));

		//Effect
		damagee.getWorld().playSound(damagee.getLocation(), Sound.BLAZE_BREATH, 0.5f, 2f);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		HashSet<Player>	expired = new HashSet<Player>();
		for (Player cur : _active)
			if (!cur.hasPotionEffect(PotionEffectType.SPEED))
				expired.add(cur);

		for (Player cur : expired)
			_active.remove(cur);
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
