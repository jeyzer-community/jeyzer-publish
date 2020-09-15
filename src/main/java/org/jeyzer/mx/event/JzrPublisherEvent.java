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
 * <p>JzrPublisherEvent is a bean class holding publisher event information<br></p> 
 * When active, the Jeyzer Recorder will access this info to store it.<br>  
 * Its fields are always set.<br>
 */
public interface JzrPublisherEvent {

	/**
	 * Get the publisher event time
	 * @return the publisher event time
	 */
	public long getTime();

	/**
	 * Get the publisher event level. 
	 * @return the publisher event level
	 */
	public JzrEventLevel getLevel();

	/**
	 * Get the publisher event sub-level.By default medium. 
	 * @return the publisher event sub level
	 */
	public JzrEventSubLevel getSubLevel();

	/**
	 * Get the publisher event code.
	 * @return the publisher event code
	 */
	public JzrPublisherEventCode getCode();

	/**
	 * Get the publisher event message. Description of the issue and recommendation.
	 * @return the publisher event message
	 */
	public String getMessage();
}
