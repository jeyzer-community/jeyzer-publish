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

import java.io.IOException;




import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.jeyzer.mx.JeyzerMXBean;
import org.jeyzer.mx.JzrThreadInfo;
import org.jeyzer.mx.event.JzrEventCode;
import org.jeyzer.mx.event.JzrEventInfo;
import org.jeyzer.mx.event.JzrEventLevel;
import org.jeyzer.mx.event.JzrEventScope;
import org.jeyzer.mx.event.JzrEventSubLevel;
import org.jeyzer.mx.event.JzrPublisherEvent;
import org.jeyzer.mx.event.JzrPublisherEventCode;
import org.jeyzer.publish.event.JzrEvent;

/**
 * <p>
 * Jeyzer Publisher is the entry point to expose process context parameters and obtain {@link org.jeyzer.publish.JzrActionHandler action handlers}.<br>
 * Process context parameters are String value pairs which represent dynamic values (ex : number of connected users) 
 *   or constant values (ex : application version)<br>
 * {@link org.jeyzer.publish.JzrActionHandler Action handlers} permit to expose thread context parameters (ex : number of database inserts) and declare Jzr actions which permit to create - at Jeyzer analysis time - thread units across time.
 * </p>
 * <p>
 * Jeyzer Publisher is also the place to generate JZR monitoring events through {@link org.jeyzer.publish.JzrMonitorHandler monitor handlers}.
 * Those applicative {@link org.jeyzer.publish.event.JzrEvent events} can either be one shot (see fire methods) or time ranged (see start and terminate methods).
 * Time ranged events do represent high level applicative events such as service interruptions.
 * One shot events do represent high level applicative events such as state transitions.
 * </p>
 * <p>
 * Jeyzer Publisher is singleton accessible through the {@link #instance() JeyzerPublisher.instance()} call.<br>
 * Jeyzer Publisher singleton is instantiated at class loading time and :
 * </p>
 * <ul>
 * <li> registers itself as a Jeyzer MX bean.</li>
 * <li> starts a stale action context reaper (daemon thread) which can be disabled with the <code>publisher.disable.action.context.reaper</code> init property.</li>
 * </ul>
 * <p>
 * By default, the Jeyzer Publisher is active.<br>
 * If your application is considered as stable enough or/and if you're looking for high end performance, Jeyzer Publisher can be disabled by setting the <code>jeyzer.publisher.active</code> system property to false in which case an empty implementation is loaded, limiting the memory footprint.<br>
 * </p>
 * <p>
 * Jeyzer Publisher can be configured - optionally - only once through the {@link #init(Properties) init} method. Supported properties are described in {@link org.jeyzer.publish.JeyzerPublisherInit JeyzerPublisherInit}<br></p>
 * <p>
 * The Jeyzer Publisher can also generate JZR publisher life cycle {@link org.jeyzer.mx.event.JzrPublisherEvent events} such as data collection activation/deactivation.<br>
 * Those events help to understand at JZR report analysis time if the applicative events or data were published.<br>
 * </p>
 * <p>
 * Thread safe.<br>
 * The implementation doesn't throw exceptions : invalid parameters will be simply ignored and without effect.<br>
 * It is up to the application to provide the right inputs.
 * </p>
 * 
 * @see org.jeyzer.publish.JeyzerPublisherInit
 */
public abstract class JeyzerPublisher implements JeyzerMXBean{
	
	private static final String JEYZER_PUBLISHER_ACTIVE_SYSTEM_PROPERTY = "jeyzer.publisher.active";

	// singleton
	private static final JeyzerPublisher publisher;

	static {
		if (Boolean.parseBoolean(System.getProperty(JEYZER_PUBLISHER_ACTIVE_SYSTEM_PROPERTY))){
			publisher = new JeyzerPublisherImpl();
		}
		else{
			publisher = new JeyzerPublisherNoImpl();
		}
	}
	
	private String publisherVersion;
	
	/**
	 * Gets the Jeyzer Publisher unique instance
	 * @return the Jeyzer publisher instance
	 */
	public static JeyzerPublisher instance(){
		return publisher;
	}
	
	/**
	 * Initialize the Jeyzer publisher. Optional.
	 * Supported properties are listed in {@link org.jeyzer.publish.JeyzerPublisherInit JeyzerPublisherInit}
	 * @param props  the initialization properties
	 * @return true if the initialization occurred
	 */
	public abstract boolean init(final Properties props);

	/**
	 * Set the Jeyzer applicative profile name. 
	 * @param profile  the profile name
	 */
	public abstract void setProfileName(final String profile);
	
	/** 
	 * Set the process or node name.  
	 * By default set with the underlying machine host name.
	 * @param nodeName    the node name
	 */
	public abstract void setNodeName(final String nodeName);
	
	/**
	 * Set the process name.  
	 * @param name    the process name
	 */
	public abstract void setProcessName(final String name);
	
	/**
	 * Set the process version.  
	 * @param version    the process version
	 */
	public abstract void setProcessVersion(final String version);
	
	/**
	 * Set the process build number if any.  
	 * @param buildNumber    the build number
	 */
	public abstract void setProcessBuildNumber(final String buildNumber);
	
	/**
	 * Reports a static process context parameter to Jeyzer.
	 * Static process context parameters are published as process card properties : those must be set at process start.
	 * @param key  the static process context parameter key
	 * @param value  the static process context parameter value
	 */
	public abstract void addStaticProcessContextParam(final String key, final String value);
	
	/**
	 * Reports a dynamic process context parameter to Jeyzer
	 * @param key  the dynamic process context parameter key
	 * @param value  the dynamic process context parameter value
	 */
	public abstract void setDynamicProcessContextParam(final String key, final String value);

	/**
	 * Get a Jeyzer monitor handler to generate events
	 * @param source the applicative source. Nullable. By default the process name if available.
	 * @param service the dynamic process context parameter value. Can be null
	 * @return the Jeyzer monitor handler
	 */
	public abstract JzrMonitorHandler getMonitorHandler(String source, String service);	
	
	/**
	 * Get a Jeyzer action handler
	 * @return the Jeyzer action handler
	 */
	public abstract JzrActionHandler getActionHandler();
	
	
	void register(){
		ObjectName mxbeanName = null;
		try {
			mxbeanName = new ObjectName(JEYZER_MXBEAN_NAME);
		} catch (MalformedObjectNameException e) {
			System.err.print("Warning : Failed to start Jeyzer Publisher. Error is : " + e.getMessage());
		}
	    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
	    
	    try {
			mbs.registerMBean(this, mxbeanName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			System.err.print("Warning : Failed to start Jeyzer Publisher. Error is : " + e.getMessage());
		}
	}
	
	@Override
	public String getPublisherVersion() {
		if (publisherVersion== null)
			publisherVersion = loadPublisherVersion();
		return publisherVersion;
	}
	
	private String loadPublisherVersion() {
		try {
			Class<JeyzerPublisher> clazz = JeyzerPublisher.class;
			String className = clazz.getSimpleName() + ".class";
			String classPath = clazz.getResource(className).toString();
			if (!classPath.startsWith("jar"))
				// Class not loaded from JAR
				return "Not available : classes mode";
			
			String manifestPath = classPath.substring(0,
					classPath.lastIndexOf('!') + 1)
					+ "/META-INF/MANIFEST.MF";
			Manifest manifest = new Manifest(new URL(manifestPath).openStream());
			Attributes attr = manifest.getMainAttributes();
			String value = attr.getValue("Specification-Version");
			if (value == null)
				// Class loaded from JAR within war file
				return "Not available : war mode";
			
			return value;
		} catch (IOException e) {
			return "Not available - Manifest read error";
		}
	}

	private static final class JeyzerPublisherEventImpl implements JzrPublisherEvent{
		
		private long time;
		private JzrPublisherEventCode code;
		private String message;
		
		public JeyzerPublisherEventImpl(JzrPublisherEventCode code, String message) {
			this.code = code;
			this.message = message;
			this.time = System.currentTimeMillis();
		}

		public JeyzerPublisherEventImpl(JzrPublisherEventCode code) {
			this(code, code.getDescription());
		}
		
		@Override
		public long getTime() {
			return time;
		}

		@Override
		public JzrEventLevel getLevel() {
			return code.getLevel();
		}

		@Override
		public JzrEventSubLevel getSubLevel() {
			return code.getSubLevel();
		}

		@Override
		public JzrPublisherEventCode getCode() {
			return code;
		}

		@Override
		public String getMessage() {
			return message;
		}
	}
	
	private static final class JeyzerPublisherImpl extends JeyzerPublisher{
		
		// Initialization is optional
		private volatile boolean initialized = false;
		private volatile boolean dataCollectionActive = true;
		private boolean generateJzrRecorderCollectionEvent = false;
		private boolean disableReaper = false;
		
		private String profile;        // optional
		private String nodeName;  	   // optional, by default host name
		private String processName;    // optional
		private String processVersion; // optional
		private String buildNumber;    // optional
		
		// Guarded by threadInfoListLock
		private Map<String, JzrThreadInfo> threadInfoMap = new HashMap<>();
		private Object threadInfoMapLock = new Object();
		
		private Map<String, String> staticProcessCtxParams = new ConcurrentHashMap<>();
		private Map<String, String> dynamicProcessCtxParams = new ConcurrentHashMap<>();

		private JzrEventManager eventMgr = new JzrEventManager();
		private ScheduledExecutorService executor;
		
		protected JeyzerPublisherImpl(){
			// register as MX bean
			register();
			
			// start the thread info reaper, can be disabled through init properties
			startRepear();
			
			// get node name
			try {
				this.nodeName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
			}
			
			JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
					JzrPublisherEventCode.JZR_PUB_002);
			this.eventMgr.addPublisherEvent(publisherEvent);
		}
		

		@Override
		public boolean init(Properties props) {
			if (initialized)
				return false;
			initialized = true;
			
			disableReaper = Boolean.parseBoolean(props.getProperty(JeyzerPublisherInit.PUBLISHER_DISABLE_REAPER_PROPERTY, "false"));
			if (disableReaper)
				this.stopReaper();
			
			boolean disableDataCollection = Boolean.parseBoolean(props.getProperty(JeyzerPublisherInit.DATA_DISABLE_COLLECTION_PROPERTY, "false"));
			if (disableDataCollection) {
				this.dataCollectionActive = false;	
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
						JzrPublisherEventCode.JZR_PUB_502);
				this.eventMgr.addPublisherEvent(publisherEvent);
			}

			generateJzrRecorderCollectionEvent = Boolean.parseBoolean(props.getProperty(JeyzerPublisherInit.PUBLISHER_ENABLE_JZR_RECORDER_COLLECTION_EVENT_PROPERTY, "false"));
			
			eventMgr.init(props);
			
			return true;
		}
		
		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public boolean isDataCollectionActive() {
			return this.dataCollectionActive;
		}
		
		@Override
		public boolean isEventCollectionActive(JzrEventLevel level) {
			return this.eventMgr.isEventCollectionActive(level);
		}

		@Override
		public void suspendDataCollection() {
			if (!this.dataCollectionActive)
				return; // already disabled
			
			this.dataCollectionActive = false;
			
			JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
					JzrPublisherEventCode.JZR_PUB_503);
			this.eventMgr.addPublisherEvent(publisherEvent);
		}
		
		@Override
		public void resumeDataCollection() {
			if (this.dataCollectionActive)
				return; // already active
			
			this.dataCollectionActive = true;
			
			JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
					JzrPublisherEventCode.JZR_PUB_504);
			this.eventMgr.addPublisherEvent(publisherEvent);		
		}

		@Override
		public void suspendEventCollection(JzrEventLevel level) {
			this.eventMgr.suspendEventCollection(level);
		}


		@Override
		public void resumeEventCollection(JzrEventLevel level) {
			this.eventMgr.resumeEventCollection(level);
		}		
		
		@Override
		public JzrActionHandler getActionHandler(){
			return new JzrActionHandlerImpl();
		}
		
		@Override
		public String getProfileName() {
			return profile;
		}

		@Override
		public String getNodeName() {
			return nodeName;
		}
		
		@Override
		public String getProcessName() {
			return processName;
		}

		@Override
		public String getProcessVersion() {
			return processVersion;
		}
		
		@Override
		public String getProcessBuildNumber() {
			return buildNumber;
		}
		
		@Override
		public List<JzrThreadInfo> getThreadInfoList() {
			List<JzrThreadInfo> threadInfoList = new ArrayList<>();

			if (disableReaper)
				// need to do the reaping work now. Higher cost
				reapFinishedThreads();
			
			synchronized(threadInfoMapLock){
				// Note that stale info could be given in case action context has not been removed on action ending. 
				// This is not an issue as the Jeyzer Recorder is relying 
				// on the thread ids returned by the MX Thread management interface in the first place.
				// Almost doesn't apply if reaping is disabled.
				threadInfoList.addAll(threadInfoMap.values());
			}
			
			return threadInfoList;
		}

		@Override
		public Map<String, String> getStaticProcessContextParams() {
			return new HashMap<String, String>(this.staticProcessCtxParams);
		}
		
		@Override
		public Map<String, String> getDynamicProcessContextParams() {
			return new HashMap<String, String>(this.dynamicProcessCtxParams);
		}

		@Override
		public void setProfileName(final String profile) {
			this.profile = profile;
		}
		
		@Override
		public void setNodeName(final String nodeName) {
			if (nodeName == null)
				return;
			this.nodeName = nodeName;
		}
		
		@Override
		public void setProcessName(String name) {
			this.processName = name;
		}
		
		@Override
		public void setProcessVersion(String version) {
			this.processVersion = version;
		}

		@Override
		public void setProcessBuildNumber(String buildNumber) {
			this.buildNumber = buildNumber;
		}		
		
		@Override
		public void addStaticProcessContextParam(final String key, final String value){
			if (key == null || value == null)
				return;
			
			if (!isDataCollectionActive())
				return;
			
			staticProcessCtxParams.put(key, value);
		}

		@Override
		public void setDynamicProcessContextParam(final String key, final String value){
			if (key == null || value == null)
				return;
			
			if (!isDataCollectionActive())
				return;
			
			dynamicProcessCtxParams.put(key, value);
		}
		
		@Override
		public List<JzrEventInfo> getEvents() {
			return this.eventMgr.getEvents();
		}
		
		@Override
		public List<JzrEventInfo> consumeEvents() {
			if (generateJzrRecorderCollectionEvent) {
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
						JzrPublisherEventCode.JZR_PUB_003);
				this.eventMgr.addPublisherEvent(publisherEvent);
			}
			
			if (eventMgr.isEmpty())
				return new ArrayList<>(); // no need to do below processing

			this.eventMgr.terminateLocalOrphanEvents();

			return this.eventMgr.fetchEventsToPublish();
		}


		@Override
		public JzrMonitorHandler getMonitorHandler(String source, String service) {
			return new JzrMonitorHandlerImpl(
					source, 
					service, 
					eventMgr,
					processName);
		}
				
		void addActionContext(JzrThreadInfo threadInfo) {
			if (threadInfo == null)
				return;
			
			synchronized(threadInfoMapLock){
				threadInfoMap.put(
						Long.toString(threadInfo.getThreadId()), 
						threadInfo
						);
			}
		}

		void removeActionContext(JzrThreadInfo threadInfo) {
			if (threadInfo == null)
				return;
			
			synchronized(threadInfoMapLock){
				threadInfoMap.remove(Long.toString(threadInfo.getThreadId()));
			}
		}	
		
		void reapActionContext(JzrThreadInfo threadInfoToRemove) {
			if (threadInfoToRemove == null)
				return;	
			
			synchronized(threadInfoMapLock){
				// let's make sure that we do not remove more recent one that came in the middle of the reaping
				JzrThreadInfo threadInfoStored = threadInfoMap.get(threadInfoToRemove.getId());
				if (threadInfoStored.getStartTime() != threadInfoToRemove.getStartTime())
					return;
				
				threadInfoMap.remove(threadInfoToRemove.getId());
			}
		}

		private void startRepear() {
			DeadThreadInfoReaperTask reaper = new DeadThreadInfoReaperTask();
			executor = Executors.newSingleThreadScheduledExecutor(
					new DeadThreadInfoReaperThreadFactory());
			executor.scheduleWithFixedDelay(reaper, 60, 60, TimeUnit.SECONDS);
		}
		
		private void stopReaper() {
			if (executor != null)
				executor.shutdown();
		}
				
		void reapFinishedThreads() {
			List<JzrThreadInfo> candidateList = getThreadInfoList();
			if (candidateList.isEmpty())
				return;  // no applicative activity
			
			List<JzrThreadInfo> deadList = new ArrayList<>();

			// get current live thread ids
			ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
			final long[] threadIds = tmbean.getAllThreadIds();
			
			// find the dead thread id
			for (JzrThreadInfo candidate : candidateList){
				if (isDeadThread(threadIds, candidate.getThreadId()))
					deadList.add(candidate);
			}

			// try to remove them
			for (JzrThreadInfo dead : deadList){
				reapActionContext(dead);
			}
		}

		private boolean isDeadThread(final long[] threadIds, final long id) {
			for (int i=0; i<threadIds.length; i++){
				if (id == threadIds[i]){
					return false;
				}
			}
			return true;
		}
		
		/*
		 * Responsible to reap old entries in case application doesn't close the actions (cf. JzrActionHandler)
		 * This is to prevent memory leaks.
		 * Reaper is running every 60s
		 */
		private static final class DeadThreadInfoReaperTask implements Runnable{
			@Override
			public void run() {
				try{
					((JeyzerPublisherImpl)publisher).reapFinishedThreads();
				}catch(Exception ex){
					// ignore
				}
			}
		}
		
		private static final class DeadThreadInfoReaperThreadFactory implements ThreadFactory {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("Jeyzer-thread-info-reaper");
				t.setDaemon(true);
				return t;
			}
		}
		
		/*
		 * Manages the event storage
		 */
		private static final class JzrEventManager{

			private static final int DEFAULT_EVENTS_LIMIT_PER_LEVEL = 1000;
			
			private JzrEventInfoImplGroup infoEventGroup = new JzrEventInfoImplGroup(JzrEventLevel.INFO);
			private JzrEventInfoImplGroup warnEventGroup = new JzrEventInfoImplGroup(JzrEventLevel.WARNING);
			private JzrEventInfoImplGroup criticalEventGroup = new JzrEventInfoImplGroup(JzrEventLevel.CRITICAL);

			private List<JzrPublisherEvent> publisherEvents = Collections.synchronizedList(new ArrayList<JzrPublisherEvent>());

			public void init(Properties props) {
				initEventLimit(criticalEventGroup, JeyzerPublisherInit.EVENTS_CRITICAL_LIMIT_PROPERTY, props);
				initEventLimit(warnEventGroup, JeyzerPublisherInit.EVENTS_WARNING_LIMIT_PROPERTY, props);
				initEventLimit(infoEventGroup, JeyzerPublisherInit.EVENTS_INFO_LIMIT_PROPERTY, props);
				
				initEventCollection(criticalEventGroup, JeyzerPublisherInit.EVENTS_CRITICAL_DISABLE_COLLECTION_PROPERTY, props, JzrPublisherEventCode.JZR_PUB_102);
				initEventCollection(warnEventGroup, JeyzerPublisherInit.EVENTS_WARNING_DISABLE_COLLECTION_PROPERTY, props, JzrPublisherEventCode.JZR_PUB_202);
				initEventCollection(infoEventGroup, JeyzerPublisherInit.EVENTS_INFO_DISABLE_COLLECTION_PROPERTY, props, JzrPublisherEventCode.JZR_PUB_302);
			}
			
			private void initEventCollection(JzrEventInfoImplGroup eventGroup, String propertyName, Properties props, JzrPublisherEventCode code) {
				boolean disableDataCollection = Boolean.parseBoolean(props.getProperty(propertyName, "false"));
				if (disableDataCollection) {
					eventGroup.suspendEventCollection();
					JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(code);
					publisherEvents.add(publisherEvent);
				}
			}

			public boolean isEventCollectionActive(JzrEventLevel level) {
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(level);
				return eventGroup.isEventCollectionActive();
			}

			public void suspendEventCollection(JzrEventLevel level) {
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(level);
				if (!eventGroup.isEventCollectionActive())
					return;  // already suspended
				
				eventGroup.suspendEventCollection();
				
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(getSuspendedCodeAtRuntime(level));
				publisherEvents.add(publisherEvent);
			}

			public void resumeEventCollection(JzrEventLevel level) {
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(level);
				if (eventGroup.isEventCollectionActive())
					return;  // already active
				
				eventGroup.resumeEventCollection();
				
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(getResumedCodeAtRuntime(level));
				publisherEvents.add(publisherEvent);
			}

			private void initEventLimit(JzrEventInfoImplGroup group, String eventsLimitProperty, Properties props) {
				String value = props.getProperty(eventsLimitProperty);
				if (value == null)
					return; /// stay with default
				try {
					int limit = Integer.parseInt(value);
					group.setEventsLimit(limit);
				}catch(NumberFormatException ex) {
					return; /// stay with default
				}
			}

			public boolean add(JzrEventInfoImpl evtInfo) {
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(evtInfo.getCode().getLevel());
				return eventGroup.addEvent(evtInfo, publisherEvents);
			}
			
			public void addPublisherEvent(JzrPublisherEvent publisherEvent) {
				this.publisherEvents.add(publisherEvent);
			}

			public List<JzrEventInfo> fetchEventsToPublish() {
				List<JzrEventInfo> eventToPublish = new ArrayList<>();

				this.criticalEventGroup.fetchEventsToPublish(eventToPublish);
				this.warnEventGroup.fetchEventsToPublish(eventToPublish);
				this.infoEventGroup.fetchEventsToPublish(eventToPublish);
				
				return eventToPublish;
			}
			
			public List<JzrEventInfo> getEvents() {
				List<JzrEventInfo> events = new ArrayList<>();

				events.addAll(this.criticalEventGroup.getEvents());
				events.addAll(this.warnEventGroup.getEvents());
				events.addAll(this.infoEventGroup.getEvents());
				
				return events;
			}

			public List<JzrPublisherEvent> fetchPublisherEvents() {
				List<JzrPublisherEvent> eventsToPublish = new ArrayList<>();
				
				eventsToPublish.addAll(this.publisherEvents);
				this.publisherEvents.removeAll(eventsToPublish); // remove only those ones in case new just got inserted
				
				return eventsToPublish;
			}
			
			public List<JzrPublisherEvent> getPublisherEvents() {
				List<JzrPublisherEvent> events = new ArrayList<>();
				
				events.addAll(this.publisherEvents);
				
				return events;
			}

			public boolean isEmpty() {
				return this.criticalEventGroup.isEmpty() 
						&& this.warnEventGroup.isEmpty()
						&& this.infoEventGroup.isEmpty();
			}

			public void terminateLocalOrphanEvents() {
				// get current live thread ids
				ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
				final long[] threadIds = tmbean.getAllThreadIds();
				
				this.criticalEventGroup.terminateLocalOrphanEvents(threadIds);
				this.warnEventGroup.terminateLocalOrphanEvents(threadIds);
				this.infoEventGroup.terminateLocalOrphanEvents(threadIds);
			}

			public boolean terminateEvent(JzrEvent event) {
				if (event == null || event.getId() == null || event.getId().isEmpty())
					return false;
				
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(event.getCode().getLevel());
				return eventGroup.terminateEvent(event);
			}

			public boolean cancelEvent(JzrEvent event) {
				if (event == null || event.getId() == null || event.getId().isEmpty())
					return false;
				
				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(event.getCode().getLevel());
				return eventGroup.cancelEvent(event);
			}

			public boolean isWaitingForPublication(JzrEvent event) {
				if (event == null || event.getId() == null || event.getId().isEmpty())
					return false;

				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(event.getCode().getLevel());
				return eventGroup.isWaitingForPublication(event);
			}
			
			public boolean isTerminated(JzrEvent event) {
				if (event == null || event.getId() == null || event.getId().isEmpty())
					return false;

				JzrEventInfoImplGroup eventGroup = getLevelBasedEventInfoImplGroup(event.getCode().getLevel());
				return eventGroup.isTerminated(event);
			}

			private JzrEventInfoImplGroup getLevelBasedEventInfoImplGroup(JzrEventLevel level) {
				switch(level) {
				case CRITICAL :
					return this.criticalEventGroup;
				case WARNING : 
					return this.warnEventGroup;
				default :
					return this.infoEventGroup;
				}
			}
			
			private JzrPublisherEventCode getSuspendedCodeAtRuntime(JzrEventLevel level) {
				switch(level) {
					case CRITICAL : return JzrPublisherEventCode.JZR_PUB_103;
					case WARNING  : return JzrPublisherEventCode.JZR_PUB_203;
					default       : return JzrPublisherEventCode.JZR_PUB_303;
				}
			}
			
			private JzrPublisherEventCode getResumedCodeAtRuntime(JzrEventLevel level) {
				switch(level) {
					case CRITICAL : return JzrPublisherEventCode.JZR_PUB_104;
					case WARNING  : return JzrPublisherEventCode.JZR_PUB_204;
					default       : return JzrPublisherEventCode.JZR_PUB_304;
				}
			}
			
			private static final class JzrEventInfoImplGroup{
				private JzrEventLevel level;
				private ConcurrentLinkedQueue<JzrEventInfoImpl> events = new ConcurrentLinkedQueue<JzrEventInfoImpl>();
				private JzrEventInfoImpl previousEvent;

				private volatile int eventsLimit = DEFAULT_EVENTS_LIMIT_PER_LEVEL;
				private volatile boolean limitReached;
				private volatile boolean eventCollectionActive = true;

				public JzrEventInfoImplGroup(JzrEventLevel level) {
					this.level = level;
				}

				public void resumeEventCollection() {
					eventCollectionActive = true;
				}

				public void suspendEventCollection() {
					eventCollectionActive = false;
				}

				public boolean isEventCollectionActive() {
					return eventCollectionActive;
				}

				public void fetchEventsToPublish(List<JzrEventInfo> eventToPublish) {
					List<JzrEventInfoImpl> terminatedEvents = new ArrayList<>();
					
					Object[] candidates = this.events.toArray();
					
					for(Object obj : candidates) {
						JzrEventInfoImpl candidate = (JzrEventInfoImpl) obj;
						eventToPublish.add(candidate);
						if (candidate.isOneshot() || candidate.isTerminated())
							terminatedEvents.add(candidate);
					}
					
					this.events.removeAll(terminatedEvents);
					if (events.size() <= eventsLimit)
						limitReached = false;
				}
				
				public List<JzrEventInfo> getEvents() {
					List<JzrEventInfo> returnedEvents = new ArrayList<>();
					returnedEvents.addAll(Arrays.asList(this.events.toArray(new JzrEventInfoImpl[0])));
					return returnedEvents;
				}

				public boolean isEmpty() {
					return this.events.isEmpty();
				}

				public boolean addEvent(JzrEventInfoImpl evtInfo, List<JzrPublisherEvent> publisherEvents) {
					if (!eventCollectionActive)
						return false;
					
					if (isLoopedEvent(evtInfo))
						return false; // similar event (with different id/thread id), no need to report. 
					                  // Prevent event flooding, for example coming from loop or different threads.
					
					// add it on the queue
					events.offer(evtInfo);
					
					// process queue limit excess
					if (events.size() > eventsLimit) {
						JzrEventInfoImpl eventLost = events.poll(); // event is lost
						if (!limitReached) {
							limitReached = true;
							SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH mm ss SSS z");
							JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
									getLimitCodeLevel(),
									getLimitCodeLevel().getDescription(format.format(new Date(eventLost.getStartTime())))
									);
							publisherEvents.add(publisherEvent);
						}
					}

					return true;
				}

				public void setEventsLimit(int eventsLimit) {
					if (eventsLimit > this.eventsLimit)
						this.eventsLimit = eventsLimit;
				}
				
				public void terminateLocalOrphanEvents(long[] threadIds) {
					Iterator<JzrEventInfoImpl> iter = this.events.iterator();
					while (iter.hasNext()) {
						JzrEventInfoImpl event = iter.next();
						if (!JzrEventScope.ACTION.equals(event.getScope()))
							continue;  // not thread event
						if (event.getEndTime()!=-1)
							continue;  // closed or one shot thread event
						if (isDeadThread(threadIds, event.getThreadId()))
							event.terminate(); // end time will be closure one
					}
				}
				
				public boolean terminateEvent(JzrEvent event) {
					Iterator<JzrEventInfoImpl> iter = events.iterator();
					while (iter.hasNext()) {
						JzrEventInfoImpl candidate = iter.next();
						if (candidate.getId().equals(event.getId())) {
							candidate.terminate();
							return true;
						}
					}
					return false;
				}
				
				public boolean cancelEvent(JzrEvent event) {
					Iterator<JzrEventInfoImpl> iter = events.iterator();
					while (iter.hasNext()) {
						JzrEventInfoImpl candidate = iter.next();
						if (candidate.getId().equals(event.getId())) {
							events.remove(candidate);
							return true;
						}
					}
					return false;
				}
				
				public boolean isWaitingForPublication(JzrEvent event) {
					Iterator<JzrEventInfoImpl> iter = events.iterator();
					while (iter.hasNext()) {
						JzrEventInfoImpl candidate = iter.next();
						if (candidate.getId().equals(event.getId()))
							return true;
					}
					return false;
				}
				
				public boolean isTerminated(JzrEvent event) {
					Iterator<JzrEventInfoImpl> iter = events.iterator();
					while (iter.hasNext()) {
						JzrEventInfoImpl candidate = iter.next();
						if (candidate.getId().equals(event.getId()))
							return candidate.isTerminated();
					}
					return false;  // not found or already published
				}
				
				private boolean isDeadThread(final long[] threadIds, final long id) {
					for (int i=0; i<threadIds.length; i++){
						if (id == threadIds[i]){
							return false;
						}
					}
					return true;
				}
				
				private boolean isLoopedEvent(JzrEventInfoImpl evtInfo) {
					if (this.previousEvent != null && this.previousEvent.equalsOrigin(evtInfo)
							&& this.previousEvent.isTimeContemporary(evtInfo)) {
						return true;  // todo : previous event should carry this discard info and a counter
					}
					else {
						this.previousEvent = evtInfo;
						return false;
					}
				}
				
				private JzrPublisherEventCode getLimitCodeLevel() {
					switch(this.level) {
						case CRITICAL : return JzrPublisherEventCode.JZR_PUB_101;
						case WARNING  : return JzrPublisherEventCode.JZR_PUB_201;
						default       : return JzrPublisherEventCode.JZR_PUB_301;
					}
				}
			}
		}
		
		private static final class JzrActionHandlerImpl implements JzrActionHandler{
			
			private JzrThreadInfoImpl threadInfo;
			
			/**
			 * Declare a starting action with Jeyzer 
			 * @param context   the associated action context.
			 */
			public synchronized void startAction(final JzrActionContext context){
				if (context == null)
					return;

				JeyzerPublisherImpl pub =  ((JeyzerPublisherImpl)publisher);
				
				if (!pub.isDataCollectionActive())
					return;
				
				if (threadInfo != null)
					// remove any previous action context
					pub.removeActionContext(threadInfo);
				
				this.threadInfo = new JzrThreadInfoImpl(
						Thread.currentThread().getId(), 
						context,
						(new Date()).getTime()
						);

				pub.addActionContext(threadInfo);
			}
			
			/**
			 * Close the action
			 */
			public synchronized void closeAction(){
				if (this.threadInfo == null)
					return;

				JeyzerPublisherImpl mgr =  ((JeyzerPublisherImpl)publisher);
				mgr.removeActionContext(threadInfo);
			}
			
			/**
			 * Set the action context parameter
			 * @param context   the context parameter
			 * @param value   the context value 
			 */
			public synchronized void setContextParameter(String param, String value){
				if (this.threadInfo == null || !publisher.isDataCollectionActive())
					return;
				
				threadInfo.setContextParameter(param, value);
			}
		}
		
		private static final class JzrMonitorHandlerImpl implements JzrMonitorHandler{

			private JzrEventManager eventMgr;
			
			private String source;
			private String service;
			
			public JzrMonitorHandlerImpl(String source, String service, JzrEventManager eventMgr, String defaultSource) {
				this.source = source != null ? source : defaultSource != null ? defaultSource : "NA";
				this.service = service; // can be null
				this.eventMgr = eventMgr;
			}

			@Override
			public boolean fireSystemEvent(JzrEvent event) {
				if (!isValidEvent(event))
					return false;
				JzrEventInfoImpl evtInfo = new JzrEventInfoImpl(
						this.source,
						this.service,
						event,
						JzrEventScope.SYSTEM,
						true);
				return eventMgr.add(evtInfo);
			}

			@Override
			public boolean fireGlobalEvent(JzrEvent event) {
				if (!isValidEvent(event))
					return false;
				JzrEventInfoImpl evtInfo = new JzrEventInfoImpl(
						this.source,
						this.service,
						event,
						JzrEventScope.GLOBAL,
						true);
				return eventMgr.add(evtInfo);
			}

			@Override
			public boolean fireLocalThreadEvent(JzrEvent event) {
				if (!isValidEvent(event))
					return false;
				JzrEventInfoImpl evtInfo = new JzrEventInfoImpl(
						this.source,
						this.service,
						event,
						JzrEventScope.ACTION,
						true, 
						Thread.currentThread().getId());
				return eventMgr.add(evtInfo);
			}
			
			@Override
			public boolean startGlobalEvent(JzrEvent event) {
				if (!isValidEvent(event))
					return false;
				JzrEventInfoImpl evtInfo = new JzrEventInfoImpl(
						this.source,
						this.service,
						event,
						JzrEventScope.GLOBAL,
						false);
				return eventMgr.add(evtInfo);
			}
			
			@Override
			public boolean startLocalThreadEvent(JzrEvent event) {
				if (!isValidEvent(event))
					return false;
				JzrEventInfoImpl evtInfo = new JzrEventInfoImpl(
						this.source,
						this.service,
						event,
						JzrEventScope.ACTION,
						false,
						Thread.currentThread().getId());
				return eventMgr.add(evtInfo);
			}

			@Override
			public boolean terminateEvent(JzrEvent event) {
				return eventMgr.terminateEvent(event);
			}
			
			@Override
			public boolean cancelEvent(JzrEvent event) {
				return eventMgr.cancelEvent(event);
			}
			
			@Override
			public boolean isWaitingForPublication(JzrEvent event) {
				return eventMgr.isWaitingForPublication(event);
			}

			@Override
			public boolean isTerminated(JzrEvent event) {
				return eventMgr.isTerminated(event);
			}
			
			private boolean isValidEvent(JzrEvent event) {
				if (event == null)
					return false;
				if (event.getId() == null || event.getId().isEmpty())
					return false;
				if (event.getCode() == null)
					return false;
				if (event.getCode().getLevel() == null || event.getCode().getSubLevel() == null)
					return false;
				return true;
			}
		}
		
		/**
		 * Internal Jeyzer action context bean<br>
		 * Holds a reference to the action context<br>
		 * Provides action internal info such as thread id, action start time and internal unique id<br>
		 * Thread safe
		 */
		private static final class JzrThreadInfoImpl implements JzrThreadInfo {

			private static final AtomicInteger idCount = new AtomicInteger(0);

			private long threadId;    // unique id
			private String jhId;      // unique id
			private JzrActionContext context;
			private long startTime;
			
			public JzrThreadInfoImpl(final long threadId, final JzrActionContext context, final long startTime) {
				this.jhId = Integer.toString(idCount.incrementAndGet());
				this.threadId = threadId;
				this.context = (JzrActionContext)context.clone();
				this.startTime = startTime;
			}

			@Override
			public String getActionId() {
				return jhId;
			}

			@Override
			public String getId() {
				return context.getId();
			}

			@Override
			public String getUser() {
				return context.getUser();
			}

			@Override
			public String getFunctionPrincipal() {
				return context.getFunctionPrincipal();
			}

			@Override
			public Map<String, String> getContextParams() {
				return context.getContextParams();
			}

			@Override
			public long getStartTime() {
				return this.startTime;
			}

			@Override
			public long getThreadId() {
				return this.threadId;
			}
			
			public void setContextParameter(String param, String value){
				context.setContextParam(param, value);
			}

		}
		
		/**
		 * Internal Jeyzer event info bean<br>
		 */
		private static final class JzrEventInfoImpl implements JzrEventInfo {
			
			private String source;
			private String service;
			
			private JzrEvent event;
			
			private long startTime;
			private long endTime = -1;
			
			private JzrEventScope scope;
			private long threadId = -1;
			
			private boolean oneshot;
			
			public JzrEventInfoImpl(String source, String service, JzrEvent evt, JzrEventScope scope, boolean oneshot) {
				this.source = source;
				this.service = service;
				this.event = (JzrEvent)evt.clone();
				this.startTime = System.currentTimeMillis();
				this.scope = scope;
				this.oneshot = oneshot;
				if (oneshot)
					this.endTime = this.startTime;
			}

			public JzrEventInfoImpl(String source, String service, JzrEvent evt, JzrEventScope scope, boolean oneshot, long threadId) {
				this(source, service, evt, scope, oneshot);
				this.threadId = threadId;
			}

			@Override
			public String getSource() {
				return source;
			}

			@Override
			public String getService() {
				return service;
			}

			@Override
			public JzrEventCode getCode() {
				return event.getCode();
			}

			@Override
			public String getId() {
				return event.getId();
			}

			@Override
			public JzrEventScope getScope() {
				return scope;
			}
			
			@Override
			public String getMessage() {
				return event.getMessage();
			}

			@Override
			public short getTrustFactor() {
				return event.getTrustFactor();
			}

			@Override
			public long getStartTime() {
				return startTime;
			}

			@Override
			public long getEndTime() {
				return endTime;
			}

			@Override
			public long getThreadId() {
				return threadId;
			}

			@Override
			public boolean isOneshot() {
				return oneshot;
			}
			
			public boolean isTerminated() {
				return endTime != -1;
			}
			
			public void terminate() {
				if (!oneshot)
					endTime = System.currentTimeMillis();
			}
			
			public boolean equalsOrigin(JzrEventInfoImpl other) {
				if (other == null)
					return false;
				if (service == null) {
					if (other.getService() != null)
						return false;
				} else if (!service.equals(other.getService()))
					return false;
				if (!source.equals(other.getSource()))  // mandatory field, never null
					return false;
				if (event.getCode() == null) {
					if (other.getCode() != null)
						return false;
				} else if (!event.getCode().equals(other.getCode()))
					return false;
				if (event.getMessage() == null) {
					if (other.getMessage() != null)
						return false;
				} else if (!event.getMessage().equals(other.getMessage()))
					return false;
				return true;
			}
			
			public boolean isTimeContemporary(JzrEventInfoImpl other) {
				if (other == null)
					return false;
				
				// Arbitrary decision : events are considered contemporary if both happening within 5 sec range
				long diffTime = other.startTime > this.startTime ? 
						other.startTime - this.startTime : this.startTime - other.startTime;
				return diffTime < 5000L;
			}
		}

		@Override
		public List<JzrPublisherEvent> getPublisherEvents() {
			return eventMgr.getPublisherEvents();
		}

		@Override
		public List<JzrPublisherEvent> consumePublisherEvents() {
			return eventMgr.fetchPublisherEvents();
		}
	}
	
	private static final class JeyzerPublisherNoImpl extends JeyzerPublisher{

		private static final String EMPTY_VALUE = "";
		
		private boolean getEventsFirstCall = true;
		
		protected JeyzerPublisherNoImpl(){
			register();
		}
		
		@Override
		public boolean init(Properties props) {
			return true;
		}
		
		@Override
		public boolean isActive() {
			return false;
		}		
		
		@Override
		public String getProfileName() {
			return EMPTY_VALUE;
		}

		@Override
		public String getNodeName() {
			return EMPTY_VALUE;
		}
		
		@Override
		public String getProcessName() {
			return EMPTY_VALUE;
		}

		@Override
		public String getProcessVersion() {
			return EMPTY_VALUE;
		}

		@Override
		public String getProcessBuildNumber() {
			return EMPTY_VALUE;
		}
		
		@Override
		public List<JzrThreadInfo> getThreadInfoList() {
			return new ArrayList<JzrThreadInfo>();
		}

		@Override
		public Map<String, String> getStaticProcessContextParams() {
			return new HashMap<String, String>();
		}
		
		@Override
		public Map<String, String> getDynamicProcessContextParams() {
			return new HashMap<String, String>();
		}

		@Override
		public void setProfileName(final String profile) {
			// do nothing
		}

		@Override
		public void setNodeName(final String nodeName) {
			// do nothing
		}

		@Override
		public void setProcessName(String name) {
			// do nothing
		}
		
		@Override
		public void setProcessVersion(final String version) {
			// do nothing
		}
		
		@Override
		public void setProcessBuildNumber(String name) {
			// do nothing
		}		
	
		@Override
		public void addStaticProcessContextParam(final String key, final String value){
			// do nothing
		}
		
		@Override
		public void setDynamicProcessContextParam(final String key, final String value){
			// do nothing
		}	

		@Override
		public JzrActionHandler getActionHandler() {
			return new JzrActionHandlerNoImpl();
		}
		
		private static final class JzrActionHandlerNoImpl implements JzrActionHandler{
		
			@Override
			public void startAction(final JzrActionContext context){
				// do nothing
			}
			
			@Override
			public void closeAction(){
				// do nothing
			}

			@Override
			public void setContextParameter(String param, String value) {
				// do nothing
			}
		}
		
		private static final class JzrMonitorHandlerNoImpl implements JzrMonitorHandler{

			@Override
			public boolean fireSystemEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean fireGlobalEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean fireLocalThreadEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean startGlobalEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean startLocalThreadEvent(JzrEvent event) {
				return true;
			}
			
			@Override
			public boolean terminateEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean cancelEvent(JzrEvent event) {
				return true;
			}

			@Override
			public boolean isWaitingForPublication(JzrEvent event) {
				return false;
			}

			@Override
			public boolean isTerminated(JzrEvent event) {
				return true;
			}
		}

		@Override
		public List<JzrEventInfo> getEvents() {
			return new ArrayList<JzrEventInfo>(0);
		}
		
		@Override
		public List<JzrEventInfo> consumeEvents() {
			return new ArrayList<JzrEventInfo>(0);
		}

		@Override
		public JzrMonitorHandler getMonitorHandler(String source, String service) {
			return new JzrMonitorHandlerNoImpl();
		}

		@Override
		public List<JzrPublisherEvent> consumePublisherEvents() {
			if (getEventsFirstCall) {
				getEventsFirstCall = false;
				List<JzrPublisherEvent> publisherEvents = new ArrayList<JzrPublisherEvent>(1);
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
						JzrPublisherEventCode.JZR_PUB_001);
				publisherEvents.add(publisherEvent);
				return publisherEvents;
			}
			else
				return new ArrayList<JzrPublisherEvent>(0);
		}

		@Override
		public List<JzrPublisherEvent> getPublisherEvents() {
			if (getEventsFirstCall) {
				List<JzrPublisherEvent> publisherEvents = new ArrayList<JzrPublisherEvent>(1);
				JzrPublisherEvent publisherEvent = new JeyzerPublisherEventImpl(
						JzrPublisherEventCode.JZR_PUB_001);
				publisherEvents.add(publisherEvent);
				return publisherEvents;
			}
			else
				return new ArrayList<JzrPublisherEvent>(0);
		}

		@Override
		public void suspendDataCollection() {
			// do nothing
		}

		@Override
		public void resumeDataCollection() {
			// do nothing		
		}

		@Override
		public void suspendEventCollection(JzrEventLevel level) {
			// do nothing
		}

		@Override
		public void resumeEventCollection(JzrEventLevel level) {
			// do nothing
		}

		@Override
		public boolean isDataCollectionActive() {
			return false;
		}

		@Override
		public boolean isEventCollectionActive(JzrEventLevel level) {
			return false;
		}
	}
}
