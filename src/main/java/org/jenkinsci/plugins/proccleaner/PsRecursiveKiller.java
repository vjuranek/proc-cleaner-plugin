package org.jenkinsci.plugins.proccleaner;

import hudson.Extension;

import java.util.List;

import org.jenkinsci.plugins.proccleaner.PsBasedProcessTree.PsProcess;

@Extension
public class PsRecursiveKiller extends PsKiller {
	
	protected void doKill(PsBasedProcessTree ptree, int me) {
		List<PsProcess> childern = ptree.getByPid(me).getChildren(); // TODO get children recursively
		for(PsProcess p : childern)
			p.killRecursively();
	}

	private static final long serialVersionUID = 1L;

}
