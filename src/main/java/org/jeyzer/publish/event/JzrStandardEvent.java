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

import java.util.concurrent.atomic.AtomicInteger;

import org.jeyzer.mx.event.JzrEventCode;



/**
 * <p>The Jeyzer standard event class is a generic bean representing a Jeyzer applicative monitoring event. 
 * Class can be used immediately in a a generic manner or extended.<br>
 * Events are created by the application and published through the Jeyzer Monitor handlers.<br>
 * The code and id must always be set otherwise the event will not get processed when fired or started.<br>
 * The event id is automatically set by concatenating the given code name and current system time.
 * All other parameters are optional.<br></p>
 * Non thread safe<br>
 */
public class JzrStandardEvent implements JzrEvent {
	
	// Required to distinguish events of same code generated at same exact time (already observed). 
	// Possible values : from 0 to 9999
	private static AtomicInteger idSuffix = new AtomicInteger(0);

	protected JzrEventCode code;
	protected String id;
	protected String message;
	protected short trustFactor;

	/**
	 * The Jeyzer event constructor
	 * @param code               the applicative code. Mandatory
	 */
	public JzrStandardEvent(JzrEventCode code) {
		this.code = code;
		this.id = code.getAbbreviation() + System.currentTimeMillis() + getSuffixId();
		this.message = code.getName();
		this.trustFactor = 100;
	}

	/**
	 * The Jeyzer event constructor
	 * @param code               the applicative code. Mandatory
	 * @param message			 the applicative event message (description, recommendation). Can be null.
	 */
	public JzrStandardEvent(JzrEventCode code, String message) {
		this.code = code;
		this.id = code.getAbbreviation() + System.currentTimeMillis() + getSuffixId();
		this.message  = message != null && !message.isEmpty() ? message : code.getName();
		this.message = message;
		this.trustFactor = 100;
	}
		
	/**
	 * The Jeyzer event constructor
	 * @param code               the applicative code. Can be null
	 * @param message			 the applicative event message (description, recommendation). Can be null.
	 * @param trustFactor	     the applicative trust factor. Between 0 and 100.
	 */
	public JzrStandardEvent(JzrEventCode code, String message, short trustFactor) {
		this.code = code;
		this.id = code.getAbbreviation() + System.currentTimeMillis() + getSuffixId();
		this.message  = message != null && !message.isEmpty() ? message : code.getName();
		this.trustFactor = trustFactor;
	}
	
	/**
	 * The Jeyzer cloning constructor
	 * @param event  	         the applicative event to clone.
	 */
	public JzrStandardEvent(JzrStandardEvent event) {
		this.code = event.getCode();
		this.id = event.getId();
		this.message = event.getMessage();
		this.trustFactor = event.getTrustFactor();
	}

	/**
	 * Get the applicative event code. Can be null.
	 * @return the applicative event code
	 */
	@Override
	public JzrEventCode getCode() {
		return code;
	}

	/**
	 * Get the applicative event unique id.
	 * @return the applicative event id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the applicative event message. Can be null.
	 * @return the applicative event message
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * Get the applicative event trust factor. 
	 * @return the applicative event trust factor
	 */
	@Override
	public short getTrustFactor() {
		return trustFactor;
	}

	/**
	 * Set the applicative event trust factor. 
	 * @param trustFactor the applicative event trust factor
	 */
	public void setTrustFactor(short trustFactor) {
		this.trustFactor = trustFactor;
	}

	/**
	 * Clone the event. 
	 * @return  the cloned object
	 */
	@Override
	public Object clone() {
		return new JzrStandardEvent(this);
	}
	
	private static String getSuffixId() {
		idSuffix.compareAndSet(10000, 0);
		return "-" + idSuffix.incrementAndGet();
	}
}
