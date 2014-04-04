/*
 * The MIT License
 *
 * Copyright (c) 2014 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.proccleaner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Project;
import hudson.tasks.Shell;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class PsCleanerTest {

    @Rule public JenkinsRule j = new JenkinsRule();
    private PsCleaner preCleaner;
    private PsCleaner postCleaner;

    @Test public void runCleanup() throws Exception {
        FreeStyleProject job = j.createFreeStyleProject();
        setupKillers(job);
        job.scheduleBuild2(0).get();

        verify(preCleaner).call();
        verify(postCleaner).call();
    }

    @Test public void doNotRunCleanForMatrixParent() throws Exception {
        MatrixProject m = j.createMatrixProject();
        m.setAxes(new AxisList(new TextAxis("axis", "a")));
        setupKillers(m);

        m.scheduleBuild2(0).get();

        verify(preCleaner).call();
        verify(postCleaner).call();
    }

    @Test public void doNotCleanOnSlaveWithOtherBuildRunning() throws Exception {
        FreeStyleProject running = j.createFreeStyleProject();
        running.getBuildersList().add(new Shell("sleep 300"));
        running.scheduleBuild2(0);

        FreeStyleProject cleaned = j.createFreeStyleProject();
        setupKillers(cleaned);

        cleaned.scheduleBuild2(0).get();

        verify(preCleaner, never()).call();
        verify(postCleaner, never()).call();
    }

    @Test public void runCleanupOnNonconcurrentJobs() throws Exception {
        FreeStyleProject job = j.createFreeStyleProject();
        setupKillers(job);
        job.scheduleBuild2(0).get();
        job.scheduleBuild2(0).get();

        verify(preCleaner, times(2)).call();
        verify(postCleaner, times(2)).call();
    }

    private void setupKillers(AbstractProject<?, ?> project) throws Exception {
        preCleaner = mock(PsCleaner.class, withSettings().serializable());
        postCleaner = mock(PsCleaner.class, withSettings().serializable());

        project.getPublishersList().add(new PostBuildCleanup(preCleaner));
        PreBuildCleanup cleaner = new PreBuildCleanup(postCleaner);
        if (project instanceof MatrixProject) {
            ((MatrixProject) project).getBuildWrappersList().add(cleaner);
        } else {
            ((Project<?, ?>) project).getBuildWrappersList().add(cleaner);
        }
    }
}
