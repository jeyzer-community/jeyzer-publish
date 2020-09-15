package org.jeyzer.mx;

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


import java.util.List;
import java.util.Map;

import org.jeyzer.mx.event.JzrEventInfo;
import org.jeyzer.mx.event.JzrEventLevel;
import org.jeyzer.mx.event.JzrPublisherEvent;

/**
 * <p>The Jeyzer MX bean interface.<br></p>
 * Its object name is : <code>org.jeyzer.mx:type=Jeyzer</code><br>
 * This interface provides applicative level accessors to the Jeyzer Recorder. 
 */
public interface JeyzerMXBean {

	/**
	 * The Jeyzer MX object name
	 */
	public static final String JEYZER_MXBEAN_NAME = "org.jeyzer.mx:type=Jeyzer";
	
	/**
	 * Determines if the Jeyzer MX bean is active
	 * @return true if active
	 */
	public boolean isActive();
	
	/**
	 * Get the Jeyzer applicative profile name
	 * @return the Jeyzer applicative profile name
	 */
	public String getProfileName();

	/**
	 * Get the application node name. 
	 * By default the underlying machine host name
	 * @return the application node name
	 */
	public String getNodeName();

	/**
	 * Get the process name 
	 * @return the process name
	 */
	public String getProcessName();
	
	/**
	 * Get the process version 
	 * @return the process version
	 */
	public String getProcessVersion();

	/**
	 * Get the process build number 
	 * @return the process build number
	 */
	public String getProcessBuildNumber();
	
	/**
	 * Get the list of thread info contexts
	 * @return the list of thread info contexts
	 */
	public List<JzrThreadInfo> getThreadInfoList();

	/**
	 * Get the list of static process context parameters
	 * @return the list of static process context parameters
	 */
	public Map<String, String> getStaticProcessContextParams();

	/**
	 * Get the list of dynamic process context parameters
	 * @return the list of dynamic process context parameters
	 */
	public Map<String, String> getDynamicProcessContextParams();

	/**
	 * Get the list of monitoring events to consume
	 * @return the list of monitoring events
	 */
	public List<JzrEventInfo> getEvents();

	/**
	 * Get the list of available monitoring events and consume the ones which have been terminated
	 * Others ones may be returned on next call if not cancelled in the meantime
	 * @return the list of monitoring events, including the consumed ones
	 */
	public List<JzrEventInfo> consumeEvents();

	/**
	 * Get the Jeyzer publish library version
	 * @return the Jeyzer publish library version
	 */
	public String getPublisherVersion();
	
	/**
	 * Get the list of publisher events
	 * All publisher events will be consumed through this call
	 * @return the list of publisher events
	 */
	public List<JzrPublisherEvent> getPublisherEvents();

	/**
	 * Consume the list of available publisher events
	 * @return the list of consumed publisher events
	 */
	public List<JzrPublisherEvent> consumePublisherEvents();	
	
	/**
	 * Suspend the data collection
	 */
	public void suspendDataCollection();
	
	/**
	 * Resume the data collection
	 */
	public void resumeDataCollection();

	/**
	 * Determines if the data collection is active
	 * @return true if active
	 */
	public boolean isDataCollectionActive();
	
	/**
	 * Suspend the data collection
	 * @param level    the Jeyzer event level
	 */
	public void suspendEventCollection(JzrEventLevel level);
	
	/**
	 * Resume the event collection
	 * @param level    the Jeyzer event level
	 */
	public void resumeEventCollection(JzrEventLevel level);
	
	/**
	 * Determines if the event collection is active
	 * @param level    the Jeyzer event level
	 * @return true if active
	 */
	public boolean isEventCollectionActive(JzrEventLevel level);
}
