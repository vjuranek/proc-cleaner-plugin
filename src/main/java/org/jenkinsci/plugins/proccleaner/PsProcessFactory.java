package org.jenkinsci.plugins.proccleaner;

import hudson.Functions;

/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsProcessFactory {

    public static PsProcess createPsProcess(int pid, int ppid, String args, PsBasedProcessTree ptree){

        if(Functions.isWindows()){
            return new PsProcessWin(pid, ppid, args, ptree);
        }else{
            return new PsProcessUnix(pid, ppid, args, ptree);
        }
    }

}
