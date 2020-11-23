package org.jeyzer.publish;

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
 * <p>
 * Jeyzer Publisher initialization properties
 * </p>
 * <p>
*  Jeyzer Publisher can be configured - optionally - only once through the {@link org.jeyzer.publish.JeyzerPublisher#init(Properties) JeyzerPublisher.init(props)} method. Supported properties are : <br></p>
* <ul> 
* <li> <code>data.disable.collection</code> : if set to true, data collection is not issued. False by default. Can be re-enabled by calling the <code>resumeDataCollection</code> method (locally or through JMX).</li>
* <li> <code>events.info.limit</code> : the maximum number of applicative info events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. Must be higher than 1000 which is the default.</li>
* <li> <code>events.warning.limit</code> : the maximum number of applicative warning events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. Must be higher than 1000 which is the default.</li>
* <li> <code>events.critical.limit</code> : the maximum number of applicative critical events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. Must be higher than 1000 which is the default.</li>
* <li> <code>events.info.disable.collection</code> : if set to true, applicative info events are not issued. False by default. Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).</li>
* <li> <code>events.warning.disable.collection</code> : if set to true, applicative warning events are not issued. False by default. Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).</li>
* <li> <code>events.critical.disable.collection</code> : if set to true, applicative critical events are not issued. False by default. Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).</li>
* <li> <code>publisher.disable.action.context.reaper</code> : if set to true, reaping is performed during context access. Useful for application servers which get re-deployed : it prevents reaper thread leaking. By default the reaper is started.</li>
* <li> <code>publisher.enable.jzr_recorder_collection.event</code> : if set to true, the publisher will issue publisher info events marking each Jeyzer Recorder collection. Disabled by default.</li>
* </ul>
* 
 * <p>
 * Important : The standard Java {@link java.util.Properties} collection support only String. Therefore convert any numerical parameter to String first, otherwise it will get ignored silently. 
 * </p>
 * 
 * @see org.jeyzer.publish.JeyzerPublisher
*/

public class JeyzerPublisherInit {

	private JeyzerPublisherInit() {}
	
	/**
	 * if set to true, data collection is not issued. False by default. 
	 * Can be re-enabled by calling the <code>resumeDataCollection</code> method (locally or through JMX).
	 */
	public static final String DATA_DISABLE_COLLECTION_PROPERTY = "data.disable.collection";
	
	/**
	 * The maximum number of applicative info events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. 
	 * Must be higher than 1000 which is the default.
	 */
	public static final String EVENTS_INFO_LIMIT_PROPERTY = "events.info.limit";
	
	/**
	 * The maximum number of applicative warning events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. 
	 * Must be higher than 1000 which is the default.
	 */
	public static final String EVENTS_WARNING_LIMIT_PROPERTY = "events.warning.limit";
	
	/**
	 * The maximum number of applicative critical events that the Jeyzer Publisher can keep between 2 Jeyzer Recorder collections. 
	 * Must be higher than 1000 which is the default.
	 */
	public static final String EVENTS_CRITICAL_LIMIT_PROPERTY = "events.critical.limit";
	
	/**
	 * If set to true, applicative info events are not issued. False by default. 
	 * Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).
	 */
	public static final String EVENTS_INFO_DISABLE_COLLECTION_PROPERTY = "events.info.disable.collection";
	
	/**
	 * If set to true, applicative warning events are not issued. False by default. 
	 * Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).
	 */
	public static final String EVENTS_WARNING_DISABLE_COLLECTION_PROPERTY = "events.warning.disable.collection";
	
	/**
	 * If set to true, applicative critical events are not issued. False by default. 
	 * Can be re-enabled by calling the <code>resumeEventCollection</code> method (locally or through JMX).
	 */
	public static final String EVENTS_CRITICAL_DISABLE_COLLECTION_PROPERTY = "events.critical.disable.collection";
	
	/**
	 * if set to true, reaping is performed during context access. 
	 * Useful for application servers which get re-deployed : it prevents reaper thread leaking. 
	 * By default the reaper is started.
	 */
	public static final String PUBLISHER_DISABLE_REAPER_PROPERTY = "publisher.disable.action.context.reaper";
	
	/**
	 * If set to true, the publisher will issue publisher info events marking each Jeyzer Recorder collection. 
	 * Disabled by default.
	 */
	public static final String PUBLISHER_ENABLE_JZR_RECORDER_COLLECTION_EVENT_PROPERTY = "publisher.enable.jzr_recorder_collection.event";
}
