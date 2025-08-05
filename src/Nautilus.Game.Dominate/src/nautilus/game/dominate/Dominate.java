package nautilus.game.dominate;

import org.bukkit.entity.Player;

import mineplex.core.chat.Chat;
import nautilus.game.core.GamePlugin;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.dominate.arena.DominateArenaParser;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.engine.DominateGameEngine;
import nautilus.game.dominate.engine.DominateNotifier;

public class Dominate extends GamePlugin
{
    private DominateGameEngine _gameEngine;

    @Override
    public void onEnable()
    {        
    	super.onEnable();
    	
        _gameEngine = new DominateGameEngine(this, HubConnection, ClientManager, DonationManager, ClassManager, ConditionManager, Energy, NpcManager,
        		new DominateNotifier(this), PacketHandler, new ArenaManager<IDominateArena>(this, "Dominate", new DominateArenaParser()), getServer().getWorlds().get(0), GetSpawnLocation(), GetWebServerAddress());
        
        new Chat(this, ClientManager);
    }

    @Override
    public String GetServerName()
    {
    	return "DOM";
    }

	@Override
	public boolean CanHurt(Player a, Player b)
	{
		return _gameEngine.CanHurt(a, b);
	}

	@Override
	public boolean CanHurt(String a, String b)
	{
		return _gameEngine.CanHurt(a, b);
	}

	@Override
	public boolean IsSafe(Player a)
	{
		return _gameEngine.IsSafe(a);
	}
}
