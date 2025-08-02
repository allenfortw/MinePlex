package nautilus.game.core.arena;

import java.io.FileReader;

public abstract interface IArenaParser<ArenaType extends IArena>
{
  public abstract ArenaType Parse(String paramString, FileReader paramFileReader);
}
