package com.idolstarastronomer.infopad;
import java.util.Date;
import java.io.Serializable;

public class Note implements Serializable, Comparable {
    String contents;
    Date creationDate;
    Date modificationDate;

    public Note(String contents) {
	creationDate = new Date();
	modificationDate = creationDate;
	this.contents=contents;
    }

    public Note() {
	this("");
    }

    public void setContents(String contents) {
	modificationDate = new Date();
	this.contents=contents;
    }

    public String getContents() {
	return contents;
    }

    public Date getCreationDate() {
	return creationDate;
    }

    public Date getModificationDate() {
	return modificationDate;
    }

    public int compareTo(Object o) {
	Note note = (Note)o;
	return this.modificationDate.compareTo(note.modificationDate);
    }
}
