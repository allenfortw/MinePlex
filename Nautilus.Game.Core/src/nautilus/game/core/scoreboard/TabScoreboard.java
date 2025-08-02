package nautilus.game.core.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.packethandler.IPacketRunnable;
import mineplex.core.packethandler.PacketArrayList;
import mineplex.core.packethandler.PacketHandler;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;
import nautilus.minecraft.core.utils.TimeStuff;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet201PlayerInfo;

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
		ClientManager = clientManager;
		ClassManager = classManager;
	    PacketHandler = packetHandler;
	    Game = game;
	    RedColumn = new NautHashMap<Integer, LineTracker>();
	    BlueColumn = new NautHashMap<Integer, LineTracker>();
		MainColumn = new NautHashMap<String, NautHashMap<Integer, LineTracker>>();
		
		for (Integer i=0; i < 20; i++)
		{
		    RedColumn.put(i, new LineTracker());
		}
        
        for (Integer i=0; i < 20; i++)
        {
            BlueColumn.put(i, new LineTracker());
        }
        
        PacketHandler.AddPacketRunnable(this);
	}
	
	public void Update()
	{
	    SetRedTeamInfo();
	    SetBlueTeamInfo();
	    
        for (PlayerType player : Game.GetPlayers())
        {
            UpdateForPlayer(player);
            
            if (player.isOnline())
            	SendPlayerScoreboard(player);
        }
        
        for (PlayerType player : Game.GetSpectators())
        {
        	UpdateForPlayer(player);
            SendPlayerScoreboard(player);
        }
        
        for (Integer i=0; i < 20; i++)
        {
            RedColumn.get(i).ClearOldLine();
        }
        
        for (Integer i=0; i < 20; i++)
        {
            BlueColumn.get(i).ClearOldLine();
        }
	}
	
	public void AddSpectator(PlayerType spectator) 
	{
    	UpdateForPlayer(spectator);
        SendPlayerScoreboard(spectator);
	}
	
    public void Stop()
    {
        for (Integer i=0; i < 20; i++)
        {
            RedColumn.get(i).SetLine("");
        }
        
        for (Integer i=0; i < 20; i++)
        {
            BlueColumn.get(i).SetLine("");
        }
        
        Packet201PlayerInfo clearPacket = new Packet201PlayerInfo("", false, 0);

        _updating = true;
        for (PlayerType player : Game.GetPlayers())
        {
        	NautHashMap<Integer, LineTracker> playerLines = MainColumn.get(player.getName());
            
            // If player quit as game was preparing and never reconnected...
            if (playerLines != null)
            {
	            for (Integer i=0; i < 20; i++)
	            {
	                playerLines.get(i).SetLine("");
	            }
	            
	            SendPlayerScoreboard(player);
	            
	            EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
	            
	            entityPlayer.playerConnection.sendPacket(clearPacket);
            }
        }
        
        for (PlayerType player : Game.GetSpectators())
        {
        	NautHashMap<Integer, LineTracker> playerLines = MainColumn.get(player.getName());
            
            for (Integer i=0; i < 20; i++)
            {
                playerLines.get(i).SetLine("");
            }
            
            SendPlayerScoreboard(player);
            
            EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
            
            entityPlayer.playerConnection.sendPacket(clearPacket);
        }
        _updating  = false;
        
        PacketHandler.RemovePacketRunnable(this);
    }
	
	public void UpdateForPlayer(PlayerType player)
	{
        SetMainInfo(player);
	}
	
	protected void SetRedTeamInfo()
	{
	    RedColumn.get(0).SetLine(ChatColor.RED + "        [RED]");
        RedColumn.get(1).SetLine(ChatColor.RED + "Score: " + ChatColor.WHITE + Game.GetRedTeam().GetScore());
        RedColumn.get(8).SetLine(ChatColor.RED + "     [Players]");
        RedColumn.get(9).SetLine(ChatColor.RED + "     [K/D/A S]");
        
        String spacer = "";
        for (int i=0; i < 5; i++)
        {
            int lineIndex = 10 + i * 2;
            spacer += " ";
            
            if (i < Game.GetRedTeam().GetPlayers().size())
            {
                PlayerType player = Game.GetRedTeam().GetPlayers().get(i);
 
                ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                RedColumn.get(lineIndex).SetLine(playerColor + player.getName());
                RedColumn.get(lineIndex+1).SetLine(ChatColor.RED + "" + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetPoints() + spacer);
            }
            else
            {
                RedColumn.get(lineIndex).SetLine(ChatColor.RED + "" + ChatColor.GREEN + spacer);
                RedColumn.get(lineIndex+1).SetLine(ChatColor.RED + "" + ChatColor.BLUE + spacer);
            }
        }
	}
	
	protected void SetMainInfo(PlayerType player)
	{
	    if (!player.isOnline())
	        return;
	    
        ClientClass clientPlayer = ClassManager.Get(player.GetPlayer());
        
        if (!MainColumn.containsKey(player.getName()))
        {
        	NautHashMap<Integer, LineTracker> playerLines = new NautHashMap<Integer, LineTracker>();
            
            for (Integer i=0; i < 20; i++)
            {
                playerLines.put(i, new LineTracker());
            }
            
            MainColumn.put(player.getName(), playerLines);
        }
        
        NautHashMap<Integer, LineTracker> playerLines = MainColumn.get(player.getName());
        
        playerLines.get(0).SetLine(ChatColor.GREEN + "    [Dominate]");
        playerLines.get(1).SetLine(ChatColor.GREEN + "Map:");
        playerLines.get(2).SetLine(Game.GetArena().GetName());
        playerLines.get(3).SetLine(ChatColor.GREEN + "Win Limit:");
        playerLines.get(4).SetLine(Game.GetWinLimit() + "");
        playerLines.get(5).SetLine(ChatColor.GREEN + "Duration:");
        playerLines.get(6).SetLine((Game.GetStartTime() == 0 ? "0" : TimeStuff.GetTimespanString(System.currentTimeMillis() - Game.GetStartTime())));
        playerLines.get(7).SetLine(ChatColor.GREEN + "");        
        playerLines.get(8).SetLine(ChatColor.GREEN + "       [Stats]");
        playerLines.get(9).SetLine(ChatColor.GREEN + "Class:" );
        playerLines.get(10).SetLine((clientPlayer.GetGameClass() == null ? "None" : clientPlayer.GetGameClass().GetName()));
        playerLines.get(11).SetLine(ChatColor.GREEN + "Kills:");
        playerLines.get(12).SetLine(player.GetKills() + " ");
        playerLines.get(13).SetLine(ChatColor.GREEN + "Deaths:");
        playerLines.get(14).SetLine(player.GetDeaths() + "  ");
        playerLines.get(15).SetLine(ChatColor.GREEN + "Assists:");
        playerLines.get(16).SetLine(player.GetAssists() + "   ");
        playerLines.get(17).SetLine(ChatColor.GREEN + "Score:");
        playerLines.get(18).SetLine(player.GetPoints() + "    ");
        playerLines.get(19).SetLine(ChatColor.GREEN + " ");
	}
	
	protected void SetBlueTeamInfo()
    {
        BlueColumn.get(0).SetLine(ChatColor.BLUE + "       [BLUE]");
        BlueColumn.get(1).SetLine(ChatColor.BLUE + "Score: " + ChatColor.WHITE + Game.GetBlueTeam().GetScore());
        BlueColumn.get(8).SetLine(ChatColor.BLUE + "     [Players]");
        BlueColumn.get(9).SetLine(ChatColor.BLUE + "     [K/D/A S]");
        
        String spacer = "";
        for (int i=0; i < 5; i++)
        {
            int lineIndex = 10 + i * 2;
            spacer += " ";
            
            if (i < Game.GetBlueTeam().GetPlayers().size())
            {
                PlayerType player = Game.GetBlueTeam().GetPlayers().get(i);
 
                ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                BlueColumn.get(lineIndex).SetLine(playerColor + player.getName());
                BlueColumn.get(lineIndex+1).SetLine(ChatColor.BLUE + "" + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetPoints() + spacer);
            }
            else
            {   
                BlueColumn.get(lineIndex).SetLine(ChatColor.BLUE + " " + ChatColor.GREEN + spacer);
                BlueColumn.get(lineIndex+1).SetLine(ChatColor.BLUE + " " + ChatColor.RED + spacer);
            }
        }
    }

	public void ClearScoreboardForSpectator(PlayerType player)
	{
		EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
		NautHashMap<Integer, LineTracker> playersLines = MainColumn.get(player.getName());
		
		if (playersLines == null)
			return;
		
		_updating = true;
        for (Integer i=0; i < 20; i++)
        {
            RedColumn.get(i).RemoveLineForPlayer(entityPlayer);
            playersLines.get(i).RemoveLineForPlayer(entityPlayer);
            BlueColumn.get(i).RemoveLineForPlayer(entityPlayer);
        }
        _updating = false;
        
        MainColumn.remove(player.getName());
	}
	
	public void SendPlayerScoreboard(PlayerType player)
	{
		EntityPlayer entityPlayer = ((CraftPlayer)player.GetPlayer()).getHandle();
		NautHashMap<Integer, LineTracker> playersLines = MainColumn.get(player.getName());
		
		_updating = true;
		for (int i=0; i < 20; i++)
		{
		    RedColumn.get(i).DisplayLineToPlayer(entityPlayer);
		    playersLines.get(i).DisplayLineToPlayer(entityPlayer);
		    BlueColumn.get(i).DisplayLineToPlayer(entityPlayer);
		    
		    playersLines.get(i).ClearOldLine();
		}
		_updating = false;
	}

	@Override
	public boolean run(Packet packet, Player owner, PacketArrayList packetList)
	{
		if (packet instanceof Packet201PlayerInfo)
		{
			if (Game.IsPlayerInGame(owner) && Game.IsActive())
				return _updating;
		}
		
		return true;
	}
}
