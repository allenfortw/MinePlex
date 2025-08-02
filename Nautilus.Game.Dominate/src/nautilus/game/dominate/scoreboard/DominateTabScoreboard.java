package nautilus.game.dominate.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.account.CoreClientManager;
import mineplex.core.packethandler.*;
import mineplex.minecraft.game.classcombat.Class.*;
import nautilus.game.core.scoreboard.TabScoreboard;
import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.player.IDominatePlayer;

public class DominateTabScoreboard extends TabScoreboard<IDominateGame, IDominatePlayer, IDominateTeam>
{
    public DominateTabScoreboard(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, PacketHandler handler, IDominateGame game)
    {
        super(plugin, clientManager, classManager, handler, game);
    }
    
    @Override
    protected void SetRedTeamInfo()
    {
        super.SetRedTeamInfo();
        
        RedColumn.get(2).SetLine(ChatColor.RED + "Control Points");
        
        String spacer = "";
        for (int i=0; i < 5; i++)
        {
            int lineIndex = 3 + i;
            spacer += " ";
            
            IControlPoint controlPoint = Game.GetControlPoints().get(i);
            
            if (controlPoint.Captured() && controlPoint.GetOwnerTeam() == Game.GetRedTeam())
            {
            	RedColumn.get(lineIndex).SetLine(ChatColor.stripColor(controlPoint.GetName()));
            	continue;
            }

            RedColumn.get(lineIndex).SetLine(ChatColor.RED + "" + ChatColor.BLACK + " " + spacer);
        }
    }
    
    @Override
    protected void SetBlueTeamInfo()
    {
        super.SetBlueTeamInfo();
        
        BlueColumn.get(2).SetLine(ChatColor.BLUE + "Control Points");
        
        String spacer = "";
        for (int i=0; i < 5; i++)
        {
            int lineIndex = 3 + i;
            spacer += " ";
            
            IControlPoint controlPoint = Game.GetControlPoints().get(i);
            
            if (controlPoint.Captured() && controlPoint.GetOwnerTeam() == Game.GetBlueTeam())
            {
            	BlueColumn.get(lineIndex).SetLine(ChatColor.stripColor(controlPoint.GetName()));
            	continue;
            }

            BlueColumn.get(lineIndex).SetLine(ChatColor.BLUE + "" + ChatColor.BLACK + " " + spacer);
        }
    }
}
