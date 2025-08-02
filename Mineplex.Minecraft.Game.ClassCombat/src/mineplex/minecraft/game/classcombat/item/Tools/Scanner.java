package mineplex.minecraft.game.classcombat.item.Tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;

public class Scanner extends ItemUsable
{
	public Scanner(ItemFactory factory, int salesPackageId, Material type,
			int amount, boolean canDamage, int creditCost,
			ActionType useAction, boolean useStock, long useDelay,
			int useEnergy, ActionType throwAction, boolean throwStock,
			long throwDelay, int throwEnergy, float throwPower, 
			long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup) 
	{
		super(factory, salesPackageId, "Scanner VR-9000", new String[] { "Displays target players skills." }, type, amount, canDamage, creditCost,
				useAction, useStock, useDelay, useEnergy, throwAction, throwStock,
				throwDelay, throwEnergy, throwPower, 
				throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
	}

	@Override
	public void UseAction(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		
		double max = 100;
		double cur = 4;
		
		while (cur < max)
		{
			for (Player target : UtilPlayer.getNearby(player.getLocation().add(player.getLocation().getDirection().multiply(cur)), 2))
			{
				Factory.ClassManager().Get(target).ListSkills(player);
				return;
			}
			
			cur += 2;
		}
		
		UtilPlayer.message(player, F.main("Scanner", "There are no targets in range."));
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{
		
	}

	@Override
	public void Idle(ProjectileUser data) 
	{
	
	}

	@Override
	public void Expire(ProjectileUser data) 
	{
		
	}
}
