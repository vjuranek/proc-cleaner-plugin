package org.jenkinsci.plugins.proccleaner;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.remoting.DelegatingCallable;

import java.io.PrintWriter;
import java.io.StringWriter;

import jenkins.model.Jenkins;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.stapler.DataBoundConstructor;

public class GroovyScriptCleaner extends ProcCleaner implements DelegatingCallable<Void,Exception> {
	
	private String script;
	private transient ClassLoader cl;
	
	@DataBoundConstructor
	public GroovyScriptCleaner(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}
	
	 public ClassLoader getClassLoader() {
         return Jenkins.getInstance().getPluginManager().uberClassLoader;
     }
	
	public Void call() throws Exception {
		CompilerConfiguration compilerConfig = new CompilerConfiguration();
		compilerConfig.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                "jenkins",
                "jenkins.model",
                "hudson",
                "hudson.model"));
		
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }

        GroovyShell shell = new GroovyShell(cl, new Binding(), compilerConfig);
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        shell.setVariable("out", pw);
        try {
            Object output = shell.evaluate(script);
            if(output!=null)
            pw.println("Result: "+output);
        } catch (Throwable t) {
            t.printStackTrace(pw);
        }
        
        return null;
	}
	
	@Extension
	public static class GroovyScriptCleanerDescriptor extends ProcCleanerDescriptor {
		public String getDisplayName() {
			return "Groovy script process cleaner";
		}
	}
	
	private static final long serialVersionUID = 1L;
}
