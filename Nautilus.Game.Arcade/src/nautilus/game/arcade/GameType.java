package nautilus.game.arcade;

public enum GameType
{	
	//Mini
	Bridge("The Bridges"),
	DeathTag("Death Tag"),
	CastleSiege("Castle Siege"),
	Dragons("Dragons"),
	MineWare("MineWare"),
	Horse("Horseback"),
	Evolution("Evolution"),
	Smash("Super Smash Mobs"),
	Spleef("Super Spleef"),
	DragonEscape("Dragon Escape"),
	Quiver("One in the Quiver"),
	Runner("Runner"),
	SnowFight("Snow Fight"),
	TurfForts("Turf Forts"),
	UHC("Ultra Hardcore"),
	ZombieSurvival("Zombie Survival");
	
	String _name;

	GameType(String name)
	{
		_name = name;
	}

	public String GetName()
	{
		return _name;
	}
}