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


import java.util.HashMap;
import java.util.Map;

/**
 * The Jeyzer action context class is a bean representing the current action executed within one thread.<br>
 * All its attributes are optional.<br>
 * Non thread safe<br>
 */
public final class JzrActionContext {

	private String id;
	private String user;
	private String functionPrincipal;
	private Map<String, String> contextParams = new HashMap<>();

	/**
	 * The Jeyzer action context constructor
	 */
	public JzrActionContext(){
	}
	
	/**
	 * The Jeyzer action context constructor
	 * @param id  	             the applicative action id. Can be null
	 * @param user               the applicative user. Can be null
	 * @param functionPrincipal  the action function principal. Can be null
	 * @param params             the context parameters. Can be null
	 */
	public JzrActionContext(final String id, final String user, final String functionPrincipal, final Map<String, String> params){
		this.id = id;
		this.user = user;
		this.functionPrincipal = functionPrincipal;
		if (params != null)
			this.contextParams = params;
	}
	
	/**
	 * The Jeyzer action context cloning constructor
	 * @param context  	         the context to clone
	 */
	@SuppressWarnings("unchecked")
	public JzrActionContext(JzrActionContext context) {
		this.id = context.id;
		this.user = context.user;
		this.functionPrincipal = context.functionPrincipal;
		if (context.contextParams != null)
			this.contextParams = (Map<String, String>)((HashMap<String, String>)context.contextParams).clone();
	}

	/**
	 * Get the applicative action id. Can be null
	 * @return the applicative action id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the applicative action id
	 * @param id the applicative action id
	 */
	public void setId(final String id) {
		this.id = id;
	}
		
	/**
	 * Get the applicative user. Can be null
	 * @return the applicative user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Set the applicative user
	 * @param user the applicative user
	 */
	public void setUser(final String user) {
		this.user = user;
	}
	
	/**
	 * Get the action function principal. Can be null
	 * @return the action function principal
	 */
	public String getFunctionPrincipal() {
		return functionPrincipal;
	}
	
	/**
	 * Set the action function principal
	 * @param functionPrincipal the action function principal
	 */
	public void setFunctionPrincipal(final String functionPrincipal) {
		this.functionPrincipal = functionPrincipal;
	}
	
	/**
	 * Get the context parameters. Can be null
	 * @return the context parameters
	 */
	public Map<String, String> getContextParams() {
		return contextParams;
	}
	
	/**
	 * Set the context parameters
	 * @param params context parameters
	 */
	public void setContextParams(final Map<String, String> params) {
		if (params != null)
			this.contextParams = params;
	}

	/**
	 * Set a context parameter
	 * @param key     the context parameter key
	 * @param value   the context parameter value. Can be null
	 */
	public void setContextParam(final String key, final String value) {
		if (key != null)
			this.contextParams.put(key, value);
	}
}
