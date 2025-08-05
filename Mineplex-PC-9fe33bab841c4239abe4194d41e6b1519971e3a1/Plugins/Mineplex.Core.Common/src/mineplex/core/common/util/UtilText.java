package mineplex.core.common.util;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.server.v1_6_R2.Chunk;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class UtilText 
{
	public enum TextAlign
	{
		LEFT,
		RIGHT,
		CENTER
	}

	public static HashMap<Character, int[][]> alphabet = new HashMap<Character, int[][]>();

	public static void MakeText(String string, Location loc, BlockFace face, int id, byte data, TextAlign align)
	{
		if (alphabet.isEmpty())
			PopulateAlphabet();

		Block block = loc.getBlock();

		//Get Width
		int width = 0;
		for (char c : string.toLowerCase().toCharArray())
		{
			int[][] letter = alphabet.get(c);

			if (letter == null)
				continue;

			width += letter[0].length+1;
		}
		
		//Shift Blocks
		if (align == TextAlign.CENTER || align == TextAlign.RIGHT)
		{
			int divisor = 1;
			if (align == TextAlign.CENTER)
				divisor = 2;

			block = block.getRelative(face, (-1 * width/divisor) + 1);
		}
		
		HashSet<Chunk> chunks = new HashSet<Chunk>();

		//Clean
		Block other = loc.getBlock();
		for (int y=0 ; y<5 ; y++)
		{
			if (align == TextAlign.CENTER)
				for (int i=-48 ; i<=48 ; i++)
					if (other.getRelative(face, i).getTypeId() != 0)
						chunks.add(MapUtil.ChunkBlockChange(other.getRelative(face, i).getLocation(), 0, (byte)0));

			if (align == TextAlign.LEFT)
				for (int i=0 ; i<=96 ; i++)
					if (other.getRelative(face, i).getTypeId() != 0)
						chunks.add(MapUtil.ChunkBlockChange(other.getRelative(face, i).getLocation(), 0, (byte)0));
			
			if (align == TextAlign.RIGHT)
				for (int i=-96 ; i<=0 ; i++)
					if (other.getRelative(face, i).getTypeId() != 0)
						chunks.add(MapUtil.ChunkBlockChange(other.getRelative(face, i).getLocation(), 0, (byte)0));
			
			other = other.getRelative(BlockFace.DOWN);
		}	
		
		//Make Blocks
		for (char c : string.toLowerCase().toCharArray())
		{
			int[][] letter = alphabet.get(c);

			if (letter == null)
				continue;

			for (int x=0 ; x<letter.length ; x++)
			{
				for (int y=0 ; y<letter[x].length ; y++)
				{
					if (letter[x][y] == 1)
						chunks.add(MapUtil.ChunkBlockChange(block.getLocation(), id, data));

					//Forward
					block = block.getRelative(face);	
				}

				//Back
				block = block.getRelative(face, -1 * letter[x].length);

				//Down
				block = block.getRelative(BlockFace.DOWN);
			}

			block = block.getRelative(BlockFace.UP, 5);
			block = block.getRelative(face, letter[0].length + 1);
		}
		
		MapUtil.ResendChunksForNearbyPlayers(chunks);
	}

	private static void PopulateAlphabet()
	{
		alphabet.put('0', new int[][] {
				{1,1,1},
				{1,0,1},
				{1,0,1},
				{1,0,1},
				{1,1,1}
		});

		alphabet.put('1', new int[][] {
				{1,1,0},
				{0,1,0},
				{0,1,0},
				{0,1,0},
				{1,1,1}
		});

		alphabet.put('2', new int[][] {
				{1,1,1},
				{0,0,1},
				{1,1,1},
				{1,0,0},
				{1,1,1}
		});

		alphabet.put('3', new int[][] {
				{1,1,1},
				{0,0,1},
				{1,1,1},
				{0,0,1},
				{1,1,1}
		});

		alphabet.put('4', new int[][] {
				{1,0,1},
				{1,0,1},
				{1,1,1},
				{0,0,1},
				{0,0,1}
		});

		alphabet.put('5', new int[][] {
				{1,1,1},
				{1,0,0},
				{1,1,1},
				{0,0,1},
				{1,1,1}
		});

		alphabet.put('6', new int[][] {
				{1,1,1},
				{1,0,0},
				{1,1,1},
				{1,0,1},
				{1,1,1}
		});

		alphabet.put('7', new int[][] {
				{1,1,1},
				{0,0,1},
				{0,0,1},
				{0,0,1},
				{0,0,1}
		});

		alphabet.put('8', new int[][] {
				{1,1,1},
				{1,0,1},
				{1,1,1},
				{1,0,1},
				{1,1,1}
		});

		alphabet.put('9', new int[][] {
				{1,1,1},
				{1,0,1},
				{1,1,1},
				{0,0,1},
				{1,1,1}
		});

		alphabet.put('.', new int[][] {
				{0},
				{0},
				{0},
				{0},
				{1}
		});

		alphabet.put('!', new int[][] {
				{1},
				{1},
				{1},
				{0},
				{1}
		});

		alphabet.put(' ', new int[][] {
				{0,0},
				{0,0},
				{0,0},
				{0,0},
				{0,0}
		});

		alphabet.put('a', new int[][] {
				{1,1,1,1},
				{1,0,0,1},
				{1,1,1,1},
				{1,0,0,1},
				{1,0,0,1}
		});

		alphabet.put('b', new int[][] {
				{1,1,1,0},
				{1,0,0,1},
				{1,1,1,0},
				{1,0,0,1},
				{1,1,1,0}
		});

		alphabet.put('c', new int[][] {
				{1,1,1,1},
				{1,0,0,0},
				{1,0,0,0},
				{1,0,0,0},
				{1,1,1,1}
		});

		alphabet.put('d', new int[][] {
				{1,1,1,0},
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,1,1,0}
		});

		alphabet.put('e', new int[][] {
				{1,1,1,1},
				{1,0,0,0},
				{1,1,1,0},
				{1,0,0,0},
				{1,1,1,1}
		});

		alphabet.put('f', new int[][] {
				{1,1,1,1},
				{1,0,0,0},
				{1,1,1,0},
				{1,0,0,0},
				{1,0,0,0}
		});

		alphabet.put('g', new int[][] {
				{1,1,1,1},
				{1,0,0,0},
				{1,0,1,1},
				{1,0,0,1},
				{1,1,1,1}
		});

		alphabet.put('h', new int[][] {
				{1,0,0,1},
				{1,0,0,1},
				{1,1,1,1},
				{1,0,0,1},
				{1,0,0,1}
		});

		alphabet.put('i', new int[][] {
				{1,1,1},
				{0,1,0},
				{0,1,0},
				{0,1,0},
				{1,1,1}
		});

		alphabet.put('j', new int[][] {
				{1,1,1,1},
				{0,0,1,0},
				{0,0,1,0},
				{1,0,1,0},
				{1,1,1,0}
		});

		alphabet.put('k', new int[][] {
				{1,0,0,1},
				{1,0,1,0},
				{1,1,0,0},
				{1,0,1,0},
				{1,0,0,1}
		});

		alphabet.put('l', new int[][] {
				{1,0,0,0},
				{1,0,0,0},
				{1,0,0,0},
				{1,0,0,0},
				{1,1,1,1}
		});

		alphabet.put('m', new int[][] {
				{1,1,1,1,1},
				{1,0,1,0,1},
				{1,0,1,0,1},
				{1,0,0,0,1},
				{1,0,0,0,1}
		});

		alphabet.put('n', new int[][] {
				{1,0,0,1},
				{1,1,0,1},
				{1,0,1,1},
				{1,0,0,1},
				{1,0,0,1}
		});

		alphabet.put('o', new int[][] {
				{1,1,1,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,1,1,1}
		});

		alphabet.put('p', new int[][] {
				{1,1,1,1},
				{1,0,0,1},
				{1,1,1,1},
				{1,0,0,0},
				{1,0,0,0}
		});

		alphabet.put('q', new int[][] {
				{1,1,1,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,0,1,0},
				{1,1,0,1}
		});

		alphabet.put('r', new int[][] {
				{1,1,1,1},
				{1,0,0,1},
				{1,1,1,0},
				{1,0,0,1},
				{1,0,0,1}
		});

		alphabet.put('s', new int[][] {
				{1,1,1,1},
				{1,0,0,0},
				{1,1,1,1},
				{0,0,0,1},
				{1,1,1,1}
		});

		alphabet.put('t', new int[][] {
				{1,1,1,1,1},
				{0,0,1,0,0},
				{0,0,1,0,0},
				{0,0,1,0,0},
				{0,0,1,0,0}
		});

		alphabet.put('u', new int[][] {
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,1,1,1}
		});

		alphabet.put('v', new int[][] {
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{1,0,0,1},
				{0,1,1,0}
		});

		alphabet.put('w', new int[][] {
				{1,0,0,0,1},
				{1,0,0,0,1},
				{1,0,1,0,1},
				{1,0,1,0,1},
				{1,1,1,1,1}
		});

		alphabet.put('x', new int[][] {
				{1,0,0,1},
				{1,0,0,1},
				{0,1,1,0},
				{1,0,0,1},
				{1,0,0,1}
		});

		alphabet.put('y', new int[][] {
				{1,0,0,1},
				{1,0,0,1},
				{1,1,1,1},
				{0,0,0,1},
				{1,1,1,1}
		});

		alphabet.put('z', new int[][] {
				{1,1,1,1},
				{0,0,0,1},
				{0,0,1,0},
				{0,1,0,0},
				{1,1,1,1}
		});
	}
}
