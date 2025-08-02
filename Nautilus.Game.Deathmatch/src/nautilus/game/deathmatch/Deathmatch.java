package nautilus.game.deathmatch;

import me.chiss.Core.PlayerTagNamer.INameColorer;
import me.chiss.Core.Plugin.IRelation;
import mineplex.core.server.IRepository;

import nautilus.game.core.GamePlugin;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.deathmatch.repository.DeathmatchRepository;

public class Deathmatch extends GamePlugin
{
	private DeathmatchRepository _repository;
	
	public void onEnable()
	{
		super.onEnable();
		
		new DeathmatchEngine(this, HubConnection, _repository, new DeathmatchNotifier(this), new ArenaManager<IDeathmatchArena>(this, "Deathmatch", new DeathmatchArenaParser()), getServer().getWorlds().get(0));
	}
	
	@Override
	public IRelation GetRelation()
	{
		return null;
	}

	@Override
	public INameColorer GetNameColorer()
	{
		return null;
	}

	@Override
	protected String GetServerName()
	{
		return "DM";
	}

	@Override
	protected IRepository GetRepository()
	{
		return _repository;
	}
}
