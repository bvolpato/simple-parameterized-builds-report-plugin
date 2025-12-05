package com.nullin.jenkins.spbr;

import com.google.common.collect.Multimap;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.matrix.TextAxis;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author nullin
 */
public class MatrixBuildsReportActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testMatrixNoAxes() throws Exception {
        MatrixProject project = j.createProject(MatrixProject.class);
        MatrixBuildsReportAction action = new MatrixBuildsReportAction(project);

        // Schedule and wait for build
        j.buildAndAssertSuccess(project);

        Multimap<Map<String, String>, MatrixRun> buildsMap = action.getBuildsMap(project.getBuilds());
        // A matrix build with no axes still creates one combination (empty)
        assertEquals(1, buildsMap.keySet().size());
        assertEquals(1, buildsMap.values().size());
    }

    @Test
    public void testMatrixOneAxis() throws Exception {
        MatrixProject project = j.createProject(MatrixProject.class, "test2");

        TextAxis axis = new TextAxis("TEST", "1", "2", "3");
        AxisList ax = new AxisList();
        ax.add(axis);
        project.setAxes(ax);

        MatrixBuildsReportAction action = new MatrixBuildsReportAction(project);

        // Schedule and wait for build
        j.buildAndAssertSuccess(project);

        Multimap<Map<String, String>, MatrixRun> buildsMap = action.getBuildsMap(project.getBuilds());
        assertEquals(3, buildsMap.keySet().size());
        assertEquals(3, buildsMap.values().size());
    }

    @Test
    public void testChangedParameterSet() throws Exception {
        MatrixProject project = j.createProject(MatrixProject.class);
        MatrixBuildsReportAction action = new MatrixBuildsReportAction(project);

        TextAxis axis = new TextAxis("TEST", "1", "2", "3");
        TextAxis axis2 = new TextAxis("TEST2", "a", "b", "c");
        TextAxis axis3 = new TextAxis("TEST3", "x", "y", "Z");

        AxisList ax = new AxisList();
        ax.add(axis);
        ax.add(axis2);
        ax.add(axis3);
        project.setAxes(ax);

        // First build
        j.buildAndAssertSuccess(project);

        Multimap<Map<String, String>, MatrixRun> buildsMap = action.getBuildsMap(project.getBuilds());
        // 3 x 3 x 3 = 27 keys
        assertEquals(27, buildsMap.keySet().size());
        assertEquals(27, buildsMap.values().size());

        ax.remove(axis3);
        project.setAxes(ax);

        // Second build
        j.buildAndAssertSuccess(project);

        buildsMap = action.getBuildsMap(project.getBuilds());
        // 3 x 3 + 1 for null
        assertEquals(10, buildsMap.keySet().size());

        // 27 for first test 9 for this one
        assertEquals(36, buildsMap.values().size());

        ax.remove(axis2);
        project.setAxes(ax);

        // Third build
        j.buildAndAssertSuccess(project);

        buildsMap = action.getBuildsMap(project.getBuilds());
        // 3 + 1 for null
        assertEquals(4, buildsMap.keySet().size());

        // 36 for previous + 3
        assertEquals(39, buildsMap.values().size());

        ax.add(axis2);
        project.setAxes(ax);

        // Fourth build
        j.buildAndAssertSuccess(project);

        buildsMap = action.getBuildsMap(project.getBuilds());
        // 3 * 3 + 3 for one axis builds and 1 for null
        assertEquals(13, buildsMap.keySet().size());

        // 39 + 9 here
        assertEquals(48, buildsMap.values().size());

        ax.add(axis3);
        project.setAxes(ax);

        // Fifth build
        j.buildAndAssertSuccess(project);

        buildsMap = action.getBuildsMap(project.getBuilds());
        // (3 x 3 x 3) + (3 x 3) + 3 = 39 keys
        assertEquals(39, buildsMap.keySet().size());

        // 48 previous + 27 here (no null now)
        assertEquals(75, buildsMap.values().size());
    }
}
