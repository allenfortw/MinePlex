package nautilus.game.capturethepig;

import org.bukkit.entity.Player;

import me.chiss.Core.PlayerTagNamer.INameColorer;
import me.chiss.Core.Plugin.IRelation;
import mineplex.core.chat.Chat;
import nautilus.game.capturethepig.arena.CaptureThePigArenaParser;
import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.capturethepig.engine.CaptureThePigGameEngine;
import nautilus.game.capturethepig.engine.CaptureThePigNotifier;
import nautilus.game.capturethepig.repository.CaptureThePigRepository;
import nautilus.game.capturethepig.repository.ICaptureThePigRepository;
import nautilus.game.core.GamePlugin;
import nautilus.game.core.arena.ArenaManager;

public class CaptureThePig extends GamePlugin
{
    private ICaptureThePigRepository _repository;
    private CaptureThePigGameEngine _gameEngine;

    @Override
    public void onEnable()
    {        
    	super.onEnable();
    	
        _gameEngine = new CaptureThePigGameEngine(this, HubConnection, ClientManager, DonationManager, ClassManager, ConditionManager, Energy, NpcManager,
        		new CaptureThePigNotifier(this), new ArenaManager<ICaptureThePigArena>(this, "CaptureThePig", new CaptureThePigArenaParser()), getServer().getWorlds().get(0));
        
        new Chat(this, ClientManager, _gameEngine);
    }

    @Override
    public String GetServerName()
    {
    	return "CTP";
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
