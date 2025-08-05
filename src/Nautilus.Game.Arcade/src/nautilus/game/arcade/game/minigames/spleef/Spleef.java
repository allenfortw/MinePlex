package nautilus.game.arcade.game.minigames.spleef;

import java.lang.reflect.Field;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.minigames.spleef.kits.*;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.perks.event.PerkLeapEvent;
import net.minecraft.server.v1_6_R2.EntityArrow;

public class Spleef extends SoloGame
{
	public Spleef(ArcadeManager manager) 
	{
		super(manager, GameType.Spleef,

				new Kit[]
						{
				new KitLeaper(manager),
				new KitBrawler(manager),
				new KitArcher(manager)
						},

						new String[]
								{
				"Punch blocks to break them!",
				"Some blocks take multiple hits.",
				"Last player alive wins!"
								});
		
		this.DamagePvP = false;
		this.HungerSet = 20;
		this.WorldWaterDamage = 4;
		
		this.PrepareFreeze = false;
	}

	@EventHandler
	public void ArrowDamage(ProjectileHitEvent event)
	{
		final Arrow arrow = (Arrow)event.getEntity();
		final double velocity = arrow.getVelocity().length();
		
		if (!(arrow.getShooter() instanceof Player))
			return;
		
		final Player player = (Player)arrow.getShooter(); 

		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				try
				{
					EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();

					Field fieldX = EntityArrow.class.getDeclaredField("d");
					Field fieldY = EntityArrow.class.getDeclaredField("e");
					Field fieldZ = EntityArrow.class.getDeclaredField("f");

					fieldX.setAccessible(true);
					fieldY.setAccessible(true); 
					fieldZ.setAccessible(true);

					int x = fieldX.getInt(entityArrow);
					int y = fieldY.getInt(entityArrow);
					int z = fieldZ.getInt(entityArrow);

					Block block = arrow.getWorld().getBlockAt(x, y, z);

					double radius = 0.5 + velocity/1.6d;
					
					BlockFade(block, player);
					
					for (Block other : UtilBlock.getInRadius(block.getLocation().add(0.5, 0.5, 0.5), radius).keySet())
					{
						BlockFade(other, player);
					}
					
					arrow.remove();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}  
			}
		}, 0);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void BlockDamage(BlockDamageEvent event)
	{
		if (!this.IsLive())
			return;
		
		if (!this.IsAlive(event.getPlayer()))
			return;

		event.setCancelled(true);
		
		BlockFade(event.getBlock(), event.getPlayer());
	}
	
	@EventHandler
	public void LeapDamage(PerkLeapEvent event)
	{
		if (!UtilEnt.isGrounded(event.GetPlayer()))
			return;
		
		for (Block block : UtilBlock.getInRadius(event.GetPlayer().getLocation().subtract(0, 1, 0), 3d, 0).keySet())
		{
			BlockFade(block, event.GetPlayer());
		}
	}
	
	public void BlockFade(Block block, Player player)
	{
		this.AddGems(player, 0.02, "Spleef Blocks Broken", true);
		
		//Wool and Stained Clay
		if (block.getTypeId() == 35 || block.getTypeId() == 159)
		{
			//Greens
			if (block.getData() == 5 || block.getData() == 13)
				block.setData((byte)4);

			//Yellow
			else if (block.getData() == 4)
				block.setData((byte)14);

			else
				Break(block);
		}

		//Stone
		else if (block.getTypeId() == 1)
		{
			block.setTypeId(4);
		}

		//Stone Brick
		else if (block.getTypeId() == 98)
		{
			if (block.getData() == 0 || block.getData() == 1)
				block.setData((byte)2);	

			else
				Break(block);
		}
		
		//Grass
		else if (block.getTypeId() == 2)
		{
			block.setTypeId(3);
		}

		//Wood Planks
		else if (block.getTypeId() == 5)
		{
			if (block.getData() == 1)
				block.setData((byte)0);

			else if (block.getData() == 0)
				block.setData((byte)2);
			
			else
				Break(block);
		}

		//Other
		else if (block.getTypeId() != 7)
		{
			Break(block);
		}
	}
	
	public void Break(Block block)
	{
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
		block.setTypeId(0);
	}
}
