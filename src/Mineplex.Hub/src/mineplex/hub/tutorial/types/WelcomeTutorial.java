package mineplex.hub.tutorial.types;

import mineplex.hub.HubManager;
import mineplex.hub.TextCreator;
import mineplex.hub.tutorial.Tutorial;
import mineplex.hub.tutorial.TutorialPhase;

public class WelcomeTutorial extends Tutorial
{
	public WelcomeTutorial(HubManager manager, TextCreator text) 
	{
		super(manager, "Welcome Tutorial", 5000, "Hub_JoinTutorial");

		double y = -manager.GetSpawn().getY();
		
		//Welcome
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(-40, y+85, 0), 
				manager.GetSpawn(),
				"Welcome to Mineplex",
				new String[] 
						{
					"Welcome!",
					"",
					"This is a very quick tutorial to help you start.",
					"Seriously, it will only take 30 seconds!!!",
					"",
					"Mineplex has many different games to play.",
					"I will show you them, and tell you how to join!"
						}
				));

		//Arcade
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(0, -3, 13), 
				text.locArcade,
				"Arcade",
				new String[] 
						{
					"This is the " + _elem + "Arcade" + _main + " game mode.",
					"",
					"Servers will rotate through many different games.",
					"So there's no need to quit after each game!",
					"",
					"They are all quick, fun 16 player games!"
						}
				));

		//Bridges
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(-13, -3, 0), 
				text.locSurvival,
				"Survival",
				new String[] 
						{
					"This is the " + _elem + "The Bridges" + _main + " game mode.",
					"This is a great team combat game.",
					"You get 10 minutes to prepare for battle,",
					"then the bridges drop, and you fight to the death!",
					"",
					_elem + "Hunger Games" + _main + " will be added here soon!"
						}
				));

		//Pig
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(-9, y+73, 53), 
				manager.GetSpawn().add(-11, y+72.5, 57),
				"???",
				new String[] 
						{
					"",
					"",
					"This is a pig standing on a log."
						}
				));

		//Classics
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(0, -3, -13), 
				text.locClassics,
				"Classics",
				new String[] 
						{
					"Here, you can play our " + _elem + "Classics" + _main + " game modes.",
					"",
					_elem + "MineKart" + _main + " is an exciting racing game.",
					"Complete with weapons, drifting and more!",
					"",
					"In " + _elem + "Super Smash Mobs" + _main + " you become a monster,",
					"then fight to the death with other players, using fun skills!"
						}
				));

		//Comp
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(13, -3, 0), 
				text.locComp,
				"Champions",
				new String[] 
						{
					"Finally, these are the " + _elem + "Champions" + _main + " games.",
					"These are extremely competitive skill based games.",
					"",
					"Each class can be customised with unlockable skills.",
					"",
					"Fight with others in three different game types!"
						}
				));

		//HORSE
		_phases.add(new TutorialPhase(
				manager.GetSpawn().add(0, -3, 19), 
				manager.GetSpawn().add(0, -3.1, 23), 
				"Joining Games",
				new String[] 
						{
					"Click on this " + _elem + "HORSE" + _main + " to join an " + _elem + "ARCADE" + _main + " server.",
					"This will open the server select menu.",
					"Click an " + _elem + "Emerald Block" + _main + " to join a server!",
					"",
					"Easy huh?",
					"",
					"Thanks for listening! Have fun!"
						}
				));
	}
}
