package com.pd.modelcg.console.application;

import java.io.IOException;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class ScriptConnector {

    private GroovyScriptEngine gse;
    private Binding            binding;

    ScriptConnector() {
        try {
            this.gse = new GroovyScriptEngine("");
        } catch (IOException ioe) {
            ErrorDialog.error("I/O-Exception in starting Groovy engine. Message is:\n"
                         + ioe.getMessage()
                         + "\n" + prepareStackTrace(ioe));
        }     

        binding = new Binding();
    } 

    //add environment variables here, e.g. text writer, drawing image
    public void addToEnvironment(String name, Object object) {
        binding.setVariable(name, object);
    }

    public boolean runScript(String fileName){
        boolean result = false;

        try {
            gse.run(fileName, binding);
            result = true;
        } catch (ResourceException re) {
            ErrorDialog.error("ResourceException in calling groovy script '" + fileName +
                        "' Message is:\n" +re.getMessage()
                        + "\n" + prepareStackTrace(re));
        } catch (ScriptException se) {
            ErrorDialog.getInstance().error(parseMessage(se.getMessage()), prepareStackTrace(se));
        } 

        return result;
    }

    //message format is "...ScriptedException:...GroovyException:...for class..."
    private String parseMessage(String message) {
        int pos = message.indexOf("ScriptException:");
 
        if (pos != -1) {
            //Skip "ScriptException: "
            message = message.substring(pos+16);
        }
        
        pos = message.indexOf("groovy.lang.");

        if (pos != -1) {
            //Skip "groovy.lang."
            message = message.substring(pos+12);
        }
        
        pos = message.indexOf("Exception:");

        if (pos != -1) {
            //Move exception to its own line
            message = message.substring(0, pos+10) + "\n" + message.substring(pos+10);
        }

        return message;
    }

    private String prepareStackTrace(Exception e) {
        Throwable exc = e;
        StringBuffer output = new StringBuffer();
        collectTraces(exc, output);

        if (exc.getCause() != null) {
           exc = exc.getCause();
           output.append("caused by::\n");
           output.append(exc.getMessage());
           output.append("\n");
           collectTraces(exc, output);
        }

        return output.toString();
    }

    private void collectTraces(Throwable e, StringBuffer output) {
        StackTraceElement[] trace = e.getStackTrace();

        for (int i=0; i < trace.length; i++) {
            output.append(trace[i].toString());
            output.append("\n");
        }
    }
}
