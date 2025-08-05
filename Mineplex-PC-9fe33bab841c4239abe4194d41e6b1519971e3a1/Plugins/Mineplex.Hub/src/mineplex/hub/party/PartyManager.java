package mineplex.hub.party;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mineplex.core.MiniPlugin;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.party.commands.PartyCommand;

public class PartyManager extends MiniPlugin
{
	public HubManager Manager;
	
	public HashSet<Party> _parties = new HashSet<Party>();
	
	public PartyManager(HubManager manager) 
	{
		super("Party Manager", manager.GetPlugin());
		
		Manager = manager;
	}
	
	@Override
	public void AddCommands() 
	{
		AddCommand(new PartyCommand(this));
	}

	public Party CreateParty(Player player)
	{
		Party party = new Party(this);
		party.JoinParty(player);
		_parties.add(party);
		
		return party;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void PlayerJoin(PlayerJoinEvent event)
	{
		for (Party party : _parties)
		{
			party.PlayerJoin(event.getPlayer());
		}
	}
	
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		for (Party party : _parties)
		{
			party.PlayerQuit(event.getPlayer());
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		ExpireParties();
		
		for (Party party : _parties)
		{
			party.ExpireInvitees();
			party.UpdateScoreboard();
		}
			
	}
	
	public void ExpireParties()
	{
		Iterator<Party> partyIterator = _parties.iterator();
		
		while (partyIterator.hasNext())
		{
			Party party = partyIterator.next();
			
			//Empty Party
			if (party.IsDead())
			{
				party.Announce("Your Party has been closed.");
				partyIterator.remove();
			}
		}
	}

	public Party GetParty(Player player) 
	{
		for (Party party : _parties)
		{
			if (party.GetPlayers().contains(player.getName()))
			{
				return party;
			}
		}
		
		return null;
	}
}
