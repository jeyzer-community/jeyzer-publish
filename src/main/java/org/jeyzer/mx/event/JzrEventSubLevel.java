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
 * <p>JzrEventSubLevel categorizes the importance of the event within his level through 5 sub levels.<br></p> 
 * By default set to medium if not specified at event creation time.
 */
public enum JzrEventSubLevel {

	VERY_HIGH(10),
	HIGH(9),
	MEDIUM(8), 
	LOW(7),
	VERY_LOW(6);

	private final int level;
	
    private JzrEventSubLevel(int value){
    	this.level = value;
    }
    
    @Override
	public String toString(){
		return Integer.toString(this.level);
	}
    
	public int value(){
		return this.level;
	}
}
