/*
 * The MIT License
 *
 * Copyright (c) 2013 Red Hat, Inc.
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

import hudson.ExtensionList;
import hudson.ExtensionPoint;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import jenkins.model.Jenkins;

public abstract class PsKiller implements ExtensionPoint, Serializable {

    public void kill(String user) throws InterruptedException, IOException {
        PsBasedProcessTree ptree = PsBasedProcessTreeFactory.createPsBasedProcessTree().createProcessTreeFor(user);
        int me = ProcCleaner.getpid();
        doKill(ptree, me);
    }

    public void kill(String user, PrintStream log) throws InterruptedException, IOException {
        PsBasedProcessTree ptree = PsBasedProcessTreeFactory.createPsBasedProcessTree().createProcessTreeFor(user);
        System.out.println("Process tree: " + ptree.toString());
        ptree.setLog(log);
        int me = ProcCleaner.getpid();
        doKill(ptree, me);
    }

    protected abstract void doKill(PsBasedProcessTree ptree, int me);

    public abstract String getDisplayName();

    public static ExtensionList<PsKiller> all() {
        return Jenkins.getInstance().getExtensionList(PsKiller.class);
    }

    private static final long serialVersionUID = 1L;
}
