package mineplex.bungee.PlayerCount;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class PlayerCount extends Plugin implements Listener, Runnable
{
	private HashMap<String, InetSocketAddress> _otherBungeeInstances;
	private int _totalPlayers;
	private int _totalMaxPlayers;
	
	private int _tempPlayers;
	private int _tempMaxPlayers;
	
	@Override
	public void onEnable()
	{
		_otherBungeeInstances = new HashMap<String, InetSocketAddress>();
		
		LoadBungeeServers();
		
		getProxy().getScheduler().schedule(this, this, 1L, 1L, TimeUnit.SECONDS);
		getProxy().getPluginManager().registerListener(this, this);
		
		getProxy().getPluginManager().registerCommand(this, new ReloadPlayerCountListCommand(this));
	}
	
	public void run()
	{
		_tempPlayers = getProxy().getOnlineCount();
		_tempMaxPlayers = 0;
		
        for(ListenerInfo li : getProxy().getConfigurationAdapter().getListeners())
        {
        	_tempMaxPlayers += li.getMaxPlayers();
        }

        for (InetSocketAddress address : _otherBungeeInstances.values())
        {
			try
			{
				UpdateServerCount(address);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
        }
        
        _totalPlayers = _tempPlayers;
        _totalMaxPlayers = _tempMaxPlayers;
	}
	
	@EventHandler
	public void ServerPing(ProxyPingEvent event)
	{
		if (!_otherBungeeInstances.containsKey(event.getConnection().getAddress().getAddress().getHostAddress()))
		{
			net.md_5.bungee.api.ServerPing serverPing = event.getResponse();
			event.setResponse(new net.md_5.bungee.api.ServerPing(serverPing.getProtocolVersion(), serverPing.getGameVersion(), serverPing.getMotd(), _totalPlayers, _totalMaxPlayers));
		}
	}
	
    protected void UpdateServerCount(InetSocketAddress address) throws IOException
	{
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
                
                _tempPlayers += Integer.parseInt(var27[4]);
                _tempMaxPlayers += Integer.parseInt(var27[5]);
            }
        }
        catch (SocketTimeoutException e)
        {
        	;
        }
        catch (ConnectException e)
        {
        	;
        }
        catch (IOException e)
        {
            System.out.println("[BungeeSigns] Error pinging " + address.getHostString() + ":" + address.getPort());
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
    
    public void LoadBungeeServers()
    {
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			File npcFile = new File("bungeeServers.dat");

			if (npcFile.exists())
			{
				fstream = new FileInputStream(npcFile);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					String address = line.split(":")[0];
					Integer port = Integer.parseInt(line.split(":")[1]);
					InetSocketAddress socketAddress = new InetSocketAddress(address, port);
					_otherBungeeInstances.put(socketAddress.getAddress().getHostAddress(), socketAddress);
					
					line = br.readLine();
				}
			}
			else
			{
				npcFile.createNewFile();
			}
		}
		catch (Exception e)
		{
			System.out.println("Error parsing bungeeServers file.");
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
    }
}
