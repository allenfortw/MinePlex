package mineplex.core.account;

import mineplex.core.common.Rank;

import org.bukkit.entity.Player;

public class CoreClient 
{
    private int _accountId;
	private String _name;
	private Player _player;
	private Rank _rank;
	
	private boolean _filterChat;
	
	public CoreClient(Player player)
	{
		_player = player;
		_name = player.getName();
	}
	
	public CoreClient(String name)
	{
		_name = name;
	}

	public String GetPlayerName()
	{
		return _name;
	}
	
	public Player GetPlayer()
	{
		return _player;
	}
	
    public void SetPlayer(Player player)
    {
        _player = player;
    }
    
    public int GetAccountId()
    {
        return _accountId;
    }

	public void Delete() 
	{
		_name = null;
		_player = null;
	}

	public void SetAccountId(int accountId)
	{
		_accountId = accountId;
	}

	public void SetFilterChat(Boolean filterChat)
	{
		_filterChat = filterChat;
	}
	
	public boolean GetFilterChat()
	{
		return _filterChat;
	}

	public Rank GetRank()
	{
		return _rank;
	}
	
	public void SetRank(Rank rank)
	{
		_rank = rank;
	}
}
