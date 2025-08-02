package nautilus.game.core.arena;

import java.io.FileReader;

public interface IArenaParser<ArenaType extends IArena>
{
	ArenaType Parse(String fileName, FileReader fileReader);
}
