/*
 * The MIT License
 *
 * Copyright (c) 2012 Red Hat, Inc.
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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

public class PreBuildCleanup extends BuildWrapper {

    private final ProcCleaner cleaner;

    @DataBoundConstructor
    public PreBuildCleanup(ProcCleaner cleaner) {
        this.cleaner = cleaner;
    }

    public ProcCleaner getCleaner() {
        return cleaner;
    }

    @Override
    public void preCheckout(AbstractBuild build, Launcher launcher,
            BuildListener listener) throws IOException, InterruptedException {
        cleaner.performCleanup(build, launcher, listener);
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
            BuildListener listener) throws IOException, InterruptedException {
        //no-op
        return new NoopEnv();
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<BuildWrapper> {

        @Override
        public String getDisplayName() {
            return Messages.PreBuildCleanup_DisplayName();
        }
    }

    class NoopEnv extends Environment {
    }
}
