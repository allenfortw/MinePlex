package nautilus.game.deathmatch;

import me.chiss.Core.Plugin.IPlugin;
import nautilus.game.core.game.Game;

import org.bukkit.entity.Player;

public class DeathmatchGame extends Game<DeathmatchPlayer, IDeathmatchArena> implements IDeathmatchGame
{
	public DeathmatchGame(IPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void RespawnPlayer(DeathmatchPlayer player)
	{
	
	}

	@Override
	protected DeathmatchPlayer CreateGamePlayer(Player player, int playerLives)
	{
		return new DeathmatchPlayer(Plugin.GetPlugin(), player);
	}
}