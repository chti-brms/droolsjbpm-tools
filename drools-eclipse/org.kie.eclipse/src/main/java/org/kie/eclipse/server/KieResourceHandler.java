/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.kie.eclipse.server;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.IServer;
import org.kie.eclipse.Activator;
import org.kie.eclipse.IKieConstants;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 */
public abstract class KieResourceHandler implements IKieResourceHandler {

	protected static IEclipsePreferences preferences;
	protected IKieResourceHandler parent;
	protected List children;
	protected String name;
	protected JsonObject properties;

	public KieResourceHandler(IKieResourceHandler parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see org.kie.eclipse.navigator.view.server.IKieResourceHandler#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public IKieResourceHandler getParent() {
		return parent;
	}
	
	@Override
	public void setParent(IKieResourceHandler parent) {
		this.parent = parent;
	}
	
	@Override
	public IKieResourceHandler getRoot() {
		if (getParent()==null)
			return this;
		return getParent().getRoot();
	}

	@Override
	public Object getResource() {
		return null;
	}
	
	@Override
	public void setResource(Object resource) {
	}
	
	/* (non-Javadoc)
	 * @see org.kie.eclipse.navigator.view.server.IKieResourceHandler#getServer()
	 */
	@Override
	public IServer getServer() {
		if (parent!=null)
			return parent.getServer();
		return null;
	}
    
	@Override
	public String getRuntimeId() {
		if (parent!=null)
			return parent.getRuntimeId();
		return null;
    }
    
	@Override
	public IKieServiceDelegate getDelegate() {
		if (parent!=null)
			return parent.getDelegate();
		return null;
	}

	@Override
	public void dispose() {
		children = null;
	}
	
	@Override
	public Object load() {
		return null;
	}
	
	@Override
	public boolean isLoaded() {
		return false;
	}
	
	@Override
	public void setProperties(JsonObject properties) {
		JsonValue v = properties.get("name");
		if (v!=null && v.isString())
			name = v.asString();
		this.properties = properties;
	}
	
	@Override
	public JsonObject getProperties() {
		return properties;
	}
	
	protected static String getCanonicalName(String name) {
		return name.replaceAll(IKieConstants.CANONICAL_NAME_PATTERN, IKieConstants.CANONICAL_NAME_REPLACEMENT);
	}
	
	/* (non-Javadoc)
	 * @see org.kie.eclipse.server.IKieResourceHandler#getPreferenceName(java.lang.String)
	 */
	@Override
	public String getPreferenceName(String name) {
		String canonicalName = getCanonicalName(getRoot().getName());
		if (name==null)
			return canonicalName;
		return canonicalName + IKieConstants.PREF_PATH_SEPARATOR + getCanonicalName(name);
	}
	
	@Override
	public Preferences getPreferences() {
		if (preferences==null) {
			preferences = (IEclipsePreferences) Platform.getPreferencesService().
					getRootNode().
					node(InstanceScope.SCOPE).
					node(Activator.PLUGIN_ID);
		}
		return preferences;
	}
	
	@Override
	public String getPreference(String name, String def) {
		return getPreferences().get(getPreferenceName(name), def);
	}
	
	@Override
	public boolean getPreference(String name, boolean def) {
		return getPreferences().getBoolean(getPreferenceName(name), def);
	}
	
	@Override
	public int getPreference(String name, int def) {
		return getPreferences().getInt(getPreferenceName(name), def);
	}
	
	@Override
	public void putPreference(String name, String value) {
		getPreferences().put(getPreferenceName(name), value);
	}
	
	@Override
	public void putPreference(String name, boolean value) {
		getPreferences().putBoolean(getPreferenceName(name), value);
	}
	
	@Override
	public void putPreference(String name, int value) {
		getPreferences().putInt(getPreferenceName(name), value);
	}
}
