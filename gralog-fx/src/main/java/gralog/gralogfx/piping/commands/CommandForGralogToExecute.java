/*
 * @author felix
 */
package gralog.gralogfx.piping.commands;
import java.util.concurrent.ThreadLocalRandom;
import gralog.events.*;
import gralog.rendering.*;
import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.gralogfx.*;
import gralog.gralogfx.StructurePane;

import java.util.function.*;

import java.util.Arrays;



import java.util.*;

import java.io.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
// import javafx.scene.control.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;


public abstract class CommandForGralogToExecute {
	// String name;

	public Structure structure;

	protected String[] externalCommandSegments;
	private Boolean failed = false;
	protected Exception error;
	private String consoleMessage = null;


	private String response = null;

	// public void CommandForGralogToExecute(String[] externalCommandSegments) {
	// 	this.externalCommandSegments = externalCommandSegments;
	// }

	protected void setResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}

	public Boolean didFail() {
		return this.failed;
	}

	public void fail() {
		this.failed=true;
	}

	public Exception getError() {
		return this.error;
	}

	public void setConsoleMessage(Exception e) {
		this.consoleMessage = e.toString() + "\n";
	}
	public void setConsoleMessage(String msg) {
		this.consoleMessage = msg;
	}
	public String getConsoleMessage() {
		return this.consoleMessage;
	}



	public abstract void handle();

	public void setStructure(Structure s) {
		this.structure = s;
	}



}
