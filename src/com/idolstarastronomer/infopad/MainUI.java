package com.idolstarastronomer.infopad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.IOException;
import java.util.regex.Matcher;

public class MainUI extends JFrame {
    JTextArea notePad;
    JTextField searchField;
    YellowHighlightPainter myHighlightPainter;
    Highlighter notePadHighlighter;
    HelpWindow helpWindow;
    AboutDialog aboutDialog;
    Icon infoPadIcon;

    public MainUI() {
	super("InfoPad");
	ClassLoader cl = getClass().getClassLoader();
	Toolkit tk = Toolkit.getDefaultToolkit();
	int shortcutKeyMask;

	// Set the accelerator shortcut key mask to default for platform
	try {
	    shortcutKeyMask = tk.getMenuShortcutKeyMask();
	}
	catch(HeadlessException e) {
	    // It's going to be hard to use this program without a head
	    shortcutKeyMask = Event.CTRL_MASK;
	}

	// Set look and feel to native
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch(Exception e) { }

	// X11 Tweak:
	// Let the window manager choose the window location.
	UnixSpecificCode.letWindowManagerPlaceWindow(this);	
	
	// Get an instance of the yellow highlight painter
	// to highlight searches
	myHighlightPainter = new YellowHighlightPainter();

	// Set a frame icon
	Image iconImage = tk.getImage(cl.getResource("icons/icon-trans-small.png"));
	setIconImage(iconImage);
	infoPadIcon = new ImageIcon(iconImage);

	// Run program termination if the window is closed
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
		public void windowClosed(WindowEvent e) {
		    MainApp.getInstance().terminateProgram();
		}
	    });

	// Stickynote Tab
	// Stickynote note area 
	// I create this so that the menus can refer to it
	notePad = new JTextArea();
	notePad.setLineWrap(true);
	notePad.setWrapStyleWord(true);
	notePad.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
	notePad.requestFocusInWindow();
	notePad.addMouseListener(new MouseInputAdapter() {
		public void mouseEntered(MouseEvent e) {
		    notePad.requestFocusInWindow();
		}
	    });

	// Add a mouse listener to insert selection into component with middle
	// mouse button (Unix only?)
	// notePad.addMouseListener(new UnixSelectionPasteListener(notePad));

	// Set ctrl-a and ctrl-e to navigate to beginning and end of lines
	// ctrl-k deletes to end of line. I couldn't find a good way to make
	// ctrl-a not select all, so I inputMapped it to "noop-action" which is
	// just plain made up, but appears to work under linux.
	InputMap inputMap = notePad.getInputMap();
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK), "noop-action");

	notePad.addKeyListener(new CtrlKListener(notePad));

	// Get the notePad's highlighter for use with searches
	notePadHighlighter = notePad.getHighlighter();

	// Make a listener to sync changes with the current note
	notePad.getDocument().addDocumentListener(new DocumentChangeListener());

	final JScrollPane noteScroll = new JScrollPane(notePad);
	noteScroll.setPreferredSize(new Dimension(300,200));
	noteScroll.setBorder(BorderFactory.createEmptyBorder());

	// Menu Bar
	final JMenuBar menubar = new JMenuBar();
	setJMenuBar(menubar);

	JMenu filemenu = new JMenu("File");
	filemenu.setMnemonic(KeyEvent.VK_F);
	JMenu editmenu = new JMenu("Edit");
	editmenu.setMnemonic(KeyEvent.VK_E);
	JMenu helpmenu = new JMenu("Help");
	helpmenu.setMnemonic(KeyEvent.VK_H);
	menubar.add(filemenu);
	menubar.add(editmenu);
	if(!MacOSSpecificCode.isMacOSX()) {
	    menubar.add(Box.createHorizontalGlue());
	}
	menubar.add(helpmenu);

	// File Menu
	JMenuItem newNote = new JMenuItem("New Note");
	newNote.setMnemonic(KeyEvent.VK_N);
	newNote.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask));
	JMenuItem deleteNote = new JMenuItem("Delete Note");
	deleteNote.setMnemonic(KeyEvent.VK_D);
	deleteNote.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask));
	JMenuItem saveNotes = new JMenuItem("Save Notes");
	saveNotes.setMnemonic(KeyEvent.VK_S);
	saveNotes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask));
	filemenu.add(newNote);
	filemenu.add(deleteNote);
	filemenu.add(saveNotes);

	// We'll add an Exit key, unless this is OS X in which case
	// they have a quit key in the application menu.
	if(!MacOSSpecificCode.isMacOSX()) {
	    JMenuItem exit = new JMenuItem("Exit");
	    exit.setMnemonic(KeyEvent.VK_X);
	    exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutKeyMask));
	    filemenu.addSeparator();
	    filemenu.add(exit);
	    
	    exit.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			MainApp.getInstance().terminateProgram();
		    }
		});
	}

	newNote.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MainApp app = MainApp.getInstance();
		    app.noteList.insert(new Note());
		    displayNote(app.noteList.getCurrent());
		}
	    });

	deleteNote.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MainApp app = MainApp.getInstance();
		    app.noteList.remove();
		    displayNote(app.noteList.getCurrent());
		}
	    });
	saveNotes.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MainApp app = MainApp.getInstance();
		    try {
			app.noteListSerializationManager.save();
		    }
		    catch(IOException outputException) {
			JOptionPane.showMessageDialog(app.ui,"Unable to save to\n"+app.noteFile.toString()+"\nPlease check permissions.","Warning",JOptionPane.WARNING_MESSAGE);
		    }
		}
	    });
	
	// Edit Menu
	ActionMap actionMap = notePad.getActionMap();
	Action cutAction = actionMap.get(DefaultEditorKit.cutAction);
	cutAction.putValue(Action.NAME, "Cut");
	cutAction.putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_T));
	cutAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X,shortcutKeyMask));
	JMenuItem cutItem = new JMenuItem(cutAction);

	Action copyAction = actionMap.get(DefaultEditorKit.copyAction);
	copyAction.putValue(Action.NAME, "Copy");
	copyAction.putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_C));
	copyAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,shortcutKeyMask));
	JMenuItem copyItem = new JMenuItem(copyAction);

	Action pasteAction = actionMap.get(DefaultEditorKit.pasteAction);
	pasteAction.putValue(Action.NAME,"Paste");
	pasteAction.putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_V));
	pasteAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V,shortcutKeyMask));
	JMenuItem pasteItem = new JMenuItem(pasteAction);

	// I want to use ctrl-a to move to the beginning of a line
	int selectAllMask;
	if(shortcutKeyMask==Event.CTRL_MASK) {
	    selectAllMask = Event.ALT_MASK;
	}
	else {
	    selectAllMask = shortcutKeyMask;
	}

	Action selectAllAction = actionMap.get(DefaultEditorKit.selectAllAction);
	selectAllAction.putValue(Action.NAME, "Select All");
	selectAllAction.putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_A));
	selectAllAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,selectAllMask));
	JMenuItem selectAllItem = new JMenuItem(selectAllAction);

	editmenu.add(cutItem);
	editmenu.add(copyItem);
	editmenu.add(pasteItem);
	editmenu.add(selectAllItem);

	// Help Menu
	JMenuItem helpItem = new JMenuItem("InfoPad Help");
	helpItem.setMnemonic(KeyEvent.VK_I);
	helpmenu.add(helpItem);
	helpWindow = new HelpWindow(this);
	helpItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    helpWindow.displayHelp();
		}
	    });
	
	aboutDialog = new AboutDialog(this);
	if(!MacOSSpecificCode.isMacOSX()) {
	    JMenuItem aboutInfoPad = new JMenuItem("About InfoPad");
	    aboutInfoPad.setMnemonic(KeyEvent.VK_A);
	    helpmenu.add(aboutInfoPad);
	    aboutInfoPad.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			aboutDialog.displayAboutDialog();
		    }
		});
	}

	// Stickynote navigation buttons
	final JButton prevNoteButton = new JButton("Previous");
	final JButton nextNoteButton = new JButton("Next");
	final JPanel noteNavPanel = new JPanel();
	noteNavPanel.setLayout(new BoxLayout(noteNavPanel, BoxLayout.LINE_AXIS));
	noteNavPanel.add(prevNoteButton);
	noteNavPanel.add(Box.createHorizontalGlue());
	noteNavPanel.add(nextNoteButton);
	noteNavPanel.setBackground(Color.WHITE);
	noteNavPanel.setBorder(BorderFactory.createEmptyBorder());

	// Navigation Button Action Listeners
	prevNoteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MainApp app = MainApp.getInstance();
		    displayNote(app.noteList.getPrevious());
		}
	    });

	nextNoteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MainApp app = MainApp.getInstance();
		    displayNote(app.noteList.getNext());
		}
	    });

	// Sticklnote create/delete buttons
	// final JButton newNoteButton = new JButton("New");
	// final JButton delNoteButton = new JButton("Delete");
	// final JPanel noteLifePanel = new JPanel();
	// noteLifePanel.setLayout(new BoxLayout(noteLifePanel, BoxLayout.LINE_AXIS));
	// noteLifePanel.add(newNoteButton);
	// noteLifePanel.add(Box.createHorizontalGlue());
	// noteLifePanel.add(delNoteButton);

	// Stickynote Panel creation
	final JPanel notePanel = new JPanel();
	notePanel.setLayout(new BorderLayout());
	// notePanel.add(noteLifePanel,BorderLayout.NORTH);
	notePanel.add(noteNavPanel,BorderLayout.SOUTH);
	notePanel.add(noteScroll,BorderLayout.CENTER);
	notePanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));

	// Tabbed Pane --- For possible future tabs
	// final JTabbedPane tabbedPane = new JTabbedPane();
	// tabbedPane.addTab("Notes",notePanel);

	// Search Pane
	JLabel searchLabel = new JLabel("Search: ");
	searchField = new JTextField(15);
	JButton searchButton = new JButton("Find Next");
	JPanel searchPanel = new JPanel();
	searchLabel.setLabelFor(searchField);
	searchPanel.add(searchLabel);
	searchPanel.add(searchField);
	searchPanel.add(searchButton);

	searchField.getDocument().addDocumentListener(new SearchChangeListener());
	searchButton.addActionListener(new SearchButtonListener());

	// Set up Main Window
	Container mainPanel = getContentPane();
	// mainPanel.add(tabbedPane,BorderLayour.CENTER);
	mainPanel.add(notePanel,BorderLayout.CENTER);
	mainPanel.add(searchPanel,BorderLayout.SOUTH);

	// Set text area to receive focus
	addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {
		    notePad.requestFocusInWindow();
		}
	    });

	if(MacOSSpecificCode.isMacOSX()) {
	    MacOSSpecificCode.createApplicationMenuListeners();
	}

	pack();
	setVisible(true);
    }

    public void highlightAllMatches(Matcher searchMatch) throws BadLocationException {
	int start = 0;

	notePadHighlighter.removeAllHighlights();
	while(searchMatch.find(start)) {
	    // Sometimes emptiness fits, but why highlight it?
	    if(start!=searchMatch.end()) {
		notePadHighlighter.addHighlight(searchMatch.start(),searchMatch.end(),myHighlightPainter);
		if(start==0) {
		    // Let's move to the end of the first match
		    notePad.setCaretPosition(searchMatch.end());
		}
		start = searchMatch.end();
	    }
	    else {
		// When the match is empty we have to keep moving
		// otherwise we never exit this loop
		start++;
	    }
	}
    }

    public void displayNote(Note n) {
	notePad.setText(n.getContents());
	notePad.setCaretPosition(0);
    }
	
    public void clearAllHighlights() {
	notePadHighlighter.removeAllHighlights();
    }

}
