package nautilus.game.deathmatch;

import me.chiss.Core.Plugin.IPlugin;
import mineplex.core.server.ServerTalker;
import nautilus.game.core.engine.GameEngine;
import nautilus.game.deathmatch.repository.DeathmatchRepository;

public class DeathmatchEngine extends GameEngine<DeathmatchGame, DeathmatchScoreHandler, IDeathmatchArena, DeathmatchPlayer>
{
    public DeathmatchEngine(IPlugin plugin, ServerTalker hubConnection, DeathmatchRepository repository, DeathmatchNotifier notifier, nautilus.game.core.arena.ArenaManager<IDeathmatchArena> arenaManager, org.bukkit.World world)
    {
        super(plugin, hubConnection, repository, arenaManager, new DeathmatchScoreHandler(plugin.GetPlugin(), notifier), world);
    }

	@Override
	public void run()
	{
	}

	@Override
	public DeathmatchGame ScheduleNewGame()
	{
		return null;
	}

	@Override
	public String GetGameType()
	{
		return "Deathmatch";
	}

	@Override
	protected void TryToActivateGames()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void ActivateGame(DeathmatchGame game, IDeathmatchArena arena)
	{
		// TODO Auto-generated method stub
		
	}
}
