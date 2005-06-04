package com.idolstarastronomer.infopad;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;

public class MacOSSpecificCode {
    static boolean isMacOSX;
    static boolean isMacOSXSet=false;

    public static boolean isMacOSX() {
	if(!isMacOSXSet) {
	    String lcOSName = System.getProperty("os.name").toLowerCase();
	    isMacOSX = lcOSName.startsWith("mac os x");
	    isMacOSXSet=true;
	}
	return isMacOSX;
    }

    public static void createApplicationMenuListeners() {
	// Set up Mac Application Menu's exit and about to work
	// Must use reflection and a proxy to make this compile everywhere
	// Based on Apple Mailing list post from Eric Eslinger
	try {
	    Class applicationClass = Class.forName("com.apple.eawt.Application");
	    final Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
	    Object applicationObject = applicationClass.newInstance();
	    ClassLoader appClassLoader = applicationClass.getClassLoader();
	    Class[] interfaces = {applicationListenerClass};

	    Object applicationListenerProxy = Proxy.newProxyInstance(appClassLoader,interfaces,new InvocationHandler() {
		    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
			if(m.getDeclaringClass().equals(applicationListenerClass)) {
			    if(m.getName().equals("handleQuit")) {
				MainApp app = MainApp.getInstance();

				app.terminateProgram();
				return null;
			    }
			    else if(m.getName().equals("handleAbout")) {
				Object appEventObject = args[0];
				Class[] appEventargs = {Boolean.TYPE};
				Method setHandled = appEventObject.getClass().getMethod("setHandled",appEventargs);
				Object[] invokeargs = {Boolean.valueOf(true)};
				setHandled.invoke(appEventObject,invokeargs);
				MainApp app = MainApp.getInstance();
				app.ui.aboutDialog.displayAboutDialog();

				return null;
			    }
			    else {
				return null;
			    }
			}
			else {
			    return m.invoke(proxy,args);
			}
		    }
		});

	    Object[] args = {applicationListenerProxy};
	    Class[] addAppListenerArgs = {applicationListenerClass};
	    Method addAppListenerMethod = applicationClass.getMethod("addApplicationListener",addAppListenerArgs);
	    addAppListenerMethod.invoke(applicationObject,args);
	}
	catch(Exception e) {
	    System.err.println("Application Menu Initialization Error");
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public static void setMacSpecificProperties() {
	System.setProperty("apple.laf.useScreenMenuBar","true");
	System.setProperty("apple.awt.showGrowBox","false");
	System.setProperty("apple.awt.brushMetalLook","true");
    }
}
