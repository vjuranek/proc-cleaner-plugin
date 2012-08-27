package org.jenkinsci.plugins.proccleaner;

import static hudson.util.jna.GNUCLibrary.LIBC;
import hudson.remoting.Callable;
import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jenkinsci.plugins.proccleaner.PsBasedProcessTree.PsProcess;

public class KillCommand implements Callable<Void,Exception> {
   
	public Void call() throws Exception {
		PsBasedProcessTree ptree = PsBasedProcessTree.createProcessTreeFor("vjuranek");
		int me = LIBC.getpid();
		System.out.println("RECURSIVE KILL: ");
		List<PsProcess> childern = ptree.getByPid(me).getChildren(); 
		for(PsProcess p : childern)
			p.killRecursively();
		System.out.println("ALL KILL: ");
		ptree.getByPid(me).killAllExceptMe();
		return null;
	}
	
    public String _call() throws IOException {
        StringBuffer bf = new StringBuffer();
        Iterator<OSProcess> procItr = ProcessTree.get().iterator();
        while(procItr.hasNext()){
            OSProcess proc = procItr.next();
            bf.append("Proc id je " + proc.getPid() + ", owner je " + getProcOwnerUid(proc.getPid()) + "\n");
        }
        return bf.toString();
    }
    
    private long getProcOwnerUid(int pid){
        long uid = 0;
        try {
            FileReader fr = new FileReader(new File("/proc/" + pid + "/loginuid"));
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            uid = Long.parseLong(line);
        } catch (IOException e) {
            //probably permission denied, don't report
        }
        return uid;
    }

}
