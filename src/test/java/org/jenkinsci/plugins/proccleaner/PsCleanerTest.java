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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class PsCleanerTest {

    @Rule public JenkinsRule j = new JenkinsRule();

    /* As long as current implementation is concerned, BuildWrapper.preCheckout()
     * is called for MatrixBuild but Notifier.perform() is not. Both are run for
     * MatrixRun.
     */
    @Test public void doNotRunCleanForMatrixParent() throws Exception {
        MatrixProject m = j.createMatrixProject();
        m.setAxes(new AxisList(new TextAxis("axis", "a", "b")));

        PsCleaner preCleaner = mock(PsCleaner.class);
        PsCleaner postCleaner = mock(PsCleaner.class);

        m.getPublishersList().add(new PostBuildCleanup(preCleaner));
        m.getBuildWrappersList().add(new PreBuildCleanup(postCleaner));

        m.scheduleBuild2(0).get();

        verify(preCleaner, times(2)).call();
        verify(postCleaner, times(2)).call();
    }
}
