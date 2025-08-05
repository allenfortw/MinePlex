package nautilus.game.deathmatch;

import me.chiss.Core.Plugin.IPlugin;
import nautilus.game.core.notifier.PlayerNotifier;

public class DeathmatchNotifier extends PlayerNotifier<DeathmatchGame, IDeathmatchArena, DeathmatchPlayer>
{
	public DeathmatchNotifier(IPlugin plugin)
	{
		super(plugin, "Deathmatch");
	}
}
