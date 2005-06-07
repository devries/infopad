package com.idolstarastronomer.infopad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.net.URL;
import java.io.IOException;

public class AboutDialog {
    JOptionPane dialogPane;
    MainUI owner;

    public AboutDialog(MainUI owner) {
	this.owner = owner;
	dialogPane = new JOptionPane("<html><p align=\"center\"><font size=\"+1\">InfoPad alpha-5</font><br>&copy; 2005<br>Christopher De Vries</html></p>",JOptionPane.INFORMATION_MESSAGE);
	dialogPane.setIcon(owner.infoPadIcon);
    }

    public void displayAboutDialog() {
	JDialog dialog = dialogPane.createDialog(owner,"About InfoPad");
	dialog.setVisible(true);
    }
}
