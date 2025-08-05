package me.chiss.Core.Plugin;

import org.bukkit.entity.Player;

public interface IRelation 
{
	public boolean CanHurt(Player a, Player b);
	public boolean CanHurt(String a, String b);
	public boolean IsSafe(Player a);
}
