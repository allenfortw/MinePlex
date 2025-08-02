package mineplex.core.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import mineplex.core.server.packet.Packet;
import mineplex.core.server.remotecall.JsonWebCall;

public class ServerBroadcaster extends Thread
{
	private static Object _queueLock = new Object();
	private static Object _serverMapLock = new Object();
	
	private HashSet<String> _serverMap = new HashSet<String>();
	private List<Packet> _queue = new ArrayList<Packet>();
	
	private String _webAddress;
	private boolean _running = true;
	private boolean _retrievingServers = false;
	
	private long _updateInterval = 15000;
	private long _lastUpdate;

	private boolean _debug = false;
	
	public ServerBroadcaster(String webAddress)
	{
		_webAddress = webAddress;		
	}
	
	public void QueuePacket(Packet packet)
	{
		synchronized(_queueLock)
		{
			_queue.add(packet);
		}
	}
	
	@Override
	public void run() 
	{
		while (_running) 
		{
			if (!HasPackets() || !HasServers())
			{
				try
				{
					Thread.sleep(25);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				if (System.currentTimeMillis() - _lastUpdate > _updateInterval)
				{
					RetrieveActiveServers();
				}
				
				continue;
			}
			
			Packet packet = null;
			
			synchronized(_queueLock)
			{
				packet = _queue.remove(0);
			}

			synchronized(_serverMapLock)
			{
				for (String server : _serverMap)
				{
					Socket socket = null;
					DataOutputStream dataOutput = null;
					
					try 
					{
						socket = new Socket(server.split(":")[0], Integer.parseInt(server.split(":")[1]));
						dataOutput = new DataOutputStream(socket.getOutputStream());
						
						packet.Write(dataOutput);
						dataOutput.flush();
						
						if (_debug)
							System.out.println("Sent packet to : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
					} 
					catch (Exception ex) 
					{
						System.out.println("ServerTalker.run Exception(" + server + ") : " + ex.getMessage());
					}
					finally
					{
						try
						{
							if (dataOutput != null)
								dataOutput.close();
						} 
						catch (IOException e)
						{
							e.printStackTrace();
						}
						
						try
						{
							if (socket != null)
								socket.close();
						} 
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public boolean HasPackets()
	{
		synchronized(_queueLock)
		{
			return _queue.size() != 0;
		}
	}
	
	public boolean HasServers()
	{
		synchronized(_serverMapLock)
		{
			return _serverMap.size() != 0;
		}
	}
	
	public void PrintPackets()
	{
		System.out.println("Listing Packets:");
		
		synchronized(_queueLock)
		{
			if (_queue.isEmpty())
			{
				System.out.println("Packet queue empty!");
			}
			else
			{
				for (Packet packet : _queue)
				{
					System.out.println(packet.getClass());
				}
			}
		}
	}
	
	public void PrintServers()
	{
		System.out.println("Listing Servers:");
		
		if (_retrievingServers)
		{
			System.out.println("Retrieving servers.  Please check again in a few seconds.");
		}
		
		synchronized(_serverMapLock)
		{
			if (_serverMap.isEmpty())
			{
				System.out.println("Server list empty!");
			}
			else
			{
				for (String server : _serverMap)
				{
					System.out.println(server);
				}
			}
		}
	}
	
	private void RetrieveActiveServers()
	{
		if (_debug)
			System.out.println("Updating servers...");
		
		List<String> servers = new JsonWebCall(_webAddress + "Servers/GetServers").Execute(new TypeToken<List<String>>(){}.getType(), null);
		
		synchronized(_serverMapLock)
		{
			_serverMap.clear();
			
			if (servers.size() > 0)
			{
				for (String server : servers)
				{
					_serverMap.add(server);
				}
			}
			else
			{
				System.out.println("No servers registered at '" + _webAddress + "'!");
			}
		}
		
		_lastUpdate = System.currentTimeMillis();
	}
}
