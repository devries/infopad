package com.idolstarastronomer.infopad;
import java.io.*;

public class SerializationManager {
    File savefile;
    Serializable serObj;

    SerializationManager(File savefile, Serializable serObj) {
	this.savefile = savefile;
	this.serObj = serObj;
    }

    public synchronized void save() throws IOException {
	ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(savefile)));
	out.writeObject(serObj);
	out.flush();
	out.close();
    }

    public synchronized Serializable load() throws IOException, ClassNotFoundException {
	ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(savefile)));
	serObj=(Serializable)in.readObject();
	in.close();
	return serObj;
    }

    public synchronized void setSerializableObject(Serializable serObj) {
	this.serObj = serObj;
    }

    public synchronized void setSaveFile(File savefile) {
	this.savefile = savefile;
    }
}
