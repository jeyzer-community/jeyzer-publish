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

import static org.jeyzer.mx.event.JzrEventLevel.*;
import static org.jeyzer.mx.event.JzrEventSubLevel.*;


/**
 * <p>JzrPublisherEventCode defines all the Publisher events codes.<br></p> 
 * Publisher events code is associated with a message (description and recommendation), a level and a sub-level.<br>
 */
public enum JzrPublisherEventCode {

	// -------------------------------------------------
	// Publisher events for internal life cycle events
	// -------------------------------------------------
	/**
	 * Jeyzer Publisher is disabled : applicative events and data will not be made available to the Jeyzer Recorder. Set the <code>jeyzer.publisher.active</code> system property to true to activate it. Requires applicative restart.
	 */
	JZR_PUB_001(WARNING, HIGH,"Jeyzer Publisher is disabled : applicative events and data will not be made available to the Jeyzer Recorder. Set the jeyzer.publisher.active system property to true to activate it. Requires applicative restart."),
	/**
	 * Jeyzer Publisher is active : applicative events and data are collected. Set the <code>jeyzer.publisher.active</code> system property to false to disable it. Requires applicative restart.
	 */
	JZR_PUB_002(INFO, LOW, "Jeyzer Publisher is active : applicative events and data are collected. Set the jeyzer.publisher.active system property to false to disable it. Requires applicative restart."),
	/**
	 * Jeyzer recording collection.
	 */
	JZR_PUB_003(INFO, VERY_LOW, "Jeyzer recording collection."),

	
	// -------------------------------------------------
	// Publisher events for critical applicative events
	// -------------------------------------------------
	/**
	 * Applicative critical event list full. Oldest critical events may be lost. Increase the <code>events.critical.limit</code> init property to accept more events or review the applicative critical event generation to reduce it.
	 */
	JZR_PUB_101(WARNING, VERY_HIGH, "Applicative critical event list full. Events created after @token@ (process time) may be lost. Increase the events.critical.limit init property to accept more events or review the applicative critical event generation to reduce it."),
	/**
	 * Applicative critical event collection suspended at initialization time.
	 */
	JZR_PUB_102(WARNING, HIGH, "Applicative critical event collection suspended at initialization time."),
	/**
	 * Applicative critical event collection suspended at runtime.
	 */
	JZR_PUB_103(WARNING, HIGH, "Applicative critical event collection suspended at runtime."),
	/**
	 * Applicative critical event collection resumed.
	 */
	JZR_PUB_104(INFO, VERY_HIGH, "Applicative critical event collection resumed."),
	

	// -------------------------------------------------
	// Publisher events for warning applicative events
	// -------------------------------------------------
	/**
	 * Applicative warning event list full. Oldest warning events may be lost. Increase the <code>events.warning.limit</code> init property to accept more events or review the applicative warning event generation to reduce it.
	 */
	JZR_PUB_201(WARNING, HIGH, "Applicative warning event list full. Events created after @token@ (process time) may be lost. Increase the events.warning.limit init property to accept more events or review the applicative warning event generation to reduce it."),
	/**
	 * Applicative warning event collection suspended at initialization time.
	 */
	JZR_PUB_202(WARNING, MEDIUM, "Applicative warning event collection suspended at initialization time."),
	/**
	 * Applicative warning event collection suspended at runtime.
	 */
	JZR_PUB_203(WARNING, MEDIUM, "Applicative warning event collection suspended at runtime."),
	/**
	 * Applicative warning event collection resumed.
	 */
	JZR_PUB_204(INFO, HIGH, "Applicative warning event collection resumed."),
	
	
	// -------------------------------------------------
	// Publisher events for info applicative events
	// -------------------------------------------------
	/**
	 * Applicative info event list full. Oldest info events may be lost. Increase the <code>events.info.limit</code> init property to accept more events or review the applicative info event generation to reduce it.
	 */
	JZR_PUB_301(WARNING, MEDIUM, "Applicative info event list full. Events created after @token@ (process time) may be lost. Increase the events.info.limit init property to accept more events or review the applicative info event generation to reduce it."),
	/**
	 * Applicative info event collection suspended at initialization time.
	 */
	JZR_PUB_302(WARNING, MEDIUM, "Applicative info event collection suspended at initialization time."),
	/**
	 * Applicative info event collection suspended at runtime.
	 */
	JZR_PUB_303(WARNING, MEDIUM, "Applicative info event collection suspended at runtime."),
	/**
	 * Applicative info event collection resumed.
	 */
	JZR_PUB_304(INFO, MEDIUM, "Applicative info event collection resumed."),
	
	
	// -------------------------------------------------
	// Publisher events for applicative data
	// -------------------------------------------------
	/**
	 * Applicative data collection suspended at initialization time.
	 */
	JZR_PUB_502(WARNING, HIGH, "Applicative data collection suspended at initialization time."),
	/**
	 * Applicative data collection suspended.
	 */
	JZR_PUB_503(WARNING, HIGH, "Applicative data collection suspended at runtime."),
	/**
	 * Applicative data collection resumed.
	 */
    JZR_PUB_504(INFO, HIGH, "Applicative data collection resumed.");
	
	private final String description;
	private final JzrEventLevel level;
	private final JzrEventSubLevel subLevel;
	
    private JzrPublisherEventCode(JzrEventLevel level, JzrEventSubLevel subLevel, String value){
    	this.level = level;
    	this.subLevel = subLevel;
    	this.description = value;
    }
    
	/**
	 * Get the publisher event code name
	 * @return the publisher event code name
	 */
    public String getDisplayValue() {
    	return this.name();
    }
    
	/**
	 * Get the publisher event level
	 * @return the publisher event code level
	 */
    public JzrEventLevel getLevel() {
    	return this.level;
    }
    
	/**
	 * Get the publisher event sub level
	 * @return the publisher event sub level
	 */
    public JzrEventSubLevel getSubLevel() {
    	return this.subLevel;
    }

	/**
	 * Get the publisher event description
	 * @return the publisher event description
	 */
    public String getDescription() {
    	return this.description;
    }
    
	/**
	 * Get the publisher event description, replacing any token in the description with the <code>tokenValue</code> parameter
	 * @param  tokenValue      the token value to inject in the description
	 * @return the resulting publisher event description
	 */
    public String getDescription(String tokenValue) {
    	return this.description.replace("@token@", tokenValue);
    }
}
