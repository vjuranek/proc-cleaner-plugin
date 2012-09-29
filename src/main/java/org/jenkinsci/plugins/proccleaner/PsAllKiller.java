package org.jenkinsci.plugins.proccleaner;

import hudson.Extension;

@Extension
public class PsAllKiller extends PsKiller {
	
	protected void doKill(PsBasedProcessTree ptree, int me) {
		ptree.getByPid(me).killAllExceptMe();
	}

	private static final long serialVersionUID = 1L;
}
