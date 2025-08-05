package mineplex.core.chat;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.chat.command.BroadcastCommand;
import mineplex.core.chat.command.ChatSlowCommand;
import mineplex.core.chat.command.SilenceCommand;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilTime;
import mineplex.core.preferences.PreferencesManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

public class Chat extends MiniPlugin
{
	private CoreClientManager _clientManager;
	private PreferencesManager _preferences;
	
	private String[] _hackusations = {"hack", "hax", "hacker", "hacking", "cheat", "cheater", "cheating", "forcefield", "flyhack", "flyhacking", "autoclick", "aimbot"};

	private int _chatSlow = 0;
	private long _silenced = 0;
	private boolean _threeSecondDelay = true;

	private HashMap<UUID, MessageData> _playerLastMessage = new HashMap<UUID, MessageData>();

	public Chat(JavaPlugin plugin, CoreClientManager clientManager, PreferencesManager preferences, String serverName)
	{
		super("Chat", plugin);

		_clientManager = clientManager;
		_preferences = preferences;
		
		try
		{
			trustCert();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void addCommands()
	{
		addCommand(new SilenceCommand(this));
		addCommand(new BroadcastCommand(this));
		addCommand(new ChatSlowCommand(this));
	}

	public void setChatSlow(int seconds, boolean inform)
	{
		if (seconds < 0)
			seconds = 0;

		_chatSlow = seconds;

		if (inform)
		{
			if (seconds == 0)
				UtilServer.broadcast(F.main("Chat", "Chat Slow is now disabled"));
			else
				UtilServer.broadcast(F.main("Chat", "Chat slow is now enabled with a cooldown of " + F.time(seconds + " seconds")));
		}
	}

	public void Silence(long duration, boolean inform)
	{
		// Set Silenced
		if (duration > 0)
			_silenced = System.currentTimeMillis() + duration;
		else
			_silenced = duration;

		if (!inform)
			return;

		// Announce
		if (duration == -1)
			UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time("Permanent") + "."));
		else if (duration == 0)
			UtilServer.broadcast(F.main("Chat", "Chat is no longer silenced."));
		else
			UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time(UtilTime.MakeStr(duration, 1))
					+ "."));
	}

	@EventHandler
	public void preventMe(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().toLowerCase().startsWith("/me ")
				|| event.getMessage().toLowerCase().startsWith("/bukkit"))
		{
			event.getPlayer().sendMessage(F.main(getName(), "No, you!"));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void lagTest(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().equals("lag") || event.getMessage().equals("ping"))
		{
			event.getPlayer().sendMessage(F.main(getName(), "PONG!"));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void SilenceUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		SilenceEnd();
	}

	public void SilenceEnd()
	{
		if (_silenced <= 0)
			return;

		if (System.currentTimeMillis() > _silenced)
			Silence(0, true);
	}

	public boolean SilenceCheck(Player player)
	{
		SilenceEnd();

		if (_silenced == 0)
			return false;

		if (_clientManager.Get(player).GetRank().Has(player, Rank.MODERATOR, false))
			return false;

		if (_silenced == -1)
			UtilPlayer.message(player, F.main(getName(), "Chat is silenced permanently."));
		else
			UtilPlayer.message(
					player,
					F.main(getName(),
							"Chat is silenced for "
									+ F.time(UtilTime.MakeStr(_silenced - System.currentTimeMillis(), 1)) + "."));

		return true;
	}

	@EventHandler
	public void removeChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.isAsynchronous())
		{
			for (Iterator<Player> playerIterator = event.getRecipients().iterator(); playerIterator.hasNext();)
			{
				if (!_preferences.Get(playerIterator.next()).ShowChat)
					playerIterator.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void HandleChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;

		Player sender = event.getPlayer();

		if (SilenceCheck(sender))
		{
			event.setCancelled(true);
			return;
		}
		else if (_threeSecondDelay &&
				_clientManager.Get(sender).GetRank() == Rank.ALL)
		{
			UtilPlayer.message(sender, C.cYellow + "You can only chat once every 3 seconds to prevent spam.");
			UtilPlayer.message(sender, C.cYellow + "Buy a Rank at " + C.cGreen + "www.mineplex.com/shop" + C.cYellow + " to remove this limit!");
			event.setCancelled(true);
		}
		else if (!_clientManager.Get(sender).GetRank().Has(Rank.MODERATOR) &&
				!Recharge.Instance.use(sender, "Chat Message", 400, false, false))
		{
			UtilPlayer.message(sender, F.main("Chat", "You are sending messages too fast."));
			event.setCancelled(true);
		}
		else if (!_clientManager.Get(sender).GetRank().Has(Rank.HELPER) &&
				msgContainsHack(event.getMessage()))
		{
			UtilPlayer.message(sender, F.main("Chat", 
					"Accusing players of cheating in-game is against the rules."
					+ "If you think someone is cheating, please gather evidence and report it at "
					+ F.link("www.mineplex.com/reports")));
			event.setCancelled(true);
		}
		else if (_playerLastMessage.containsKey(sender.getUniqueId()))
		{
			MessageData lastMessage = _playerLastMessage.get(sender.getUniqueId());
			long chatSlowTime = 1000L * _chatSlow;
			long timeDiff = System.currentTimeMillis() - lastMessage.getTimeSent();
			if (timeDiff < chatSlowTime && !_clientManager.Get(sender).GetRank().Has(Rank.HELPER))
			{
				UtilPlayer.message(sender, F.main("Chat", "Chat slow enabled. Please wait " + F.time(UtilTime.convertString(chatSlowTime - timeDiff, 1, UtilTime.TimeUnit.FIT))));
				event.setCancelled(true);
			}
			else if (!_clientManager.Get(sender).GetRank().Has(Rank.MODERATOR) &&
					UtilText.isStringSimilar(event.getMessage(), lastMessage.getMessage(), 0.8f))
			{
				UtilPlayer.message(sender, F.main("Chat", "This message is too similar to your previous message."));
				event.setCancelled(true);
			}
		}

		if (!event.isCancelled())
			_playerLastMessage.put(sender.getUniqueId(), new MessageData(event.getMessage()));
	}

	private boolean msgContainsHack(String msg) 
	{
		msg = " " + msg.toLowerCase().replaceAll("[^a-z ]", "") + " ";
		for (String s : _hackusations) {
			if (msg.contains(" " + s + " ")) {
				return true;
			}
		}
		return false;
	}

	public String hasher(JSONArray hasharray, String message)
	{
		StringBuilder newmsg = new StringBuilder(message);

		for (int i = 0; i < hasharray.size(); i++)
		{
			Long charindex = ((Long) hasharray.get(i));
			int charidx = charindex.intValue();
			newmsg.setCharAt(charidx, '*');
		}

		return newmsg.toString();
	}

	public JSONArray parseHashes(String response)
	{
		JSONObject checkhash = (JSONObject) JSONValue.parse(response);
		JSONArray hasharray;
		hasharray = (JSONArray) checkhash.get("hashes");

		return hasharray;
	}

	public static void trustCert() throws Exception
	{
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
		{
			public java.security.cert.X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType)
			{
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType)
			{
			}

		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier()
		{
			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	public long Silenced()
	{
		return _silenced;
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event)
	{
		_playerLastMessage.remove(event.getPlayer().getUniqueId());
	}

	public void setThreeSecondDelay(boolean b) 
	{
		_threeSecondDelay = b;
	}
}
