package org.polarion.team.svn.connector.javahl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.subversion.javahl.NativeResources;
import org.apache.subversion.javahl.SVNClient;

/**
 * Unit tests specifically for OpenSSL dependency loading on Windows.
 * 
 * Tests verify the fix for the "Can't find dependent libraries" error.
 */
public class OpenSSLDependencyTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("OpenSSL pre-loading should not fail on Windows")
    public void testOpenSSLPreloadingWindows() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
        }, "OpenSSL pre-loading should complete without UnsatisfiedLinkError");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("SVNClient should load with OpenSSL dependencies on Windows")
    public void testSVNClientWithOpenSSL() {
        assertDoesNotThrow(() -> {
            SVNClient client = new SVNClient();
            assertNotNull(client, "SVNClient should be created with OpenSSL dependencies");
        }, "SVNClient creation should work with OpenSSL dependencies on Windows");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("JavaHLConnector should load with OpenSSL dependencies on Windows")
    public void testConnectorWithOpenSSL() {
        assertDoesNotThrow(() -> {
            JavaHLConnector connector = new JavaHLConnector();
            assertNotNull(connector, "Connector should be created with OpenSSL dependencies");
        }, "Connector creation should work with OpenSSL dependencies on Windows");
    }

    @Test
    @DisplayName("Library loading should be OS-aware")
    public void testOSAwareLoading() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
            
            String osName = System.getProperty("os.name").toLowerCase();
            assertNotNull(osName, "OS name should be available");
            
            assertNotNull(NativeResources.getRuntimeVersion(), 
                "RuntimeVersion should be available regardless of OS");
        }, "Library loading should adapt to the operating system");
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("Non-Windows platforms should load without OpenSSL pre-loading")
    public void testNonWindowsLoading() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
            
            assertNotNull(NativeResources.getRuntimeVersion(), 
                "Library should load on non-Windows platforms");
        }, "Library loading should work on Linux/Mac without OpenSSL pre-loading");
    }
}
