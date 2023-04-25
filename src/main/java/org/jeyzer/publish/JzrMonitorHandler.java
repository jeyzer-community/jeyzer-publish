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

import org.jeyzer.publish.event.JzrEvent;


/**
 * <p>
 * Each JzrMonitor handler object :<br>
 * </p>
 * <ul>
 * <li> permits to fire, start, terminate and cancel Jeyzer monitoring events.</li>
 * <li> is specific to a source (application) and service (optional).</li>
 * </ul>
 *  
 *  <p>
 *  Handlers are obtained from the Jeyzer Publisher instance.<br>
 *  Started events must be either terminated or cancelled (if not already published).<br>
 *  Fired events are one shot and can be cancelled (if not already published).<br>
 *  Events get published upon Jeyzer Recorder data collection.<br>
 *  Started events can be published several times, until terminated. All other events get published only once.<br>
 *  Thread safe
 *  </p>
 */
public interface JzrMonitorHandler {

	/**
	 * Fire the monitoring event at system level. 
	 * Event lifetime covers for the whole recording
	 * Event is time stamped through current method call.
	 * @param event the event to fire
	 * @return true if the event got fired
	 */
	public boolean fireSystemEvent(JzrEvent event);
	
	/**
	 * Fire the monitoring event at global level. 
	 * Event is oneshot and time stamped through current method call.
	 * @param event the event to fire
	 * @return true if the event got fired
	 */
	public boolean fireGlobalEvent(JzrEvent event);

	/**
	 * Fire the monitoring event at thread level. 
	 * Event is oneshot and time stamped through current method call. 
	 * @param event the event to fire
	 * @return true if the event got fired
	 */
	public boolean fireLocalThreadEvent(JzrEvent event);

	/**
	 * Start the monitoring event's life at global level. 
	 * Event start time is time stamped through current method call
	 * Event lifetime must be terminated through the terminateEvent call.
	 * @param event the event to fire
	 * @return true if the event got fired
	 */
	public boolean startGlobalEvent(JzrEvent event);

	/**
	 * Start the monitoring event's life at thread level. 
	 * Event start time is time stamped through current method call
	 * Event lifetime must be terminated through the terminateEvent call.
	 * @param event the event to fire
	 * @return true if the event got fired
	 */
	public boolean startLocalThreadEvent(JzrEvent event);

	/**
	 * Terminates the monitoring event's life.
	 * @param event the original event to terminate
	 * @return true if the event got terminated
	 */
	public boolean terminateEvent(JzrEvent event);

	/**
	 * Cancels the monitoring event registered through the event fire and start methods.
	 * @param event the original event to cancel
	 * @return true if the event got cancelled
	 */
	public boolean cancelEvent(JzrEvent event);
	
	/**
	 * Checks if the given event is waiting for Jeyzer recorder publication. 
	 * @param event the original event to check
	 * @return true if the given event is waiting for Jeyzer recorder publication
	 */
	public boolean isWaitingForPublication(JzrEvent event);
	
	/**
	 * Checks if the given event is terminated.
	 * Event is terminated if one shot, terminated or already published (not found)
	 * @param event the original event to check
	 * @return true if the given event is terminated
	 */
	public boolean isTerminated(JzrEvent event);
	
}
