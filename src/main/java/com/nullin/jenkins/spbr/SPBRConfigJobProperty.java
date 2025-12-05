package com.nullin.jenkins.spbr;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * SPBR configuration job property.
 *
 * Not sure how to ensure that this is only visible when the job is configured as parameterized.
 *
 * @author nullin
 */
public class SPBRConfigJobProperty extends JobProperty<AbstractProject<?, ?>> {

    public final String includes;
    public final String excludes;
    public final boolean showCurrentBuilds;

    public SPBRConfigJobProperty(String includes, String excludes) {
        this(includes, excludes, false);
    }

    public SPBRConfigJobProperty(String includes, String excludes, boolean showCurrentBuilds) {
        this.includes = includes;
        this.excludes = excludes;
        this.showCurrentBuilds = showCurrentBuilds;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public String getDisplayName() {
            return "Customize Simple Parameterized Builds Report";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req,
                                          JSONObject formData) throws FormException {
            if (formData.isNullObject()) {
                return null;
            }

            JSONObject config = formData.getJSONObject("spbrConfig");

            if (config.isNullObject()) {
                return null;
            }

            String includes = config.getString("includes");
            String excludes = config.getString("excludes");
            boolean showCurrentBuilds = config.optBoolean("showCurrentBuilds", false);
            return new SPBRConfigJobProperty(includes, excludes, showCurrentBuilds);
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }
    }

}
