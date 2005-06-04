package com.idolstarastronomer.infopad;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import java.util.regex.*;

public class SearchChangeListener implements DocumentListener {
    MainApp app;

    public SearchChangeListener() {
	app = MainApp.getInstance();
    }

    public void anyChange(DocumentEvent e) {
	Document d;
	String searchContent;
	Pattern searchPattern;
	Matcher searchMatch;

	d = e.getDocument();
	if(d.getLength()>0) {
	    try {
		searchContent = d.getText(0,d.getLength());
		searchPattern = Pattern.compile(searchContent, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		searchMatch = app.noteList.beginSearch(searchPattern);
		app.ui.displayNote(app.noteList.getCurrent());
		app.ui.highlightAllMatches(searchMatch);
	    }
	    catch(Exception ex) {
		// Bad search or something
	    }
	}
	else {
	    app.ui.clearAllHighlights();
	}
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
