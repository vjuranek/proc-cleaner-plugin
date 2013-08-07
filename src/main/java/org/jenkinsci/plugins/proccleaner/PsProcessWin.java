package org.jenkinsci.plugins.proccleaner;

/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsProcessWin extends PsProcess {

    public PsProcessWin(int pid, int ppid, String args, PsBasedProcessTree ptree) {
        super(pid, ppid, args, ptree);
    }

    @Override
    public void kill(int signum) {
        //TODO
    }
}
