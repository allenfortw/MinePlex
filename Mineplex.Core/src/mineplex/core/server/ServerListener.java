package mineplex.core.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import mineplex.core.server.packet.IPacketHandler;
import mineplex.core.server.packet.PacketType;
import mineplex.core.server.remotecall.JsonWebCall;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ServerListener extends Thread
{	
	private IPacketHandler _packetHandler;
	private final String _host;
	private final int _port;
	
	private String _webServer;

	private ServerSocket server;

	private boolean running = true;

	public ServerListener(IPacketHandler packetHandler, String webserver, String host, int port)
	{
		_packetHandler = packetHandler;
		_webServer = webserver;
		
		_host = host;
		_port = port;

		Initialize();
		
		System.out.println("Initialized ServerListener");
		
		new JsonWebCall(_webServer + "Servers/RegisterServer").Execute(host + ":" + port);
		
		System.out.println("Finished with constructor");
	}
	
	public ServerListener(String webserver, String host, int port)
	{
		this(null, webserver, host, port);
	}

	private void Initialize()
	{
		try 
		{
			server = new ServerSocket();
			server.bind(new InetSocketAddress(_host, _port));
			System.out.println("Listening to " + _host + ":" + _port);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	public void Shutdown() 
	{
		running = false;
		
		if (server == null)
			return;
		
		try 
		{
			server.close();
		} 
		catch (Exception ex) 
		{
			System.out.println("ServerListener.Shutdown Exception : " + ex.getMessage());
		}
		
		new JsonWebCall(_webServer + "Servers/RemoveServer").Execute(_host + ":" + _port);
	}

	@Override
	public void run() 
	{
		while (running) 
		{
			Socket socket = null;
			DataInputStream dataInput = null;
			
			try 
			{
				socket = server.accept();
				socket.setSoTimeout(5000);
				dataInput = new DataInputStream(socket.getInputStream());

				if (_packetHandler != null)
				{
					_packetHandler.HandlePacketEvent(PacketType.GetPacketEventById(dataInput.readShort(), dataInput), socket);
				}
				else
				{
					PluginManager pluginManager = Bukkit.getPluginManager();
					
					if (pluginManager != null)
						Bukkit.getPluginManager().callEvent(PacketType.GetPacketEventById(dataInput.readShort(), dataInput));
				}
				System.out.println("received packet");
			} 
			catch (Exception ex) 
			{
				System.out.println("ServerListener.run Exception : " + ex.getMessage());
				try
				{
					throw ex;
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			finally
			{
				try
				{
					if (dataInput != null)
						dataInput.close();
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