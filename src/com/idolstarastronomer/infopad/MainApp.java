package com.idolstarastronomer.infopad;
import javax.swing.SwingUtilities;
import java.io.*;
import javax.swing.JOptionPane;

public class MainApp {
    static MainApp instance = null;
    public MainUI ui=null;
    public NoteList noteList;
    SerializationManager noteListSerializationManager;
    PeriodicSaveThread periodicSave;
    File noteFile;
    boolean continueTermination;

    private MainApp() {
	noteList = new NoteList();
	noteFile = new File(System.getProperty("user.home"),".infopad_nots");
	noteListSerializationManager = new SerializationManager(noteFile,noteList);

	// Mac OS Specifics
	if(MacOSSpecificCode.isMacOSX()) {
	    MacOSSpecificCode.setMacSpecificProperties();
	}

	if(noteFile.exists()) {
	    try {
		noteList = (NoteList)noteListSerializationManager.load();

	    }
	    catch(Exception e) {
		// Must not exist or be readable, or not right class?
	    }
	}

	periodicSave = new PeriodicSaveThread(noteListSerializationManager);
	periodicSave.start();
    }

    public void initializeUserInterface() {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    ui = new MainUI();
		    ui.displayNote(noteList.getCurrent());
		}
	    });
    }

    public void terminateProgram() {
	continueTermination=true;
	try {
	    noteListSerializationManager.save();
	}
	catch(IOException e) {
	    Object[] options = {"OK", "Cancel"};
	    int result = JOptionPane.showOptionDialog(this.ui,"Unable to save to\n"+this.noteFile.toString()+"\nFile Permissions may need to be changed.\nExit without saving?","Warning",JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[1]);
	    if(result==1 || result==JOptionPane.CLOSED_OPTION) continueTermination=false;
	}
	if(continueTermination) System.exit(0);
    }

    // Wrap the UI display note in the swing event loop
    public void displayNote(final Note n) {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    ui.displayNote(n);
		}
	    });
    }

    public static MainApp getInstance() {
	if(instance==null) {
	    instance = new MainApp();
	}
	return instance;
    }

    public static void main(String[] args) {
	MainApp app;

	app = MainApp.getInstance();
	app.initializeUserInterface();

    }
}
