package mineplex.hub;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilText.TextAlign;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;

public class TextCreator extends MiniPlugin
{
	public HubManager Manager;
	
	public Location locComp;
	public Location locArcade;
	public Location locSurvival;
	public Location locClassics;
	
	BlockFace faceComp = BlockFace.SOUTH;
	BlockFace faceArcade = BlockFace.WEST;
	BlockFace faceSurvival = BlockFace.NORTH;
	BlockFace faceOther = BlockFace.EAST;
	
	String[] arcadeGames;
	int arcadeIndex = 0;
	
	int smashIndex = 0;

	public TextCreator(HubManager manager)
	{
		super("Text Creator", manager.GetPlugin());
		
		Manager = manager;
		
		locComp = 		manager.GetSpawn().add(40, 10, 0);
		locArcade = 	manager.GetSpawn().add(0, 10, 40);
		locSurvival = 	manager.GetSpawn().add(-40, 10, 0);
		locClassics = 		manager.GetSpawn().add(0, 10, -40);
		
		arcadeGames = new String[]
				{
				"ONE IN THE QUIVER",
				"ZOMBIE SURVIVAL",
				"SUPER SPLEEF",
				"TURF FORTS",
				"DRAGONS",
				"RUNNER"
				};
		
		CreateText();
	}
	
	public void CreateText()
	{
		//Comp
		UtilText.MakeText("CHAMPIONS", locComp, faceComp, 159, (byte)5, TextAlign.CENTER);
		UtilText.MakeText("CHAMPIONS", locComp.clone().add(1, 0, 0), faceComp, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("DOMINATE", locComp.clone().add(15, 14, 0), faceComp, 159, (byte)4, TextAlign.CENTER);
		UtilText.MakeText("DOMINATE", locComp.clone().add(16, 14, 0), faceComp, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("DEATHMATCH", locComp.clone().add(15, 21, 0), faceComp, 159, (byte)1, TextAlign.CENTER);
		UtilText.MakeText("DEATHMATCH", locComp.clone().add(16, 21, 0), faceComp, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("CAPTURE THE PIG", locComp.clone().add(15, 28, 0), faceComp, 159, (byte)14, TextAlign.CENTER);
		UtilText.MakeText("CAPTURE THE PIG", locComp.clone().add(16, 28, 0), faceComp, 159, (byte)15, TextAlign.CENTER);
		
		//Arcade
		UtilText.MakeText("ARCADE", locArcade, faceArcade, 159, (byte)5, TextAlign.CENTER);
		UtilText.MakeText("ARCADE", locArcade.clone().add(0, 0, 1), faceArcade, 159, (byte)15, TextAlign.CENTER);
		
		//Survival
		UtilText.MakeText("SURVIVAL", locSurvival, faceSurvival, 159, (byte)5, TextAlign.CENTER);
		UtilText.MakeText("SURVIVAL", locSurvival.clone().add(-1, 0, 0), faceSurvival, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("THE BRIDGES", locSurvival.clone().add(-15, 14, 0), faceSurvival, 159, (byte)4, TextAlign.CENTER);
		UtilText.MakeText("THE BRIDGES", locSurvival.clone().add(-16, 14, 0), faceSurvival, 159, (byte)15, TextAlign.CENTER);
		
		//UtilText.MakeText("CASTLE SIEGE", locSurvival.clone().add(-15, 21, 0), faceSurvival, 159, (byte)1, TextAlign.CENTER);
		//UtilText.MakeText("CASTLE SIEGE", locSurvival.clone().add(-16, 21, 0), faceSurvival, 159, (byte)15, TextAlign.CENTER);
		
		
		//Other
		UtilText.MakeText("CLASSICS", locClassics, faceOther, 159, (byte)5, TextAlign.CENTER);
		UtilText.MakeText("CLASSICS", locClassics.add(0, 0, -1), faceOther, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("SUPER SMASH MOBS", locClassics.clone().add(0, 14, -15), faceOther, 159, (byte)4, TextAlign.CENTER);
		UtilText.MakeText("SUPER SMASH MOBS", locClassics.clone().add(0, 14, -16), faceOther, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("MINEKART", locClassics.clone().add(0, 21, -15), faceOther, 159, (byte)1, TextAlign.CENTER);
		UtilText.MakeText("MINEKART", locClassics.clone().add(0, 21, -16), faceOther, 159, (byte)15, TextAlign.CENTER);
	}
	
	@EventHandler
	public void UpdateArcadeGames(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;
		
		
		UtilText.MakeText(GetArcadeText(0), locArcade.clone().add(0, 14, 15), faceArcade, 159, (byte)4, TextAlign.CENTER);
		UtilText.MakeText(GetArcadeText(0), locArcade.clone().add(0, 14, 16), faceArcade, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText(GetArcadeText(1), locArcade.clone().add(0, 21, 15), faceArcade, 159, (byte)1, TextAlign.CENTER);
		UtilText.MakeText(GetArcadeText(1), locArcade.clone().add(0, 21, 16), faceArcade, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText(GetArcadeText(2), locArcade.clone().add(0, 28, 15), faceArcade, 159, (byte)14, TextAlign.CENTER);
		UtilText.MakeText(GetArcadeText(2), locArcade.clone().add(0, 28, 16), faceArcade, 159, (byte)15, TextAlign.CENTER);
		
		arcadeIndex = (arcadeIndex + 3)%arcadeGames.length;
	}
	
	public String GetArcadeText(int offset)
	{
		int index = (arcadeIndex + offset)%arcadeGames.length;

		return	arcadeGames[index];
	}
	
	/*
	@EventHandler
	public void UpdateNew(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		long startTime = System.currentTimeMillis();
		
		smashIndex = (smashIndex+1)%2;
		
		byte color = 4;
		if (smashIndex == 1)	color = 0;
		
		//UtilText.MakeText("SUPER SMASH MOBS", locOther, faceOther, 159, color, TextAlign.CENTER);
		//UtilText.MakeText("SUPER SMASH MOBS", locOther.clone().add(0, 0, -1), faceOther, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("SUPER SMASH MOBS", locClassics.clone().add(0, 14, -15), faceOther, 159, color, TextAlign.CENTER);
		UtilText.MakeText("SUPER SMASH MOBS", locClassics.clone().add(0, 14, -16), faceOther, 159, (byte)15, TextAlign.CENTER);
		
		UtilText.MakeText("DOMINATE", locComp.clone().add(15, 14, 0), faceComp, 159, color, TextAlign.CENTER);
		UtilText.MakeText("DOMINATE", locComp.clone().add(16, 14, 0), faceComp, 159, (byte)15, TextAlign.CENTER);
		//System.out.println("TextCreator : " + (System.currentTimeMillis() - startTime) + "ms");
	}
	*/
}
