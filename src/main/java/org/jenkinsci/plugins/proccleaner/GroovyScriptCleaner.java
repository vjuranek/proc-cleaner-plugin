/*
 * The MIT License
 *
 * Copyright (c) 2012 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.proccleaner;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;

import java.io.PrintStream;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.stapler.DataBoundConstructor;

public class GroovyScriptCleaner extends ProcCleaner {

    private String script;
    private transient ClassLoader cl;

    @DataBoundConstructor
    public GroovyScriptCleaner(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    @Override
    public void clean(CleanRequest request) {
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
        final PrintStream scriptOut = request.getListener().getLogger();
        shell.setVariable("out", scriptOut);
        try {
            Object output = shell.evaluate(script);
            if(output != null) {
                scriptOut.println("Result: " + output);
            }
        } catch (Throwable t) {
            t.printStackTrace(scriptOut);
        }
    }

    @Extension
    public static class GroovyScriptCleanerDescriptor extends ProcCleanerDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.GroovyScriptCleaner_DisplayName();
        }
    }

    private static final long serialVersionUID = 1L;
}
