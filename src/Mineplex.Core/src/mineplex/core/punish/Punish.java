package mineplex.core.punish;

import java.util.HashMap;

import mineplex.core.MiniPlugin;
import mineplex.core.account.event.ClientWebRequestEvent;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.punish.Command.*;
import mineplex.core.punish.Tokens.PunishClientToken;
import mineplex.core.punish.Tokens.PunishmentToken;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class Punish extends MiniPlugin
{
	private HashMap<String, PunishClient> _punishClients;
	private PunishRepository _repository;
	
	public Punish(JavaPlugin plugin, String webServerAddress) 
	{
		super("Punish", plugin);

        _punishClients = new HashMap<String, PunishClient>();
        _repository = new PunishRepository(webServerAddress);
	}
	
	public PunishRepository GetRepository()
	{
		return _repository;
	}
	
	@Override
	public void AddCommands()
	{
		AddCommand(new PunishCommand(this));
	}
	
	@EventHandler
	public void OnClientWebRequest(ClientWebRequestEvent event)
	{
		/*
		try
		{
			// TODO Parse infractions/punishments here
			// event.GetJsonWriter().beginObject();
			// event.GetJsonWriter().name("Punish");
			// event.GetJsonWriter().value("true");
			// event.GetJsonWriter().endObject();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		*/
	}
	
	@EventHandler
	public void OnClientWebResponse(ClientWebResponseEvent event)
	{
		/*
		JsonReader reader = null;
		
		try
		{
			while (reader.hasNext())
			{				
				if (reader.nextName().equalsIgnoreCase("Punish"))
				{
					reader.beginObject();
					
					// TODO Parse infractions/punishments here
					// PunishClient client = new PunishClient();
					// client.AddInfraction(token.Category, new Infraction(token.Reason, token.Admin, token.Time));
			        // client.AddPunishment(token.Category, new Punishment(token.PunishmentSentence, token.Reason, token.Admin, token.Hours, token.Time));
			        // _punishClients.put(event.GetClient().GetPlayerName(), client);
					
					break;
				}
				
				reader.endObject();
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		*/
		
		PunishClientToken token = new Gson().fromJson(event.GetResponse(), PunishClientToken.class);
		LoadClient(token);
	}
	
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		_punishClients.remove(event.getPlayer().getName().toLowerCase());
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerLogin(AsyncPlayerPreLoginEvent event)
    {
    	if (_punishClients.containsKey(event.getName().toLowerCase()))
		{
    		PunishClient client = GetClient(event.getName());
    		
    		if (client.IsBanned())
    		{
    			Punishment punishment = client.GetPunishment(PunishmentSentence.Ban);
    			String time = F.time(UtilTime.convertString((long)(punishment.GetHours() * 3600000), 0, TimeUnit.FIT));
    			
    			if (punishment.GetHours() == -1)
    				time = C.cRed + "Permanent";
    			
                String reason = C.consoleHead + F.main(GetName(), punishment.GetAdmin() + " banned you because of '" + F.elem(punishment.GetReason()) + "' for " + time);

                event.disallow(Result.KICK_BANNED, reason);
    		}
		}
        
        /*
        for (String alias : client.Acc().GetAliasIP())
        {
            if (Clients().Get(alias).Ban().IsBanned())
            {
                String reason = C.consoleHead + "Alias Banned" +
                        C.consoleFill + " - " +
                        C.consoleBody + client.Ban().GetBan().RemainingString() +
                        C.consoleFill + " - " +
                        C.consoleBody + client.Ban().Reason();

                event.disallow(Result.KICK_BANNED, reason);
                return;
            }
        }
        
        for (String alias : client.Acc().GetAliasMAC())
        {
            if (Clients().Get(alias).Ban().IsBanned())
            {
                String reason = C.consoleHead + "Alias Banned" +
                        C.consoleFill + " - " +
                        C.consoleBody + client.Ban().GetBan().RemainingString() +
                        C.consoleFill + " - " +
                        C.consoleBody + client.Ban().Reason();

                event.disallow(Result.KICK_BANNED, reason);
                return;
            }
        }
        */
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void PunishChatEvent(AsyncPlayerChatEvent event)
    {
        PunishClient client = GetClient(event.getPlayer().getName());
        
        if (client != null && client.IsMuted())
        {
        	event.getPlayer().sendMessage(F.main(GetName(), "Shh, you're muted for " + C.cGreen + UtilTime.convertString(client.GetPunishment(PunishmentSentence.Mute).GetRemaining(), 1, TimeUnit.FIT) + "."));
        	event.setCancelled(true);
        }
    }

	public void Help(Player caller)
	{
		UtilPlayer.message(caller, F.main(_moduleName, "Commands List:"));
		UtilPlayer.message(caller, F.help("/punish", "<player> <reason>", Rank.MODERATOR));
	}
	
	public void AddPunishment(final String playerName, final Category category, final String reason, final Player caller, final int severity, boolean ban, long duration)
	{
		if (!_punishClients.containsKey(playerName.toLowerCase()))
		{
			_punishClients.put(playerName.toLowerCase(), new PunishClient());
		}
		
		final PunishmentSentence sentence = !ban ? PunishmentSentence.Mute : PunishmentSentence.Ban;
		
		final long finalDuration = duration;
		
		_repository.Punish(new Callback<String>()
		{
			public void run(String result)
			{
				PunishmentResponse banResult = PunishmentResponse.valueOf(result);
				
				if (banResult == PunishmentResponse.AccountDoesNotExist)
				{
					if (caller != null)
						caller.sendMessage(F.main(GetName(), "Account with name " + F.elem(playerName) + " does not exist."));
					else
						System.out.println(F.main(GetName(), "Account with name " + F.elem(playerName) + " does not exist."));
				}
				else if (banResult == PunishmentResponse.InsufficientPrivileges)
				{
					if (caller != null)
						caller.sendMessage(F.main(GetName(), "You have insufficient rights to punish " + F.elem(playerName) + "."));
					else
						System.out.println(F.main(GetName(), "You have insufficient rights to punish " + F.elem(playerName) + "."));
				}
				else if (banResult == PunishmentResponse.Punished)
				{
					String durationString = F.time(UtilTime.convertString(finalDuration < 0 ? -1 : (long)(finalDuration * 3600000), 1, TimeUnit.FIT));
					
					if (sentence == PunishmentSentence.Ban)
					{
						if (caller == null)
							System.out.println(F.main(GetName(), F.elem(caller == null ? "Mineplex Enjin Server" : caller.getName()) + " banned " + F.elem(playerName) + " because of " + F.elem(reason) + " for " + durationString + "."));
						
						UtilPlayer.kick(UtilPlayer.searchOnline(null, playerName, false), GetName(), caller == null ? "Mineplex Enjin Server" : caller.getName() + " banned you because of " + F.elem(reason) + " for " + 
								durationString + ".");
						
						UtilServer.broadcast(F.main(GetName(), F.elem(caller == null ? "Mineplex Enjin Server" : caller.getName()) + " banned " + F.elem(playerName) + " because of " + F.elem(reason) + " for " + durationString + "."));
					}
					else
					{
						if (caller == null)
							System.out.println(F.main(GetName(), F.elem(caller == null ? "Mineplex Enjin Server" : caller.getName()) + " muted " + F.elem(playerName) + " because of " + F.elem(reason) + " for " +
									durationString + "."));
						
						UtilServer.broadcast(F.main(GetName(), F.elem(caller == null ? "Mineplex Enjin Server" : caller.getName()) + " muted " + F.elem(playerName) + " because of " + F.elem(reason) + " for " +
								durationString + "."));
						
						_repository.LoadPunishClient(playerName, new Callback<PunishClientToken>()
						{
							public void run(PunishClientToken token)
							{
								LoadClient(token);
							}
						});
					}
				}
			}
		}, playerName, category.toString(), sentence, reason, duration, caller == null ? "Mineplex Enjin Server" : caller.getName(), severity, System.currentTimeMillis());
	}
	
	public void LoadClient(PunishClientToken token)
	{
		PunishClient client = new PunishClient();
		
		for (PunishmentToken punishment : token.Punishments)
		{
			client.AddPunishment(Category.valueOf(punishment.Category), new Punishment(punishment.PunishmentId, PunishmentSentence.valueOf(punishment.Sentence), Category.valueOf(punishment.Category), punishment.Reason, punishment.Admin, punishment.Duration, punishment.Severity, punishment.Time, punishment.Active, punishment.Removed, punishment.RemoveAdmin, punishment.RemoveReason));
		}
		
		_punishClients.put(token.Name.toLowerCase(), client);
	}
	
	public PunishClient GetClient(String name)
	{
		synchronized (this)
		{
			return _punishClients.get(name.toLowerCase());
		}
	}

	public void RemovePunishment(int punishmentId, String target, final Player admin, String reason, Callback<String> callback)
	{
		_repository.RemovePunishment(callback, punishmentId, target, reason, admin.getName());
	}

	public void RemoveBan(String name, String reason)
	{
		_repository.RemoveBan(name, reason);
	}
}
