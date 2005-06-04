package com.idolstarastronomer.infopad;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.regex.*;

public class SearchButtonListener implements ActionListener {
    MainApp app;

    public SearchButtonListener() {
	app = MainApp.getInstance();
    }

    public void actionPerformed(ActionEvent e) {
	String searchContent;
	Pattern searchPattern;
	Matcher searchMatch;

	try {
	    searchContent = app.ui.searchField.getText();
	    if(searchContent.length()>0) {
		searchPattern = Pattern.compile(searchContent, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		searchMatch = app.noteList.continueSearch(searchPattern);
		app.ui.displayNote(app.noteList.getCurrent());
		app.ui.highlightAllMatches(searchMatch);
	    }
	}
	catch(Exception ex) {
	    // Bad search or something
	}
    }

}
