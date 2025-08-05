package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid;

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
import mineplex.core.common.util.UtilBlock;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Construction extends SkillActive
{
	public Construction(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			block = block.getRelative(event.getBlockFace());
		
		if (block.getTypeId() != 8 && block.getTypeId() != 9 && event.getAction() == Action.RIGHT_CLICK_BLOCK)
			return;

		if (block.getTypeId() != 79 && event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;
		
		//Check Energy
		if (!Factory.Energy().Use(player, GetName(), 12 - (level * 2), true, true))
			return;

		//Block
		if (block.getTypeId() == 79)
		{
			block.setTypeId(8);
			
			//Sound
			player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 0.5f, 0.5f);
		}
		else
		{
			block.setTypeId(79);
			
			//Sound
			player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 3f);
		}

		//Effect
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 80);
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
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
