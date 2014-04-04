package org.jenkinsci.plugins.proccleaner;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

public class PostBuildCleanup extends Notifier {

	private final ProcCleaner cleaner;

	@DataBoundConstructor
	public PostBuildCleanup(ProcCleaner cleaner) {
		this.cleaner = cleaner;
	}

	public ProcCleaner getCleaner() {
		return cleaner;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		cleaner.performCleanup(build, launcher, listener);
		return true;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.STEP;
	}

	@Extension(ordinal = -999)
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		@Override
        public String getDisplayName() {
			return "Process cleanup";// Messages.PreBuildCleanup_Delete_workspace();
		}

		@Override
		public boolean isApplicable(Class clazz) {
			return true;
		}

	}

}
