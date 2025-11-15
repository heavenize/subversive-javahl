# Connector Optimization Notes

## Overview
This document explains the optimizations made to reduce repeated connector instantiation in the JavaHL connector for Eclipse Subversive.

## Problem
Subversive's architecture calls `ISVNConnectorFactory.createConnector()` frequently - potentially once per SVN operation. This is by design in Subversive, not a bug. However, it was creating unnecessary object churn and debug console spam.

## Solution: Thread-Local Connector Cache

### Implementation
Added a `ThreadLocal<JavaHLConnector>` cache in `JavaHLConnectorFactory.java`:

```java
// Thread-local connector cache to reduce repeated instantiation
// Each thread gets its own connector to avoid concurrency issues
private static final ThreadLocal<JavaHLConnector> connectorCache = new ThreadLocal<>();

public ISVNConnector createConnector() {
    String error = JavaHLConnectorFactory.checkLibraries();
    if (error != null) {
        throw new RuntimeException(error);
    }
    
    // Try to reuse thread-local connector to reduce object creation
    JavaHLConnector connector = connectorCache.get();
    if (connector == null) {
        // DEBUG_REMOVE_LATER: System.out.println("Creating new JavaHLConnector for thread: " + Thread.currentThread().getName());
        connector = new JavaHLConnector();
        connectorCache.set(connector);
    } else {
        // DEBUG_REMOVE_LATER: System.out.println("Reusing cached JavaHLConnector for thread: " + Thread.currentThread().getName());
    }
    
    return connector;
}
```

### Why Thread-Local?

1. **Thread Safety**: Each thread gets its own connector instance
2. **State Isolation**: `JavaHLConnector` holds client state (callbacks, credentials, etc.) that shouldn't be shared
3. **No Locking**: No synchronization overhead - each thread accesses only its own connector
4. **Eclipse Model**: Eclipse typically uses dedicated threads for SVN operations

### Benefits

- **Reduced Object Creation**: Only one connector per thread instead of one per operation
- **Lower GC Pressure**: Fewer short-lived objects to collect
- **Same Semantics**: Subversive still thinks it's getting a new connector each time
- **Debug Visibility**: Can see when new connectors are created vs reused

## Debug Markers

All debug statements are marked with `// DEBUG_REMOVE_LATER:` for easy removal:

```bash
# To find all debug statements:
grep -n "DEBUG_REMOVE_LATER" org.polarion.eclipse.team.svn.connector.javahl21/src/org/polarion/team/svn/connector/javahl/JavaHLConnectorFactory.java

# To remove all debug statements (PowerShell):
(Get-Content file.java) | Where-Object { $_ -notmatch 'DEBUG_REMOVE_LATER' } | Set-Content file.java
```

Current debug locations:
- Line ~147: Native library path found
- Line ~168: subversion.native.library property set
- Line ~190: VC++ runtime DLL loading
- Line ~44: New connector created for thread
- Line ~47: Cached connector reused for thread

## Performance Notes

### What's Cached
- The `JavaHLConnector` wrapper object (Java side)
- Not the native `SVNClient` (created fresh each time anyway)

### What's Not Cached
- Native library loading (only happens once per JVM via `librariesLoaded` flag)
- VC++ runtime DLLs (loaded once at first connector creation)
- The underlying `libsvnjavahl-1.dll` (loaded once by JavaHL's `NativeResources`)

### Memory Impact
- **Before**: N connector objects for N operations (garbage collected)
- **After**: 1 connector object per thread (retained until thread dies)
- **Trade-off**: Small memory increase (bytes per thread) vs reduced allocation/GC overhead

## Testing

### Enable Debug Output
Uncomment the debug statements in `JavaHLConnectorFactory.java`:

```java
// DEBUG_REMOVE_LATER: System.out.println("Creating new JavaHLConnector for thread: " + Thread.currentThread().getName());
```

### Expected Output
First operation on a thread:
```
Creating new JavaHLConnector for thread: Worker-42
```

Subsequent operations on same thread:
```
Reusing cached JavaHLConnector for thread: Worker-42
```

### Disable Caching (for testing)
To test without caching, comment out the cache logic:

```java
public ISVNConnector createConnector() {
    String error = JavaHLConnectorFactory.checkLibraries();
    if (error != null) {
        throw new RuntimeException(error);
    }
    // return connectorCache.get(); // OLD: caching
    return new JavaHLConnector();   // NEW: always create new
}
```

## Production Readiness

### Current State (Version 7.0.0.202511121209)
- ✅ Thread-local caching implemented
- ✅ Debug statements present but commented
- ✅ All JNI signatures verified and working
- ✅ Build succeeds, connector functional
- ⚠️ Debug markers present for easy removal

### To Remove Debug Statements
Once you're confident the connector is stable:

```powershell
# Navigate to source directory
cd org.polarion.eclipse.team.svn.connector.javahl21\src\org\polarion\team\svn\connector\javahl

# Remove all DEBUG_REMOVE_LATER lines
$content = Get-Content JavaHLConnectorFactory.java
$content | Where-Object { $_ -notmatch 'DEBUG_REMOVE_LATER' } | Set-Content JavaHLConnectorFactory.java

# Rebuild
cd ..\..\..\..\..\..
mvn clean package -DskipTests
```

## Alternative Approaches Considered

### 1. Global Singleton
❌ Rejected - not thread-safe, would need locking

### 2. Object Pool
❌ Overkill - adds complexity for minimal benefit

### 3. Weak References
❌ Unpredictable - GC could collect at any time

### 4. Request Subversive to Cache
❌ Not feasible - would require changes to Subversive core

## References
- `JavaHLConnectorFactory.java` - Factory with thread-local cache
- `JavaHLConnector.java` - Connector implementation (holds state)
- `ISVNConnectorFactory.java` - Subversive API interface (no lifecycle methods)

## Version History
- 7.0.0.202511121158 - Initial working version with all JNI fixes
- 7.0.0.202511121209 - Added thread-local caching and debug markers
