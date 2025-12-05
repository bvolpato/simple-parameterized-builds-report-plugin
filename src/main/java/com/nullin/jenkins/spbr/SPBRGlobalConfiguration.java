package com.nullin.jenkins.spbr;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Global configuration for Simple Parameterized Builds Report plugin.
 * Allows customization of the colors used to display build statuses in the report.
 *
 * @author bvolpato
 */
@Extension
public class SPBRGlobalConfiguration extends GlobalConfiguration {

    /** Custom color for successful builds (default: blue) */
    private String successColor;

    /** Custom color for failed builds (default: red) */
    private String failureColor;

    /** Custom color for unstable builds (default: yellow) */
    private String unstableColor;

    /** Custom color for aborted builds (default: gray) */
    private String abortedColor;

    /** Custom color for not-built/disabled builds (default: gray) */
    private String notBuiltColor;

    // Default Jenkins colors (softer variants for better readability)
    private static final String DEFAULT_SUCCESS_COLOR = "#7fbaff";    // blue
    private static final String DEFAULT_FAILURE_COLOR = "#ef9a9a";    // red  
    private static final String DEFAULT_UNSTABLE_COLOR = "#ffe57f";   // yellow
    private static final String DEFAULT_ABORTED_COLOR = "#9e9e9e";    // gray
    private static final String DEFAULT_NOT_BUILT_COLOR = "#9e9e9e";  // gray

    public SPBRGlobalConfiguration() {
        load();
    }

    public static SPBRGlobalConfiguration get() {
        return GlobalConfiguration.all().get(SPBRGlobalConfiguration.class);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        this.successColor = json.optString("successColor", null);
        this.failureColor = json.optString("failureColor", null);
        this.unstableColor = json.optString("unstableColor", null);
        this.abortedColor = json.optString("abortedColor", null);
        this.notBuiltColor = json.optString("notBuiltColor", null);
        save();
        return true;
    }

    /**
     * Returns the color to use for a given build status based on the icon color name.
     *
     * @param iconColorName The build's icon color name (e.g., "blue", "red", "yellow")
     * @param defaultColor The default color to use if no custom color is configured
     * @return The color to use (either custom or default)
     */
    public String getColorForBuild(String iconColorName, String defaultColor) {
        if (iconColorName == null) {
            return getNotBuiltColorOrDefault();
        }

        // Normalize the color name (remove _anime suffix for building jobs)
        String normalizedColor = iconColorName.toLowerCase().replace("_anime", "");
        
        switch (normalizedColor) {
            case "blue":
                return getSuccessColorOrDefault();
            case "red":
                return getFailureColorOrDefault();
            case "yellow":
                return getUnstableColorOrDefault();
            case "grey":
            case "disabled":
            case "notbuilt":
                return getNotBuiltColorOrDefault();
            case "aborted":
                return getAbortedColorOrDefault();
            default:
                return defaultColor;
        }
    }

    public String getSuccessColor() {
        return successColor;
    }

    public void setSuccessColor(String successColor) {
        this.successColor = successColor;
    }

    public String getSuccessColorOrDefault() {
        return isNotBlank(successColor) ? successColor : DEFAULT_SUCCESS_COLOR;
    }

    public String getFailureColor() {
        return failureColor;
    }

    public void setFailureColor(String failureColor) {
        this.failureColor = failureColor;
    }

    public String getFailureColorOrDefault() {
        return isNotBlank(failureColor) ? failureColor : DEFAULT_FAILURE_COLOR;
    }

    public String getUnstableColor() {
        return unstableColor;
    }

    public void setUnstableColor(String unstableColor) {
        this.unstableColor = unstableColor;
    }

    public String getUnstableColorOrDefault() {
        return isNotBlank(unstableColor) ? unstableColor : DEFAULT_UNSTABLE_COLOR;
    }

    public String getAbortedColor() {
        return abortedColor;
    }

    public void setAbortedColor(String abortedColor) {
        this.abortedColor = abortedColor;
    }

    public String getAbortedColorOrDefault() {
        return isNotBlank(abortedColor) ? abortedColor : DEFAULT_ABORTED_COLOR;
    }

    public String getNotBuiltColor() {
        return notBuiltColor;
    }

    public void setNotBuiltColor(String notBuiltColor) {
        this.notBuiltColor = notBuiltColor;
    }

    public String getNotBuiltColorOrDefault() {
        return isNotBlank(notBuiltColor) ? notBuiltColor : DEFAULT_NOT_BUILT_COLOR;
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
