package nautilus.game.arcade.kit.perks;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.kit.Perk;

public class PerkBlink extends Perk
{
	private String _name = "";
	private double _range;
	private long _recharge;

	public PerkBlink(String name, double range, long recharge) 
	{
		super("Leaper", new String[] 
				{
				C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + name
				});

		_name = name;
		_range = range;
		_recharge = recharge;
	}

	@EventHandler
	public void Leap(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;

		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (UtilBlock.usable(event.getClickedBlock()))
			return;

		if (event.getPlayer().getItemInHand() == null)
			return;

		if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE"))
			return;

		Player player = event.getPlayer();

		if (!Kit.HasKit(player))
			return;

		if (!Recharge.Instance.use(player, _name, _recharge, true))
			return;

		//Smoke Trail
		Block lastSmoke = player.getLocation().getBlock();

		double curRange = 0;
		while (curRange <= _range)
		{
			Location newTarget = player.getLocation().add(new Vector(0,0.2,0)).add(player.getLocation().getDirection().multiply(curRange));

			if (!UtilBlock.airFoliage(newTarget.getBlock()) || 
					!UtilBlock.airFoliage(newTarget.getBlock().getRelative(BlockFace.UP)))
				break;

			//Progress Forwards
			curRange += 0.2;

			//Smoke Trail
			if (!lastSmoke.equals(newTarget.getBlock()))
			{
				lastSmoke.getWorld().playEffect(lastSmoke.getLocation(), Effect.SMOKE, 4);
			}
	
			lastSmoke = newTarget.getBlock();
		}

		//Modify Range
		curRange -= 0.4;
		if (curRange < 0)
			curRange = 0;

		//Destination
		Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0, 0.4, 0)));

		if (curRange > 0)
		{
			//Firework
			FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.BLACK).with(Type.BALL).trail(false).build();

			try 
			{
				Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
				player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2f, 2f);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			player.teleport(loc);
			
			//Firework
			try 
			{
				Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
				player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2f, 2f);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
			

		player.setFallDistance(0);

		//Inform
		UtilPlayer.message(player, F.main("Game", "You used " + F.skill(_name) + "."));
	}
}


