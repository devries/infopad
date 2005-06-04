package com.idolstarastronomer.infopad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.net.URL;
import java.io.IOException;

public class HelpWindow extends JDialog {
    
    public HelpWindow(Frame owner) {
	super(owner,"InfoPad Help");

	JEditorPane htmlPane;
	JScrollPane htmlScroll;
	ClassLoader cl = getClass().getClassLoader();

	htmlPane = new JEditorPane();
	htmlPane.setEditable(false);
	htmlPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
	htmlPane.setContentType("text/html");
	URL helpDocUrl = cl.getResource("helpdoc/index.html");

	try {
	    htmlPane.setPage(helpDocUrl);
	}
	catch(IOException e) {
	    htmlPane.setText("<pre>Unable to load help documentation</pre>");
	}

	htmlScroll = new JScrollPane(htmlPane);

	htmlScroll.setPreferredSize(new Dimension(400,300));

	getContentPane().add(htmlScroll);
	// setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	pack();
    }

    public void displayHelp() {
	setVisible(true);
    }
}
