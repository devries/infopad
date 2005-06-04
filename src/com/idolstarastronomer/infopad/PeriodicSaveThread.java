package com.idolstarastronomer.infopad;
import java.io.IOException;

public class PeriodicSaveThread extends Thread {
    SerializationManager sm;
    boolean keepRunning = false;
    boolean doSave = false;

    PeriodicSaveThread(SerializationManager sm) {
	this.sm=sm;
	setDaemon(true);
    }

    public void run() {
	keepRunning=true;
	while(keepRunning) {
	    if(isSaveScheduled()) {
		unscheduleSave();
		try {
		    sm.save();
		}
		catch(IOException ioe) {
		    // We should issue some sort of warning I think
		}
	    }
	    try {
		sleep(60000);
	    }
	    catch(InterruptedException ie) { 
		// Just continue loop in interrupted
	    }
	}
    }

    public synchronized void scheduleSave() {
	doSave = true;
    }

    public synchronized boolean isSaveScheduled() {
	return doSave;
    }

    public synchronized void unscheduleSave() {
	doSave = false;
    }

    public synchronized void scheduleStop() {
	keepRunning = false;
    }

}
