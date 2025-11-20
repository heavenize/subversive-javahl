package org.apache.subversion.javahl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SVNClient initialization.
 */
public class SVNClientTest {

    @BeforeAll
    public static void setupNativeLibrary() {
        NativeResources.loadNativeLibrary();
    }

    @Test
    @DisplayName("SVNClient should instantiate successfully")
    public void testSVNClientCreation() {
        assertDoesNotThrow(() -> {
            ISVNClient client = new SVNClient();
            assertNotNull(client, "SVNClient should not be null");
        }, "SVNClient creation should not throw exceptions");
    }

    @Test
    @DisplayName("Multiple SVNClient instances can be created")
    public void testMultipleSVNClientInstances() {
        assertDoesNotThrow(() -> {
            ISVNClient client1 = new SVNClient();
            ISVNClient client2 = new SVNClient();
            
            assertNotNull(client1, "First client should not be null");
            assertNotNull(client2, "Second client should not be null");
            assertNotSame(client1, client2, "Clients should be different instances");
        }, "Multiple SVNClient instances should be creatable");
    }

    @Test
    @DisplayName("SVNClient should have proper version info")
    public void testSVNClientVersion() {
        ISVNClient client = new SVNClient();
        
        assertDoesNotThrow(() -> {
            RuntimeVersion version = NativeResources.getRuntimeVersion();
            assertNotNull(version, "Runtime version should be available");
            assertTrue(version.getVersion().getMajor() >= 1, "Major version should be valid");
        }, "Version information should be accessible");
    }

    @Test
    @DisplayName("SVNClient type checks")
    public void testSVNClientTypes() {
        ISVNClient client = new SVNClient();
        
        assertTrue(client instanceof ISVNClient, "Client should implement ISVNClient");
        assertTrue(client instanceof SVNClient, "Client should be SVNClient instance");
    }
}
