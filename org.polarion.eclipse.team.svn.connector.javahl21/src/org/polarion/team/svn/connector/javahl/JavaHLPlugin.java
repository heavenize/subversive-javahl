/*******************************************************************************
 * Copyright (c) 2005-2025 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alexander Gurov - Initial API and implementation
 *******************************************************************************/

package org.polarion.team.svn.connector.javahl;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.team.svn.core.utility.FileUtility;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Alexander Gurov
 */
public class JavaHLPlugin extends Plugin {
	private static JavaHLPlugin plugin;
	private String location;
	
	public JavaHLPlugin() {
		super();
		JavaHLPlugin.plugin = this;
	}

	public String getLocation() {
	    return this.location;
	}
	
    public String getVersionString() {
        return (String)this.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
    }
    
    public String getResource(String key) {
        return FileUtility.getResource(Platform.getResourceBundle(this.getBundle()), key);
    }
    
    public String getResource(String key, Object []args) {
        String message = this.getResource(key);
        return MessageFormat.format(message, args);
    }
    
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		System.out.println("=== JavaHL Connector Plugin Starting ===");
		System.out.println("Bundle: " + context.getBundle().getSymbolicName());
		System.out.println("Version: " + context.getBundle().getVersion());
		
		URL url = FileLocator.toFileURL(context.getBundle().getEntry("/"));
		this.location = url.getFile();
		if (this.location.startsWith("/")) {
		    this.location = this.location.substring(1);
		}
		if (this.location.endsWith("/")) {
		    this.location = this.location.substring(0, this.location.length() - 1);
		}
		
		System.out.println("Location: " + this.location);
		System.out.println("=== JavaHL Connector Plugin Started Successfully ===");
	}

	public static JavaHLPlugin instance() {
		return JavaHLPlugin.plugin;
	}

}
