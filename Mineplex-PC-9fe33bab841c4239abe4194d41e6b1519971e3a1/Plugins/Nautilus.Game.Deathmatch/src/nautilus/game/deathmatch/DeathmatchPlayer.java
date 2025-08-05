package nautilus.game.deathmatch;

import org.bukkit.plugin.java.JavaPlugin;

import nautilus.game.core.player.GamePlayer;

public class DeathmatchPlayer extends GamePlayer
{
	public DeathmatchPlayer(JavaPlugin plugin, org.bukkit.entity.Player player)
	{
		super(plugin, player);
	}
}
