package org.jenkinsci.plugins.proccleaner;

import static hudson.util.jna.GNUCLibrary.LIBC;
import hudson.ExtensionList;
import hudson.ExtensionPoint;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import jenkins.model.Jenkins;

public abstract class PsKiller implements ExtensionPoint, Serializable {

	public void kill(String user) throws InterruptedException, IOException {
		PsBasedProcessTree ptree = PsBasedProcessTree.createProcessTreeFor(user);
		int me = LIBC.getpid();
		doKill(ptree, me);
	}
	
	public void kill(String user, PrintStream log) throws InterruptedException, IOException {
		PsBasedProcessTree ptree = PsBasedProcessTree.createProcessTreeFor(user);
		System.out.println("Process tree: " + ptree.toString());
		ptree.setLog(log);
		int me = LIBC.getpid();
		doKill(ptree, me);
	}
	
	protected abstract void doKill(PsBasedProcessTree ptree, int me);
	
	public static ExtensionList<PsKiller> all() {
        return Jenkins.getInstance().getExtensionList(PsKiller.class);
    }

	private static final long serialVersionUID = 1L;
}
