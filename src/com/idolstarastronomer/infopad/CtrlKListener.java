package com.idolstarastronomer.infopad;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CtrlKListener extends KeyAdapter {
    JTextArea textArea;
    Clipboard clipboard;

    public CtrlKListener(JTextArea textArea) {
	super();
	this.textArea = textArea;
	Toolkit tk = Toolkit.getDefaultToolkit();
	clipboard = tk.getSystemClipboard();
    }

    public void keyPressed(KeyEvent e) {
	if(e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) {
	    // ctrl-k (kill to end of line)
	    if(e.getKeyCode()==KeyEvent.VK_K) {
		try {
		    int currpos = textArea.getCaretPosition();
		    int endpos = textArea.getLineEndOffset(textArea.getLineOfOffset(currpos));
		    Document doc = textArea.getDocument();
		    StringSelection killedString = new StringSelection(doc.getText(currpos,endpos-currpos-1));
		    if(endpos-currpos>1) {
			doc.remove(currpos,endpos-currpos-1);
			// textArea.setCaretPosition(currpos);
			clipboard.setContents(killedString,killedString);

		    }
		    else {
			doc.remove(currpos,endpos-currpos);
			// right now we wont put plant likes in the clipboard
		    }
		}
		catch(BadLocationException ex) {
		    // This should not happen
		}
	    }
	    else if(e.getKeyCode()==KeyEvent.VK_A) {
		try {
		    int currpos = textArea.getCaretPosition();
		    int startpos = textArea.getLineStartOffset(textArea.getLineOfOffset(currpos));

		    textArea.setCaretPosition(startpos);
		}
		catch(BadLocationException ex) {
		    // This also should not happen
		}
	    }
	    else if(e.getKeyCode()==KeyEvent.VK_E) {
		try {
		    int currpos = textArea.getCaretPosition();
		    int endpos = textArea.getLineEndOffset(textArea.getLineOfOffset(currpos));
		    
		    textArea.setCaretPosition(endpos-1);
		}
		catch(BadLocationException ex) {
		    // And the same with this
		}
	    }
	}
    }
}

