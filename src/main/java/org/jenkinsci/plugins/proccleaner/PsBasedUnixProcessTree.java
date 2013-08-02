package org.jenkinsci.plugins.proccleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsBasedUnixProcessTree extends PsBasedProcessTree {


    @Override
    public PsBasedProcessTree createProcessTreeFor(String user) throws InterruptedException, IOException {
        String[] cmd = {"ps","-u",user,"-o","pid,ppid,args"};
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        int ec = proc.waitFor();
        PsBasedProcessTree ptree = new PsBasedUnixProcessTree();
        String line = reader.readLine(); // first line should be "PID  PPID COMMAND" - skip it
        while((line = reader.readLine()) != null)
            ptree.addProcess(line);
        return ptree;
    }


}
