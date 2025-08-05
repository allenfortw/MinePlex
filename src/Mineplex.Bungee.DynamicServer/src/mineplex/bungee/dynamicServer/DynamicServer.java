package mineplex.bungee.dynamicServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class DynamicServer extends Plugin implements Listener
{
	@Override
	public void onEnable()
	{
		LoadServers();
		
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new ReloadServerListCommand(this));
	}
	
    public void LoadServers()
    {
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			File npcFile = new File("servers.dat");

			if (npcFile.exists())
			{
				fstream = new FileInputStream(npcFile);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					String name = line.split(",")[0];
					String address = line.split(",")[1].split(":")[0];
					Integer port = Integer.parseInt(line.split(",")[1].split(":")[1]);
					InetSocketAddress socketAddress = new InetSocketAddress(address, port);

					getProxy().getServers().put(name, getProxy().constructServerInfo(name, socketAddress, "DynamicServer", false));
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
			System.out.println("Error parsing servers file.");
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
