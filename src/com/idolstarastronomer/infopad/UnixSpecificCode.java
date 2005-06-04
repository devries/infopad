package com.idolstarastronomer.infopad;
import java.awt.Window;
import java.lang.reflect.Method;

public class UnixSpecificCode {
    static boolean isUnix;
    static boolean isUnixSet=false;

    public static boolean isUnix() {
	if(!isUnixSet) {
	    String lcOsName = System.getProperty("os.name").toLowerCase();
	    if(lcOsName.startsWith("linux")) {
		isUnix=true;
	    }
	    else if(lcOsName.startsWith("sunos")) {
		isUnix=true;
	    }
	    else {
		isUnix=false;
	    }
	}
	isUnixSet=true;
	return isUnix;
    }

    public static void letWindowManagerPlaceWindow(Window window) {
	try {
	    Class[] args = {Boolean.TYPE};
	    Method setLocationByPlatformMethod = window.getClass().getMethod("setLocationByPlatform",args);
	    Object[] invokeargs = {Boolean.valueOf(true)};
	    setLocationByPlatformMethod.invoke(window,invokeargs);
	}
	catch(Exception e) {
	    // Don't worry, if it didn't work the program will still run
	}
    }

    // Create a middle button listener that inserts text from the Selection
    // into the component. 
}
