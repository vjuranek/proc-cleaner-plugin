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
				getLog().getLogger().println("Proc cleanup globally switched off, contact you Jenkins administrator to turn it on");
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
		
		public String getDisplayName() {
			return "Ps-based process cleaner";
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