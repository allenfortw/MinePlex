package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class SpinWeb extends SkillActive
{
	public SpinWeb(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
	}

	@Override
	@EventHandler(priority = EventPriority.LOW)
	public void Interact(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		//Check Block
		if (UtilBlock.usable(event.getClickedBlock()))
			return;

		//Check Action
		if (!_actionSet.contains(event.getAction()))
			return;

		//Check Material
		if (!_itemSet.contains(player.getItemInHand().getType()))
			return;

		//Level
		int level = GetLevel(player);
		if (level <= 0)		return;

		if (!CustomCheck(player, level))
			return;

		//Unique Weapon
		if (player.getItemInHand().getEnchantments().containsKey(Enchantment.ARROW_DAMAGE))
			return;

		//Block
		Block block = event.getClickedBlock();
		if (block == null)	return;

		block = block.getRelative(event.getBlockFace());

		//Check Energy
		if (!Factory.Energy().Use(player, GetName(), 20 - (level * 2), true, true))
			return;

		//Block
		Factory.BlockRestore().Add(block, 30, (byte)0, 5000 + (1000 * level));

		//Effect
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 30);

		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.SPIDER_IDLE, 1f, 0.3f);
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

	}

	@Override
	public void Reset(Player player) 
	{
		
	}
}
