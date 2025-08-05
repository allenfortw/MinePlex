package me.chiss.Core.Modules;

import java.util.HashMap;
import java.util.LinkedList;


import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import me.chiss.Core.Wiki.WikiArticle;
import me.chiss.Core.Wiki.WikiInput;
import me.chiss.Core.Wiki.WikiUtil;
import mineplex.core.server.IRepository;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;

public class Wiki extends AModule
{
	private WikiInput _wikiInput;
	
	public Wiki(JavaPlugin plugin, IRepository repository) 
	{
		super("Wiki", plugin, repository);
	}
	
	private HashMap<String, LinkedList<WikiArticle>> _articleMap;
	private HashMap<String, String> _itemMap;
	private LinkedList<WikiArticle> _pendingMap;
	private LinkedList<WikiArticle> _deniedMap;

	@Override
	public void enable() 
	{
		_wikiInput = new WikiInput(this);
	    _articleMap = new HashMap<String, LinkedList<WikiArticle>>();
	    _itemMap = new HashMap<String, String>();
	    _pendingMap = new LinkedList<WikiArticle>();
	    _deniedMap = new LinkedList<WikiArticle>();

	    _articleMap.clear();
	    
	    /*
	    ServerWikiToken serverWikiToken = Repository.GetServerWikis();
	    
        for (ItemWikiToken item : serverWikiToken.ItemWikis)
        {
            for (WikiRevisionToken revision : item.Revisions)
            {
                AddItem(item.TypeId, item.Data, revision.Title, revision.Body, revision.Revision, revision.Author, revision.SystemTime);
                
                if (revision.Revision > 0)
                {
                    publishCount++;
                }          
                else if (revision.Revision == 0)
                {
                    pendingCount++;
                }
                else
                {
                    deniedCount++;
                }
            }
        }
        
        for (GameClassWikiToken gameClass : serverWikiToken.GameClassWikis)
        {
            for (WikiRevisionToken revision : gameClass.Revisions)
            {
                AddArticle(new WikiArticle(revision.Title, revision.Body, revision.Revision, revision.Author, revision.SystemTime));
                
                if (revision.Revision > 0)
                {
                    publishCount++;
                }          
                else if (revision.Revision == 0)
                {
                    pendingCount++;
                }
                else
                {
                    deniedCount++;
                }
            }
        }
        
        for (SkillWikiToken skillWiki : serverWikiToken.SkillWikis)
        {
            for (WikiRevisionToken revision : skillWiki.Revisions)
            {
                AddArticle(new WikiArticle(revision.Title, revision.Body, revision.Revision, revision.Author, revision.SystemTime));
                
                if (revision.Revision > 0)
                {
                    publishCount++;
                }          
                else if (revision.Revision == 0)
                {
                    pendingCount++;
                }
                else
                {
                    deniedCount++;
                }
            }
        }

        Log("Loaded " + publishCount + " Published Wiki Articles");
        Log("Loaded " + pendingCount + " Pending Wiki Articles.");
        Log("Loaded " + deniedCount + " Denied Wiki Articles.");
        Log("Loaded Wiki. " + UtilTime.since(epoch));
        */
	}

	@Override
	public void disable() 
	{
		_articleMap.clear();
	}

	@Override
	public void config() 
	{

	}
	
	@Override
	public void commands() 
	{
	    // AddCommand("w");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (args.length == 0)
		    return;
		
		// create commands
	}

	@EventHandler
	public void handleInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;

		_wikiInput.wikiBlock(event);
	}

	@EventHandler
	public void handleInteractEntity(PlayerInteractEntityEvent event) 
	{
		_wikiInput.wikiEntity(event);
	}
	
	public HashMap<String, LinkedList<WikiArticle>> GetArticles()
	{
		return _articleMap;
	}

	public HashMap<String, String> GetItems() 
	{
		return _itemMap;
	}

	public LinkedList<WikiArticle> GetArticlesPending() 
	{
		return _pendingMap;
	}


	public LinkedList<WikiArticle> GetArticlesDenied() 
	{
		return _deniedMap;
	}

    public void Display(Player player, WikiArticle article)
    {
        if (article == null)
            return;

        //Display
        UtilPlayer.message(player, article.Display(), true);

        //Save
        Clients().Get(player).Wiki().SetLastArticle(article);
        Clients().Get(player).Wiki().SetLastTime(System.currentTimeMillis());
    }

    public String link(String message) 
    {
        for (String cur : GetArticles().keySet())
        {
            String endColor = ChatColor.getLastColors(C.cWhite + message);
            message = message.replaceAll("(?i)" + C.mChat + cur, C.mLink + cur + endColor);
            message = message.replaceAll("(?i)" + " " + cur, C.mLink + " " + cur + endColor);
        }

        return message;
    }
	
    public WikiArticle searchArticle(String title, Player caller, boolean inform)
    {
        LinkedList<String> matchList = new LinkedList<String>();

        for (String cur : GetArticles().keySet())
        {
            if (cur.equalsIgnoreCase(title))
                return getActive(cur);

            if (cur.toLowerCase().contains(title.toLowerCase()))
            {
                matchList.add(cur);
            }
        }

        if (matchList.size() != 1)
        {
            if (!inform)
                return null;

            //Inform
            UtilPlayer.message(caller, F.main("Wiki Search", "" +
                    C.mCount + matchList.size() +
                    C.mBody + " matches for [" +
                    C.mElem + title +
                    C.mBody + "]."));

            if (matchList.size() > 0)
            {
                String matchString = "";
                for (String cur : matchList)
                    matchString += C.mElem + cur + C.mBody + ", ";
                matchString = matchString.substring(0, matchString.length()-2);

                UtilPlayer.message(caller, F.main("Wiki Search", "" +
                        C.mBody + "Matches [" +
                        C.mElem + matchString +
                        C.mBody + "]."));
            }

            return null;
        }

        return getActive(matchList.getFirst());
    }

    public WikiArticle getActive(String title)
    {
        if (!GetArticles().containsKey(title))
            return null;

        return GetArticles().get(title).getFirst();
    }

    public WikiArticle getActive(ItemStack stack)
    {
        String title = GetItems().get(WikiUtil.getItem(stack, false));

        if (title != null)
            return getActive(title);

        title = GetItems().get(WikiUtil.getItem(stack, true));

        if (title != null)
            return getActive(title);

        return null;
    }

    public WikiArticle getActive(Block block)
    {
        String title = GetItems().get(WikiUtil.getBlock(block, false));

        if (title != null)
            return getActive(title);

        title = GetItems().get(WikiUtil.getBlock(block, true));

        if (title != null)
            return getActive(title);

        return null;
    }
    
    public WikiArticle getRevision(String title, int revision)
    {
        if (!GetArticles().containsKey(title))
            return null;

        if (revision < 0)
            revision = 0;

        if (revision >= GetArticles().get(title).size())
            revision = GetArticles().get(title).size() - 1;

        return GetArticles().get(title).get(revision);
    }
}