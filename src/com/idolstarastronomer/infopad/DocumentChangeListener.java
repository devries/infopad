package com.idolstarastronomer.infopad;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

public class DocumentChangeListener implements DocumentListener {
    MainApp app;

    public DocumentChangeListener() {
	app = MainApp.getInstance();
    }

    public void anyChange(DocumentEvent e) {
	Document d;
	String noteContent;
	
	// After a change we clear the highlights
	app.ui.clearAllHighlights();

	d = e.getDocument();
	try {
	    noteContent = d.getText(0,d.getLength());
	    app.noteList.getCurrent().setContents(noteContent);

	}
	catch(BadLocationException ex) {
	    // Shouldn't get here
	}
	app.periodicSave.scheduleSave();
    }

    public void changedUpdate(DocumentEvent e) {
	anyChange(e);
    }

    public void insertUpdate(DocumentEvent e) {
	anyChange(e);
    }

    public void removeUpdate(DocumentEvent e) {
	anyChange(e);
    }
}
