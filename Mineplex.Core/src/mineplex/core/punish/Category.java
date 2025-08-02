package mineplex.core.punish;

public enum Category
{
	ChatOffense,
	Advertisement,
	Exploiting,
	Hacking,
	Other;
	
    public static boolean contains(String s) 
    {
        try 
        {
        	Category.valueOf(s);
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
     }
}
