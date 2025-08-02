package mineplex.minecraft.game.classcombat.Skill;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import mineplex.minecraft.game.classcombat.Skill.Brute.Rampage;
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
import mineplex.minecraft.game.classcombat.Skill.Ranger.PinDown;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Ranger;
import mineplex.minecraft.game.classcombat.Skill.Ranger.RopedArrow;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Shadowmeld;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Sharpshooter;
import mineplex.minecraft.game.classcombat.Skill.Ranger.VitalitySpores;
import mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsFury;
import mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsPounce;
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
import mineplex.minecraft.game.classcombat.Skill.Shifter.Polysmash;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Shifter;
import mineplex.minecraft.game.classcombat.Skill.Shifter.TreeShift;
import mineplex.minecraft.game.classcombat.Skill.repository.SkillRepository;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;
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
  
  public SkillFactory(JavaPlugin plugin, DamageManager damageManager, IRelation relation, CombatManager combatManager, ConditionManager conditionManager, ProjectileManager projectileManager, BlockRestore blockRestore, Fire fire, Movement movement, Teleport teleport, Energy energy, String webAddress)
  {
    super("Skill Factory", plugin);
    
    this._repository = new SkillRepository(webAddress);
    this._damageManager = damageManager;
    this._relation = relation;
    this._combatManager = combatManager;
    this._conditionManager = conditionManager;
    this._projectileManager = projectileManager;
    this._blockRestore = blockRestore;
    this._fire = fire;
    this._movement = movement;
    this._teleport = teleport;
    this._energy = energy;
    this._skillMap = new HashMap();
    this._skillSalesPackageMap = new HashMap();
    
    PopulateSkills();
  }
  
  public ConditionManager Condition()
  {
    return this._conditionManager;
  }
  
  public Teleport Teleport()
  {
    return this._teleport;
  }
  
  public Energy Energy()
  {
    return this._energy;
  }
  
  private void PopulateSkills()
  {
    this._skillMap.clear();
    
    AddAssassin();
    AddBrute();
    AddKnight();
    AddMage();
    AddRanger();
    
    AddGlobal();
    
    for (Skill skill : this._skillMap.values()) {
      GetPlugin().getServer().getPluginManager().registerEvents(skill, GetPlugin());
    }
    List<SkillToken> skillTokens = new ArrayList();
    int i;
    for (Iterator localIterator2 = this._skillMap.values().iterator(); localIterator2.hasNext(); 
        
        i < 1)
    {
      Skill skill = (Skill)localIterator2.next();
      
      i = 0; continue;
      
      SkillToken skillToken = new SkillToken();
      
      skillToken.Name = skill.GetName();
      skillToken.Level = Integer.valueOf(i + 1);
      skillToken.SalesPackage = new GameSalesPackageToken();
      skillToken.SalesPackage.Gems = Integer.valueOf(1200);
      
      skillTokens.add(skillToken);i++;
    }
    










    for (SkillToken skillToken : this._repository.GetSkills(skillTokens))
    {
      if (this._skillMap.containsKey(skillToken.Name))
      {
        Skill skill = (Skill)this._skillMap.get(skillToken.Name);
        this._skillSalesPackageMap.put(skillToken.SalesPackage.GameSalesPackageId, skill);
        ((Skill)this._skillMap.get(skillToken.Name)).Update(skillToken);
      }
    }
  }
  

  public void AddGlobal()
  {
    AddSkill(new BreakFall(this, "Break Fall", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Fitness(this, "Fitness", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Stamina(this, "Stamina", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Recharge(this, "Recharge", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Resistance(this, "Resistance", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new QuickRecovery(this, "Quick Recovery", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Swim(this, "Swim", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
    AddSkill(new Rations(this, "Rations", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 1));
  }
  
  public void AddAssassin()
  {
    AddSkill(new Assassin(this, "Assassin Class", IPvpClass.ClassType.Assassin, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new Evade(this, "Evade", IPvpClass.ClassType.Assassin, ISkill.SkillType.Sword, 
      0, 1, 
      20, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new Blink(this, "Blink", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      24000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Flash(this, "Flash", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      0, 1, 
      20, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Leap(this, "Leap", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      0, 1, 
      20, 0, 
      6000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new MarkedForDeath(this, "Marked for Death", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      0, 1, 
      40, 0, 
      15000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new ToxicArrow(this, "Toxic Arrow", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      0, 1, 
      40, 0, 
      15000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new SilencingArrow(this, "Silencing Arrow", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      0, 1, 
      40, 0, 
      15000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    


    AddSkill(new SmokeBomb(this, "Smoke Bomb", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Stealth(this, "Stealth", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Recall(this, "Recall", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveA, 0, 1));
    

    AddSkill(new ShockingStrikes(this, "Shocking Strikes", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new RepeatedStrikes(this, "Repeated Strikes", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new WoundingStrikes(this, "Wounding Strikes", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new BackStab(this, "Backstab", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 0, 1));
  }
  
  public void AddBrute()
  {
    AddSkill(new Brute(this, "Brute Class", IPvpClass.ClassType.Brute, ISkill.SkillType.Class, 0, 1));
    


    AddSkill(new DwarfToss(this, "Dwarf Toss", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 
      0, 1, 
      20, 0, 
      20000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new FleshHook(this, "Flesh Hook", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 
      10, 5, 
      20, 0, 
      10000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new BlockToss(this, "Block Toss", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 10, 5));
    


    AddSkill(new SeismicSlam(this, "Seismic Slam", IPvpClass.ClassType.Brute, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Rampage(this, "Rampage", IPvpClass.ClassType.Brute, ISkill.SkillType.Axe, 
      10, 5, 
      60, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Takedown(this, "Takedown", IPvpClass.ClassType.Brute, ISkill.SkillType.Axe, 
      10, 5, 
      60, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new Stampede(this, "Stampede", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Bloodlust(this, "Bloodlust", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Intimidation(this, "Intimidation", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 0, 1));
    

    AddSkill(new CripplingBlow(this, "Crippling Blow", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Colossus(this, "Colossus", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Overwhelm(this, "Overwhelm", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 0, 1));
  }
  
  public void AddKnight()
  {
    AddSkill(new Knight(this, "Knight Class", IPvpClass.ClassType.Knight, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new HiltSmash(this, "Hilt Smash", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      0, 1, 
      30, 0, 
      10000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Riposte(this, "Riposte", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      0, 1, 
      40, 0, 
      4000L, 0L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new DefensiveStance(this, "Defensive Stance", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new BullsCharge(this, "Bulls Charge", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      10000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new HoldPosition(this, "Hold Position", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      0, 1, 
      60, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new PowerChop(this, "Power Chop", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      0, 1, 
      12, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new Cleave(this, "Cleave", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Swordsmanship(this, "Swordsmanship", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new Deflection(this, "Deflection", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 0, 1));
    

    AddSkill(new Vengeance(this, "Vengeance", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Fortitude(this, "Fortitude", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new LevelField(this, "Level Field", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 0, 1));
  }
  
  public void AddMage()
  {
    AddSkill(new Mage(this, "Mage Class", IPvpClass.ClassType.Mage, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new Blizzard(this, "Blizzard", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    








    AddSkill(new Inferno(this, "Inferno", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Rupture(this, "Rupture", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      0, 1, 
      20, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new FireBlast(this, "Fire Blast", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      15000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new LightningOrb(this, "Lightning Orb", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new FreezingBlast(this, "Freezing Blast", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Fissure(this, "Fissure", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    




    AddSkill(new ArcticArmor(this, "Arctic Armor", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 20, 1));
    AddSkill(new Immolate(this, "Immolate", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 20, 1));
    AddSkill(new Void(this, "Void", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 5, 5));
    AddSkill(new LifeBonds(this, "Life Bonds", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 20, 1));
    


    AddSkill(new GlacialBlade(this, "Glacial Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 
      0, 1, 
      4, 0, 
      350L, 0L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new MagmaBlade(this, "Magma Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new NullBlade(this, "Null Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new RootingAxe(this, "Rooting Axe", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 0, 1));
  }
  
  public void AddRanger()
  {
    AddSkill(new Ranger(this, "Ranger Class", IPvpClass.ClassType.Ranger, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new Disengage(this, "Disengage", IPvpClass.ClassType.Ranger, ISkill.SkillType.Sword, 
      0, 1, 
      40, 0, 
      12000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new WolfsPounce(this, "Wolfs Pounce", IPvpClass.ClassType.Ranger, ISkill.SkillType.Sword, 0, 1));
    


    AddSkill(new Agility(this, "Agility", IPvpClass.ClassType.Ranger, ISkill.SkillType.Axe, 
      0, 1, 
      60, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new WolfsFury(this, "Wolfs Fury", IPvpClass.ClassType.Ranger, ISkill.SkillType.Axe, 
      0, 1, 
      60, 0, 
      30000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new HealingShot(this, "Healing Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      0, 1, 
      40, 0, 
      10000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new IncendiaryShot(this, "Incendiary Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      0, 1, 
      0, 1, 
      10000L, 0L, false, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new NapalmShot(this, "Napalm Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      0, 1, 
      40, 0, 
      15000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new PinDown(this, "Pin Down", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      0, 1, 
      30, 0, 
      8000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new RopedArrow(this, "Roped Arrow", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      0, 1, 
      10, 0, 
      250L, 0L, false, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    

    AddSkill(new Barrage(this, "Barrage", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Overcharge(this, "Overcharge", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 0, 1));
    AddSkill(new VitalitySpores(this, "Vitality Spores", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 0, 1));
    

    AddSkill(new BarbedArrows(this, "Barbed Arrows", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new HeavyArrows(this, "Heavy Arrows", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Shadowmeld(this, "Shadowmeld", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Longshot(this, "Longshot", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Sharpshooter(this, "Sharpshooter", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
    AddSkill(new Fletcher(this, "Fletcher", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 0, 1));
  }
  
  public void AddShifter()
  {
    AddSkill(new Shifter(this, "Shifter Class", IPvpClass.ClassType.Shifter, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new TreeShift(this, "Tree Shift", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      4000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new Polysmash(this, "Polysmash", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      30, 0, 
      16000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new ChickenForm(this, "Chicken Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 0, 5));
    
    AddSkill(new Flap(this, "Flap", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      5, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    



    AddSkill(new SquidForm(this, "Squid Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 0, 5));
    
    AddSkill(new Propel(this, "Propel", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      12, 0, 
      250L, 0L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Construction(this, "Ice Construction", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      8, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new GolemForm(this, "Magnetic Golem Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveA, 0, 5));
    
    AddSkill(new MagneticPull(this, "Magnetic Pull", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new MagneticRepel(this, "Magnetic Repel", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      60, 0, 
      30000L, -3000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new SpiderForm(this, "Spitting Spider Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveA, 0, 5));
    
    AddSkill(new Needler(this, "Needler", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new SpinWeb(this, "Spin Web", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      20, -1, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Pounce(this, "Pounce", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 
      0, 1, 
      20, 0, 
      6000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
  }
  
  public ISkill GetSkillBySalesPackageId(int id)
  {
    return (ISkill)this._skillSalesPackageMap.get(Integer.valueOf(id));
  }
  
  public Skill GetSkill(String skillName)
  {
    return (Skill)this._skillMap.get(skillName);
  }
  
  public Collection<Skill> GetAllSkills()
  {
    return this._skillMap.values();
  }
  
  public void AddSkill(Skill skill)
  {
    this._skillMap.put(skill.GetName(), skill);
  }
  
  public void RemoveSkill(String skillName, String defaultReplacement)
  {
    if (skillName == null)
    {
      System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
      return;
    }
    
    Skill remove = (Skill)this._skillMap.get(skillName);
    if (remove == null)
    {
      System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
      return;
    }
    
    Skill replacement = null;
    if (defaultReplacement != null)
    {
      replacement = (Skill)this._skillMap.get(defaultReplacement);
      if (replacement == null)
      {
        System.out.println("[Skill Factory] Remove Skill: Replacement Skill NULL [" + defaultReplacement + "].");
        return;
      }
    }
    

    this._skillMap.remove(remove.GetName());
    HandlerList.unregisterAll(remove);
    
    System.out.println("Skill Factory: Removed " + remove.GetName() + " from SkillMap.");
  }
  

  public List<ISkill> GetGlobalSkills()
  {
    List<ISkill> skills = new LinkedList();
    
    for (ISkill cur : this._skillMap.values())
    {
      if (cur.GetClassType() == IPvpClass.ClassType.Global)
      {
        skills.add(cur);
      }
    }
    
    return skills;
  }
  

  public List<ISkill> GetSkillsFor(IPvpClass gameClass)
  {
    List<ISkill> skills = new LinkedList();
    
    for (ISkill cur : this._skillMap.values())
    {
      if (cur.GetClassType() == gameClass.GetType())
      {
        skills.add(cur);
      }
    }
    
    return skills;
  }
  


  public HashMap<ISkill, Integer> GetDefaultSkillsFor(IPvpClass classType)
  {
    HashMap<ISkill, Integer> skills = new HashMap();
    if (classType.GetType() == IPvpClass.ClassType.Knight)
    {
      AddSkill(skills, "Knight Class", 1);
      
      AddSkill(skills, "Bulls Charge", 1);
      AddSkill(skills, "Riposte", 1);
      AddSkill(skills, "Deflection", 1);
      AddSkill(skills, "Vengeance", 1);
      
      AddSkill(skills, "Resistance", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Ranger)
    {
      AddSkill(skills, "Ranger Class", 1);
      
      AddSkill(skills, "Napalm Shot", 1);
      AddSkill(skills, "Agility", 1);
      AddSkill(skills, "Disengage", 1);
      AddSkill(skills, "Barrage", 1);
      AddSkill(skills, "Barbed Arrows", 1);
      
      AddSkill(skills, "Quick Recovery", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Brute)
    {
      AddSkill(skills, "Brute Class", 1);
      
      AddSkill(skills, "Seismic Slam", 1);
      AddSkill(skills, "Dwarf Toss", 1);
      AddSkill(skills, "Stampede", 1);
      AddSkill(skills, "Crippling Blow", 1);
      
      AddSkill(skills, "Resistance", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Assassin)
    {
      AddSkill(skills, "Assassin Class", 1);
      
      AddSkill(skills, "Blink", 1);
      AddSkill(skills, "Evade", 1);
      AddSkill(skills, "Toxic Arrow", 1);
      AddSkill(skills, "Smoke Bomb", 1);
      AddSkill(skills, "Repeated Strikes", 1);
      
      AddSkill(skills, "Break Fall", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Mage)
    {
      AddSkill(skills, "Mage Class", 1);
      
      AddSkill(skills, "Freezing Blast", 1);
      AddSkill(skills, "Blizzard", 1);
      AddSkill(skills, "Arctic Armor", 1);
      AddSkill(skills, "Glacial Blade", 1);
      
      AddSkill(skills, "Fitness", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Shifter)
    {
      AddSkill(skills, "Shifter Class", 1);
      
      AddSkill(skills, "Tree Shift", 1);
      AddSkill(skills, "Polysmash", 1);
      AddSkill(skills, "Golem Form", 1);
      AddSkill(skills, "Chicken Form", 1);
      
      AddSkill(skills, "Quick Recovery", 1);
    }
    
    skills.remove(null);
    
    return skills;
  }
  
  public void AddSkill(HashMap<ISkill, Integer> skills, String skillName, int level)
  {
    ISkill skill = GetSkill(skillName);
    
    if (skill == null) {
      return;
    }
    skills.put(skill, Integer.valueOf(level));
  }
  
  public Movement Movement()
  {
    return this._movement;
  }
  
  public DamageManager Damage()
  {
    return this._damageManager;
  }
  
  public CombatManager Combat()
  {
    return this._combatManager;
  }
  
  public ProjectileManager Projectile()
  {
    return this._projectileManager;
  }
  
  public BlockRestore BlockRestore()
  {
    return this._blockRestore;
  }
  
  public Fire Fire()
  {
    return this._fire;
  }
  
  public IRelation Relation()
  {
    return this._relation;
  }
}
