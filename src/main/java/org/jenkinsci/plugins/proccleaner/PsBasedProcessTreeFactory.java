package org.jenkinsci.plugins.proccleaner;

import hudson.Functions;
import hudson.model.Hudson;
import jenkins.model.Jenkins;

/**
 * Author: psrna
 * Date: 8/2/13
 */
public class PsBasedProcessTreeFactory {

    public static PsBasedProcessTree createPsBasedProcessTree(){

        if(Functions.isWindows()){
            return new PsBasedWinProcessTree();
        }else{
            return new PsBasedUnixProcessTree();
        }
    }

}
