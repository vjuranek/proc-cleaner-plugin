package org.jenkinsci.plugins.proccleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: psrna
 * Date: 8/6/13
 */
public class WMICProcess {

    private int pid;
    private int ppid;
    private String args;

    public WMICProcess(int pid) throws WMICProcessException{
        this.pid = pid;
        this.ppid = callForPpid(pid);
        this.args = callForArgs(pid);
    }

    private int callForPpid(int pid) throws WMICProcessException{

        int ppid = -1;
        String cmd = "cmd.exe /c \"WMIC PROCESS where (processid=" + pid + ") get parentprocessid \"";

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader stdin = new BufferedReader((new InputStreamReader(process.getInputStream())));
            String s = stdin.readLine(); //skip first line - it's parentprocessid label
            while((s = stdin.readLine()) != null){
                if(!s.isEmpty()){
                    ppid = Integer.valueOf(s.trim());
                }
            }
            stdin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(ppid == -1)
            throw new WMICProcessException("no instance for pid:" + pid);
        return ppid;
    }

    private String callForArgs(int pid) {

        StringBuilder args = new StringBuilder();
        String cmd = "cmd.exe /c \"WMIC PROCESS where (processid=" + pid + ") get caption,commandline \"";

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader stdin = new BufferedReader((new InputStreamReader(process.getInputStream())));
            String s = stdin.readLine(); //skip first line - it's caption and commandline label
            while((s = stdin.readLine()) != null){
                 args.append(s);
            }
            stdin.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return args.toString();
    }

    protected static List<Integer> getUserProcesses(String user){
        //regex for pid
        Pattern PID_PATTERN = Pattern.compile("(.+Handle=\")(\\d+)(\".+)");
        Pattern RETVAL_PATTERN = Pattern.compile("(\\s*ReturnValue = )(\\d+)(;\\s*)");
        Pattern USER_PATTERN = Pattern.compile("(\\s*User = \")(\\w+)(\";\\s*)");

        ArrayList<Integer> uplist = new ArrayList<Integer>();
        String cmd = "cmd.exe /c \"WMIC PROCESS where (name like \"%exe%\") call getowner\"";

        try {
            // Run Windows command
            Process process = Runtime.getRuntime().exec(cmd);

            // Get input stream
            BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read and parse command standard output
            String s;
            while ((s = stdin.readLine()) != null) {
                //search for pid line
                Matcher mpid = PID_PATTERN.matcher(s);
                if(mpid.matches()){
                    int pid = Integer.valueOf(mpid.group(2));
                    while((s = stdin.readLine()) != null){
                        //search for ReturnValue line
                        if(!s.isEmpty()){
                            Matcher mret = RETVAL_PATTERN.matcher(s);
                            if(mret.matches()){
                                int retval = Integer.valueOf(mret.group(2));
                                if(retval == 0){
                                    //call getowner successfull completion
                                    while((s = stdin.readLine()) != null){
                                        //search for user line
                                        Matcher muser = USER_PATTERN.matcher(s);
                                        if(muser.matches() && user.equals(muser.group(2))){
                                            //success, add PID to the list
                                            uplist.add(pid);
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return uplist;
    }


    public int getPpid() {
        return ppid;
    }

    public String getArgs() {
        return args;
    }

    public int getPid() {
        return pid;
    }

    public class WMICProcessException extends Exception {
        public WMICProcessException(String message) {
            super(message);
        }
    }

}
