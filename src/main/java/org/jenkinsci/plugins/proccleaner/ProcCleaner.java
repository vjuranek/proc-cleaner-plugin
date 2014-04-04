package org.jenkinsci.plugins.proccleaner;

import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Executor;
import hudson.model.Queue.Executable;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;

import java.lang.management.ManagementFactory;

import jenkins.model.Jenkins;

public abstract class ProcCleaner implements Callable<Void,Exception>, Describable<ProcCleaner>, ExtensionPoint {

	private static final long serialVersionUID = 1L;
	private BuildListener log;

    /**
     *
     * @return pid of the ProcCleaner process
     */
    public static int getpid(){
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf('@');
        return Integer.parseInt(jvmName.substring(0,index));
    }

	public BuildListener getLog() {
		return log;
	}

	public void setLog(BuildListener log) {
		this.log = log;
	}

	public void setup(BuildListener log) {
		this.log = log;
	}

	public void tearDown() {
		this.log = null;
	}

	public final void performCleanup(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
	    // Do not run for matrix parent
	    if (build instanceof MatrixBuild) return;

	    if (areThereOtherBuilds(build)) {
	        listener.getLogger().println("[Process cleanup] Skipping, there are other builds using the slave.");
	        return;
	    }

	    listener.getLogger().println("[Process cleanup]");

	    this.setup(listener);
	    VirtualChannel c = launcher.getChannel();
	    try {
	        c.call(this);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    this.tearDown();
	}

	private boolean areThereOtherBuilds(AbstractBuild<?, ?> build) {
	    Computer computer = Computer.currentComputer();
	    for (Executor e: computer.getExecutors()) {
	        if (runsDifferentBuilds(e, build)) return true;
	    }

	    for (Executor e: computer.getOneOffExecutors()) {
	        if (runsDifferentBuilds(e, build)) return true;
	    }

	    return false;
	}

	/**
	 * @return true if executor runs build different than current or its matrix parent.
	 */
	private boolean runsDifferentBuilds(Executor executor, AbstractBuild<?, ?> current) {
	    Executable candidate = executor.getCurrentExecutable();
	    if (candidate == null) return false; // idle

	    if (current.equals(candidate)) return false; // runs current

	    if (current instanceof MatrixRun) {
	        MatrixBuild parent = ((MatrixRun) current).getParentBuild();
	        if (parent.equals(candidate)) return false; // runs parent
	    }

	    return true;
	}

	public ProcCleanerDescriptor getDescriptor() {
		return (ProcCleanerDescriptor) Jenkins.getInstance().getDescriptor(getClass());
	}

	public static ExtensionList<ProcCleaner> all() {
		return Jenkins.getInstance().getExtensionList(ProcCleaner.class);
	}

	public static DescriptorExtensionList<ProcCleaner,Descriptor<ProcCleaner>> getCleanerDescriptors() {
		return Jenkins.getInstance().<ProcCleaner,Descriptor<ProcCleaner>>getDescriptorList(ProcCleaner.class);
	}

	public static class ProcCleanerDescriptor extends Descriptor<ProcCleaner> {

		protected ProcCleanerDescriptor(Class<? extends ProcCleaner> clazz) {
			super(clazz);
		}

		protected ProcCleanerDescriptor() {
		}

		@Override
        public String getDisplayName() {
            return clazz.getSimpleName();
        }
	}

}
