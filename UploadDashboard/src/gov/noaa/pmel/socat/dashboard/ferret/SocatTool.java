package gov.noaa.pmel.socat.dashboard.ferret;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.jdom2.Element;



public class SocatTool extends Thread {

	FerretConfig ferret = new FerretConfig();
	String filename;
	String message;
	boolean error = false;
	boolean done = false;


	public SocatTool(FerretConfig ferretConf) {
	    ferret = new FerretConfig();
	    ferret.setRootElement((Element)ferretConf.getRootElement().clone());
	}

	public void init(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		done = false;
		error = false;
		PrintStream script_writer;
		try {

			String temp_dir = ferret.getTempDir();
			if ( !temp_dir.endsWith(File.separator) ) temp_dir = temp_dir + "/";
			
			String driver = ferret.getDriverScript();
			
			File temp = new File(temp_dir);
			if ( !temp.exists() ) {
				temp.mkdirs();
			}
			String fileKey = DashboardUtils.passhashFromPlainText(filename, filename);

			File script = new File(temp_dir, "ferret_operation_"+fileKey+".jnl");			
			
			script_writer = new PrintStream(script);

			script_writer.println("go " +driver+" "+"\""+filename+"\"");
			List<String> args = ferret.getArgs();
		    String interpreter = ferret.getInterpreter();
		    String executable = ferret.getExecutable();
		    String[] fullCmd;
		    int offset = 0;
		    if ( interpreter != null && !interpreter.equals("") ) {
		    	fullCmd = new String[args.size() + 3];
		    	fullCmd[0] = interpreter;
		    	fullCmd[1] = executable;
		    	offset = 2;
		    } else {
		    	fullCmd = new String[args.size() + 2];
		    	fullCmd[0] = executable;
		    	offset = 1;
		    }
		    for (int index = 0; index < args.size(); index++) {
	            String arg = (String) args.get(index);
	            fullCmd[offset+index] = arg;
            }

			fullCmd[args.size()+offset] = script.getAbsolutePath();
			
			long timelimit = ferret.getTimeLimit();

			Task task = new Task(fullCmd, ferret.getRuntimeEnvironment().getEnv(), new File(temp_dir), new File("cancel"), timelimit, ferret.getErrorKeys());
			task.run();
			error = task.getHasError();
			message = task.getErrorMessage();
			done = true;
		} catch ( Exception e ) {
			done = true;
			error = true;
			message = e.getMessage();
		} 


	}
    public boolean hasError() {
        return error;
    }
    public String getErrorMessage() {
        return message;
    }
	public boolean isDone() {
		return done;
	}
}