package nautilus.game.core.scoreboard;

import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.Packet201PlayerInfo;

public class LineTracker
{
  private String _line = null;
  private String _oldLine = null;
  private Packet201PlayerInfo _clearOldPacket;
  private Packet201PlayerInfo _addNewPacket;
  private Packet201PlayerInfo _clearNewPacket;
  
  public LineTracker()
  {
    this._line = null;
  }
  
  public void SetLine(String s)
  {
    if ((s != null) && (s.length() > 16)) {
      s = s.substring(0, 16);
    }
    this._oldLine = this._line;
    this._line = s;
    
    if (this._oldLine != null)
    {
      this._clearOldPacket = new Packet201PlayerInfo(this._oldLine, false, 0);
    }
    
    if (this._line != null)
    {
      this._addNewPacket = new Packet201PlayerInfo(this._line, true, 0);
      this._clearNewPacket = new Packet201PlayerInfo(this._line, false, 0);
    }
  }
  
  public void DisplayLineToPlayer(EntityPlayer entityPlayer)
  {
    if (this._oldLine != null)
    {
      entityPlayer.playerConnection.sendPacket(this._clearOldPacket);
    }
    
    if (this._line != null)
    {
      entityPlayer.playerConnection.sendPacket(this._addNewPacket);
    }
  }
  
  public void RemoveLineForPlayer(EntityPlayer entityPlayer)
  {
    if (this._line != null)
    {
      entityPlayer.playerConnection.sendPacket(this._clearNewPacket);
    }
  }
  
  public void ClearOldLine()
  {
    this._oldLine = null;
  }
}
