package org.jenkinsci.plugins.proccleaner;

import hudson.Extension;

@Extension
public class PsAllKiller extends PsKiller {
	
	protected void doKill(PsBasedProcessTree ptree, int me) {
		System.out.println("KILL THEM ALL: ");
		ptree.getByPid(me).killAllExceptMe();
	}

}
