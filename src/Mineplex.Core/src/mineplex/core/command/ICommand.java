package mineplex.core.command;

import java.util.Collection;

import mineplex.core.common.Rank;

import org.bukkit.entity.Player;

public interface ICommand
{
	void SetCommandCenter(CommandCenter commandCenter);
	void Execute(Player caller, String[] args);

	Collection<String> Aliases();

	void SetAliasUsed(String name);

	Rank GetRequiredRank();
}
