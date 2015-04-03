package net.gliby.voicechat.client.gui.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum CustomEnumFormatting
{
    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1);
    /** Maps a name (e.g., 'underline') to its corresponding enum value (e.g., UNDERLINE). */
    private static final Map nameMapping = Maps.newHashMap();
    /**
     * Matches formatting codes that indicate that the client should treat the following text as bold, recolored,
     * obfuscated, etc.
     */
    private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
    /** The name of this color/formatting */
    private final String name;
    /** The formatting code that produces this format. */
    private final char formattingCode;
    private final boolean fancyStyling;
    /**
     * The control string (section sign + formatting code) that can be inserted into client-side text to display
     * subsequent text in this format.
     */
    private final String controlString;
    /** The numerical index that represents this color */
    private final int colorIndex;

    private static final String __OBFID = "CL_00000342";

    private static String func_175745_c(String p_175745_0_)
    {
        return p_175745_0_.toLowerCase().replaceAll("[^a-z]", "");
    }

    private CustomEnumFormatting(String formattingName, char formattingCodeIn, int colorIndex)
    {
        this(formattingName, formattingCodeIn, false, colorIndex);
    }

    private CustomEnumFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn)
    {
        this(formattingName, formattingCodeIn, fancyStylingIn, -1);
    }

    private CustomEnumFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex)
    {
        this.name = formattingName;
        this.formattingCode = formattingCodeIn;
        this.fancyStyling = fancyStylingIn;
        this.colorIndex = colorIndex;
        this.controlString = "\u00a7" + formattingCodeIn;
    }

    /**
     * Returns the numerical color index that represents this formatting
     */
    public int getColorIndex()
    {
        return this.colorIndex;
    }

    /**
     * False if this is just changing the color or resetting; true otherwise.
     */
    public boolean isFancyStyling()
    {
        return this.fancyStyling;
    }

    /**
     * Checks if this is a color code.
     */
    public boolean isColor()
    {
        return !this.fancyStyling && this != RESET;
    }

    /**
     * Gets the friendly name of this value.
     */
    public String getFriendlyName()
    {
        return this.name().toLowerCase();
    }

    public String toString()
    {
        return this.controlString;
    }

    /**
     * Returns a copy of the given string, with formatting codes stripped away.
     *  
     * @param text The text to strip formatting codes from
     */
    @SideOnly(Side.CLIENT)
    public static String getTextWithoutFormattingCodes(String text)
    {
        return text == null ? null : formattingCodePattern.matcher(text).replaceAll("");
    }

    /**
     * Gets a value by its friendly name; null if the given name does not map to a defined value.
     *  
     * @param friendlyName The friendly name
     */
    public static CustomEnumFormatting getValueByName(String friendlyName)
    {
        return friendlyName == null ? null : (CustomEnumFormatting)nameMapping.get(func_175745_c(friendlyName));
    }

    public static CustomEnumFormatting func_175744_a(int p_175744_0_)
    {
        if (p_175744_0_ < 0)
        {
            return RESET;
        }
        else
        {
        	CustomEnumFormatting[] aenumchatformatting = values();
            int j = aenumchatformatting.length;

            for (int k = 0; k < j; ++k)
            {
            	CustomEnumFormatting enumchatformatting = aenumchatformatting[k];

                if (enumchatformatting.getColorIndex() == p_175744_0_)
                {
                    return enumchatformatting;
                }
            }

            return null;
        }
    }

    /**
     * Gets all the valid values. Args: @param par0: Whether or not to include color values. @param par1: Whether or not
     * to include fancy-styling values (anything that isn't a color value or the "reset" value).
     */
    public static Collection getValidValues(boolean p_96296_0_, boolean p_96296_1_)
    {
        ArrayList arraylist = Lists.newArrayList();
        CustomEnumFormatting[] aenumchatformatting = values();
        int i = aenumchatformatting.length;

        for (int j = 0; j < i; ++j)
        {
        	CustomEnumFormatting enumchatformatting = aenumchatformatting[j];

            if ((!enumchatformatting.isColor() || p_96296_0_) && (!enumchatformatting.isFancyStyling() || p_96296_1_))
            {
                arraylist.add(enumchatformatting.getFriendlyName());
            }
        }

        return arraylist;
    }

    static
    {
    	CustomEnumFormatting[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
        	CustomEnumFormatting var3 = var0[var2];
            nameMapping.put(func_175745_c(var3.name), var3);
        }
    }
}