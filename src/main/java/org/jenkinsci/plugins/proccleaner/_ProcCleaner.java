package org.jenkinsci.plugins.proccleaner;

import static hudson.util.jna.GNUCLibrary.LIBC;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.listeners.RunListener;
import hudson.remoting.Channel;
import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

//@Extension
public class _ProcCleaner extends RunListener<Run> {

	List<String> procNames;
	
	public _ProcCleaner(){
		procNames = new ArrayList<String>();
		//TODO make configurable
		//names of the processes which shouldn't be killed
		procNames.add("slave.jar");
		procNames.add("sshd");
	}
	
	public void onFinalized(Run run) {
	    Channel c = (Channel)run.getExecutor().getOwner().getChannel();
	    /*
	    ProcessTree rTree2 = new ProcessTree();
	    ProcessTree.Remote rTree = new ProcessTree.Remote(rTree2,c);
		Iterator<OSProcess> procItr = rTree.iterator(); //ProcessTree.get().iterator();
		while(procItr.hasNext()){
			OSProcess proc = procItr.next();
			LOGGER.info("investigating process " + proc.getPid());
			if(shouldProcBeKilled(proc))
				killProcess(proc);
		}
		*/
	    KillCommand kc = new KillCommand();
	    try{
	        c.call(kc);
	        //System.out.println("OUTPUT: " + output);
	    } catch(Exception e){
	        e.printStackTrace();
	    }
		LOGGER.info("onFinalized - finished");
	}

	
	/**
	 * Checks if the process should be killed. 
	 *  
	 */
	private boolean shouldProcBeKilled(OSProcess proc){
		if(isProcOwnedByMe(proc) && isProcUseless(proc))
			return true;
		return false;
	}

	/**
	 * Checks if the process is owned by current user.
	 */
	private boolean isProcOwnedByMe(OSProcess proc){
		long myUID = (long) LIBC.getegid(); //on Linux uid can be out of int range
		long procUID = getProcOwnerUid(proc.getPid());
		LOGGER.info("my uid: " + myUID + ", process owner uid: " + procUID);
		return myUID == procUID;
	}

	/**
	 * Check, if process is useless. Process is considered as useless (i.e. should be killed) if it's not  
	 * on the list of desired processes.
	 * 
	 */
	private boolean isProcUseless(OSProcess proc){
		List<String> arguments = proc.getArguments();
		if(arguments == null || arguments.size() == 0) //probably some permissions problem, shouldn't be killed
			return false; 
		String execName = arguments.get(0); //we do care only about the first argument which contains executable
		for(String item : procNames)
			if(execName.contains(item)) 
				return false;
		return true;
		
	}

	
	/**
	 * Executes process termination
	 */
	private void killProcess(OSProcess proc){
		LOGGER.info("killing process PID " + proc.getPid());
		LIBC.kill(proc.getPid(), 9);
	}
	
	/**
	 * Gets UID of the owner of the process
	 */
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
	
	private static final Logger LOGGER = Logger.getLogger(_ProcCleaner.class.getName());
}
