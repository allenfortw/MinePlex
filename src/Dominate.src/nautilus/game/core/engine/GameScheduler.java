package nautilus.game.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import nautilus.game.core.game.IGame;

public class GameScheduler<GameType extends IGame<?, ?>>
{
  private List<GameType> _schedule;
  
  public GameScheduler()
  {
    this._schedule = new ArrayList();
  }
  
  public List<GameType> GetGames()
  {
    return this._schedule;
  }
  
  public boolean ContainsGame(GameType game)
  {
    return this._schedule.contains(game);
  }
  
  public GameType ScheduleNewGame(GameType game)
  {
    this._schedule.add(game);
    
    return game;
  }
  
  public void RemoveGame(GameType game)
  {
    this._schedule.remove(game);
  }
  
  public void CleanEmptyGames()
  {
    Iterator<GameType> gameIterator = this._schedule.iterator();
    
    while (gameIterator.hasNext())
    {
      GameType game = (IGame)gameIterator.next();
      
      if (game.GetPlayers().isEmpty())
      {
        game.Deactivate();
        gameIterator.remove();
      }
    }
  }
}
