package nautilus.game.capturethepig.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.packethandler.PacketHandler;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;

import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.core.scoreboard.LineTracker;
import nautilus.game.core.scoreboard.TabScoreboard;
import nautilus.minecraft.core.utils.TimeStuff;

public class CaptureThePigTabScoreboard extends TabScoreboard<ICaptureThePigGame, ICaptureThePigPlayer, ICaptureThePigTeam>
{
	List<ICaptureThePigPlayer> _redSortedTeamPlayers = new ArrayList<ICaptureThePigPlayer>();
	List<ICaptureThePigPlayer> _blueSortedTeamPlayers = new ArrayList<ICaptureThePigPlayer>();
	
	public CaptureThePigTabScoreboard(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, PacketHandler packetHandler, ICaptureThePigGame game) 
	{
		super(plugin, clientManager, classManager, packetHandler, game);
	}
	
	protected void SetRedTeamInfo()
	{
	    RedColumn.get(0).SetLine(ChatColor.RED + "        [RED]");
        RedColumn.get(1).SetLine(ChatColor.RED + "Score: " + ChatColor.WHITE + Game.GetRedTeam().GetScore());
        RedColumn.get(2).SetLine(ChatColor.RED + "[Top Players]");
        RedColumn.get(3).SetLine(ChatColor.RED + "     [K/D/A C]");
        
        _redSortedTeamPlayers.clear();
        _redSortedTeamPlayers.addAll(Game.GetRedTeam().GetPlayers());
        Collections.sort(_redSortedTeamPlayers, new PlayerSorter());
        
        String spacer = "";
        for (int i=0; i < 8; i++)
        {
            int lineIndex = 4 + i * 2;
            spacer += " ";
            
            if (i < _redSortedTeamPlayers.size())
            {
            	ICaptureThePigPlayer player = _redSortedTeamPlayers.get(i);
 
                ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                RedColumn.get(lineIndex).SetLine(playerColor + player.getName());
                RedColumn.get(lineIndex+1).SetLine(ChatColor.RED + "" + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetCaptures() + spacer);
            }
            else
            {
                RedColumn.get(lineIndex).SetLine(ChatColor.RED + "" + ChatColor.GREEN + spacer);
                RedColumn.get(lineIndex+1).SetLine(ChatColor.RED + "" + ChatColor.BLUE + spacer);
            }
        }
	}
	
	protected void SetMainInfo(ICaptureThePigPlayer player)
	{
	    if (!player.isOnline())
	        return;
	    
        ClientClass clientPlayer = ClassManager.Get(player.GetPlayer());
        
        if (!MainColumn.containsKey(player.getName()))
        {
        	NautHashMap<Integer, LineTracker> playerLines = new NautHashMap<Integer, LineTracker>();
            
            for (Integer i=0; i < 20; i++)
            {
                playerLines.put(i, new LineTracker(PacketHandler, "Game"));
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
        playerLines.get(17).SetLine(ChatColor.GREEN + "Captures:");
        playerLines.get(18).SetLine(player.GetCaptures() + "    ");
        playerLines.get(19).SetLine(ChatColor.GREEN + " ");
	}

	protected void SetBlueTeamInfo()
    {
        BlueColumn.get(0).SetLine(ChatColor.BLUE + "       [BLUE]");
        BlueColumn.get(1).SetLine(ChatColor.BLUE + "Score: " + ChatColor.WHITE + Game.GetBlueTeam().GetScore());
        BlueColumn.get(2).SetLine(ChatColor.BLUE + "[Top Players]");
        BlueColumn.get(3).SetLine(ChatColor.BLUE + "     [K/D/A C]");
        
        _blueSortedTeamPlayers.clear();
        _blueSortedTeamPlayers.addAll(Game.GetBlueTeam().GetPlayers());
        Collections.sort(_blueSortedTeamPlayers, new PlayerSorter());
        
        String spacer = "";
        for (int i=0; i < 8; i++)
        {
            int lineIndex = 4 + i * 2;
            spacer += " ";
            
            if (i < _blueSortedTeamPlayers.size())
            {
                ICaptureThePigPlayer player = _blueSortedTeamPlayers.get(i);
 
                ChatColor playerColor = !player.isOnline() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                BlueColumn.get(lineIndex).SetLine(playerColor + player.getName());
                BlueColumn.get(lineIndex+1).SetLine(ChatColor.BLUE + "" + player.GetKills() + "/" + player.GetDeaths() + "/" + player.GetAssists() + " " + player.GetCaptures() + spacer);
            }
            else
            {   
                BlueColumn.get(lineIndex).SetLine(ChatColor.BLUE + " " + ChatColor.GREEN + spacer);
                BlueColumn.get(lineIndex+1).SetLine(ChatColor.BLUE + " " + ChatColor.RED + spacer);
            }
        }
    }
}
