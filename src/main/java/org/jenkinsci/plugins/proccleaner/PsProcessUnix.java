package org.jenkinsci.plugins.proccleaner;

import static hudson.util.jna.GNUCLibrary.LIBC;


/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsProcessUnix extends PsProcess{

    public PsProcessUnix(int pid, int ppid, String args, PsBasedProcessTree ptree) {
        super(pid, ppid, args, ptree);
    }

    @Override
    public void kill(int signum) {
        LIBC.kill(super.getPid(), signum);
    }
}
