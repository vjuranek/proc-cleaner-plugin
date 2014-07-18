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

import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Project;

/*package*/ class Util {

    public static void setPreProcCleaner(AbstractProject<?, ?> project, ProcCleaner preCleaner) throws Exception {

        PreBuildCleanup cleaner = new PreBuildCleanup(preCleaner);
        if (project instanceof MatrixProject) {
            ((MatrixProject) project).getBuildWrappersList().add(cleaner);
        } else {
            ((Project<?, ?>) project).getBuildWrappersList().add(cleaner);
        }
    }

    public static void setPostProcCleaner(AbstractProject<?, ?> project, ProcCleaner postCleaner) throws Exception {

        project.getPublishersList().add(new PostBuildCleanup(postCleaner));
    }
}
