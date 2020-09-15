package org.jeyzer.mx.event;

/*-
 * ---------------------------LICENSE_START---------------------------
 * Jeyzer Publisher
 * --
 * Copyright (C) 2020 Jeyzer SAS
 * --
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * ----------------------------LICENSE_END----------------------------
 */


/**
 * <p>JzrEventLevel indicates the importance of the event : info, warning or critical. <br></p>
 */
public enum JzrEventLevel {

	INFO('I'), 
	WARNING('W'), 
	CRITICAL('C');

    private final char capital;
	
    private JzrEventLevel(char capital){
    	this.capital = capital;
    }
	
	public char getCapital(){
		return this.capital;
	}
}
