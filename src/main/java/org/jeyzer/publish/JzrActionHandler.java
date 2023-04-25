package org.jeyzer.publish;

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
 * <p>
 * Each JzrAction handler object :<br>
 * </p>
 * <ul>
 * <li> is a reference to a current action declared with Jeyzer.</li>
 * <li> handles only one action at a time.</li>
 * <li> must be unique per thread.</li>
 * </ul>
 *  
 *  <p>
 *  Handlers are obtained from the Jeyzer Publisher instance and must be called on startAction
 *  to register the action with Jeyzer.<br> 
 *  Once the action is completed, the closeAction method
 *  must be called to release the action context.<br>
 *  Thread safe
 *  </p>
 */
public interface JzrActionHandler {
	
	/**
	 * Declare a starting action with Jeyzer 
	 * @param context   the associated action context.
	 */
	public abstract void startAction(final JzrActionContext context);
	
	/**
	 * Close the action
	 */
	public abstract void closeAction();

	/**
	 * Set the action context parameter
	 * @param param   the context parameter
	 * @param value   the context value 
	 */
	public abstract void setContextParameter(String param, String value);
	
}
