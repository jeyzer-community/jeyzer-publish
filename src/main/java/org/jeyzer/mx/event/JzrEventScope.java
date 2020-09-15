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
 * <p>JzrEventScope indicates the scope of the event : <br></p>
 * <ul> 
 * <li> Action : event applies at thread level and can be time ranged or one shot.</li>
 * <li> Global : event applies at process level and can be time ranged or one shot.</li>
 * <li> System : event applies at process level and covers its whole life time.</li>
 * </ul>
 */
public enum JzrEventScope {

	ACTION("Action", 'A'),
	GLOBAL("Global", 'G'),
	SYSTEM("System", 'S');

	private final String label;
    private final char capital;
	
    private JzrEventScope(String label, char capital){
    	this.label = label;
    	this.capital = capital;
    }
    
    @Override
	public String toString(){
		return this.label;
	}
    
	public char getCapital(){
		return this.capital;
	}
}
