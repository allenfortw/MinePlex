package mineplex.core.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import mineplex.core.server.packet.Packet;

public class ServerTalker extends Thread
{
	private static Object _queueLock = new Object();
	
	private List<Packet> _queue = new ArrayList<Packet>();
	
	private String _serverAddress;
	private boolean _running = true;

	private boolean _debug = false;
	
	public ServerTalker(String serverAddress)
	{
		_serverAddress = serverAddress;		
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
			if (!HasPackets())
			{
				try
				{
					Thread.sleep(25);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				continue;
			}
			
			Packet packet = null;
			
			synchronized(_queueLock)
			{
				packet = _queue.remove(0);
			}

			Socket socket = null;
			DataOutputStream dataOutput = null;
				
			try 
			{
				socket = new Socket(_serverAddress.split(":")[0], Integer.parseInt(_serverAddress.split(":")[1]));
				dataOutput = new DataOutputStream(socket.getOutputStream());
				
				packet.Write(dataOutput);
				dataOutput.flush();
				
				if (_debug)
					System.out.println("Sent packet to : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
			} 
			catch (Exception ex) 
			{
				System.out.println("ServerTalker.run Exception(" + _serverAddress + ") : " + ex.getMessage());
				_queue.add(packet);
				
				try
				{
					Thread.sleep(15000);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
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
	
	public boolean HasPackets()
	{
		synchronized(_queueLock)
		{
			return _queue.size() != 0;
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
}
