package net.osial.osialpets.utils;

public class ProgressUtils {

    public static String generateProgressBar(long current, long max) {
        int progressBarLength = 40;
        String progressBarIcon = ":";
        String completedColor = "&a&l";
        String uncompletedColor = "&c&l";
        String progressBar = "";
        int completed = (int) (current * progressBarLength / max);
        for (int i = 0; i < completed; i++) {
            progressBar += completedColor + progressBarIcon;
        }
        for (int i = 0; i < progressBarLength - completed; i++) {
            progressBar += uncompletedColor + progressBarIcon;
        }
        return progressBar;
    }

}
