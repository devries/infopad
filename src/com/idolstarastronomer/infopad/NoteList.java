package com.idolstarastronomer.infopad;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.Serializable;
import java.util.Date;

public class NoteList implements Serializable {
    int currentNoteIndex;
    LinkedList notes;
    transient int lastSearchPage;
    static final long serialVersionUID = -9058829022067804539L;

    public NoteList() {
	notes = new LinkedList();
	append(new Note("Welcome to InfoPad\n\nTo get started delete this note and start making notes of your own. You can use the search bar to search through your notes and navigate using the previous and next buttons. Look under the file menu to create new notes, delete notes, and save notes to disk."));
	currentNoteIndex=0;
	lastSearchPage=0;
    }

    public synchronized Note getCurrent() {
	return (Note)notes.get(currentNoteIndex);
    }

    public synchronized Note getNext() {
	currentNoteIndex++;
	if(currentNoteIndex==notes.size()) {
	    currentNoteIndex=0;
	}

	return (Note)notes.get(currentNoteIndex);
    }

    public synchronized Note getPrevious() {
	if(currentNoteIndex==0) {
	    currentNoteIndex=notes.size();
	}
	currentNoteIndex--;

	return (Note)notes.get(currentNoteIndex);
    }

    public synchronized void insert(Note n) {
	notes.add(currentNoteIndex,n);
    }

    public synchronized void append(Note n) {
	notes.add(n);
    }

    public synchronized void remove() {
	notes.remove(currentNoteIndex);
	if(currentNoteIndex==notes.size()) {
	    currentNoteIndex=0;
	}
	if(notes.size()==0) {
	    notes.add(new Note());
	}
    }
	
    public synchronized Matcher beginSearch(Pattern p) {
	Note n = getCurrent();
	Matcher m = p.matcher(n.getContents());
	lastSearchPage = currentNoteIndex;
	while(!m.find()) {
	    n = getNext();
	    m = p.matcher(n.getContents());
	    if(lastSearchPage==currentNoteIndex) break;
	}
	return m;
    }

    public synchronized Matcher continueSearch(Pattern p) {
	Note n = getNext();
	Matcher m = p.matcher(n.getContents());
	while(!m.find()) {
	    n = (Note)getNext();
	    m = p.matcher(n.getContents());
	    if(lastSearchPage==currentNoteIndex) break;
	}
	return m;
    }

    public synchronized Note getRecentlyModifiedNote() {
	Note n;
	Date resultDate=null;
	int resultNoteIndex=0;
	
	for(currentNoteIndex=0;currentNoteIndex<notes.size();currentNoteIndex++) {
	    n=(Note)notes.get(currentNoteIndex);
	    if(resultDate==null) {
		resultDate = n.getModificationDate();
		resultNoteIndex = currentNoteIndex;
	    }
	    else if(resultDate.compareTo(n.getModificationDate())>0) {
		resultDate = n.getModificationDate();
		resultNoteIndex = currentNoteIndex;
	    }
	}

	currentNoteIndex=resultNoteIndex;
	return getCurrent();
    }
}



