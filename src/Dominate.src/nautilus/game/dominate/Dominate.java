

java.util.List
mineplex.core.chat.Chat
nautilus.game.core.GamePlugin
nautilus.game.core.arena.ArenaManager
nautilus.game.dominate.arena.DominateArenaParser
nautilus.game.dominate.engine.DominateGameEngine
nautilus.game.dominate.engine.DominateNotifier
org.bukkit.Server
org.bukkit.World
org.bukkit.entity.Player

Dominate

  _gameEngine
  
  onEnable
  
    onEnable()
    
    _gameEngine = new DominateGameEngine(this, this.HubConnection, this.ClientManager, this.DonationManager, this.ClassManager, this.ConditionManager, this.Energy, this.NpcManager, 
      new DominateNotifier(this), this.PacketHandler, new ArenaManager(this, "Dominate", new DominateArenaParser()), (World)getServer().getWorlds().get(0), GetSpawnLocation(), GetWebServerAddress());
    
    new Chat(this, this.ClientManager);
  }
  

  public String GetServerName()
  {
    return "DOM";
  }
  

  public boolean CanHurt(Player a, Player b)
  {
    return this._gameEngine.CanHurt(a, b);
  }
  

  public boolean CanHurt(String a, String b)
  {
    return this._gameEngine.CanHurt(a, b);
  }
  

  public boolean IsSafe(Player a)
  {
    return this._gameEngine.IsSafe(a);
  }
}
