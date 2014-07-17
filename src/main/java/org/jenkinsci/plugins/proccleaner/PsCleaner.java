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
import hudson.model.BuildListener;

import java.io.IOException;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class PsCleaner extends ProcCleaner {

    private static final long serialVersionUID = 1L;

    private final String killerType;
    private final PsKiller killer;
    private String username;
    private boolean switchedOff;

    @DataBoundConstructor
    public PsCleaner(String killerType) {
        this.killerType = killerType;
        this.killer = PsKiller.all().getDynamic(killerType);
    }

    public String getKillerType() {
        return killerType;
    }

    @Override
    public void setup(BuildListener log) {
        setLog(log);
        username = getDescriptor().getUsername();  //TODO setup remotely in call() method, use different class loader?
        switchedOff = getDescriptor().isSwitchedOff();
    }

    public Void call() throws Exception {
        try {
            if(switchedOff) {
                getLog().getLogger().println("Process cleanup is globally turned off, contact your Jenkins administartor to turn it on.");
                return null;
            }
            killer.kill(username,getLog().getLogger());
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PsCleanerDescriptor getDescriptor() {
        return (PsCleanerDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    @Extension
    public static class PsCleanerDescriptor extends ProcCleanerDescriptor {

        private String username;
        private boolean switchedOff;

        public PsCleanerDescriptor() {
            load();
        }

        public String getUsername() {
            return username;
        }

        public boolean isSwitchedOff() {
            return switchedOff;
        }

        @Override
        public String getDisplayName() {
            return Messages.PsCleaner_DisplayName();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) {
            username = json.getString("username");
            switchedOff = json.getBoolean("switchedOff");
            save();
            return true;
        }
    }
}