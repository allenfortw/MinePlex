package nautilus.game.core.engine;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;

public abstract interface ITeamGameEngine<GameType extends ITeamGame<ArenaType, PlayerType, PlayerTeamType>, ArenaType extends ITeamArena, PlayerTeamType extends ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>>
  extends IGameEngine<GameType, ArenaType, PlayerType>
{}
