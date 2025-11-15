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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.subversion.javahl.SVNClient;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.connector.ISVNManager;
import org.eclipse.team.svn.core.extension.factory.ISVNConnectorFactory;
import org.eclipse.team.svn.core.extension.factory.ISVNConnectorFactory.APICompatibility;
import org.eclipse.team.svn.core.extension.factory.ISVNConnectorFactory.OptionalFeatures;
import org.eclipse.team.svn.core.utility.FileUtility;

/**
 * Default implementation. Works with native SVN client.
 * 
 * @author Alexander Gurov
 */
public class JavaHLConnectorFactory implements ISVNConnectorFactory {
	public static final String CLIENT_ID = "org.eclipse.team.svn.connector.javahl21";
	
	private static boolean librariesLoaded = false;
	
	public JavaHLConnectorFactory() {
	}
	
	public ISVNConnector createConnector() {
		String error = JavaHLConnectorFactory.checkLibraries();
		if (error != null) {
			throw new RuntimeException(error);
		}
		return new JavaHLConnector();
	}

	public ISVNManager createManager() {
		String error = JavaHLConnectorFactory.checkLibraries();
		if (error != null) {
			throw new RuntimeException(error);
		}
		return new JavaHLManager();
	}
	
	public String getName() {
		try {
			String format = "%1$s %2$s r%3$s (SVN %4$s)";
			/*
			 * Parse client version string
			 * Example: svn:1.14.3 (r12345) ...
			 */		
			String fullClientVersion = this.getClientVersion();
			Pattern regex = Pattern.compile("^svn:(\\d+\\.\\d+\\.\\d+.*)\\s+\\(r(\\d+)\\)\njni:\\d+\\.\\d+\\.\\d+$");
			Matcher matcher = regex.matcher(fullClientVersion);
			if (matcher.matches()) {
				String version = matcher.group(1);
				String revision = matcher.group(2);
				return String.format(format,
					JavaHLPlugin.instance().getResource("ClientName"),
					version,
					revision,
					version);	
			} else {
				//we can't parse so return ordinary name and text because it could be an error message
				return JavaHLPlugin.instance().getResource("ClientName") + " " + fullClientVersion;
			}
		} catch (Throwable t) {
			// Don't crash during connector discovery phase
			return JavaHLPlugin.instance().getResource("ClientName") + " (not loaded)";
		}
	}
	
	public String getId() {
		return JavaHLConnectorFactory.CLIENT_ID;
	}

	public String getClientVersion() {
		try {
			String error = JavaHLConnectorFactory.checkLibraries();
			if (error != null) {
				return error;
			}
			return SVNClient.version();
		} catch (Throwable t) {
			// Don't crash if libraries fail to load during discovery
			return "Error loading JavaHL: " + t.getMessage();
		}
	}

	public String getVersion() {
		return JavaHLPlugin.instance().getVersionString();
	}
	
	public String getCompatibilityVersion() {
		return "5.0.0"; // Must match ISVNConnectorFactory.CURRENT_COMPATIBILITY_VERSION
	}
	
	public int getSupportedFeatures() {
		// JavaHL native library supports atomic commits across multiple working copies
		return OptionalFeatures.CREATE_REPOSITORY 
		     | OptionalFeatures.ATOMIC_X_COMMIT;
	}

	public String toString() {
		return this.getId();
	}

	public int getSVNAPIVersion() {
		// Subversive 5.1.0 only supports up to 1.10, but we're using JavaHL 1.14
		// Report as 1.10 for compatibility with Subversive API
		return APICompatibility.SVNAPI_1_10_x;
	}

	protected static String checkLibraries() {
		try {
			if (!JavaHLConnectorFactory.librariesLoaded) {
				if (FileUtility.isWindows() && System.getProperty("subversion.native.library") == null) {
					// Try to find the native libraries in the fragment bundle
					String nativePath = null;
					try {
						org.osgi.framework.Bundle fragmentBundle = org.eclipse.core.runtime.Platform.getBundle("org.polarion.eclipse.team.svn.connector.javahl21.win64");
						if (fragmentBundle != null) {
							java.net.URL nativeUrl = org.eclipse.core.runtime.FileLocator.toFileURL(fragmentBundle.getEntry("native"));
							if (nativeUrl != null) {
								nativePath = new java.io.File(nativeUrl.toURI()).getAbsolutePath();
								// DEBUG_REMOVE_LATER: System.out.println("Found native libraries at: " + nativePath);
							}
						}
					} catch (Exception e) {
						System.err.println("Warning: Could not locate native library path: " + e.getMessage());
					}
					
					if (nativePath == null) {
						throw new RuntimeException("Could not locate native library directory");
					}
					
				// Load VC++ runtime dependencies first (required even for static builds)
				JavaHLConnectorFactory.windowsLibraryLoadHelper(nativePath, new String[] {
					"VCRUNTIME140",    // VC++ Runtime 2015-2022
					"MSVCP140"         // VC++ Runtime 2015-2022
				});
				
				// Set the path to the statically-linked DLL for JavaHL's NativeResources
			// This must be the full path including the DLL name WITH the .dll extension
			String libPath = nativePath + java.io.File.separator + "libsvnjavahl-1.dll";
			System.setProperty("subversion.native.library", libPath);
			// DEBUG_REMOVE_LATER: System.out.println("Set subversion.native.library to: " + libPath);
			JavaHLConnectorFactory.librariesLoaded = true;
			}
			}
			return null;
		}
		catch (Throwable ex) {
			if (ex.getMessage() != null) {
				return JavaHLPlugin.instance().getResource("Error.CannotLoadLibraries0", new String[] {ConversionUtility.convertZeroCodedLine(ex.getMessage())});
			}
			return JavaHLPlugin.instance().getResource("Error.CannotLoadLibraries1");
		}
	}
	
	public static void windowsLibraryLoadHelper(String path, String []dependencies) {
		// warning, the code can't be made a common part because it should exists in the context of this plug-in, otherwise System.loadLibrary() will fail!
		for (String dependency : dependencies) {
			if (path == null) {
				System.loadLibrary(dependency);
			}
			else {
				// Use File.separator to ensure proper path separators on Windows
				String dllPath = path + java.io.File.separator + dependency + ".dll";
				// DEBUG_REMOVE_LATER: System.out.println("Loading: " + dllPath);
				System.load(dllPath);
			}
		}
	}

}
