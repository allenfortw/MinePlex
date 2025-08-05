package nautilus.game.tutorial.part;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.action.types.Dialogue;
import nautilus.game.tutorial.action.types.Index;

public abstract class Part implements Listener
{
	public TutorialManager Manager;
	public TutorialData Data;
	
	private Player _player;
	private String _playerName;
	
	private ArrayList<Action> _actions;
	private int _index = 0;
	private long _nextAction = 0;
	private boolean _isActing = true;
	
	private boolean _allowAction = false;
	private boolean _allowClassShop = false;
	private boolean _allowClassSetup = false;
	
	private boolean _completed = false;
	
	private boolean _creditRemove = false;
	
	public Part(TutorialManager manager, TutorialData data, Player player)
	{
		Manager = manager;
		Data = data;
		
		_player = player;
		_playerName = player.getName();
		
		_actions = new ArrayList<Action>();
		
		CreateActions();
	}
	
	public abstract void CreateActions();
	
	public ArrayList<Action> GetActions()
	{
		return _actions;
	}
	
	public Action GetAction() 
	{
		return _actions.get(_index);
	}
	
	public abstract Part GetNext();
	
	public boolean IsActing()
	{
		return _isActing;
	}
	
	public void IndexJump(String jumpTo)
	{
		SetNextAction(0);
		
		for (int i=0 ; i<GetActions().size() ; i++) 
		{
			if (!(GetActions().get(i) instanceof Index))
				continue;
			
			if (!jumpTo.equals(((Index)GetActions().get(i)).GetIndex()))
				continue;
			
			SetIndex(i);
		}
	}

	public void SetIndex(int i) 
	{
		_index = i;
	}
	
	public int GetIndex()
	{
		return _index;
	}
	
	public void IncrementIndex() 
	{
		_index++;
	}
	
	public void Dialogue(Player player, String message)
	{
		//Set Delay
		SetNextAction(System.currentTimeMillis() + Dialogue.dialogueBase + (Dialogue.dialogueChar * message.length()));

		UtilPlayer.message(player, F.tute("Cat", message));
		
		//Effect
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
	}
	
	public long GetNextAction() 
	{
		return _nextAction;
	}

	public void SetNextAction(long lastAction) 
	{
		_nextAction = lastAction;
	}
	
	public boolean Progress()
	{
		if (_index >= _actions.size())
			return false;
		
		return (System.currentTimeMillis() > _nextAction);
	}
	
	public void SetCompleted(boolean completed) 
	{
		_completed = completed;
	}

	public boolean IsCompleted() 
	{	
		return _completed;
	}
	
	public void SetCreditRemove(boolean creditRemove)
	{
		_creditRemove = creditRemove;
	}
	
	public boolean GetPointRemove()
	{
		return _creditRemove;
	}
	
	public void FoodUpdate(Player player)
	{
		player.setFoodLevel(18);
	}
	
	public Player GetPlayer()
	{
		return _player;
	}
	
	public String GetPlayerName()
	{
		return _playerName;
	}
	
	public void Add(Action action)
	{
		_actions.add(action);
	}
	
	public void SetAllowAction(boolean value)
	{
		_allowAction = value;
	}
	
	public boolean AllowAction()
	{
		return _allowAction;
	}
	
	public void SetAllowClassShop(boolean value)
	{
		_allowClassShop = value;
	}
	
	public boolean AllowClassShop()
	{
		return _allowClassShop;
	}
	
	public void SetAllowClassSetup(boolean value)
	{
		_allowClassSetup = value;
	}
	
	public boolean AllowClassSetup()
	{
		return _allowClassSetup;
	}

	public void TutorialCompleted() 
	{
		Manager.EndTutorial(GetPlayer(), true);
	}
	
	public void TutorialCompletedNoAward() 
	{
		Manager.EndTutorial(GetPlayer(), false);
	}
}
