package nautilus.game.dominate.scoreboard;

import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.packethandler.PacketHandler;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import nautilus.game.core.scoreboard.LineTracker;
import nautilus.game.core.scoreboard.TabScoreboard;
import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DominateTabScoreboard extends TabScoreboard<IDominateGame, nautilus.game.dominate.player.IDominatePlayer, nautilus.game.dominate.engine.IDominateTeam>
{
  public DominateTabScoreboard(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, PacketHandler handler, IDominateGame game)
  {
    super(plugin, clientManager, classManager, handler, game);
  }
  

  protected void SetRedTeamInfo()
  {
    super.SetRedTeamInfo();
    
    ((LineTracker)this.RedColumn.get(Integer.valueOf(2))).SetLine(ChatColor.RED + "Control Points");
    
    String spacer = "";
    for (int i = 0; i < 5; i++)
    {
      int lineIndex = 3 + i;
      spacer = spacer + " ";
      
      IControlPoint controlPoint = (IControlPoint)((IDominateGame)this.Game).GetControlPoints().get(i);
      
      if ((controlPoint.Captured()) && (controlPoint.GetOwnerTeam() == ((IDominateGame)this.Game).GetRedTeam()))
      {
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.stripColor(controlPoint.GetName()));
      }
      else
      {
        ((LineTracker)this.RedColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.RED + ChatColor.BLACK + " " + spacer);
      }
    }
  }
  
  protected void SetBlueTeamInfo()
  {
    super.SetBlueTeamInfo();
    
    ((LineTracker)this.BlueColumn.get(Integer.valueOf(2))).SetLine(ChatColor.BLUE + "Control Points");
    
    String spacer = "";
    for (int i = 0; i < 5; i++)
    {
      int lineIndex = 3 + i;
      spacer = spacer + " ";
      
      IControlPoint controlPoint = (IControlPoint)((IDominateGame)this.Game).GetControlPoints().get(i);
      
      if ((controlPoint.Captured()) && (controlPoint.GetOwnerTeam() == ((IDominateGame)this.Game).GetBlueTeam()))
      {
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.stripColor(controlPoint.GetName()));
      }
      else
      {
        ((LineTracker)this.BlueColumn.get(Integer.valueOf(lineIndex))).SetLine(ChatColor.BLUE + ChatColor.BLACK + " " + spacer);
      }
    }
  }
}
