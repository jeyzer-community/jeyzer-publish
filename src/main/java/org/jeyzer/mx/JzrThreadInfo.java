package org.jeyzer.mx;

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


import java.util.Map;

/**
 * <p>JzrThreadInfo is a bean class holding the thread context info<br></p> 
 * When active, the Jeyzer Recorder will access this info to store it.<br>  
 * The action id, start time, thread id are always set.
 * All other parameters are optional.
 */
public interface JzrThreadInfo {
	
	/**
	 * Get the Jeyzer internal action id
	 * @return the Jeyzer internal action id
	 */
	public String getActionId();

	/**
	 * Get the action start time
	 * @return the action start time
	 */
	public long getStartTime();

	/**
	 * Get the unique thread id
	 * @return the thread id
	 */
	public long getThreadId();

	/**
	 * Get the applicative action id. Optional
	 * @return the applicative action id
	 */
	public String getId();

	/**
	 * Get the user id associated to the current action. Optional 
	 * @return the user id
	 */
	public String getUser();

	/**
	 * Get the function principal associated to the current action. Optional
	 * @return the function principal
	 */
	public String getFunctionPrincipal();

	/**
	 * Get the context parameters associated to the current action. Optional
	 * @return the context parameters 
	 */
	public Map<String, String> getContextParams();

}
