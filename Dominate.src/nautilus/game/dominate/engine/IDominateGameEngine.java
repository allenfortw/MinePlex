package nautilus.game.dominate.engine;

import nautilus.game.core.engine.ITeamGameEngine;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.IDominatePlayer;

public abstract interface IDominateGameEngine
  extends ITeamGameEngine<IDominateGame, IDominateArena, IDominateTeam, IDominatePlayer>
{}
