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
 * <p>The Jeyzer event code interface gives access to the applicative event information.<br>
 *    The event code is identified by a unique abbreviation (ex: MAK-101), which is usually a support documentation pointer.<br>
 *    The name and description (optional, considering event could be documented externally) describe the event.<br>
 *    The ticket is optional and should refer to a ticket in an issue tracking product such as JIRA.<br>
 *    The type is optional and could be used to group codes (ex: MAK).<br>
 *    The event code is associated to a level and sub level indicating its importance.<br></p>
 * <p>It is recommended to implement enumerations of codes that will inherit this interface.
 * </p>
 */
public interface JzrEventCode {

	/**
	 * Get the event code abbreviation. Mandatory. 
	 * Usually the <code>enum.name()</code>
	 * @return the event code abbreviation
	 */
	public String getAbbreviation();	
	
	/**
	 * Get the event code name. Mandatory
	 * @return the event code name
	 */
	public String getName();

	/**
	 * Get the event code description. Optional
	 * @return the event code description
	 */
	public String getDescription();

	/**
	 * Get the event code ticket. Optional
	 * @return the event code ticket
	 */
	public String getTicket();
	
	/**
	 * Get the event type. Optional
	 * @return the event type
	 */
	public String getType();
	
	/**
	 * Get the applicative event level.
	 * @return the applicative event level
	 */
	public JzrEventLevel getLevel();

	/**
	 * Get the applicative event sub level. 
	 * @return the applicative event sub level
	 */
	public JzrEventSubLevel getSubLevel();
}
