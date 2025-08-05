package nautilus.game.lobby.ServerMenu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import me.chiss.Core.Portal.Portal;
import mineplex.core.MiniPlugin;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

public class ServerInfoManager extends MiniPlugin implements PluginMessageListener
{
	private int _domPlayerCount;
	private int _pvpPlayerCount;
	private int _mk64PlayerCount;
	private int _tutPlayerCount;
	
	private ServerMenu _serverMenu;
	
	public ServerInfoManager(JavaPlugin plugin, Portal portal)
	{
		super("ServerInfoManager", plugin);
		
		_serverMenu = new ServerMenu(this, portal);
		
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(GetPlugin(), "BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(GetPlugin(), "BungeeCord", this);
	}
	
	/*
	@EventHandler
	public void PlayerCountUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (GetPlugin().getServer().getOnlinePlayers().length > 0)
		{
			Player player = GetPlugin().getServer().getOnlinePlayers()[0];
			SendServerPlayerCountRequest(player, "dom");
			SendServerPlayerCountRequest(player, "pvp");
			SendServerPlayerCountRequest(player, "mk64");
			SendServerPlayerCountRequest(player, "tut");
			
			_serverMenu.UpdatePages();
		}
	}
*/
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
        if (!channel.equals("BungeeCord"))
            return;
        
        DataInputStream in = null;
        String subchannel = null;
		try
		{
			in = new DataInputStream(new ByteArrayInputStream(message));
			subchannel = in.readUTF();

	        if (subchannel.equals("PlayerCount")) 
	        {
	            switch (in.readUTF())
	            {
	            	case "dom":
	            		_domPlayerCount = in.readInt();
	            		break;
	            	case "pvp":
	            		_pvpPlayerCount = in.readInt();
	            		break;
	            	case "mk64":
	            		_mk64PlayerCount = in.readInt();
	            		break;
	            	case "tut":
	            		_tutPlayerCount = in.readInt();
	            		break;
	            }
	        }
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				in.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public int GetDomPlayerCount()
	{
		return _domPlayerCount;
	}
	
	public int GetPvpPlayerCount()
	{
		return _pvpPlayerCount;
	}
	
	public int GetMK64PlayerCount()
	{
		return _mk64PlayerCount;
	}
	
	public int GetTutPlayerCount()
	{
		return _tutPlayerCount;
	}
	
	private void SendServerPlayerCountRequest(Player player, String server)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try 
		{
		    out.writeUTF("PlayerCount");
		    out.writeUTF(server);
		}
		catch (IOException e) 
		{
		    // Can never happen
		}
		finally
		{
			try
			{
				out.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		player.sendPluginMessage(GetPlugin(), "BungeeCord", b.toByteArray());
	}
}
