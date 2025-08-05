package nautilus.minecraft.core.utils;

import java.util.regex.Pattern;

public class ColorUtil 
{
	private static transient final Pattern REPLACE_COLOR_PATTERN = Pattern.compile("&([0-9a-f])");
	
	public static String formatString(final String input)
	{
		if (input == null)
		{
			return null;
		}

		return REPLACE_COLOR_PATTERN.matcher(input).replaceAll("\u00a7$1");
	}
}
