package nautilus.game.core.scoreboard;

import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.packethandler.IPacketRunnable;
import mineplex.core.packethandler.PacketArrayList;
import mineplex.core.packethandler.PacketHandler;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet201PlayerInfo;
import net.minecraft.server.v1_6_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TabScoreboard<GameType extends ITeamGame<?, PlayerType, TeamType>, PlayerType extends ITeamGamePlayer<TeamType>, TeamType extends ITeam<PlayerType>> implements IPacketRunnable
{
  protected CoreClientManager ClientManager;
  protected ClassManager ClassManager;
  protected PacketHandler PacketHandler;
  protected NautHashMap<String, NautHashMap<Integer, LineTracker>> MainColumn;
  protected GameType Game;
  protected NautHashMap<Integer, LineTracker> RedColumn;
  protected NautHashMap<Integer, LineTracker> BlueColumn;
  private boolean _updating = false;
  
  public TabScoreboard(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, PacketHandler packetHandler, GameType game)
  {
    this.ClientManager = clientManager;
    this.ClassManager = classManager;
    this.PacketHandler = packetHandler;
    this.Game = game;
    this.RedColumn = new NautHashMap();
    this.BlueColumn = new NautHashMap();
    this.MainColumn = new NautHashMap();
    
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      this.RedColumn.put(i, new LineTracker());
    }
    
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      this.BlueColumn.put(i, new LineTracker());
    }
    
    this.PacketHandler.AddPacketRunnable(this);
  }
  
  public void Update()
  {
    SetRedTeamInfo();
    SetBlueTeamInfo();
    
    for (PlayerType player : this.Game.GetPlayers())
    {
      UpdateForPlayer(player);
      
      if (player.isOnline()) {
        SendPlayerScoreboard(player);
      }
    }
    for (PlayerType player : this.Game.GetSpectators())
    {
      UpdateForPlayer(player);
      SendPlayerScoreboard(player);
    }
    
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      ((LineTracker)this.RedColumn.get(i)).ClearOldLine();
    }
    
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      ((LineTracker)this.BlueColumn.get(i)).ClearOldLine();
    }
  }
  
  public void AddSpectator(PlayerType spectator)
  {
    UpdateForPlayer(spectator);
    SendPlayerScoreboard(spectator);
  }
  
  public void Stop()
  {
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      ((LineTracker)this.RedColumn.get(i)).SetLine("");
    }
    
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      ((LineTracker)this.BlueColumn.get(i)).SetLine("");
    }
    
    Packet201PlayerInfo clearPacket = new Packet201PlayerInfo("", false, 0);
    
    this._updating = true;
    for (PlayerType player : this.Game.GetPlayers())
    {
      NautHashMap<Integer, LineTracker> playerLines = (NautHashMap)this.MainColumn.get(player.getName());
      

      if (playerLines != null)
      {
        for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
        {
          ((LineTracker)playerLines.get(i)).SetLine("");
        }
        
        SendPlayerScoreboard(player);
        
        EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
        
        entityPlayer.playerConnection.sendPacket(clearPacket);
      }
    }
    
    for (PlayerType player : this.Game.GetSpectators())
    {
      NautHashMap<Integer, LineTracker> playerLines = (NautHashMap)this.MainColumn.get(player.getName());
      
      for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
      {
        ((LineTracker)playerLines.get(i)).SetLine("");
      }
      
      SendPlayerScoreboard(player);
      
      EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
      
      entityPlayer.playerConnection.sendPacket(clearPacket);
    }
    this._updating = false;
    
    this.PacketHandler.RemovePacketRunnable(this);
  }
  
  public void UpdateForPlayer(PlayerType player)
  {
    SetMainInfo(player);
  }
  
  protected void SetRedTeamInfo()
  {
    ((LineTracker)this.RedColumn.get(Integer.valueOf(0))).SetLine(ChatColor.RED + "        [RED]");
    ((LineTracker)this.RedColumn.get(Integer.valueOf(1))).SetLine(ChatColor.RED + "Score: " + ChatColor.WHITE + this.Game.GetRedTeam().GetScore());
    ((LineTracker)this.RedColumn.get(Integer.valueOf(8))).SetLine(ChatColor.RED + "     [Players]");
    ((LineTracker)this.RedColumn.get(Integer.valueOf(9))).SetLine(ChatColor.RED + "     [K/D/A S]");
    
    String spacer = "";
    for (int i = 0; i < 5; i++)
    {
      int lineIndex = 10 + i * 2;
      spacer = spacer + " ";
      
      if (i < this.Game.GetRedTeam().GetPlayers().size())
      {
        PlayerType player = (ITeamGamePlayer)this.Game.GetRedTeam().GetPlayers().get(i);
        
        ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex))).SetLine(playerColor + player.getName());
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex + 1))).SetLine(ChatColor.RED + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetPoints() + spacer);
      }
      else
      {
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.RED + ChatColor.GREEN + spacer);
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex + 1))).SetLine(ChatColor.RED + ChatColor.BLUE + spacer);
      }
    }
  }
  
  protected void SetMainInfo(PlayerType player)
  {
    if (!player.isOnline()) {
      return;
    }
    ClientClass clientPlayer = (ClientClass)this.ClassManager.Get(player.GetPlayer());
    
    if (!this.MainColumn.containsKey(player.getName()))
    {
      NautHashMap<Integer, LineTracker> playerLines = new NautHashMap();
      
      for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
      {
        playerLines.put(i, new LineTracker());
      }
      
      this.MainColumn.put(player.getName(), playerLines);
    }
    
    NautHashMap<Integer, LineTracker> playerLines = (NautHashMap)this.MainColumn.get(player.getName());
    
    ((LineTracker)playerLines.get(Integer.valueOf(0))).SetLine(ChatColor.GREEN + "    [Dominate]");
    ((LineTracker)playerLines.get(Integer.valueOf(1))).SetLine(ChatColor.GREEN + "Map:");
    ((LineTracker)playerLines.get(Integer.valueOf(2))).SetLine(((ITeamArena)this.Game.GetArena()).GetName());
    ((LineTracker)playerLines.get(Integer.valueOf(3))).SetLine(ChatColor.GREEN + "Win Limit:");
    ((LineTracker)playerLines.get(Integer.valueOf(4))).SetLine(this.Game.GetWinLimit());
    ((LineTracker)playerLines.get(Integer.valueOf(5))).SetLine(ChatColor.GREEN + "Duration:");
    ((LineTracker)playerLines.get(Integer.valueOf(6))).SetLine(this.Game.GetStartTime() == 0L ? "0" : nautilus.minecraft.core.utils.TimeStuff.GetTimespanString(System.currentTimeMillis() - this.Game.GetStartTime()));
    ((LineTracker)playerLines.get(Integer.valueOf(7))).SetLine(ChatColor.GREEN);
    ((LineTracker)playerLines.get(Integer.valueOf(8))).SetLine(ChatColor.GREEN + "       [Stats]");
    ((LineTracker)playerLines.get(Integer.valueOf(9))).SetLine(ChatColor.GREEN + "Class:");
    ((LineTracker)playerLines.get(Integer.valueOf(10))).SetLine(clientPlayer.GetGameClass() == null ? "None" : clientPlayer.GetGameClass().GetName());
    ((LineTracker)playerLines.get(Integer.valueOf(11))).SetLine(ChatColor.GREEN + "Kills:");
    ((LineTracker)playerLines.get(Integer.valueOf(12))).SetLine(player.GetKills() + " ");
    ((LineTracker)playerLines.get(Integer.valueOf(13))).SetLine(ChatColor.GREEN + "Deaths:");
    ((LineTracker)playerLines.get(Integer.valueOf(14))).SetLine(player.GetDeaths() + "  ");
    ((LineTracker)playerLines.get(Integer.valueOf(15))).SetLine(ChatColor.GREEN + "Assists:");
    ((LineTracker)playerLines.get(Integer.valueOf(16))).SetLine(player.GetAssists() + "   ");
    ((LineTracker)playerLines.get(Integer.valueOf(17))).SetLine(ChatColor.GREEN + "Score:");
    ((LineTracker)playerLines.get(Integer.valueOf(18))).SetLine(player.GetPoints() + "    ");
    ((LineTracker)playerLines.get(Integer.valueOf(19))).SetLine(ChatColor.GREEN + " ");
  }
  
  protected void SetBlueTeamInfo()
  {
    ((LineTracker)this.BlueColumn.get(Integer.valueOf(0))).SetLine(ChatColor.BLUE + "       [BLUE]");
    ((LineTracker)this.BlueColumn.get(Integer.valueOf(1))).SetLine(ChatColor.BLUE + "Score: " + ChatColor.WHITE + this.Game.GetBlueTeam().GetScore());
    ((LineTracker)this.BlueColumn.get(Integer.valueOf(8))).SetLine(ChatColor.BLUE + "     [Players]");
    ((LineTracker)this.BlueColumn.get(Integer.valueOf(9))).SetLine(ChatColor.BLUE + "     [K/D/A S]");
    
    String spacer = "";
    for (int i = 0; i < 5; i++)
    {
      int lineIndex = 10 + i * 2;
      spacer = spacer + " ";
      
      if (i < this.Game.GetBlueTeam().GetPlayers().size())
      {
        PlayerType player = (ITeamGamePlayer)this.Game.GetBlueTeam().GetPlayers().get(i);
        
        ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex))).SetLine(playerColor + player.getName());
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex + 1))).SetLine(ChatColor.BLUE + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetPoints() + spacer);
      }
      else
      {
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.BLUE + " " + ChatColor.GREEN + spacer);
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex + 1))).SetLine(ChatColor.BLUE + " " + ChatColor.RED + spacer);
      }
    }
  }
  
  public void ClearScoreboardForSpectator(PlayerType player)
  {
    EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
    NautHashMap<Integer, LineTracker> playersLines = (NautHashMap)this.MainColumn.get(player.getName());
    
    if (playersLines == null) {
      return;
    }
    this._updating = true;
    for (Integer i = Integer.valueOf(0); i.intValue() < 20; i = Integer.valueOf(i.intValue() + 1))
    {
      ((LineTracker)this.RedColumn.get(i)).RemoveLineForPlayer(entityPlayer);
      ((LineTracker)playersLines.get(i)).RemoveLineForPlayer(entityPlayer);
      ((LineTracker)this.BlueColumn.get(i)).RemoveLineForPlayer(entityPlayer);
    }
    this._updating = false;
    
    this.MainColumn.remove(player.getName());
  }
  
  public void SendPlayerScoreboard(PlayerType player)
  {
    EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
    NautHashMap<Integer, LineTracker> playersLines = (NautHashMap)this.MainColumn.get(player.getName());
    
    this._updating = true;
    for (int i = 0; i < 20; i++)
    {
      ((LineTracker)this.RedColumn.get(Integer.valueOf(i))).DisplayLineToPlayer(entityPlayer);
      ((LineTracker)playersLines.get(Integer.valueOf(i))).DisplayLineToPlayer(entityPlayer);
      ((LineTracker)this.BlueColumn.get(Integer.valueOf(i))).DisplayLineToPlayer(entityPlayer);
      
      ((LineTracker)playersLines.get(Integer.valueOf(i))).ClearOldLine();
    }
    this._updating = false;
  }
  

  public boolean run(Packet packet, Player owner, PacketArrayList packetList)
  {
    if ((packet instanceof Packet201PlayerInfo))
    {
      if ((this.Game.IsPlayerInGame(owner)) && (this.Game.IsActive())) {
        return this._updating;
      }
    }
    return true;
  }
}
