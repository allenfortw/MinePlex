package mineplex.bungee.lobbyBalancer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class LobbyBalancer extends Plugin implements Listener, Runnable
{
	private HashMap<String, Integer> _lobbyServers = new HashMap<String, Integer>();

	@Override
	public void onEnable()
	{
		loadLobbyServers();
		
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getScheduler().schedule(this, this, 1L, 1L, TimeUnit.SECONDS);
		
		getProxy().getPluginManager().registerCommand(this, new ReloadLobbyServerListCommand(this));
	}
	
	@EventHandler
	public void playerConnect(ServerConnectEvent event)
	{
		if (!event.getTarget().getName().equalsIgnoreCase("Lobby"))
			return;
		
		String bestServer = null;
		Entry<String, Integer> leastPlayerServer = null;
		
    	for (Entry<String, Integer> entry : _lobbyServers.entrySet())
    	{
    		if (entry.getValue() == 999)
    			continue;
    		
    		if (bestServer == null)
    		{
    			bestServer = entry.getKey();
    			leastPlayerServer = entry;
    		}
    		else if (entry.getValue() > _lobbyServers.get(bestServer) && entry.getValue() < 80)
    			bestServer = entry.getKey();
    		
    		if (entry.getValue() < leastPlayerServer.getValue())
    		{
    			leastPlayerServer = entry;
    		}
    	}
    	
    	if  (_lobbyServers.get(bestServer) > 80)
    	{
    		bestServer = leastPlayerServer.getKey();
    	}
    	
    	event.setTarget(getProxy().getServerInfo(bestServer));
	}
	
	public void run()
	{
        for (String name : _lobbyServers.keySet())
        {
			try
			{
				UpdateServerCount(name);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
        }
	}
    
    protected void UpdateServerCount(String name) throws IOException
	{
		InetSocketAddress address = getProxy().getServerInfo(name).getAddress();
		
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        
        try
        {
            socket = new Socket();
            socket.setSoTimeout(3000);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(18);
            socket.connect(address, 3000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            
            dataOutputStream.writeByte(254);
            dataOutputStream.writeByte(1);
            dataOutputStream.writeByte(254); 
            writeString("MC|PingHost", dataOutputStream);
            dataOutputStream.writeShort(3 + 2 * address.getAddress().getHostName().length() + 4);
            dataOutputStream.writeByte(73);
            writeString(address.getAddress().getHostName(), dataOutputStream);
            dataOutputStream.writeInt(address.getPort());

            if (dataInputStream.read() != 255)
            {
            	System.out.println("not 255");
                return;
            }

            String var6 = readString(dataInputStream, 256);
            
            String[] var27;

            if (var6.startsWith("\u00a7") && var6.length() > 1)
            {
                var27 = var6.substring(1).split("\u0000");
                
                if (var27[3].contains("Restarting"))
                	_lobbyServers.put(name, 999);
                else
                	_lobbyServers.put(name, Integer.parseInt(var27[4]));
            }
        }
        catch (SocketTimeoutException e)
        {
        	_lobbyServers.put(name, 999);
        }
        catch (ConnectException e)
        {
        	_lobbyServers.put(name, 999);
        }
        catch (IOException e)
        {
            System.out.println("[LobbyBalancer IOException] Error pinging " + address.getHostString() + ":" + address.getPort());
            _lobbyServers.put(name, 999);
            throw e;
        }
        finally
        {
            try
            {
                if (dataInputStream != null)
                {
                    dataInputStream.close();
                }
            }
            catch (Exception exception)
            {
                ;
            }

            try
            {
                if (dataOutputStream != null)
                {
                    dataOutputStream.close();
                }
            }
            catch (Exception exception)
            {
                ;
            }

            try
            {
                if (socket != null)
                {
                    socket.close();
                }
            }
            catch (Exception exception)
            {
                ;
            }
        }
    }
    
    public static void writeString(String par0Str, DataOutput par1DataOutput) throws IOException
    {
        if (par0Str.length() > 32767)
        {
            throw new IOException("String too big");
        }
        else
        {
            par1DataOutput.writeShort(par0Str.length());
            par1DataOutput.writeChars(par0Str);
        }
    }
    
    public static String readString(DataInput par0DataInput, int par1) throws IOException
    {
        short var2 = par0DataInput.readShort();

        if (var2 > par1)
        {
            throw new IOException("Received string length longer than maximum allowed (" + var2 + " > " + par1 + ")");
        }
        else if (var2 < 0)
        {
            throw new IOException("Received string length is less than zero! Weird string!");
        }
        else
        {
            StringBuilder var3 = new StringBuilder();

            for (int var4 = 0; var4 < var2; ++var4)
            {
                var3.append(par0DataInput.readChar());
            }

            return var3.toString();
        }
    }
    
	public void loadLobbyServers()
	{
		_lobbyServers.clear();
		
    	for (String key : getProxy().getServers().keySet())
    	{
    		if (key.toUpperCase().contains("LOBBY"))
    		{
    			_lobbyServers.put(key, 0);
    		}
    	}
	}
}
