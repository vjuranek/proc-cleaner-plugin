package org.jenkinsci.plugins.proccleaner;

import java.io.IOException;

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

        try {
            Process p = Runtime.getRuntime().exec("cmd.exe /c \"taskkill /F /PID " + super.getPid() + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
