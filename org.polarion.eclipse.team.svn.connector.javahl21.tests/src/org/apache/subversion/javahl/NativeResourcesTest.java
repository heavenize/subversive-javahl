package org.apache.subversion.javahl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NativeResources native library loading.
 */
public class NativeResourcesTest {

    @Test
    @DisplayName("Native library should load successfully")
    public void testNativeLibraryLoads() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
        }, "Native library should load without throwing UnsatisfiedLinkError");
    }

    @Test
    @DisplayName("RuntimeVersion should be available after loading")
    public void testRuntimeVersionAvailable() {
        NativeResources.loadNativeLibrary();
        
        assertDoesNotThrow(() -> {
            RuntimeVersion version = NativeResources.getRuntimeVersion();
            assertNotNull(version, "RuntimeVersion should not be null");
        }, "RuntimeVersion should be accessible after library load");
    }

    @Test
    @DisplayName("RuntimeVersion should have valid version information")
    public void testRuntimeVersionInformation() {
        NativeResources.loadNativeLibrary();
        RuntimeVersion version = NativeResources.getRuntimeVersion();
        
        assertNotNull(version.getVersion(), "Version object should not be null");
        assertTrue(version.getVersion().getMajor() >= 1, "Major version should be at least 1");
        assertNotNull(version.getBuildDate(), "Build date should not be null");
        assertNotNull(version.getBuildTime(), "Build time should not be null");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("On Windows, OpenSSL DLLs should be loadable")
    public void testOpenSSLDLLsLoadableOnWindows() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
        }, "Native library with OpenSSL dependencies should load on Windows");
    }

    @Test
    @DisplayName("Loading native library multiple times should be safe")
    public void testMultipleLoadsSafe() {
        assertDoesNotThrow(() -> {
            NativeResources.loadNativeLibrary();
            NativeResources.loadNativeLibrary();
        }, "Multiple calls to loadNativeLibrary should not cause errors");
    }

    @Test
    @DisplayName("Test library loading reports proper version")
    public void testLibraryVersionString() {
        NativeResources.loadNativeLibrary();
        RuntimeVersion version = NativeResources.getRuntimeVersion();
        
        String versionString = version.toString();
        assertNotNull(versionString, "Version string should not be null");
        assertTrue(versionString.contains("1.14"), "Version should be 1.14.x");
    }
}
