package mineplex.minecraft.game.core.condition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import mineplex.core.MiniPlugin;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ConditionManager extends MiniPlugin
{
	private ConditionFactory _factory;
	private ConditionApplicator _applicator;
	protected ConditionEffect Effect;

	private WeakHashMap<LivingEntity, LinkedList<Condition>> _conditions = new WeakHashMap<LivingEntity, LinkedList<Condition>>();
	private WeakHashMap<LivingEntity, LinkedList<ConditionIndicator>> _indicators = new WeakHashMap<LivingEntity, LinkedList<ConditionIndicator>>();
	private WeakHashMap<LivingEntity, Entity> _buffer = new WeakHashMap<LivingEntity, Entity>();
	private HashSet<LivingEntity> _hideIndicator = new HashSet<LivingEntity>();
	private HashSet<Entity> _items = new HashSet<Entity>();

	public ConditionManager(JavaPlugin plugin) 
	{
		super("Condition Manager", plugin);

		Factory();
		Applicator();
		Effect();
	}

	public ConditionFactory Factory()
	{
		if (_factory == null)
			_factory = new ConditionFactory(this);

		return _factory;
	}

	public ConditionApplicator Applicator()
	{
		if (_applicator == null)
			_applicator = new ConditionApplicator();

		return _applicator;
	}

	public ConditionEffect Effect()
	{
		if (Effect == null)
			Effect = new ConditionEffect(this);		

		return Effect;
	}

	@Override
	public void Disable()
	{
		for (LivingEntity ent : _indicators.keySet())
			for (ConditionIndicator ind : _indicators.get(ent))
			{
				if (ind.GetIndicator() != null)
					ind.GetIndicator().remove();
				
				HandlerList.unregisterAll(ind);
			}
	}

	public Condition AddCondition(Condition newCon)
	{
		//Event
		ConditionApplyEvent condEvent = new ConditionApplyEvent(newCon);
		GetPlugin().getServer().getPluginManager().callEvent(condEvent);

		if (condEvent.isCancelled())
			return null;

		//Add Condition
		if (!_conditions.containsKey(newCon.GetEnt()))
			_conditions.put(newCon.GetEnt(), new LinkedList<Condition>());

		_conditions.get(newCon.GetEnt()).add(newCon);

		//Condition Add
		newCon.OnConditionAdd();

		//Indicator
		HandleIndicator(newCon);

		return newCon;
	}

	public void HandleIndicator(Condition newCon)
	{
		ConditionIndicator ind = GetIndicatorType(newCon);

		//New Condition
		if (ind == null)
		{
			AddIndicator(newCon);
		}
		//Condition Exists
		else
		{
			UpdateIndicator(ind, newCon);
		}
	}

	public ConditionIndicator GetIndicatorType(Condition newCon)
	{
		if (!_indicators.containsKey(newCon.GetEnt()))
			_indicators.put(newCon.GetEnt(), new LinkedList<ConditionIndicator>());

		for (ConditionIndicator ind : _indicators.get(newCon.GetEnt()))
			if (ind.GetCondition().GetType() == newCon.GetType())
				return ind;

		return null;
	}

	public void AddIndicator(Condition newCon)
	{
		//Create
		ConditionIndicator newInd = new ConditionIndicator(newCon);

		//Get Inds
		if (!_indicators.containsKey(newCon.GetEnt()))
			_indicators.put(newCon.GetEnt(), new LinkedList<ConditionIndicator>());
		
		LinkedList<ConditionIndicator> entInds = _indicators.get(newCon.GetEnt());

		/** Spawn Indicator if Applicable **/
		if (false && !_hideIndicator.contains(newCon.GetEnt()) && newInd.GetCondition().IsVisible())
		{
			LivingEntity ent = newInd.GetCondition().GetEnt();

			//First, Add Buffer
			if (!_buffer.containsKey(ent))
			{
				Entity buffer = ent.getWorld().dropItem(ent.getLocation(), ItemStackFactory.Instance.CreateStack(Material.GHAST_TEAR, 1));	
				ent.setPassenger(buffer);	
				_buffer.put(ent, buffer);	
			}

			//Indicator on Player
			_buffer.get(ent).setPassenger(newInd.GetIndicator());

			//Indicator Stack
			if (!entInds.isEmpty())
				if (entInds.getFirst().GetCondition().IsVisible())
					newInd.GetIndicator().setPassenger(entInds.getFirst().GetIndicator());
		}

		//Register Events
		UtilServer.getServer().getPluginManager().registerEvents(newInd, _plugin);

		//Add
		entInds.addFirst(newInd);

		//Inform
		if (newCon.GetInformOn() != null)
			UtilPlayer.message(newCon.GetEnt(), F.main("Condition", newCon.GetInformOn()));
	}

	public void UpdateIndicator(ConditionIndicator ind, Condition newCon)
	{
		//Not Additive

		if (!ind.GetCondition().IsExpired())
			if (ind.GetCondition().IsBetterOrEqual(newCon, newCon.IsAdd()))
				return;

		ind.SetCondition(newCon);
	}

	@EventHandler
	public void ExpireConditions(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		/** Conditions **/
		for (LivingEntity ent : _conditions.keySet())
		{
			Iterator<Condition> conditionIterator = _conditions.get(ent).iterator();

			while (conditionIterator.hasNext())
			{
				Condition cond = conditionIterator.next();

				if (cond.Tick())
					conditionIterator.remove();
			}
		}

		/** Indicators **/
		for (LivingEntity ent : _indicators.keySet())
		{
			Iterator<ConditionIndicator> conditionIndicatorIterator = _indicators.get(ent).iterator();

			while (conditionIndicatorIterator.hasNext())
			{
				ConditionIndicator conditionIndicator = conditionIndicatorIterator.next();

				if (conditionIndicator.GetCondition().IsExpired())
				{
					Condition replacement = GetBestCondition(ent, conditionIndicator.GetCondition().GetType());

					if (replacement == null)
					{
						/** Despawn Indicator **/
						if (!_hideIndicator.contains(ent) && conditionIndicator.GetIndicator() != null)
						{
							Entity below = conditionIndicator.GetIndicator().getVehicle();
							Entity above = conditionIndicator.GetIndicator().getPassenger();

							conditionIndicator.GetIndicator().eject();
							conditionIndicator.GetIndicator().leaveVehicle();

							if (above != null && below != null)
								below.setPassenger(above);

							Vector vec = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
							vec.normalize().multiply(0.1).setY(0.2);
							conditionIndicator.GetIndicator().setVelocity(vec);

							//Remove
							_items.add(conditionIndicator.GetIndicator());
						}

						HandlerList.unregisterAll(conditionIndicator);
						conditionIndicatorIterator.remove();

						//Remove Buffer
						if (_indicators.get(ent).isEmpty())
						{
							RemoveBuffer(_buffer.remove(ent));
							// _indicators.remove(ent);
						}

						//Inform
						if (conditionIndicator.GetCondition().GetInformOff() != null)
							UtilPlayer.message(conditionIndicator.GetCondition().GetEnt(), F.main("Condition", conditionIndicator.GetCondition().GetInformOff()));
					}
					else
						UpdateIndicator(conditionIndicator, replacement);
				}
			}
		}
	}

	public Condition GetBestCondition(LivingEntity ent, ConditionType type)
	{
		if (!_conditions.containsKey(ent))
			return null;

		Condition best = null;

		for (Condition con : _conditions.get(ent))
		{
			if (con.GetType() != type)
				continue;

			if (con.IsExpired())
				continue;

			if (best == null)
			{
				best = con;
				continue;
			}

			if (con.IsBetterOrEqual(best, false))
				best = con;
		}

		return best;
	}

	public Condition GetActiveCondition(LivingEntity ent, ConditionType type)
	{
		if (!_indicators.containsKey(ent))
			return null;

		for (ConditionIndicator ind : _indicators.get(ent))
		{
			if (ind.GetCondition().GetType() != type)
				continue;

			if (ind.GetCondition().IsExpired())
				continue;

			return ind.GetCondition();
		}

		return null;
	}

	@EventHandler
	public void Remove(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		HashSet<Entity> expired = new HashSet<Entity>();

		for (Entity cur : _items)
			if (UtilEnt.isGrounded(cur) || cur.isDead() || !cur.isValid())
				expired.add(cur);

		for (Entity cur : expired)
		{
			_items.remove(cur);
			cur.remove(); 
		}
	}

	@EventHandler
	public void Respawn(PlayerRespawnEvent event)
	{
		Clean(event.getPlayer());
	}

	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		Clean(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void Death(EntityDeathEvent event)
	{
		//Still Alive - SHOULD IGNORE DEATHS FROM DOMINATE
		if (event.getEntity() instanceof Player)
			if (event.getEntity().getHealth() > 0)
				return;

		Clean(event.getEntity());
	}

	public void Clean(LivingEntity ent)
	{
		//Wipe Conditions
		_conditions.remove(ent);

		//Remove Buffer
		RemoveBuffer(_buffer.remove(ent));

		_hideIndicator.remove(ent);
		
		//Clean Indicators
		LinkedList<ConditionIndicator> inds = _indicators.remove(ent);	
		if (inds == null)
			return;

		for (ConditionIndicator ind : inds)
		{
			HandlerList.unregisterAll(ind);
			
			if (ind.GetCondition().IsVisible())
				ind.GetIndicator().remove();
		}
	}

	public void DebugInfo(Player player)
	{
		int count = 0;
		for (LivingEntity ent : _indicators.keySet())
		{
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				count++;
			}
		}
		
		player.sendMessage(F.main(GetName(), count + " Invalid Indicators."));
		
		count = 0;
		for (LivingEntity ent : _conditions.keySet())
		{
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				count++;
			}
		}
		
		player.sendMessage(F.main(GetName(), count + " Invalid Conditions."));
		
		count = 0;
		for (LivingEntity ent : _buffer.keySet())
		{
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				count++;
			}
		}
		
		player.sendMessage(F.main(GetName(), count + " Invalid Buffers."));
		
		count = 0;
		for (Entity ent : _items)
		{
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				count++;
			}
		}
		
		player.sendMessage(F.main(GetName(), count + " Invalid Items."));
		
		count = 0;
		for (LivingEntity ent : _hideIndicator)
		{
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				count++;
			}
		}
		
		player.sendMessage(F.main(GetName(), count + " Invalid Hidden Indicators."));
	}
	
	@EventHandler
	public void Debug(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (LivingEntity ent : _indicators.keySet())
		{
			if (!(ent instanceof Player))
				continue;

			Player player = (Player)ent;
			if (player.getItemInHand() == null)
				continue;
			
			if (player.getItemInHand().getType() != Material.PAPER)
				continue;
			
			if (!player.isOp())
				continue;

			UtilPlayer.message(player, C.cGray + _indicators.get(ent).size() + " Indicators ----------- " + _conditions.get(ent).size() + " Conditions");
			for (ConditionIndicator ind : _indicators.get(ent))
				UtilPlayer.message(player, 
						F.elem(ind.GetCondition().GetType() + " " + (ind.GetCondition().GetMult()+1)) + " for " + 
								F.time(UtilTime.convertString(ind.GetCondition().GetTicks()*50L, 1, TimeUnit.FIT)) + " via " +
								F.skill(ind.GetCondition().GetReason()) + " from " + 
								F.name(UtilEnt.getName(ind.GetCondition().GetSource())) + ".");
		}
	}

	@EventHandler
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_items.contains(event.getItem()))
			event.setCancelled(true);

		else if (_buffer.containsValue(event.getItem()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_items.contains(event.getItem()))
			event.setCancelled(true);

		else if (_buffer.containsValue(event.getItem()))
			event.setCancelled(true);
	}

	public void EndCondition(LivingEntity target, ConditionType type, String reason)
	{
		if (!_conditions.containsKey(target))
			return;

		for (Condition cond : _conditions.get(target))
			if (reason == null || cond.GetReason().equals(reason))
				if (type == null || cond.GetType() == type)
				{
					cond.Expire();
					
					Condition best = GetBestCondition(target, cond.GetType());
					if (best != null)	best.Apply();
				}
	}

	public boolean HasCondition(LivingEntity target, ConditionType type, String reason)
	{
		if (!_conditions.containsKey(target))
			return false;

		for (Condition cond : _conditions.get(target))
			if (reason == null || cond.GetReason().equals(reason))
				if (type == null || cond.GetType() == type)
					return true;

		return false;
	}

	public WeakHashMap<LivingEntity, LinkedList<ConditionIndicator>> GetIndicators()
	{
		return _indicators;
	}

	public void SetIndicatorVisibility(LivingEntity ent, boolean showIndicator)
	{
		if (!showIndicator)
		{
			if (_hideIndicator.contains(ent))
				return;

			_hideIndicator.add(ent);

			LinkedList<ConditionIndicator> inds = _indicators.remove(ent);
			if (inds == null)		return;

			//Delete Indicators
			for (ConditionIndicator ind : inds)
			{
				HandlerList.unregisterAll(ind);
				
				if (ind.GetCondition().IsVisible())
					ind.GetIndicator().remove();
			}

			//Remove Buffer
			RemoveBuffer(_buffer.remove(ent));
		}
		else
		{
			if (_hideIndicator.remove(ent))
				LoadIndicators(ent);
		}		
	}

	public void RemoveBuffer(Entity buffer)
	{
		if (buffer == null)
			return;

		buffer.eject();
		buffer.leaveVehicle();
		buffer.remove();
	}

	public void LoadIndicators(LivingEntity ent)
	{
		LinkedList<ConditionIndicator> inds = _indicators.get(ent);
		if (inds == null)			return;

		Entity previous = null;
		for (ConditionIndicator ind : inds)
		{
			/** Spawn Indicator if Applicable **/
			if (ind.IsVisible() && !_hideIndicator.contains(ent))
			{
				//First, Add Buffer
				if (!_buffer.containsKey(ent))
				{
					Entity buffer = ent.getWorld().dropItem(ent.getLocation(), ItemStackFactory.Instance.CreateStack(Material.GHAST_TEAR));	
					ent.setPassenger(buffer);	
					_buffer.put(ent, buffer);	
					previous = buffer;
				}

				previous.setPassenger(ind.GetIndicator());
				previous = ind.GetIndicator();
			}
		}
	}

	public boolean IsSilenced(LivingEntity ent, String ability)
	{
		if (!_indicators.containsKey(ent))
			return false;

		for (ConditionIndicator ind : _indicators.get(ent))
			if (ind.GetCondition().GetType() == ConditionType.SILENCE)
			{
				if (ability != null)
				{
					if (ent instanceof Player)
					{
						if (Recharge.Instance.use((Player)ent, "Silence Feedback", 200, false))
						{
							//Inform
							UtilPlayer.message(ent, F.main("Condition", "Cannot use " + F.skill(ability) + " while silenced."));
							
							//Effect
							((Player)ent).playSound(ent.getLocation(), Sound.BAT_HURT, 0.8f, 0.8f);
						}
					}			
				}
				return true;
			}

		return false;
	}

	public boolean IsInvulnerable(LivingEntity ent)
	{
		if (!_indicators.containsKey(ent))
			return false;

		for (ConditionIndicator ind : _indicators.get(ent))
			if (ind.GetCondition().GetType() == ConditionType.INVULNERABLE)
				return true;

		return false;
	}
	
	public boolean IsCloaked(LivingEntity ent)
	{
		if (!_indicators.containsKey(ent))
			return false;

		for (ConditionIndicator ind : _indicators.get(ent))
			if (ind.GetCondition().GetType() == ConditionType.CLOAK)
				return true;

		return false;
	}

	@EventHandler
	public void DisableIndicators(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		Iterator<Entry<LivingEntity, LinkedList<ConditionIndicator>>> conditionIndIterator = _indicators.entrySet().iterator();
		
		while (conditionIndIterator.hasNext())
		{
			Entry<LivingEntity, LinkedList<ConditionIndicator>> entry = conditionIndIterator.next();
			LivingEntity ent = entry.getKey();
			
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				ent.remove();
				
				for (ConditionIndicator ind : entry.getValue())
				{
					HandlerList.unregisterAll(ind);
					
					if (ind.GetIndicator() != null)
						ind.GetIndicator().remove();
				}
				
				conditionIndIterator.remove();
			}
		}
		
		Iterator<Entry<LivingEntity, LinkedList<Condition>>> conditionIterator = _conditions.entrySet().iterator();
		
		while (conditionIterator.hasNext())
		{
			Entry<LivingEntity, LinkedList<Condition>> entry = conditionIterator.next();
			LivingEntity ent = entry.getKey();
			
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				ent.remove();
				
				conditionIterator.remove();
			}
		}
		
		Iterator<Entry<LivingEntity, Entity>> bufferIterator = _buffer.entrySet().iterator();
		
		while (bufferIterator.hasNext())
		{
			Entry<LivingEntity, Entity> entry = bufferIterator.next();
			LivingEntity ent = entry.getKey();
			
			if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player)ent).isOnline()))
			{
				ent.remove();
				
				bufferIterator.remove();
			}
		}
	}
}
