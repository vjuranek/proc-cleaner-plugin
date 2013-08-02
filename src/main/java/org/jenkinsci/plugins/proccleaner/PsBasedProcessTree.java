package org.jenkinsci.plugins.proccleaner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents process tree obtain from ps utility.
 * Should work on Linux and most Unixes (at least Sun, HP and AIX)
 * 
 * @author vjuranek
 *
 */
public abstract class PsBasedProcessTree {
	
	private List<PsProcess> processList;
	private PrintStream log;
	
	public PsBasedProcessTree() {
		this.processList = new ArrayList<PsProcess>();
	}
	
	public List<PsProcess> getProcessList() {
		return processList;
	}
	
	public PrintStream getLog() {
		return log;
	}
	
	public void setLog(PrintStream log) {
		this.log = log;
	}
	
	public void addProcess(String psLine) {
		processList.add(createFromString(psLine));
	}
	
	public PsProcess getByPid(int pid) {
		for(PsProcess p : PsBasedProcessTree.this.processList)
			if(pid == p.getPid())
				return p;
		return null;
	}
	
	public PsProcess createFromString(String psLine) {
		String[] ps = psLine.trim().split(" +", 3);
		if(ps.length < 3)
			return null;
		return PsProcessFactory.createPsProcess(s2i(ps[0]), s2i(ps[1]), ps[2], PsBasedProcessTree.this);
	}
	
	private int s2i(String str) {
        return new Integer(str.trim()).intValue();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(PsProcess ps : processList)
			sb.append(ps.toString());
		return sb.toString();
	}
	
	public abstract PsBasedProcessTree createProcessTreeFor(String user) throws InterruptedException, IOException;
	
	
	/*
	  
   PID  PPID COMMAND
14833 14829 sshd: hudson@pts/0
14834 14833 -bash
14926 14834 /usr/bin/mc -P /tmp/mc-hudson/mc.pwd.14834
14928 14926 bash -rcfile .bashrc
15063 30851 /qa/tools/opt/amd64/jdk1.6.0_last/bin/java -Dqe.artifacts=/home/hudson/static_build_env/teiid-test-artifacts/scenario-deploy-artifacts -Dfile=scripts/soa/teiid_querytesting.groovy -Dserver.name=production -Dds.output.dir=testresults -Dctc.framework.version=/home/hudson/static_build_env/bqt_testing_framework/16 -Dbits.type=EMBEDDED -Dds.config.file=/home/hudson/static_build_env/bqt_testing_framework/16/ctc_tests/ctc-test.properties -Dds.result.mode=COMPARE -Dds.queryset.dir=/home/hudson/static_build_env/teiid-test-artifacts/ctc-tests/queries -classpath /qa/tools/opt/groovy-1.7.6/lib/groovy-1.7.6.jar -Dscript.name=/qa/tools/opt/groovy-1.7.6/bin/groovy -Dprogram.name=groovy -Dgroovy.starter.conf=/qa/tools/opt/groovy-1.7.6/conf/groovy-starter.conf -Dgroovy.home=/qa/tools/opt/groovy-1.7.6 -Dtools.jar=/qa/tools/opt/amd64/jdk1.6.0_last/lib/tools.jar org.codehaus.groovy.tools.GroovyStarter --main groovy.ui.GroovyMain --conf /qa/tools/opt/groovy-1.7.6/conf/groovy-starter.conf --classpath . /mnt/hudson_workspace/workspace/soa-teiid-bqt-pass1_matrix/jdk/java16_default/label/RHEL6_x86_64/scripts/common/run.groovy
16403 16400 sshd: hudson@pts/2
16404 16403 -bash
16525 15063 /qa/tools/opt/x86_64/jdk1.6.0_30/bin/java -cp /mnt/hudson_workspace/workspace/soa-teiid-bqt-pass1_matrix/jdk/java16_default/label/RHEL6_x86_64/jbosssoa/jboss-as/bin/run.jar:/qa/tools/opt/amd64/jdk1.6.0_last/lib/tools.jar -Dhornetq.broadcast.bg-group1.address=231.125.28.156 -Dhornetq.broadcast.dg-group1.address=231.125.28.156 -Xmx1303m -XX:MaxPermSize=1024m -Djboss.server.log.threshold=INFO -Djboss.messaging.ServerPeerID=1 -Djava.endorsed.dirs=/mnt/hudson_workspace/workspace/soa-teiid-bqt-pass1_matrix/jdk/java16_default/label/RHEL6_x86_64/jbosssoa/jboss-as/lib/endorsed -Djgroups.udp.ip_ttl=0 org.jboss.Main -c production -b localhost -u 227.43.88.174
18120 15063 /qa/tools/opt/x86_64/jdk1.6.0_30/jre/bin/java -Xmx1024m -Dconfig=/home/hudson/static_build_env/bqt_testing_framework/16/ctc_tests/ctc-test.properties -Dscenariofile=/home/hudson/static_build_env/teiid-test-artifacts/scenario-deploy-artifacts/PassOne/scenarios/mysql50_bqt_push.properties -Dqueryset.artifacts.dir=/home/hudson/static_build_env/teiid-test-artifacts/ctc-tests/queries -Dquery.scenario.classname=org.jboss.bqt.test.client.ctc.CTCQueryScenario -Dserver.host.name=localhost -Dproj.dir=/home/hudson/static_build_env/bqt_testing_framework/16 -Doutput.dir=/mnt/hudson_workspace/workspace/soa-teiid-bqt-pass1_matrix/jdk/java16_default/label/RHEL6_x86_64/testresults/PassOne -Dresult.mode=COMPARE -Dexceedpercent=-1 -Dexectimemin=-1 -Dusername=user -Dpassword=user -classpath /home/hudson/static_build_env/bqt_testing_framework/16/lib/bqt-client-0.0.1-SNAPSHOT.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/bqt-core-0.0.1-SNAPSHOT.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/bqt-framework-0.0.1-SNAPSHOT.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/bqt-jdk-16-support-0.0.1-SNAPSHOT.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/commons-collections-3.2.1.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/jdom-1.0.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/junit-4.4.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/log4j-1.2.14.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/slf4j-api-1.5.6.jar:/home/hudson/static_build_env/bqt_testing_framework/16/lib/teiid-client.jar org.jboss.bqt.test.client.TestClient
18161 16404 ps -u hudson -o pid,ppid,args
30850 30846 sshd: hudson@notty
30851 30850 /qa/tools/opt/amd64/jdk1.6.0_last/bin/java -Djava.net.preferIPv4Stack=true -Djava.home=/qa/tools/opt/amd64/jdk1.6.0_last/jre -Xmx700m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -jar /home/hudson/hudson_release/WEB-INF/slave.jar
	 
	 */
	

}
