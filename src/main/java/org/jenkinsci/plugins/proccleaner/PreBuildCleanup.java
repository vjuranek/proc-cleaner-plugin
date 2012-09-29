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
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {

		listener.getLogger().println("[Process cleanup]");

		if (!launcher.isUnix()) {
			listener.getLogger().println("Not a Linux/Unix slave, skipping Process cleanup");
			return new NoopEnv();
		}

		cleaner.setLog(listener);
		VirtualChannel c = launcher.getChannel();
		try {
			c.call(cleaner);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cleaner.setLog(null);
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
