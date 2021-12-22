package me.Hayden.LevelTools.other;

import com.google.common.base.Strings;
import me.Hayden.LevelTools.LevelTools;
import org.bukkit.ChatColor;

public class ProgressBar {
    public static String getProgressBar(int current, int max) {
        char symbol = LevelTools.plugin.getConfig().getString("settings.progressbar.filler").charAt(0);
        int totalBars = LevelTools.plugin.getConfig().getInt("settings.progressbar.bars");
        ChatColor completedColor = ChatColor.getByChar(LevelTools.plugin.getConfig().getString("settings.progressbar.complete_color").charAt(0));
        ChatColor notCompletedColor = ChatColor.getByChar(LevelTools.plugin.getConfig().getString("settings.progressbar.incomplete_color").charAt(0));
        if (max == 0) {
            return Strings.repeat("" + completedColor + symbol, totalBars);
        }
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
}
