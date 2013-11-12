package org.jenkinsci.plugins.proccleaner;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.remoting.VirtualChannel;
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

		listener.getLogger().println("[Process cleanup]");

		cleaner.setup(listener);
		VirtualChannel c = launcher.getChannel();
		try {
		    c.call(cleaner);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cleaner.tearDown();
	}


	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
	    //no-op
	    return new NoopEnv();
	}

	@Extension
	public static final class DescriptorImpl extends Descriptor<BuildWrapper> {

		public String getDisplayName() {
			return "Process cleanup";// Messages.PreBuildCleanup_Delete_workspace();
		}

	}

	class NoopEnv extends Environment {
	}

}
