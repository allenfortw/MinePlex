package nautilus.game.arcade.managers;

import java.util.HashMap;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class IdleManager implements Listener
{
	ArcadeManager Manager;

	private HashMap<Player, Float> _yaw = new HashMap<Player, Float>();
	private HashMap<Player, Long> _idle = new HashMap<Player, Long>();
	private HashMap<Player, Integer> _beep = new HashMap<Player, Integer>();
	
	
	public IdleManager(ArcadeManager manager)
	{
		Manager = manager;

		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}

	@EventHandler
	public void KickIdlePlayers(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (Manager.GetGame() != null && !Manager.GetGame().IdleKick)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (!_yaw.containsKey(player) || !_idle.containsKey(player))
			{
				_yaw.put(player, player.getLocation().getYaw());
				_idle.put(player, System.currentTimeMillis());
			}

			if (_yaw.get(player) == player.getLocation().getYaw())
			{		
				if (UtilTime.elapsed(_idle.get(player), 120000))
				{
					if (Manager.GetGame().GetState() != GameState.Recruit && !Manager.GetGame().IsAlive(player))
						continue;
					
					if (Manager.GetClients().Get(player).GetRank().Has(Rank.MODERATOR))
						continue;
					
					//Start Beeps
					if (!_beep.containsKey(player))
					{
						_beep.put(player, 20);
					}
					//Countdown
					else
					{
						int count = _beep.get(player);
						
						if (count == 0)
						{
							player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10f, 1f);
							Manager.GetPortal().SendPlayerToServer(player, "Lobby");	
						}
						else
						{	
							float scale = (float) (0.8 + (((double)count/20d)*1.2));
							player.playSound(player.getLocation(), Sound.NOTE_PLING, scale, scale);
							
							if (count%2 == 0)
							{
								UtilPlayer.message(player, C.cGold + C.Bold + "You will be AFK removed in " + (count/2) + " seconds...");
							}
							
							count--;
							_beep.put(player, count);
						}
					}	
				}
					
				continue;
			}
				
			_yaw.put(player, player.getLocation().getYaw());
			_idle.put(player, System.currentTimeMillis());
			_beep.remove(player);
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		_yaw.remove(event.getPlayer());
		_idle.remove(event.getPlayer());
		_beep.remove(event.getPlayer());
	}
}
