package mineplex.minecraft.game.classcombat.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.energy.Energy;
import mineplex.core.movement.Movement;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Brute.*;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Assassin;
import mineplex.minecraft.game.classcombat.Skill.Assassin.BackStab;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Blink;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Evade;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Flash;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Leap;
import mineplex.minecraft.game.classcombat.Skill.Assassin.MarkedForDeath;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Recall;
import mineplex.minecraft.game.classcombat.Skill.Assassin.RepeatedStrikes;
import mineplex.minecraft.game.classcombat.Skill.Assassin.ShockingStrikes;
import mineplex.minecraft.game.classcombat.Skill.Assassin.SilencingArrow;
import mineplex.minecraft.game.classcombat.Skill.Assassin.SmokeBomb;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Stealth;
import mineplex.minecraft.game.classcombat.Skill.Assassin.ToxicArrow;
import mineplex.minecraft.game.classcombat.Skill.Assassin.WoundingStrikes;
import mineplex.minecraft.game.classcombat.Skill.Brute.BlockToss;
import mineplex.minecraft.game.classcombat.Skill.Brute.Bloodlust;
import mineplex.minecraft.game.classcombat.Skill.Brute.Brute;
import mineplex.minecraft.game.classcombat.Skill.Brute.Colossus;
import mineplex.minecraft.game.classcombat.Skill.Brute.CripplingBlow;
import mineplex.minecraft.game.classcombat.Skill.Brute.DwarfToss;
import mineplex.minecraft.game.classcombat.Skill.Brute.FleshHook;
import mineplex.minecraft.game.classcombat.Skill.Brute.Intimidation;
import mineplex.minecraft.game.classcombat.Skill.Brute.Overwhelm;
import mineplex.minecraft.game.classcombat.Skill.Brute.SeismicSlam;
import mineplex.minecraft.game.classcombat.Skill.Brute.Stampede;
import mineplex.minecraft.game.classcombat.Skill.Brute.Takedown;
import mineplex.minecraft.game.classcombat.Skill.Global.BreakFall;
import mineplex.minecraft.game.classcombat.Skill.Global.Fitness;
import mineplex.minecraft.game.classcombat.Skill.Global.QuickRecovery;
import mineplex.minecraft.game.classcombat.Skill.Global.Rations;
import mineplex.minecraft.game.classcombat.Skill.Global.Recharge;
import mineplex.minecraft.game.classcombat.Skill.Global.Resistance;
import mineplex.minecraft.game.classcombat.Skill.Global.Stamina;
import mineplex.minecraft.game.classcombat.Skill.Global.Swim;
import mineplex.minecraft.game.classcombat.Skill.Knight.BullsCharge;
import mineplex.minecraft.game.classcombat.Skill.Knight.Cleave;
import mineplex.minecraft.game.classcombat.Skill.Knight.DefensiveStance;
import mineplex.minecraft.game.classcombat.Skill.Knight.Deflection;
import mineplex.minecraft.game.classcombat.Skill.Knight.Fortitude;
import mineplex.minecraft.game.classcombat.Skill.Knight.HiltSmash;
import mineplex.minecraft.game.classcombat.Skill.Knight.HoldPosition;
import mineplex.minecraft.game.classcombat.Skill.Knight.Knight;
import mineplex.minecraft.game.classcombat.Skill.Knight.LevelField;
import mineplex.minecraft.game.classcombat.Skill.Knight.PowerChop;
import mineplex.minecraft.game.classcombat.Skill.Knight.Riposte;
import mineplex.minecraft.game.classcombat.Skill.Knight.Swordsmanship;
import mineplex.minecraft.game.classcombat.Skill.Knight.Vengeance;
import mineplex.minecraft.game.classcombat.Skill.Mage.ArcticArmor;
import mineplex.minecraft.game.classcombat.Skill.Mage.Blizzard;
import mineplex.minecraft.game.classcombat.Skill.Mage.FireBlast;
import mineplex.minecraft.game.classcombat.Skill.Mage.Fissure;
import mineplex.minecraft.game.classcombat.Skill.Mage.FreezingBlast;
import mineplex.minecraft.game.classcombat.Skill.Mage.GlacialBlade;
import mineplex.minecraft.game.classcombat.Skill.Mage.Immolate;
import mineplex.minecraft.game.classcombat.Skill.Mage.Inferno;
import mineplex.minecraft.game.classcombat.Skill.Mage.LifeBonds;
import mineplex.minecraft.game.classcombat.Skill.Mage.LightningOrb;
import mineplex.minecraft.game.classcombat.Skill.Mage.Mage;
import mineplex.minecraft.game.classcombat.Skill.Mage.MagmaBlade;
import mineplex.minecraft.game.classcombat.Skill.Mage.NullBlade;
import mineplex.minecraft.game.classcombat.Skill.Mage.RootingAxe;
import mineplex.minecraft.game.classcombat.Skill.Mage.Rupture;
import mineplex.minecraft.game.classcombat.Skill.Mage.Void;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Agility;
import mineplex.minecraft.game.classcombat.Skill.Ranger.BarbedArrows;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Barrage;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Disengage;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Fletcher;
import mineplex.minecraft.game.classcombat.Skill.Ranger.HealingShot;
import mineplex.minecraft.game.classcombat.Skill.Ranger.HeavyArrows;
import mineplex.minecraft.game.classcombat.Skill.Ranger.IncendiaryShot;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Longshot;
import mineplex.minecraft.game.classcombat.Skill.Ranger.NapalmShot;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Overcharge;
import mineplex.minecraft.game.classcombat.Skill.Ranger.PinDown;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Ranger;
import mineplex.minecraft.game.classcombat.Skill.Ranger.RopedArrow;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Shadowmeld;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Sharpshooter;
import mineplex.minecraft.game.classcombat.Skill.Ranger.VitalitySpores;
import mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsFury;
import mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsPounce;
import mineplex.minecraft.game.classcombat.Skill.Shifter.*;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken.ChickenForm;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken.Flap;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.GolemForm;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.MagneticPull;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.MagneticRepel;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.Needler;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.Pounce;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.SpiderForm;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.SpinWeb;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.Construction;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.Propel;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.SquidForm;
import mineplex.minecraft.game.classcombat.Skill.repository.SkillRepository;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;

import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillFactory extends MiniPlugin implements ISkillFactory
{
	private DamageManager _damageManager;
	private IRelation _relation;
	private CombatManager _combatManager;
	private ConditionManager _conditionManager;
	private ProjectileManager _projectileManager;
	private BlockRestore _blockRestore;
	private Fire _fire;
	private Movement _movement;
	private Teleport _teleport;
	private Energy _energy;
	private SkillRepository _repository;
	private HashMap<String, Skill> _skillMap;
	private HashMap<Integer, ISkill> _skillSalesPackageMap;

	public SkillFactory(JavaPlugin plugin, DamageManager damageManager, IRelation relation, 
			CombatManager combatManager, ConditionManager conditionManager, ProjectileManager projectileManager, 
			BlockRestore blockRestore, Fire fire, Movement movement, Teleport teleport, Energy energy, String webAddress) 
	{
		super("Skill Factory", plugin);
		
		_repository = new SkillRepository(webAddress);
		_damageManager = damageManager;
		_relation = relation;
		_combatManager = combatManager;
		_conditionManager = conditionManager;
		_projectileManager = projectileManager;
		_blockRestore = blockRestore;
		_fire = fire;
		_movement = movement;
		_teleport = teleport;
		_energy = energy;
		_skillMap = new HashMap<String, Skill>();
		_skillSalesPackageMap = new HashMap<Integer, ISkill>();

		PopulateSkills();
	}
	
	public ConditionManager Condition()
	{
		return _conditionManager;
	}
	
	public Teleport Teleport()
	{
		return _teleport;
	}
	
	public Energy Energy()
	{
		return _energy;
	}

	private void PopulateSkills()
	{
		_skillMap.clear();

		AddAssassin();
		AddBrute();
		AddKnight();
		AddMage();
		AddRanger();
		//AddShifter();
		AddGlobal();

		for (Skill skill : _skillMap.values())
			GetPlugin().getServer().getPluginManager().registerEvents(skill, GetPlugin());

		List<SkillToken> skillTokens = new ArrayList<SkillToken>();

		for (Skill skill : _skillMap.values())
		{
			for (int i=0; i < 1; i++)
			{
				SkillToken skillToken = new SkillToken();

				skillToken.Name = skill.GetName();				
				skillToken.Level = i + 1;
				skillToken.SalesPackage = new GameSalesPackageToken();
				skillToken.SalesPackage.Gems = 1200;

				skillTokens.add(skillToken);
			}
		}

		for (SkillToken skillToken : _repository.GetSkills(skillTokens))
		{
			if (_skillMap.containsKey(skillToken.Name))
			{
				Skill skill = _skillMap.get(skillToken.Name);
				_skillSalesPackageMap.put(skillToken.SalesPackage.GameSalesPackageId, skill);
				_skillMap.get(skillToken.Name).Update(skillToken);
			}
		}
	}

	public void AddGlobal()
	{
		//Passive C
		AddSkill(new BreakFall(this, "Break Fall", ClassType.Global, SkillType.GlobalPassive, 1, 1));	
		AddSkill(new Fitness(this, "Fitness", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new Stamina(this, "Stamina", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new Recharge(this, "Recharge", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new Resistance(this, "Resistance", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new QuickRecovery(this, "Quick Recovery", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new Swim(this, "Swim", ClassType.Global, SkillType.GlobalPassive, 1, 1));
		AddSkill(new Rations(this, "Rations", ClassType.Global, SkillType.GlobalPassive, 1, 1));
	}

	public void AddAssassin()
	{
		AddSkill(new Assassin(this, "Assassin Class", ClassType.Assassin, SkillType.Class, 0, 1));

		//Sword
		AddSkill(new Evade(this, "Evade", ClassType.Assassin, SkillType.Sword, 
				0, 1, 
				20, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		//Axe
		AddSkill(new Blink(this, "Blink", ClassType.Assassin, SkillType.Axe, 
				0, 1, 
				40, 0, 
				24000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Flash(this, "Flash", ClassType.Assassin, SkillType.Axe, 
				0, 1, 
				20, 0, 
				0, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Leap(this, "Leap", ClassType.Assassin, SkillType.Axe, 
				0, 1,
				20, 0, 
				6000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		//Bow
		AddSkill(new MarkedForDeath(this, "Marked for Death", ClassType.Assassin, SkillType.Bow, 
				0, 1,
				40, 0, 
				15000, 0, true,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new ToxicArrow(this, "Toxic Arrow", ClassType.Assassin, SkillType.Bow, 
				0, 1,
				40, 0, 
				15000, 0, true,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new SilencingArrow(this, "Silencing Arrow", ClassType.Assassin, SkillType.Bow, 	
				0, 1,
				40, 0, 
				15000, 0, true,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));


		//Passive A
		AddSkill(new SmokeBomb(this, "Smoke Bomb", ClassType.Assassin, SkillType.PassiveA, 0, 1));
		AddSkill(new Stealth(this, "Stealth", ClassType.Assassin, SkillType.PassiveA, 0, 1));
		AddSkill(new Recall(this, "Recall", ClassType.Assassin, SkillType.PassiveA, 0, 1));

		//Passive B
		AddSkill(new ShockingStrikes(this, "Shocking Strikes", ClassType.Assassin, SkillType.PassiveB, 0, 1));
		AddSkill(new RepeatedStrikes(this, "Repeated Strikes", ClassType.Assassin, SkillType.PassiveB, 0, 1));
		AddSkill(new WoundingStrikes(this, "Wounding Strikes", ClassType.Assassin, SkillType.PassiveB, 0, 1));
		AddSkill(new BackStab(this, "Backstab", ClassType.Assassin, SkillType.PassiveB, 0, 1));
	}

	public void AddBrute()
	{
		AddSkill(new Brute(this, "Brute Class", ClassType.Brute, SkillType.Class, 0, 1));


		//Sword
		AddSkill(new DwarfToss(this, "Dwarf Toss", ClassType.Brute, SkillType.Sword, 
				0, 1,
				20, 0, 
				20000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new FleshHook(this, "Flesh Hook", ClassType.Brute, SkillType.Sword, 
				10, 5,
				20, 0, 
				10000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new BlockToss(this, "Block Toss", ClassType.Brute, SkillType.Sword, 10, 5));


		//Axe
		AddSkill(new SeismicSlam(this, "Seismic Slam", ClassType.Brute, SkillType.Axe, 
				0, 1,
				40, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Rampage(this, "Rampage", ClassType.Brute, SkillType.Axe, 
				10, 5,
				60, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Takedown(this, "Takedown", ClassType.Brute, SkillType.Axe, 
				10, 5,
				60, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));


		//Passive A
		AddSkill(new Stampede(this, "Stampede", ClassType.Brute, SkillType.PassiveA, 0, 1));
		AddSkill(new Bloodlust(this, "Bloodlust", ClassType.Brute, SkillType.PassiveA, 0, 1));
		AddSkill(new Intimidation(this, "Intimidation", ClassType.Brute, SkillType.PassiveA, 0, 1));

		//Passive B
		AddSkill(new CripplingBlow(this, "Crippling Blow", ClassType.Brute, SkillType.PassiveB, 0, 1));
		AddSkill(new Colossus(this, "Colossus", ClassType.Brute, SkillType.PassiveB, 0, 1));
		AddSkill(new Overwhelm(this, "Overwhelm", ClassType.Brute, SkillType.PassiveB, 0, 1));
	}

	public void AddKnight()
	{
		AddSkill(new Knight(this, "Knight Class", ClassType.Knight, SkillType.Class, 0, 1));

		//Sword
		AddSkill(new HiltSmash(this, "Hilt Smash", ClassType.Knight, SkillType.Sword,
				0, 1,
				30, 0, 
				10000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Riposte(this, "Riposte", ClassType.Knight, SkillType.Sword, 
				0, 1,
				40, 0, 
				4000, 0, false,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new DefensiveStance(this, "Defensive Stance", ClassType.Knight, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));


		//Axe
		AddSkill(new BullsCharge(this, "Bulls Charge", ClassType.Knight, SkillType.Axe, 
				0, 1,
				40, 0, 
				10000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new HoldPosition(this, "Hold Position", ClassType.Knight, SkillType.Axe, 
				0, 1,
				60, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new PowerChop(this, "Power Chop", ClassType.Knight, SkillType.Axe, 				
				0, 1,
				12, 0, 
				0, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));


		//Passive A
		AddSkill(new Cleave(this, "Cleave", ClassType.Knight, SkillType.PassiveA, 0, 1));
		AddSkill(new Swordsmanship(this, "Swordsmanship", ClassType.Knight, SkillType.PassiveA, 0, 1));
		AddSkill(new Deflection(this, "Deflection", ClassType.Knight, SkillType.PassiveA, 0, 1));

		//Passive B
		AddSkill(new Vengeance(this, "Vengeance", ClassType.Knight, SkillType.PassiveB, 0, 1));
		AddSkill(new Fortitude(this, "Fortitude", ClassType.Knight, SkillType.PassiveB, 0, 1));
		AddSkill(new LevelField(this, "Level Field", ClassType.Knight, SkillType.PassiveB, 0, 1));
	}

	public void AddMage()
	{
		AddSkill(new Mage(this, "Mage Class", ClassType.Mage, SkillType.Class, 0, 1));

		//Sword
		AddSkill(new Blizzard(this, "Blizzard", ClassType.Mage, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		/*
		AddSkill(new Magnetize(this, "Magnetize", ClassType.Mage, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));
		 */
		AddSkill(new Inferno(this, "Inferno", ClassType.Mage, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Rupture(this, "Rupture", ClassType.Mage, SkillType.Sword, 
				0, 1,
				20, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		//Axe
		AddSkill(new FireBlast(this, "Fire Blast", ClassType.Mage, SkillType.Axe, 
				0, 1,
				40, 0, 
				15000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new LightningOrb(this, "Lightning Orb", ClassType.Mage, SkillType.Axe, 
				0, 1,
				40, 0, 
				20000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new FreezingBlast(this, "Freezing Blast", ClassType.Mage, SkillType.Axe, 
				0, 1,
				40, 0, 
				20000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new Fissure(this, "Fissure", ClassType.Mage, SkillType.Axe, 
				0, 1,
				40, 0, 
				20000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		// AddSkill(new Tundra(this, "Tundra", ClassType.Mage, SkillType.Axe, 830, 200, 1));


		//Passive A
		AddSkill(new ArcticArmor(this, "Arctic Armor", ClassType.Mage, SkillType.PassiveA, 20, 1));
		AddSkill(new Immolate(this, "Immolate", ClassType.Mage, SkillType.PassiveA, 20, 1));
		AddSkill(new Void(this, "Void", ClassType.Mage, SkillType.PassiveA, 5, 5));
		AddSkill(new LifeBonds(this, "Life Bonds", ClassType.Mage, SkillType.PassiveA, 20, 1));


		//Passive B
		AddSkill(new GlacialBlade(this, "Glacial Blade", ClassType.Mage, SkillType.PassiveB, 
				0, 1,
				4, 0, 
				350, 0, false,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new MagmaBlade(this, "Magma Blade", ClassType.Mage, SkillType.PassiveB, 0, 1));
		AddSkill(new NullBlade(this, "Null Blade", ClassType.Mage, SkillType.PassiveB, 0, 1));
		AddSkill(new RootingAxe(this, "Rooting Axe", ClassType.Mage, SkillType.PassiveB, 0, 1));
	}

	public void AddRanger()
	{
		AddSkill(new Ranger(this, "Ranger Class", ClassType.Ranger, SkillType.Class, 0, 1));

		//Sword
		AddSkill(new Disengage(this, "Disengage", ClassType.Ranger, SkillType.Sword, 
				0, 1,
				40, 0, 
				12000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new WolfsPounce(this, "Wolfs Pounce", ClassType.Ranger, SkillType.Sword, 0, 1));


		//Axe
		AddSkill(new Agility(this, "Agility", ClassType.Ranger, SkillType.Axe, 
				0, 1,
				60, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new WolfsFury(this, "Wolfs Fury", ClassType.Ranger, SkillType.Axe, 
				0, 1,
				60, 0, 
				30000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		//Bow
		AddSkill(new HealingShot(this, "Healing Shot", ClassType.Ranger, SkillType.Bow, 
				0, 1,
				40, 0, 
				10000, 0, true,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new IncendiaryShot(this, "Incendiary Shot", ClassType.Ranger, SkillType.Bow, 
				0, 1,
				0, 1,
				10000, 0, false,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new NapalmShot(this, "Napalm Shot", ClassType.Ranger, SkillType.Bow, 				
				0, 1,
				40, 0, 
				15000, 0, true,
				new Material[] {Material.BOW},  
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new PinDown(this, "Pin Down", ClassType.Ranger, SkillType.Bow, 
				0, 1,
				30, 0, 
				8000, 0, true,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		AddSkill(new RopedArrow(this, "Roped Arrow", ClassType.Ranger, SkillType.Bow, 
				0, 1,
				10, 0, 
				250, 0, false,
				new Material[] {Material.BOW}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));

		//Passive A
		AddSkill(new Barrage(this, "Barrage", ClassType.Ranger, SkillType.PassiveA, 0, 1));
		AddSkill(new Overcharge(this, "Overcharge", ClassType.Ranger, SkillType.PassiveA, 0, 1));
		AddSkill(new VitalitySpores(this, "Vitality Spores", ClassType.Ranger, SkillType.PassiveA, 0, 1));

		//Passive B	
		AddSkill(new BarbedArrows(this, "Barbed Arrows", ClassType.Ranger, SkillType.PassiveB, 0, 1));
		AddSkill(new HeavyArrows(this, "Heavy Arrows", ClassType.Ranger, SkillType.PassiveB, 0, 1));
		AddSkill(new Shadowmeld(this, "Shadowmeld", ClassType.Ranger, SkillType.PassiveB, 0, 1));
		AddSkill(new Longshot(this, "Longshot", ClassType.Ranger, SkillType.PassiveB, 0, 1));
		AddSkill(new Sharpshooter(this, "Sharpshooter", ClassType.Ranger, SkillType.PassiveB, 0, 1));
		AddSkill(new Fletcher(this, "Fletcher", ClassType.Ranger, SkillType.PassiveB, 0, 1));
	}

	public void AddShifter()
	{
		AddSkill(new Shifter(this, "Shifter Class", ClassType.Shifter, SkillType.Class, 0, 1));

		//Axe
		AddSkill(new TreeShift(this, "Tree Shift", ClassType.Shifter, SkillType.Axe, 
				0, 1,
				40, 0, 
				4000, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		//Sword
		AddSkill(new Polysmash(this, "Polysmash", ClassType.Shifter, SkillType.Sword,
				0, 1,
				30, 0, 
				16000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));
		
		//CHICKEN=========================================================================================
		AddSkill(new ChickenForm(this, "Chicken Form", ClassType.Shifter, SkillType.PassiveB, 0, 5));

		AddSkill(new Flap(this, "Flap", ClassType.Shifter, SkillType.Sword, 
				0, 1,
				5, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));


		
		//SQUID=========================================================================================
		AddSkill(new SquidForm(this, "Squid Form", ClassType.Shifter, SkillType.PassiveB, 0, 5));

		AddSkill(new Propel(this, "Propel", ClassType.Shifter, SkillType.Sword, 
				0, 1,
				12, 0, 
				250, 0, false,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));
		
		AddSkill(new Construction(this, "Ice Construction", ClassType.Shifter, SkillType.Axe, 
				0, 1,
				8, 0, 
				0, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK}));


		//GOLEM=========================================================================================
		AddSkill(new GolemForm(this, "Magnetic Golem Form", ClassType.Shifter, SkillType.PassiveA, 0, 5));

		AddSkill(new MagneticPull(this, "Magnetic Pull", ClassType.Shifter, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new MagneticRepel(this, "Magnetic Repel", ClassType.Shifter, SkillType.Axe, 
				0, 1,
				60, 0, 
				30000, -3000, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		
		//SPIDER=========================================================================================
		AddSkill(new SpiderForm(this, "Spitting Spider Form", ClassType.Shifter, SkillType.PassiveA, 0, 5));

		AddSkill(new Needler(this, "Needler", ClassType.Shifter, SkillType.Sword, 
				0, 1,
				0, 0, 
				0, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD}, 
				new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));

		AddSkill(new SpinWeb(this, "Spin Web", ClassType.Shifter, SkillType.Axe, 
				0, 1,
				20, -1, 
				0, 0, true,
				new Material[] {Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.RIGHT_CLICK_BLOCK}));
		
		AddSkill(new Pounce(this, "Pounce", ClassType.Shifter, SkillType.PassiveB, 
				0, 1,
				20, 0, 
				6000, 0, true,
				new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE}, 
				new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
	}

	public ISkill GetSkillBySalesPackageId(int id)
	{
		return _skillSalesPackageMap.get(id);
	}

	public Skill GetSkill(String skillName)
	{
		return _skillMap.get(skillName);
	}

	public Collection<Skill> GetAllSkills()
	{
		return _skillMap.values();
	}

	public void AddSkill(Skill skill)
	{
		_skillMap.put(skill.GetName(), skill);
	}

	public void RemoveSkill(String skillName, String defaultReplacement)
	{
		if (skillName == null)
		{
			System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
			return;
		}

		Skill remove = _skillMap.get(skillName);
		if (remove == null)
		{
			System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
			return;
		}

		Skill replacement = null;
		if (defaultReplacement != null)
		{
			replacement = _skillMap.get(defaultReplacement);
			if (replacement == null)
			{
				System.out.println("[Skill Factory] Remove Skill: Replacement Skill NULL [" + defaultReplacement + "].");
				return;
			}
		}

		//Remove
		_skillMap.remove(remove.GetName());
		HandlerList.unregisterAll(remove);

		System.out.println("Skill Factory: Removed " + remove.GetName() + " from SkillMap.");
	}

	@Override
	public List<ISkill> GetGlobalSkills() 
	{
		List<ISkill> skills = new LinkedList<ISkill>();

		for (ISkill cur : _skillMap.values())
		{
			if (cur.GetClassType() == ClassType.Global)
			{
				skills.add(cur);
			}
		}

		return skills;
	}

	@Override
	public List<ISkill> GetSkillsFor(IPvpClass gameClass) 
	{
		List<ISkill> skills = new LinkedList<ISkill>();

		for (ISkill cur : _skillMap.values())
		{
			if (cur.GetClassType() == gameClass.GetType())
			{
				skills.add(cur);
			}
		}

		return skills;
	}

	//Called once, upon Class creation.
	@Override
	public HashMap<ISkill, Integer> GetDefaultSkillsFor(IPvpClass classType) 
	{
		HashMap<ISkill, Integer> skills = new HashMap<ISkill, Integer>();
		if (classType.GetType() == ClassType.Knight)
		{
			AddSkill(skills, "Knight Class", 1);         	 //Class

			AddSkill(skills, "Bulls Charge", 1);			//Axe
			AddSkill(skills, "Riposte", 1);					//Sword
			AddSkill(skills, "Deflection", 1);				//Passive A
			AddSkill(skills, "Vengeance", 1);				//Passive B

			AddSkill(skills, "Resistance", 1);				//Passive C
		}

		else if (classType.GetType() == ClassType.Ranger)
		{
			AddSkill(skills, "Ranger Class", 1);          	//Class

			AddSkill(skills, "Napalm Shot", 1);				//Bow
			AddSkill(skills, "Agility", 1);					//Axe
			AddSkill(skills, "Disengage", 1);				//Sword
			AddSkill(skills, "Barrage", 1);					//Passive A
			AddSkill(skills, "Barbed Arrows", 1);			//Passive B

			AddSkill(skills, "Quick Recovery", 1);			//Passive D
		}

		else if (classType.GetType() == ClassType.Brute)
		{
			AddSkill(skills, "Brute Class", 1);              //Class

			AddSkill(skills, "Seismic Slam", 1);			//Axe
			AddSkill(skills, "Dwarf Toss", 1);				//Sword
			AddSkill(skills, "Stampede", 1);				//Passive A
			AddSkill(skills, "Crippling Blow", 1);			//Passive B

			AddSkill(skills, "Resistance", 1);				//Passive C
		}

		else if (classType.GetType() == ClassType.Assassin)
		{
			AddSkill(skills, "Assassin Class", 1);          //Class

			AddSkill(skills, "Blink", 1);					//Axe
			AddSkill(skills, "Evade", 1);					//Sword
			AddSkill(skills, "Toxic Arrow", 1);				//Bow
			AddSkill(skills, "Smoke Bomb", 1);				//Passive A
			AddSkill(skills, "Repeated Strikes", 1);		//Passive B

			AddSkill(skills, "Break Fall", 1);				//Passive C
		}

		else if (classType.GetType() == ClassType.Mage)
		{
			AddSkill(skills, "Mage Class", 1);              //Class

			AddSkill(skills, "Freezing Blast", 1);			//Axe
			AddSkill(skills, "Blizzard", 1);				//Sword
			AddSkill(skills, "Arctic Armor", 1);			//Passive A
			AddSkill(skills, "Glacial Blade", 1);			//Passive B

			AddSkill(skills, "Fitness", 1);					//Passive D
		}

		else if (classType.GetType() == ClassType.Shifter)
		{
			AddSkill(skills, "Shifter Class", 1);      		//Class

			AddSkill(skills, "Tree Shift", 1);				//Axe
			AddSkill(skills, "Polysmash", 1);				//Sword
			AddSkill(skills, "Golem Form", 1);				//Passive A
			AddSkill(skills, "Chicken Form", 1);			//Passive B

			AddSkill(skills, "Quick Recovery", 1);			//Passive D
		}

		skills.remove(null);

		return skills;
	}

	public void AddSkill(HashMap<ISkill, Integer> skills, String skillName, int level)
	{
		ISkill skill = GetSkill(skillName);

		if (skill == null)
			return;

		skills.put(skill, level);
	}

	public Movement Movement()
	{
		return _movement;
	}
	
	public DamageManager Damage()
	{
		return _damageManager;
	}
	
	public CombatManager Combat()
	{
		return _combatManager;
	}
	
	public ProjectileManager Projectile()
	{
		return _projectileManager;
	}
	
	public BlockRestore BlockRestore()
	{
		return _blockRestore;
	}
	
	public Fire Fire()
	{
		return _fire;
	}
	
	public IRelation Relation()
	{
		return _relation;
	}
}
