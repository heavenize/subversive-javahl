# Maintenance Guide - Subversive SVN 1.14 JavaHL Connector

**For:** Future maintainers and developers  
**Version:** 7.0.0.202511151608  
**Last Updated:** November 15, 2025

---

## ⚠️ DOCUMENTATION POLICY

**When documenting fixes or changes:**
- ✅ Update this MAINTENANCE_GUIDE.md with technical details
- ✅ Update PROJECT_SUMMARY.md with version/feature changes
- ✅ Update history/DEVELOPMENT_SESSION_SUMMARY.md for session logs
- ❌ DO NOT create new FIX_XXX.md or CHANGE_XXX.md files
- ❌ Avoid document proliferation - merge, don't create

---

## Table of Contents

1. [Critical File Locations](#critical-file-locations)
2. [Architecture Overview](#architecture-overview)
3. [Common Maintenance Tasks](#common-maintenance-tasks)
4. [Fixing JNI Signature Mismatches](#fixing-jni-signature-mismatches)
5. [Adding New Native Methods](#adding-new-native-methods)
6. [Updating to New SVN Versions](#updating-to-new-svn-versions)
7. [Platform Support](#platform-support)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Version Management](#version-management)
10. [Testing and Validation](#testing-and-validation)

---

## Critical File Locations

### Core Implementation Files

#### 1. SVNClient.java (778 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/apache/subversion/javahl/SVNClient.java`

**Purpose:** Main JavaHL API implementation with all native method declarations

**Critical Sections:**
- **Lines 120-151:** list() method with ListItemCallback (FIXED in 7.0.0.202511141802)
  - Native method signature
  - ListCallbackWrapper inner class for backwards compatibility
  - Deprecated wrapper method
  
- **Lines 759-776:** openRemoteSession() methods (ADDED in 7.0.0.202511141802)
  - Public wrapper methods with validation
  - Private nativeOpenRemoteSession() native method

- **Native Method Count:** 66 methods total
  - All must match JNI signatures in libsvnjavahl-1.dll
  - Use exact parameter types (e.g., List vs Collection matters)

**Common Issues:**
- JNI signature mismatches → `UnsatisfiedLinkError` or `NoSuchMethodError`
- Wrong callback types → Runtime ClassCastException
- Wrong parameter types (Collection vs List) → JNI lookup fails

#### 2. JavaHLConnector.java (2344 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/polarion/team/svn/connector/javahl/JavaHLConnector.java`

**Purpose:** Bridge between Eclipse Subversive API and JavaHL implementation

**Critical Sections:**
- **Lines 484-492:** Auto-add unversioned files during commit (ADDED in 7.0.0.202511141802)
  ```java
  if (status.getNodeKind() == Kind.file && status.getTextStatus() == Kind.unversioned) {
      this.client.add(status.getPath(), Depth.empty, false, true, true);
  }
  ```
  
- **Lines 1836-1843:** list() method call using wrapper (FIXED in 7.0.0.202511141802)
  - Uses 2-parameter doEntry() callback
  - Calls wrapper method, not native method directly

- **Line 1942:** setPropertyLocal() call (Location of Fix 4 error before resolution)

**Common Issues:**
- Missing parameter conversions → Wrong data passed to native methods
- Incorrect callback implementations → Runtime errors
- Missing null checks → NullPointerException

#### 3. JavaHLConnectorFactory.java (~250 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/polarion/team/svn/connector/javahl/JavaHLConnectorFactory.java`

**Purpose:** Factory for creating connector instances, handles native library loading

**Critical Sections:**
- **Lines 40-50:** Thread-local connector cache
  ```java
  private static final ThreadLocal<JavaHLConnector> connectorCache = new ThreadLocal<>();
  ```
  - Reduces object creation overhead
  - Each thread gets its own connector instance

- **Lines 140-200:** Native library loading logic
  - Windows-specific DLL loading
  - VC++ runtime detection
  - Library path resolution

**Common Issues:**
- Native library not found → UnsatisfiedLinkError
- Wrong platform → Architecture mismatch errors
- Missing dependencies → DLL load failures

#### 4. NativeException.java (125 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/apache/subversion/javahl/NativeException.java`

**Purpose:** Base exception class for native library errors

**Critical Implementation:**
- **MUST have ONLY ONE constructor:**
  ```java
  NativeException(String message, String source, Throwable cause, int aprError)
  ```
- **DO NOT add 3-parameter constructor** - causes JNI lookup failures
- **Constructor is called by native code** - signature must match exactly

**History of Fix:**
- Originally had TWO constructors (3-param and 4-param)
- Native library expected only 4-param version
- Extra constructor caused NoSuchMethodError at JNI boundary
- Fixed in build 7.0.0.202511141809

#### 5. ClientException.java (128 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/apache/subversion/javahl/ClientException.java`

**Purpose:** Specific exception for client operation errors

**Critical Sections:**
- **Lines 84-88:** 5-parameter constructor (called by native code)
- **Lines 98-101:** 3-parameter convenience constructor
  - Calls 5-param with null for cause and messageStack
  - Used by Java code, not native code

#### 6. ClientNotifyInformation.java (~350 lines) 🔥 CRITICAL
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/apache/subversion/javahl/ClientNotifyInformation.java`

**Purpose:** Notification object created by JNI during ALL SVN operations (move, commit, update, etc.)

**Critical Constructor Signature (FIXED in 7.0.0.202511151557):**
```java
public ClientNotifyInformation(String path, Action action, NodeKind kind,
                         String mimeType, Lock lock, String errMsg,
                         List<ClientException.ErrorMessage> errMsgStack,  // ← CRITICAL PARAMETER #7
                         Status contentState, Status propState,
                         LockStatus lockState, long revision,
                         String changelistName, RevisionRange mergeRange,
                         String url, String pathPrefix, String propName,  // ← url is CRITICAL PARAMETER #13
                         Map<String, String> revProps, long oldRevision,
                         long hunkOriginalStart, long hunkOriginalLength,
                         long hunkModifiedStart, long hunkModifiedLength,
                         long hunkMatchedLine, int hunkFuzz)
```

**CRITICAL NOTES:**
- **Constructor MUST have exactly 23 parameters in this exact order**
- **Parameter 7:** `List<ClientException.ErrorMessage> errMsgStack` - NOT optional, even if always null
- **Parameter 13:** `String url` - NOT optional, even if empty string
- **Called by native code:** JNI CreateJ.cpp lines 801-817 during notification creation
- **Error if wrong:** `NoSuchMethodError: <init>` during ANY SVN operation

**History of Issues:**
- **Before fix:** Constructor had only 21 parameters (missing errMsgStack and url)
- **Symptom:** `NoSuchMethodError: <init>` when attempting file move/rename
- **Root cause:** JNI method signature in CreateJ.cpp expects 23 parameters:
  ```cpp
  "(Ljava/lang/String;"                                    // 1. path
  JAVAHL_ARG("/ClientNotifyInformation$Action;")           // 2. action
  JAVAHL_ARG("/types/NodeKind;")                           // 3. kind
  "Ljava/lang/String;"                                     // 4. mimeType
  JAVAHL_ARG("/types/Lock;")                               // 5. lock
  "Ljava/lang/String;Ljava/util/List;"                     // 6-7. errMsg, errMsgStack ← Missing List
  JAVAHL_ARG("/ClientNotifyInformation$Status;")           // 8. contentState
  JAVAHL_ARG("/ClientNotifyInformation$Status;")           // 9. propState
  JAVAHL_ARG("/ClientNotifyInformation$LockStatus;")       // 10. lockState
  "JLjava/lang/String;"                                    // 11-12. revision, changelistName
  JAVAHL_ARG("/types/RevisionRange;")                      // 13. mergeRange
  "Ljava/lang/String;"                                     // 14. url ← Missing String
  "Ljava/lang/String;Ljava/lang/String;"                   // 15-16. pathPrefix, propName
  "Ljava/util/Map;JJJJJJI)V");                             // 17-23. revProps, 5 longs, int
  ```
- **Fix:** Added both missing parameters in correct positions (Nov 15, 2025)
- **Verified:** Compared with official Apache SVN 1.14.5 source code
- **Result:** Move/rename operations now work correctly

**Maintenance Warning:**
- DO NOT remove or reorder parameters
- DO NOT make errMsgStack or url optional (no overloads)
- ALWAYS verify against JNI signature if modifying
- ALWAYS cross-check with official SVN source code

#### 7. ISVNClient.java (268 lines)
**Location:** `org.polarion.eclipse.team.svn.connector.javahl21/src/org/polarion/team/svn/connector/javahl/ISVNClient.java`

**Purpose:** Interface defining SVN client contract for Subversive

**Critical Sections:**
- **Lines 101-119:** list() method signatures (both old and new)
- All method signatures must match SVNClient implementation

---

## Architecture Overview

### Component Hierarchy

```
Eclipse Subversive (Team Provider)
         ↓
ISVNConnectorFactory.createConnector()
         ↓
JavaHLConnectorFactory
         ↓
JavaHLConnector (implements ISVNConnector)
         ↓
SVNClient (JavaHL API)
         ↓
JNI Bridge (libsvnjavahl-1.dll)
         ↓
Native SVN Libraries (C/C++)
```

### Key Interfaces

1. **ISVNConnectorFactory** - Eclipse extension point for connector factories
2. **ISVNConnector** - Main SVN operations interface for Subversive
3. **ISVNClient** - JavaHL client interface
4. **ISVNRemote** - Remote repository operations (no working copy)
5. **ISVNEditor** - Low-level repository editing
6. **ISVNReporter** - Update/status reporting protocol

### Package Structure

```
org.apache.subversion.javahl/
├── callback/                    # 19 callback interfaces
├── remote/                      # 6 remote operation classes (ADDED in Phase 3)
├── types/                       # 24 data type classes
├── util/                        # 8 utility classes (ADDED in Phase 3)
├── ClientException.java
├── ISVNClient.java
├── ISVNConfig.java
├── ISVNEditor.java
├── ISVNRemote.java
├── ISVNReporter.java
├── NativeException.java
├── OperationContext.java
├── SubversionException.java
├── SVNClient.java               # 66 native methods
└── SVNRepos.java

org.polarion.team.svn.connector.javahl/
├── JavaHLConnector.java         # Main implementation
├── JavaHLConnectorFactory.java  # Factory + native loading
└── JavaHLPlugin.java            # OSGi plugin activator
```

---

## Common Maintenance Tasks

### Task 1: Update Version Number

**Files to Update:**
1. Root `pom.xml`: `<version>7.0.0-SNAPSHOT</version>`
2. `org.polarion.eclipse.team.svn.connector.javahl21/pom.xml`
3. `org.polarion.eclipse.team.svn.connector.javahl21.win64/pom.xml`
4. `org.polarion.eclipse.team.svn.connector.javahl21.feature/pom.xml`
5. `org.polarion.eclipse.team.svn.connector.javahl21.site/pom.xml`
6. `META-INF/MANIFEST.MF` in each plugin: `Bundle-Version: 7.0.0.qualifier`
7. `feature.xml`: `<feature ... version="7.0.0.qualifier">`

**Command to Change All Versions:**
```powershell
# Use Maven versions plugin
mvn versions:set -DnewVersion=7.1.0-SNAPSHOT
mvn versions:commit  # or versions:revert to undo
```

### Task 2: Rebuild After Code Changes

**Standard Build:**
```powershell
cd <workspace>
mvn clean package -DskipTests
```

**Clean Build (forces re-download of dependencies):**
```powershell
mvn clean package -U -DskipTests
```
*Replace `<workspace>` with your project directory*

**Fast Build (plugin only):**
```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21
mvn clean compile
```

**Update Site Build:**
```powershell
.\build-updatesite.ps1
# Output: org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository\
```

### Task 3: Update Native Libraries

**Location:** `org.polarion.eclipse.team.svn.connector.javahl21.win64/native/`

**Required DLLs (22 files):**
- libsvnjavahl-1.dll (main JNI bridge)
- libsvn_*.dll (8 files - SVN libraries)
- libapr*.dll (3 files - Apache Portable Runtime)
- libssl-3-x64.dll, libcrypto-3-x64.dll (OpenSSL 3.x)
- libsasl.dll, libeay32.dll, etc.
- VCRUNTIME140.dll, MSVCP140.dll (VC++ runtime)

**Updating Process:**
1. Download new Subversion 1.14+ binaries for Windows x64
2. Extract all DLLs from `bin/` directory
3. Replace files in `native/` directory
4. Verify count: `(Get-ChildItem native\*.dll).Count` should be 22
5. Test: Rebuild and verify connector loads

**Compatibility:**
- All DLLs must be 64-bit (x64)
- SVN version should match (e.g., all 1.14.5)
- OpenSSL version matters (currently 3.x)
- VC++ runtime must match (2015-2022)

### Task 4: Add New Callback Interface

**Example: Adding a new callback**

1. **Create callback interface:**
   ```java
   // Location: org/apache/subversion/javahl/callback/NewCallback.java
   package org.apache.subversion.javahl.callback;
   
   public interface NewCallback {
       void onEvent(String path, int status);
   }
   ```

2. **Add method to SVNClient.java:**
   ```java
   public native void someOperation(String path, NewCallback callback)
           throws ClientException;
   ```

3. **Update ISVNClient.java interface:**
   ```java
   void someOperation(String path, NewCallback callback) throws SVNException;
   ```

4. **Implement in JavaHLConnector.java:**
   ```java
   public void someOperation(String path, ISVNProgressMonitor monitor) throws SVNConnectorException {
       this.client.someOperation(path, new NewCallback() {
           public void onEvent(String p, int s) {
               // Handle callback
           }
       });
   }
   ```

5. **Rebuild and test**

---

## Fixing JNI Signature Mismatches

### Understanding JNI Signatures

**Java to JNI Type Mapping:**
```
Java Type                    JNI Signature
---------                    -------------
void                         V
boolean                      Z
byte                         B
char                         C
short                        S
int                          I
long                         J
float                        F
double                       D
Object (any class)           Lclassname;
Array                        [type
String                       Ljava/lang/String;
List                         Ljava/util/List;
Collection                   Ljava/util/Collection;
```

**Method Signature Format:**
```
(parameter-types)return-type
```

**Example:**
```java
public native void commit(Set<String> paths, String message, Depth depth, boolean noUnlock)

JNI Signature:
(Ljava/util/Set;Ljava/lang/String;Lorg/apache/subversion/javahl/types/Depth;Z)V
```

### Common Mismatch Scenarios

#### Scenario 1: Wrong Parameter Type
**Problem:** `Collection<String>` vs `List<String>`

```java
// WRONG - causes JNI lookup failure
public native void list(..., Collection<String> patterns, ...)

// CORRECT - matches native library
public native void list(..., List<String> patterns, ...)
```

**Fix:** Change parameter type to match native library expectation

#### Scenario 2: Wrong Callback Type
**Problem:** `ListCallback` vs `ListItemCallback`

```java
// WRONG - different interface
public native void list(..., ListCallback callback)

// CORRECT - matches native library
public native void list(..., ListItemCallback callback)
```

**Fix:** Use correct callback interface

#### Scenario 3: Missing Constructor
**Problem:** Native code calls constructor that doesn't exist

```java
// WRONG - missing constructor
class NativeException {
    NativeException(String msg, String src, Throwable cause, int err) { }
    // Native code expects this, but it's missing:
    // NativeException(String msg, String src, int err) { }
}
```

**Symptom:** `NoSuchMethodError: <init>`

**Fix:** Add the missing constructor, or remove extra constructors

### Debugging Process

1. **Get the Error:**
   ```
   java.lang.UnsatisfiedLinkError: 
   org.apache.subversion.javahl.SVNClient.list(
     Ljava/lang/String;...Lorg/apache/subversion/javahl/callback/ListCallback;
   )V
   ```

2. **Identify the Method:**
   - Method: `list`
   - Problem parameter: `ListCallback` (should be `ListItemCallback`)

3. **Check SVN Source:**
   ```powershell
   # Download SVN 1.14.5 source from https://subversion.apache.org/download/
   # Set $svnSource to your extracted location
   Get-Content "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" | Select-String -Pattern "public.*void list"
   ```

4. **Compare Signatures:**
   - Check parameter types (exact match required)
   - Check callback interfaces
   - Check return types

5. **Fix and Rebuild:**
   ```powershell
   # Fix the signature in SVNClient.java
   mvn clean compile
   # Test
   ```

### Verification Tools

**Check Native Method Count:**
```powershell
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java" -Pattern "native " | Measure-Object
```

**Compare with SVN Source:**
```powershell
$ourFile = "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java"
$refFile = "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java"

# Compare method signatures
Compare-Object (Select-String -Path $ourFile -Pattern "public native") (Select-String -Path $refFile -Pattern "public native")
```

---

## Adding New Native Methods

### Step-by-Step Process

1. **Check SVN Source:**
   ```powershell
   # Download SVN source from https://subversion.apache.org/download/
   Get-Content "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" | Select-String -Pattern "methodName"
   ```

2. **Add to SVNClient.java:**
   ```java
   public native ReturnType methodName(ParamType param) throws ClientException;
   ```

3. **Add to ISVNClient.java interface:**
   ```java
   ReturnType methodName(ParamType param) throws SVNException;
   ```

4. **Implement in JavaHLConnector.java:**
   ```java
   public ReturnType methodName(ParamType param) throws SVNConnectorException {
       try {
           return this.client.methodName(param);
       } catch (ClientException e) {
           throw new SVNConnectorException(e);
       }
   }
   ```

5. **Handle Callbacks (if any):**
   ```java
   // If method takes a callback, create adapter
   this.client.methodName(param, new SomeCallback() {
       public void onEvent(Data data) {
           // Convert to Subversive format and notify
       }
   });
   ```

6. **Test:**
   - Rebuild: `mvn clean package`
   - Install in Eclipse
   - Test with real repository

### Example: Adding vacuum() Method

**History:** Added in SVN 1.14.5, provides working copy optimization

**1. Signature from SVN Source:**
```java
public native void vacuum(String wcPath,
                          boolean removeUnversionedItems,
                          boolean removeIgnoredItems,
                          boolean fixRecordedTimestamps,
                          boolean removeUnusedPristines,
                          boolean includeExternals) throws ClientException;
```

**2. Added to SVNClient.java** ✅

**3. Added to ISVNClient.java** ✅

**4. Implementation in JavaHLConnector.java:**
```java
public void vacuum(String wcPath, boolean removeUnversioned, 
                   boolean removeIgnored, boolean fixTimestamps,
                   boolean removeUnusedPristines, boolean includeExternals)
        throws SVNConnectorException {
    try {
        this.client.vacuum(wcPath, removeUnversioned, removeIgnored,
                          fixTimestamps, removeUnusedPristines, includeExternals);
    } catch (ClientException e) {
        throw new SVNConnectorException(e);
    }
}
```

---

## Updating to New SVN Versions

### When Subversion Releases New Version

**Example: Updating from SVN 1.14.5 to 1.15.0**

#### Step 1: Get New Source
```powershell
# Download new SVN version from https://subversion.apache.org/download/
# Or clone from repository:
cd <your-source-directory>
git clone --branch 1.15.0 https://github.com/apache/subversion subversion-1.15.0
cd subversion-1.15.0\subversion\bindings\javahl\src
```

#### Step 2: Compare Source
```powershell
# Compare SVNClient.java (replace <svn-source-old> and <svn-source-new> with your paths)
code --diff "<svn-source-old>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" "<svn-source-new>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java"
```

*Note: Set your SVN source paths in LOCAL_PATHS.md*

#### Step 3: Identify Changes
Look for:
- New native methods
- Changed method signatures
- New callback interfaces
- New type classes
- Deprecated methods

#### Step 4: Update Code
- Add new methods to SVNClient.java
- Add new callbacks to callback/ package
- Add new types to types/ package
- Update ISVNClient.java interface
- Update JavaHLConnector.java implementation

#### Step 5: Update Native Libraries (Maintainer Task)

**IMPORTANT:** This is for maintainers updating the bundled DLLs. End users don't need to do this - all libraries are included in the plugin.

- Download SVN 1.15.0 binaries for Windows x64 (SlikSVN, WANdisco, or build from source)
- Replace DLLs in `org.polarion.eclipse.team.svn.connector.javahl21.win64/native/`
- Verify compatibility:
  ```powershell
  # Check version
  & "native\svn.exe" --version
  ```

#### Step 6: Update Version Strings
```java
// JavaHLConnectorFactory.java
public String getClientVersion() {
    Version version = Version.getInstance();
    return version.getMajor() + "." + version.getMinor() + "." + version.getPatch();
    // Should return "1.15.0" or similar
}
```

#### Step 7: Test Thoroughly
- Basic operations (checkout, commit, update)
- New features specific to 1.15.0
- Regression test all existing features
- Check for deprecation warnings

---

## Platform Support

### Current Support
- ✅ Windows x64 (fully supported)
- ❌ Linux x86_64 (not included)
- ❌ macOS Intel (not included)
- ❌ macOS ARM (not included)

### Adding Linux Support

**1. Create Fragment Project:**
```
org.polarion.eclipse.team.svn.connector.javahl21.linux.x86_64/
├── META-INF/
│   └── MANIFEST.MF
├── native/
│   ├── libsvnjavahl-1.so
│   ├── libsvn_*.so.1
│   └── ... (other .so files)
└── pom.xml
```

**2. Create MANIFEST.MF:**
```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: Subversive SVN 1.14 JavaHL Connector - Linux x86_64
Bundle-SymbolicName: org.polarion.eclipse.team.svn.connector.javahl21.linux.x86_64
Bundle-Version: 7.0.0.qualifier
Fragment-Host: org.polarion.eclipse.team.svn.connector.javahl21
Bundle-Vendor: Polarion
Eclipse-PlatformFilter: (& (osgi.os=linux) (osgi.arch=x86_64))
```

**3. Add to Parent POM:**
```xml
<modules>
    <module>org.polarion.eclipse.team.svn.connector.javahl21</module>
    <module>org.polarion.eclipse.team.svn.connector.javahl21.win64</module>
    <module>org.polarion.eclipse.team.svn.connector.javahl21.linux.x86_64</module> <!-- NEW -->
    <module>org.polarion.eclipse.team.svn.connector.javahl21.feature</module>
    <module>org.polarion.eclipse.team.svn.connector.javahl21.site</module>
</modules>
```

**4. Update feature.xml:**
```xml
<feature ...>
    <plugin id="org.polarion.eclipse.team.svn.connector.javahl21" .../>
    <plugin id="org.polarion.eclipse.team.svn.connector.javahl21.win64" .../>
    <plugin id="org.polarion.eclipse.team.svn.connector.javahl21.linux.x86_64" .../> <!-- NEW -->
</feature>
```

**5. Build and Test:**
```bash
mvn clean package
# Test on Linux machine
```

### Adding macOS Support

Similar process, but:
- Use `.dylib` files instead of `.so`
- Platform filter: `(osgi.os=macosx)`
- Architecture: `x86_64` for Intel, `aarch64` for ARM
- May need code signing for macOS 10.15+

---

## Troubleshooting Guide

### Problem: Native Library Not Found

**Error:**
```
java.lang.UnsatisfiedLinkError: no svnjavahl-1 in java.library.path
```

**Diagnosis:**
1. Check if fragment bundle is installed
2. Verify DLL files exist in `native/` directory
3. Check platform filter matches your OS

**Solutions:**
```powershell
# Verify DLLs in JAR
Expand-Archive "org.polarion.eclipse.team.svn.connector.javahl21.win64-*.jar" -DestinationPath temp
Get-ChildItem temp\native\*.dll

# Check Eclipse plugins folder (replace <eclipse-install> with your Eclipse directory)
Get-ChildItem "<eclipse-install>\plugins\org.polarion.eclipse.team.svn.connector.javahl21*"

# Restart Eclipse with -clean
<eclipse-install>\eclipse.exe -clean
```

### Problem: NoSuchMethodError

**Error:**
```
java.lang.NoSuchMethodError: org.apache.subversion.javahl.SVNClient.someMethod(...)
```

**Diagnosis:**
- Method signature doesn't match native library
- Wrong parameter types
- Wrong callback type
- Constructor mismatch (error shows `<init>`)

**Solutions:**
1. Compare with SVN source:
   ```powershell
   # Download SVN source from https://subversion.apache.org/download/
   Get-Content "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" | Select-String -Pattern "someMethod"
   ```

2. Check parameter types exactly:
   - `Collection` vs `List`
   - `ListCallback` vs `ListItemCallback`
   - Primitive vs wrapper types

3. If error is `<init>`:
   - Check exception constructors
   - Verify NativeException has only 4-param constructor
   - Verify ClientException constructors match SVN source

### Problem: ClassCastException in Callbacks

**Error:**
```
java.lang.ClassCastException: WrongCallback cannot be cast to ExpectedCallback
```

**Diagnosis:**
- Using wrong callback interface
- Callback implementation doesn't match signature

**Solution:**
- Use correct callback type
- Check SVN source for expected callback interface
- Ensure callback methods have correct signatures

### Problem: Build Fails - Dependency Not Found

**Error:**
```
[ERROR] Cannot resolve org.eclipse.team.svn.core [4.0.0,6.0.0)
```

**Solution:**
```powershell
# Clear Maven cache
Remove-Item -Recurse "$env:USERPROFILE\.m2\repository\p2" -Force

# Rebuild with force update
mvn clean package -U
```

### Problem: Eclipse Won't Load Plugin

**Diagnosis:**
1. Check Error Log: Window → Show View → Error Log
2. Check Installation Details: Help → About → Installation Details → Plug-ins
3. Look for conflicts with other SVN connectors

**Solutions:**
1. Uninstall other SVN connectors
2. Restart with -clean flag
3. Check Java version (needs Java 21+)
4. Verify Subversive is installed

---

## Version Management

### Version Number Scheme

**Format:** `MAJOR.MINOR.PATCH.qualifier`

**Example:** `7.0.0.202511141809`
- `7.0.0` - Version from POM
- `202511141809` - Build timestamp (YYYYMMDDHHMM)

### Changing Version

**Maven POM Version:**
```xml
<version>7.0.0-SNAPSHOT</version>
```

**OSGi Bundle Version:**
```
Bundle-Version: 7.0.0.qualifier
```

**Tycho automatically generates qualifier** based on:
- Git commit hash (if in Git repo)
- Or timestamp: `YYYYMMDDHHMM`

### Release Process

1. **Update Version in POMs:**
   ```powershell
   mvn versions:set -DnewVersion=7.1.0-SNAPSHOT
   mvn versions:commit
   ```

2. **Update MANIFEST.MF:**
   - Change `Bundle-Version: 7.1.0.qualifier`

3. **Update feature.xml:**
   - Change `version="7.1.0.qualifier"`

4. **Build:**
   ```powershell
   mvn clean package
   ```

5. **Tag Release:**
   ```bash
   git tag -a v7.1.0 -m "Release 7.1.0"
   git push origin v7.1.0
   ```

---

## Testing and Validation

### Manual Testing Checklist

**Basic Operations:**
- [ ] Checkout repository
- [ ] Commit changes
- [ ] Update working copy
- [ ] Show status
- [ ] Show log
- [ ] Diff files
- [ ] Revert changes

**File Operations:**
- [ ] Add file (verify auto-add during commit)
- [ ] Copy file
- [ ] Move file
- [ ] Delete file
- [ ] Create directory

**Advanced:**
- [ ] Merge changes
- [ ] Resolve conflicts
- [ ] Lock/unlock files
- [ ] Set properties
- [ ] Use changelists
- [ ] Vacuum working copy

### Automated Testing

**Unit Tests (if added):**
```powershell
mvn test
```

**Integration Tests:**
```powershell
# Set up test repository
svnadmin create test-repo

# Run tests against test repository
mvn verify -Dtest.repo.url=file:///path/to/test-repo
```

### Validation Against SVN Source

**1. Compare Class Count:**
```powershell
# Our implementation
(Get-ChildItem -Recurse "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\*.java").Count

# SVN source (download from https://subversion.apache.org/download/)
(Get-ChildItem -Recurse "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\*.java").Count
```

**2. Compare Native Method Count:**
```powershell
(Select-String -Path "...\SVNClient.java" -Pattern "native ").Count
```

**3. Verify All Callbacks Present:**
```powershell
Get-ChildItem "...\src\org\apache\subversion\javahl\callback\*.java" | Select-Object Name
```

**4. Check for Missing Classes:**
```powershell
# Compare directories (download SVN source from https://subversion.apache.org/download/)
Compare-Object -ReferenceObject (Get-ChildItem "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\types\*.java").Name -DifferenceObject (Get-ChildItem "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\types\*.java").Name
```

---

## Quick Reference

### Build Commands
```powershell
# Full build
mvn clean package -DskipTests

# Fast compile
mvn compile

# Update site only
cd org.polarion.eclipse.team.svn.connector.javahl21.site
mvn clean package

# Version update
mvn versions:set -DnewVersion=7.1.0-SNAPSHOT
```

### Important Paths
```
Source:     org.polarion.eclipse.team.svn.connector.javahl21/src/
Native DLLs: org.polarion.eclipse.team.svn.connector.javahl21.win64/native/
Build Output: */target/
Update Site: org.polarion.eclipse.team.svn.connector.javahl21.site/target/repository/
SVN Reference: Download from https://subversion.apache.org/download/ (set path in LOCAL_PATHS.md)
```

### Key Classes
```
SVNClient.java            - 66 native methods, main API
JavaHLConnector.java      - Subversive bridge implementation
JavaHLConnectorFactory.java - Factory + native loading
NativeException.java      - Base exception (4-param constructor only!)
ClientException.java      - Client exception (5-param + 3-param)
ISVNClient.java          - Interface for Subversive
```

### Contact Points for Issues

**JNI Errors:**
- Check SVNClient.java native method signatures
- Compare with SVN 1.14.5 source
- Verify parameter types exactly match

**Build Errors:**
- Check Maven/Tycho versions
- Verify Java 21 is being used
- Clear Maven cache if dependency issues

**Runtime Errors:**
- Check Error Log in Eclipse
- Verify native libraries loaded correctly
- Test with simple repository first

---

## Final Notes

**Most Common Mistakes:**
1. Adding wrong constructor to NativeException → NoSuchMethodError
2. Using Collection instead of List → JNI signature mismatch
3. Wrong callback type → ClassCastException or UnsatisfiedLinkError
4. Forgetting to rebuild after changes → Old code still running
5. Not restarting Eclipse with -clean → OSGi caches old bundles

**Best Practices:**
1. Always compare with SVN source before changing signatures
2. Test every JNI method after adding/changing
3. Keep native library versions in sync (all 1.14.x)
4. Document all fixes in BUILD_SUCCESS.md or similar
5. Use thread-local caching for performance
6. Validate with real repositories, not just toy examples

**Remember:**
- JNI signatures must match EXACTLY
- Native code can't be debugged easily - get signatures right
- When in doubt, check SVN 1.14.5 source code
- Test on real Eclipse instance, not just build

---

*Document Version: 1.0*  
*For questions or issues, refer to this guide first, then check SVN source.*  
*Last Updated: November 15, 2025*
