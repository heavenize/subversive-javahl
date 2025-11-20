package org.polarion.team.svn.connector.javahl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.subversion.javahl.NativeResources;
import org.eclipse.team.svn.core.connector.ISVNConnector;

/**
 * Unit tests for JavaHLConnector initialization.
 */
public class JavaHLConnectorTest {

    @BeforeAll
    public static void setupNativeLibrary() {
        NativeResources.loadNativeLibrary();
    }

    @Test
    @DisplayName("JavaHLConnector should instantiate successfully")
    public void testConnectorCreation() {
        assertDoesNotThrow(() -> {
            JavaHLConnector connector = new JavaHLConnector();
            assertNotNull(connector, "Connector should not be null");
        }, "JavaHLConnector creation should not throw exceptions");
    }

    @Test
    @DisplayName("Connector should implement ISVNConnector")
    public void testConnectorInterface() {
        JavaHLConnector connector = new JavaHLConnector();
        
        assertTrue(connector instanceof ISVNConnector, 
            "Connector should implement ISVNConnector interface");
    }

    @Test
    @DisplayName("Multiple connectors can be created")
    public void testMultipleConnectors() {
        assertDoesNotThrow(() -> {
            JavaHLConnector connector1 = new JavaHLConnector();
            JavaHLConnector connector2 = new JavaHLConnector();
            
            assertNotNull(connector1, "First connector should not be null");
            assertNotNull(connector2, "Second connector should not be null");
            assertNotSame(connector1, connector2, "Connectors should be different instances");
        }, "Multiple connector instances should be creatable");
    }

    @Test
    @DisplayName("Connector inherits from JavaHLService")
    public void testConnectorInheritance() {
        JavaHLConnector connector = new JavaHLConnector();
        
        assertTrue(connector instanceof JavaHLService, 
            "Connector should extend JavaHLService");
        assertTrue(connector instanceof ISVNConnector, 
            "Connector should implement ISVNConnector");
    }
}
