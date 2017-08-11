package com.perfectrem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class f
{
	/**
	 * core Android logging function: retrieves class and method names from stack trace and builds a templated output string for the Android LogCat with a "frankify" filter tag.
	 * @param message templated string specifying caller class and method.
	 */
	public static void log(final String message) {
		String callerClass = "<callerClass>", callerMethod = "<callerMethod>", t = "frankify";

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTraceElements.length; i++) {
			StackTraceElement element = stackTraceElements[i];
			if (!element.getClassName().equals(f.class.getName()) && element.getClassName().indexOf("java.lang.Thread") != 0) {
				String classname = element.getClassName();
				callerClass = classname.lastIndexOf(".") != -1 ? classname.substring(classname.lastIndexOf(".") + 1, classname.length()) : classname;
				callerMethod = element.getMethodName();
				break;
			}
		}
		Log.d(t, message != null ? callerClass + " #" + callerMethod + " " + message : callerClass + " #" + callerMethod);
	}

	/**
	 * Android logging function for printing out an integer value.
	 * @param message templated string specifying caller class and method.
	 */
	public static void log(final int message) {
		log(message + "");
	}

	/**
	 * Android logging function for printing content of a map.
	 * @param message templated string specifying caller class and method.
	 * @param map     input variable holding concerned elements to be printed.
	 */
	public static void log(final String message, final Map map) {
		if (map == null) log(message + " map is null"); else if (map.entrySet() == null) log(message + " map entry set is null");
		StringBuilder sb = new StringBuilder();
		sb.append("map: {");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			sb.append(" ").append(pair.getKey()).append(":").append(pair.getValue());
		}
		sb.append(" }");
		log(message + " " + sb.toString());
	}

	/**
	 * Android logging function for printing content of array objects.
	 * @param message templated string specifying caller class and method.
	 * @param array   input variable holding concerned elements to be printed.
	 */
	public static void log(final String message, final Object[] array) {
		if (array == null) log(message + " array: null");
		int iMax = (array != null ? array.length : 0) - 1;
		if (iMax == -1) log(message + " array: []");
		StringBuilder sb = new StringBuilder();
		sb.append(" [ ");
		for (int i = 0; ; ++i) {
			sb.append(String.valueOf(array[i]));
			if (i == iMax) {
				log(message + sb.append(" ] ").toString());
				return;
			}
			sb.append(", ");
		}
	}

	/**
	 * Android logging function for printing the content of a singular object.
	 * @param message templated string specifying caller class and method.
	 * @param object  input variable holding concerned elements to be printed.
	 */
	public static void log(final String message, final Object object) {
		log(message, object);
	}

	/**
	 * Android logging function for printing content of an array list.
	 * @param message templated string specifying caller class and method.
	 * @param list    input variable holding concerned elements to be printed.
	 */
	public static void log(final String message, final ArrayList list) {
		log(message, list.toArray());
	}
	
	public static void log(final Intent intent) {
		if (intent == null) {
			f.log("intent: null");
		}
		Bundle bundle = intent.getExtras();
		for (String key : bundle.keySet()) {
			f.log("key: " + key + " value: " + bundle.get(key));
		}
	}

	/**
	 * Default Android logging function for only printing out class name and method.
	 */
	public static void log() {
		log("");
	}

	/**
	* Prints out whether the variable passed in null or not.
	*/
	public static void nullCheck(final String name, Object object) {
		if (object == null) {
			log(name + ": null");
		} else {
			log(name + ": not null");
		}
	}

	public static void threwException(Exception e) {
		log("threw exception: " + e.getMessage());
	}
}

