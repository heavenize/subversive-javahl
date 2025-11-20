import org.apache.subversion.javahl.*;
import org.apache.subversion.javahl.types.*;

/**
 * Standalone test for JavaHL connector and native library loading.
 * Run with: javac -cp path\to\connector.jar StandaloneJavaHLTest.java
 *           java -Djava.library.path=path\to\native StandaloneJavaHLTest
 */
public class StandaloneJavaHLTest {
    
    public static void main(String[] args) {
        System.out.println("=== JavaHL Standalone Test ===\n");
        
        int passed = 0;
        int failed = 0;
        
        // Test 1: Native library loading
        System.out.print("Test 1: Load native library... ");
        try {
            NativeResources.loadNativeLibrary();
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            failed++;
            return; // Can't continue if native library won't load
        }
        
        // Test 2: RuntimeVersion available
        System.out.print("Test 2: Get RuntimeVersion... ");
        try {
            Version version = NativeResources.getRuntimeVersion();
            if (version != null) {
                System.out.println("✓ PASSED (version: " + version.toString() + ")");
                passed++;
            } else {
                System.out.println("✗ FAILED: Version is null");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }
        
        // Test 3: SVNClient creation
        System.out.print("Test 3: Create SVNClient... ");
        try {
            SVNClient client = new SVNClient();
            if (client != null) {
                System.out.println("✓ PASSED");
                passed++;
                
                // Test 4: Get version string
                System.out.print("Test 4: Get version string... ");
                try {
                    String versionStr = client.getVersion().toString();
                    System.out.println("✓ PASSED (" + versionStr + ")");
                    passed++;
                } catch (Exception e) {
                    System.out.println("✗ FAILED: " + e.getMessage());
                    failed++;
                }
                
                // Test 5: Multiple SVNClient instances
                System.out.print("Test 5: Create second SVNClient... ");
                try {
                    SVNClient client2 = new SVNClient();
                    if (client2 != null && client2 != client) {
                        System.out.println("✓ PASSED");
                        passed++;
                        client2.dispose();
                    } else {
                        System.out.println("✗ FAILED: Second client is null or same as first");
                        failed++;
                    }
                } catch (Exception e) {
                    System.out.println("✗ FAILED: " + e.getMessage());
                    failed++;
                }
                
                client.dispose();
            } else {
                System.out.println("✗ FAILED: SVNClient is null");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }
        
        // Test 6: Re-loading library is safe
        System.out.print("Test 6: Re-load native library... ");
        try {
            NativeResources.loadNativeLibrary();
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            failed++;
        }
        
        // Print summary
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
        
        if (failed == 0) {
            System.out.println("\n✓ All tests passed!");
            System.exit(0);
        } else {
            System.out.println("\n✗ Some tests failed");
            System.exit(1);
        }
    }
}
