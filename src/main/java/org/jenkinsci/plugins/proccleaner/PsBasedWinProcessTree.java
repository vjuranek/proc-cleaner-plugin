package org.jenkinsci.plugins.proccleaner;

import java.io.IOException;
import java.util.List;

/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsBasedWinProcessTree extends PsBasedProcessTree {


    @Override
    public PsBasedProcessTree createProcessTreeFor(String user) throws InterruptedException, IOException {

        List<Integer> upList = WMICProcess.getUserProcesses(user);

        for(int p : upList){

            try {
                WMICProcess wmicp = new WMICProcess(p);
                PsProcess psp = PsProcessFactory.createPsProcess(wmicp.getPid(), wmicp.getPpid(), wmicp.getArgs(), this);
                if(!blacklisted(psp)) {
                    getProcessList().add(psp);
                }
            } catch (WMICProcess.WMICProcessException e) {
                //no instance for pid, don't add
            }
        }
        return this;
    }

    private boolean blacklisted(PsProcess p){

        if(p.getArgs().contains("sshd"))
            return true;
        if(p.getArgs().contains("bash"))
            return true;
        if(p.getArgs().contains("Explorer"))
            return true;

        return false;
    }


}
