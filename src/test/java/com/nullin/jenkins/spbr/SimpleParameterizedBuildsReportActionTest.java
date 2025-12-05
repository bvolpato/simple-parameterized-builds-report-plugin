package com.nullin.jenkins.spbr;

import com.google.common.collect.Multimap;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author nullin
 */
public class SimpleParameterizedBuildsReportActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testBuildMapContents() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        SimpleParameterizedBuildsReportAction action = new SimpleParameterizedBuildsReportAction(project);

        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(
                new StringParameterDefinition("string", "defaultValue", "string description"));
        project.addProperty(pdp);

        // Schedule and wait for the build to complete
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        
        @SuppressWarnings("unchecked")
        Multimap<Map<String, String>, AbstractBuild> buildsMap = action.getBuildsMap((java.util.Collection) project.getBuilds());
        assertEquals(1, buildsMap.keySet().size());
        assertEquals(1, buildsMap.values().size());
    }

    @Test
    public void testChangedParameterSet() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        SimpleParameterizedBuildsReportAction action = new SimpleParameterizedBuildsReportAction(project);

        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(
                new StringParameterDefinition("string", "defaultValue", "string description"));
        project.addProperty(pdp);

        // First build
        j.buildAndAssertSuccess(project);

        project.removeProperty(pdp);
        pdp = new ParametersDefinitionProperty(
                new StringParameterDefinition("string", "defaultValue", "string description"),
                new StringParameterDefinition("string1", "defaultValue1", "string description"));
        project.addProperty(pdp);

        // Second and third builds
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);

        // Fourth build with different value using ParameterizedJobMixIn
        project.scheduleBuild2(0, new hudson.model.ParametersAction(
                new hudson.model.StringParameterValue("string", "newValue"),
                new hudson.model.StringParameterValue("string1", "defaultValue1")
        )).get();

        @SuppressWarnings("unchecked")
        Multimap<Map<String, String>, AbstractBuild> buildsMap = action.getBuildsMap((java.util.Collection) project.getBuilds());
        assertEquals(3, buildsMap.keySet().size());
        assertTrue(buildsMap.keySet().contains(null));
        assertEquals(4, buildsMap.values().size());

        Map<String, String> buildVars = new HashMap<>();
        buildVars.put("string", "defaultValue");
        assertEquals(1, buildsMap.get(null).size());
        assertEquals(buildVars, buildsMap.get(null).iterator().next().getBuildVariables());

        buildVars.put("string1", "defaultValue1");
        assertEquals(2, buildsMap.get(buildVars).size());

        buildVars.put("string", "newValue");
        assertEquals(1, buildsMap.get(buildVars).size());
    }
}
