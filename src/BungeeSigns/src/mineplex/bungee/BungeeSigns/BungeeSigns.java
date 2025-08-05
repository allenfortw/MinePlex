package mineplex.bungee.BungeeSigns;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PacketFAPluginMessage;

public class BungeeSigns extends Plugin implements Listener
{
	@Override
	public void onEnable()
	{
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().registerChannel("BungeeSigns");
	}
	
	@EventHandler
	public void ReceiveServerRequest(final PluginMessageEvent event)
	{
		if (event.getTag().equals("BungeeSigns"))
		{
			DataInputStream in = null;
			
			try
			{
				in = new DataInputStream(new ByteArrayInputStream(event.getData()));
				final ServerInfo serverInfo = getProxy().getServerInfo(in.readUTF());

				in.close();
				
				if (serverInfo != null)
				{
					new Thread() {
						public void run()
						{
							SendServerInfo(serverInfo, event.getSender());
						}
					}.start();
				}
			} 
			catch (IOException e)
			{
				System.out.println("[BungeeSigns] Error retrieving serverInfo.");
			}
			finally
			{
				try
				{
					if (in != null)
					{
						in.close();
					}
				}
				catch (Exception e)
				{
					
				}
			}
		}
	}
	
    protected void SendServerInfo(ServerInfo serverInfo, Connection sender)
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
            socket.connect(serverInfo.getAddress(), 3000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            
            dataOutputStream.writeByte(254);
            dataOutputStream.writeByte(1);
            dataOutputStream.writeByte(254);
            writeString("MC|PingHost", dataOutputStream);
            dataOutputStream.writeShort(3 + 2 * serverInfo.getAddress().getHostString().length() + 4);
            dataOutputStream.writeByte(74);
            writeString(serverInfo.getAddress().getHostString(), dataOutputStream);
            dataOutputStream.writeInt(serverInfo.getAddress().getPort());

            if (dataInputStream.read() != 255)
            {
                return;
            }

            String var6 = readString(dataInputStream, 256);
            
            String[] var27;

            if (var6.startsWith("\u00a7") && var6.length() > 1)
            {
                var27 = var6.substring(1).split("\u0000");
                
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                
        		out.writeUTF(serverInfo.getName());
        		out.writeUTF(var27[3]);
        		out.writeInt(Integer.parseInt(var27[4]));
        		out.writeInt(Integer.parseInt(var27[5]));
        		
        		byte[] b = out.toByteArray();
        		
        		if (b.length != 0)
        		{
        			sender.unsafe().sendPacket(new PacketFAPluginMessage("BungeeSigns", b));
        		}
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
        catch(IOException e)
        {
            System.out.println("[BungeeSigns] Error pinging " + serverInfo.getName() + "(" + serverInfo.getAddress().getHostString() + ":" + serverInfo.getAddress().getPort());
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
}
