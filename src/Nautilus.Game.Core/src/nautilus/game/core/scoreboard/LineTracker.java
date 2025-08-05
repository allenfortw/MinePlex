package nautilus.game.core.scoreboard;

import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet201PlayerInfo;

public class LineTracker 
{
	private String _line = null;
	private String _oldLine = null;
	private Packet201PlayerInfo _clearOldPacket;
	private Packet201PlayerInfo _addNewPacket;
	private Packet201PlayerInfo _clearNewPacket;
	
	public LineTracker()
	{
	    _line = null;
	}
	
	public void SetLine(String s)
	{		
		if (s != null && s.length() > 16)
			s = s.substring(0, 16);
		
		_oldLine = _line;
		_line = s;
		
		if (_oldLine != null)
		{
			_clearOldPacket = new Packet201PlayerInfo(_oldLine, false, 0);			
		}
			
		if (_line != null)
		{
			_addNewPacket = new Packet201PlayerInfo(_line, true, 0);
			_clearNewPacket = new Packet201PlayerInfo(_line, false, 0);
		}
	}
	
	public void DisplayLineToPlayer(EntityPlayer entityPlayer)
	{
		if (_oldLine != null)
		{
			entityPlayer.playerConnection.sendPacket(_clearOldPacket);
		}
			
		if (_line != null)
		{
			entityPlayer.playerConnection.sendPacket(_addNewPacket);
		}
	}
	
	public void RemoveLineForPlayer(EntityPlayer entityPlayer)
	{
		if (_line != null)
		{
			entityPlayer.playerConnection.sendPacket(_clearNewPacket);
		}
	}
	
	public void ClearOldLine()
	{
		_oldLine = null;
	}
}
