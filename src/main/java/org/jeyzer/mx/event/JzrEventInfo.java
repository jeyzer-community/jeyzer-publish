package org.jeyzer.mx.event;

/*-
 * ---------------------------LICENSE_START---------------------------
 * Jeyzer Publisher
 * --
 * Copyright (C) 2020 - 2023 Jeyzer
 * --
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * ----------------------------LICENSE_END----------------------------
 */


/**
 * <p>JzrEventInfo is a bean class holding monitoring event information<br></p> 
 * When active, the Jeyzer Recorder will access this info to store it.<br>  
 * The source, scope, event type, event id, level, start time are always set.<br>
 * All other parameters are optional.
 */
public interface JzrEventInfo {
	
	/**
	 * Get the name of the source that initiated that event
	 * By default the application name
	 * @return the event source name
	 */
	public String getSource();

	/**
	 * Get the name of the service that initiated that event. Optional
	 * @return the event service name
	 */
	public String getService();
	
	/**
	 * Get the event id. Must be unique
	 * @return the event id
	 */
	public String getId();

	/**
	 * Get the event scope. By default session one
	 * @return the event scope
	 */
	public JzrEventScope getScope();
	
	/**
	 * Get the event code. Mandatory 
	 * @return the event code
	 */
	public JzrEventCode getCode();
	
	/**
	 * Get the event message. Description of the issue and recommendation. Optional.
	 * @return the event message
	 */
	public String getMessage();

	/**
	 * Get the event trustFactor. Optional. 100 (full trust) by default
	 * @return the event message
	 */
	public short getTrustFactor();
	
	/**
	 * Get the event start time
	 * @return the event start time
	 */
	public long getStartTime();

	/**
	 * Get the event end time. Optional
	 * @return the event end time
	 */
	public long getEndTime();
	
	/**
	 * Get the event thread id. Set only if event scope is action
	 * @return the event thread id
	 */
	public long getThreadId();

	/**
	 * Indicates if the event is one shot.In such case, start date equals end date.
	 * @return true if one shot
	 */
	public boolean isOneshot();
}
