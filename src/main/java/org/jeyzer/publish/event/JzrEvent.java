package org.jeyzer.publish.event;

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


import org.jeyzer.mx.event.JzrEventCode;

/**
 * <p>The Jeyzer event interface describing a Jeyzer applicative monitoring event instance.<br>
 * Events are created by the application and published through the Jeyzer Monitor handlers.<br>
 * The id and code must always be set otherwise the event will not be processed when fired or started.<br>
 * All other parameters are optional.<br></p>
 */
public interface JzrEvent extends Cloneable {

	/**
	 * Get the event id. Must be unique
	 * @return the event id
	 */
	public String getId();

	/**
	 * Get the applicative event code. Mandatory.
	 * @return the applicative event code
	 */
	public JzrEventCode getCode();
		
	/**
	 * Get the event message. Description of the issue and recommendation. Optional.
	 * If not specified, the applicative event code description could be considered as default message.
	 * @return the event message
	 */
	public String getMessage();

	/**
	 * Get the event trustFactor, between 0 and 100. 
	 * Optional. Default is usually 100 (full trust)
	 * @return the trust factor
	 */
	public short getTrustFactor();
	
	/**
	 * Clone the event. Deep copy must be ensured.
	 * @return the cloned event
	 */
	public Object clone();
}
