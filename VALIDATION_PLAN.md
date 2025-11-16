# JavaHL Connector Validation Plan

**Purpose:** Comprehensive validation of all Java class signatures against Apache Subversion 1.14.5 reference implementation and verification of JNI bindings to prevent JVM crashes.

**Version:** 7.0.0.202511151608  
**Reference:** Apache Subversion 1.14.5 JavaHL bindings  
**Date:** 2025-11-16

---

## Overview

This validation plan ensures:
1. **Java Signature Parity:** Every Java class, interface, method, and field matches SVN 1.14.5 reference
2. **JNI Binding Safety:** All native method signatures match C++ JNI calls exactly
3. **ABI Compatibility:** No mismatches that could cause JVM crashes or native library failures

---

## Implementation Inventory

### Total Files: 94 Java Classes

#### Main Package (10 files)
- `ClientException.java` - Exception for client operations
- `ClientNotifyInformation.java` - **FIXED** Constructor signature validated
- `CommitInfo.java` - Result of commit operations
- `CommitItem.java` - Item to be committed
- `CommitItemStateFlags.java` - State flags for commit items
- `ConflictDescriptor.java` - Describes a conflict
- `ConflictResult.java` - Result of conflict resolution
- `DiffSummary.java` - Summary of differences
- `JNIError.java` - JNI-specific errors
- `JNIObject.java` - Base class for JNI objects

#### Core Interfaces (6 files)
- `ISVNClient.java` - Main client interface (primary API)
- `ISVNConfig.java` - Configuration interface
- `ISVNEditor.java` - Editor interface for tree deltas
- `ISVNRemote.java` - Remote repository operations
- `ISVNReporter.java` - Status reporter interface
- `ISVNRepos.java` - Repository operations interface

#### Main Implementations (6 files)
- `SVNClient.java` - **66 native methods** - Primary client implementation
- `SVNRepos.java` - Repository implementation
- `SVNUtil.java` - Utility functions
- `NativeException.java` - Native exception wrapper
- `NativeResources.java` - Native library loader
- `OperationContext.java` - Context for operations
- `ProgressEvent.java` - Progress reporting
- `ReposNotifyInformation.java` - Repository notifications
- `SubversionException.java` - Base exception class

#### Callback Package (29 files)
- `AuthnCallback.java` - Authentication callback
- `BlameCallback.java` - Blame annotation callback
- `BlameLineCallback.java` - Per-line blame callback
- `BlameRangeCallback.java` - Range blame callback
- `ChangelistCallback.java` - Changelist callback
- `ClientNotifyCallback.java` - Client notification callback
- `CommitCallback.java` - Commit progress callback
- `CommitMessageCallback.java` - Commit message provider
- `ConfigEvent.java` - Configuration events
- `ConflictResolverCallback.java` - Conflict resolution callback
- `DiffSummaryCallback.java` - Diff summary callback
- `ImportFilterCallback.java` - Import filter
- `InfoCallback.java` - Info retrieval callback
- `InheritedProplistCallback.java` - Inherited properties callback
- `ListCallback.java` - Directory list callback (deprecated)
- `ListItemCallback.java` - Directory list item callback
- `LogMessageCallback.java` - Log message callback
- `PatchCallback.java` - Patch application callback
- `ProgressCallback.java` - Progress reporting callback
- `ProplistCallback.java` - Property list callback
- `RemoteFileRevisionsCallback.java` - File revisions callback
- `RemoteLocationSegmentsCallback.java` - Location segments callback
- `RemoteStatus.java` - Remote status information
- `ReposFreezeAction.java` - Repository freeze action
- `ReposNotifyCallback.java` - Repository notification callback
- `ReposVerifyCallback.java` - Repository verify callback
- `StatusCallback.java` - Status callback
- `TunnelAgent.java` - SSH tunnel agent
- `UserPasswordCallback.java` - User/password authentication

#### Types Package (26 files)
- `ChangePath.java` - Path changed in revision
- `Checksum.java` - File checksum
- `ConflictVersion.java` - Conflict version info
- `CopySource.java` - Copy operation source
- `Depth.java` - **Enum** - Operation depth (empty, files, immediates, infinity)
- `DiffOptions.java` - Diff operation options
- `DirEntry.java` - Directory entry info
- `ExternalItem.java` - External definition
- `Info.java` - Path information
- `Lock.java` - Lock information
- `LogDate.java` - Log entry date
- `Mergeinfo.java` - Merge tracking info
- `NativeInputStream.java` - JNI input stream
- `NativeOutputStream.java` - JNI output stream
- `NodeKind.java` - **Enum** - Node type (none, file, dir, unknown, symlink)
- `Property.java` - Versioned property
- `Revision.java` - **Complex** - Revision specification (HEAD, BASE, COMMITTED, etc.)
- `RevisionRange.java` - Range of revisions
- `RevisionRangeList.java` - List of revision ranges
- `RuntimeVersion.java` - Runtime library version
- `Status.java` - Working copy status
- `Tristate.java` - **Enum** - Three-state value (true, false, unknown)
- `Version.java` - Version information
- `VersionExtended.java` - Extended version info

#### Remote Package (6 files)
- `CommitEditor.java` - Remote commit editor
- `RemoteFactory.java` - Factory for remote sessions
- `RemoteSession.java` - Remote repository session
- `RetryOpenSession.java` - Retry wrapper for sessions
- `StateReporter.java` - State reporter for remote ops
- `StatusEditor.java` - Status editor

#### Util Package (8 files)
- `ConfigImpl.java` - Configuration implementation
- `ConfigLib.java` - Configuration utilities
- `DiffLib.java` - Diff utilities
- `PropLib.java` - Property utilities
- `RequestChannel.java` - Request channel for tunneling
- `ResponseChannel.java` - Response channel for tunneling
- `SubstLib.java` - Keyword substitution utilities
- `TunnelChannel.java` - Tunnel channel implementation

---

## Validation Checklist

### Phase 1: Java Signature Validation

#### 1.1 SVNClient.java - Native Methods (66 methods)
**Priority: CRITICAL** - All methods use JNI, any mismatch causes JVM crash

**Validation Steps:**
1. Download SVN 1.14.5 source from https://subversion.apache.org/download/
2. Compare our implementation against reference:
   ```powershell
   # Reference file
   code --diff "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java"
   ```

3. Extract all native method declarations:
   ```powershell
   # From our implementation
   Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java" -Pattern "native " | Select-Object Line, LineNumber
   ```

4. Verify each native method signature:
   - Method name (exact match)
   - Return type (primitive, object, array)
   - Parameter count
   - Parameter types (order matters!)
   - Parameter names (for documentation)
   - Throws clauses

**Expected Native Methods (to be extracted from reference):**
```java
// Example format - actual list to be generated from reference
public native void dispose();
public native String getAdminDirectoryName();
public native boolean isAdminDirectory(String name);
// ... (63 more methods)
```

#### 1.2 Callback Interfaces (29 files)
**Priority: HIGH** - Incorrect signatures cause callback failures

**Validation for each callback:**
- Interface methods match reference
- Parameter types correct
- Return types correct
- Exception declarations match

**Critical Callbacks:**
- `ClientNotifyCallback.java` - Used in all operations
- `CommitMessageCallback.java` - Used in commits
- `ConflictResolverCallback.java` - Conflict handling
- `UserPasswordCallback.java` - Authentication

#### 1.3 Type Classes (26 files)
**Priority: HIGH** - Used by JNI layer for data marshaling

**Special Attention:**
- **Enums** (Depth, NodeKind, Tristate) - Ordinal values must match C++ enums
- **Revision.java** - Complex class with multiple constructors
- **Status.java** - Large class with many fields
- **Info.java** - Complex nested information

**Enum Validation:**
```java
// Depth.java - Verify ordinal order matches C++ svn_depth_t
public enum Depth {
    unknown,    // 0 - svn_depth_unknown
    exclude,    // 1 - svn_depth_exclude
    empty,      // 2 - svn_depth_empty
    files,      // 3 - svn_depth_files
    immediates, // 4 - svn_depth_immediates
    infinity    // 5 - svn_depth_infinity
}
```

#### 1.4 Core Interfaces (6 files)
**Priority: MEDIUM** - Used for polymorphism

- Verify method signatures
- Check default methods (Java 8+)
- Validate inheritance hierarchy

#### 1.5 Exception Classes (4 files)
**Priority: MEDIUM** - Error handling

- `ClientException.java` - Verify constructors
- `SubversionException.java` - Base class validation
- `NativeException.java` - JNI exception wrapper
- `JNIError.java` - JNI error conditions

---

### Phase 2: JNI Binding Validation

**Objective:** Extract all JNI method calls from C++ native code and verify they match Java signatures.

#### 2.1 Locate C++ Native Source
```
<svn-source>\subversion\bindings\javahl\native\
```

**Key Files:**
- `SVNClient.cpp` - Main client JNI implementations
- `JNIUtil.cpp` - JNI utility functions
- `org_apache_subversion_javahl_*.cpp` - Generated JNI stubs
- `*.h` - JNI header files

#### 2.2 Extract JNI FindClass Calls

**Pattern:** `jclass cls = env->FindClass("org/apache/subversion/javahl/...")`

**Validation:**
- Class path matches Java package structure
- Class exists in our implementation
- No typos in class names

**PowerShell Script:**
```powershell
# Search for FindClass calls in C++ code
Select-String -Path "<svn-source>\subversion\bindings\javahl\native\*.cpp" -Pattern 'FindClass\s*\(\s*"([^"]+)"' | ForEach-Object {
    if ($_.Matches.Groups.Count -gt 1) {
        $_.Matches.Groups[1].Value
    }
} | Sort-Object -Unique
```

#### 2.3 Extract JNI GetMethodID Calls

**Pattern:** `jmethodID mid = env->GetMethodID(cls, "methodName", "(Ljava/lang/String;)V")`

**Validation:**
- Method name exists in Java class
- JNI signature matches Java method signature
- Return type matches
- Parameter types match in order

**JNI Signature Encoding:**
```
Z - boolean          L<classname>; - object (e.g., Ljava/lang/String;)
B - byte             [<type> - array (e.g., [I for int[], [Ljava/lang/String; for String[])
C - char             V - void
S - short            (params)returntype - method signature
I - int
J - long
F - float
D - double
```

**PowerShell Script:**
```powershell
# Extract GetMethodID calls with signatures
Select-String -Path "<svn-source>\subversion\bindings\javahl\native\*.cpp" -Pattern 'GetMethodID\s*\([^,]+,\s*"([^"]+)"\s*,\s*"([^"]+)"' | ForEach-Object {
    $methodName = $_.Matches.Groups[1].Value
    $signature = $_.Matches.Groups[2].Value
    Write-Output "Method: $methodName, Signature: $signature, File: $($_.Path), Line: $($_.LineNumber)"
}
```

#### 2.4 Extract JNI GetFieldID Calls

**Pattern:** `jfieldID fid = env->GetFieldID(cls, "fieldName", "Ljava/lang/String;")`

**Validation:**
- Field exists in Java class
- Field type matches JNI signature
- Field accessibility (public/private doesn't matter for JNI, but check final/static)

#### 2.5 Extract JNI CallXXXMethod Calls

**Patterns:**
- `CallVoidMethod(obj, mid, ...)`
- `CallObjectMethod(obj, mid, ...)`
- `CallIntMethod(obj, mid, ...)`
- etc.

**Validation:**
- Return type matches method signature
- Arguments match parameter types

#### 2.6 Native Method Name Mangling

**JNI Name Mangling Rules:**
```
Java: void someMethod(String arg)
C++:  Java_org_apache_subversion_javahl_SVNClient_someMethod(JNIEnv *env, jobject obj, jstring arg)
```

**Verify:**
- Native method declarations in Java match C++ function names
- Parameter counts align (JNIEnv* and jobject/jclass don't count)

---

### Phase 3: Critical Validation Points

#### 3.1 Constructor Validation
**Already Fixed:** `ClientNotifyInformation.java` constructor

**Other Critical Constructors:**
- `Revision.java` - Multiple constructors for different revision types
- `Status.java` - Complex constructor
- `Info.java` - Nested object construction
- `Lock.java` - Lock information construction

**JNI Constructor Calls:**
```cpp
// Pattern in C++
jmethodID constructor = env->GetMethodID(cls, "<init>", "(params)V");
jobject obj = env->NewObject(cls, constructor, args...);
```

#### 3.2 Enum Ordinal Validation
**CRITICAL:** Enum ordinal values must match C++ enum values exactly

**Files to Check:**
1. `Depth.java` vs `svn_depth_t` in `svn_types.h`
2. `NodeKind.java` vs `svn_node_kind_t`
3. `Tristate.java` vs custom tristate implementation

**Validation Script:**
```powershell
# Extract enum definitions from Java
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\types\*.java" -Pattern "public enum|^\s+\w+[,;]" -Context 0,5
```

#### 3.3 Array Handling
**JNI Arrays require special handling**

**Java Arrays in Native Code:**
- `String[]` → `jobjectArray` → `GetObjectArrayElement()`
- `int[]` → `jintArray` → `GetIntArrayElements()`
- `byte[]` → `jbyteArray` → `GetByteArrayElements()`

**Validation:**
- Array parameters declared correctly in Java
- C++ code uses correct JNI array functions
- Array release functions called (no memory leaks)

#### 3.4 Exception Propagation

**Java Exceptions from Native:**
```cpp
// C++ throws Java exception
env->ThrowNew(exceptionClass, "Error message");
```

**Validation:**
- Exception classes exist in Java
- Exception constructors match JNI calls
- Checked exceptions declared in Java methods

---

### Phase 4: Automated Validation Scripts

#### Script 1: Class Inventory Comparison
```powershell
# Compare class lists between reference and implementation
$refClasses = Get-ChildItem "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\*.java" -Recurse | ForEach-Object { $_.FullName -replace '.*\\org\\apache\\', '' }
$ourClasses = Get-ChildItem "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\*.java" -Recurse | ForEach-Object { $_.FullName -replace '.*\\org\\apache\\', '' }

Compare-Object $refClasses $ourClasses | Format-Table -AutoSize
```

#### Script 2: Method Count Validation
```powershell
# Count methods in each class
Get-ChildItem "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\*.java" -Recurse | ForEach-Object {
    $methodCount = (Select-String -Path $_ -Pattern '^\s*(public|protected|private).*\(.*\)' | Measure-Object).Count
    [PSCustomObject]@{
        Class = $_.Name
        Methods = $methodCount
    }
} | Sort-Object Methods -Descending | Format-Table -AutoSize
```

#### Script 3: Native Method Extraction
```powershell
# Extract all native method signatures
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java" -Pattern 'native .*\(' | ForEach-Object {
    $_.Line.Trim()
} | Out-File "native_methods.txt"
```

#### Script 4: JNI Signature Validator
```powershell
# Extract JNI method signatures from C++ code
$jniCalls = Select-String -Path "<svn-source>\subversion\bindings\javahl\native\*.cpp" -Pattern 'Get(Method|Static Method)ID\s*\([^,]+,\s*"([^"]+)"\s*,\s*"([^"]+)"'

$jniCalls | ForEach-Object {
    [PSCustomObject]@{
        File = $_.Filename
        Line = $_.LineNumber
        Method = $_.Matches.Groups[2].Value
        Signature = $_.Matches.Groups[3].Value
    }
} | Export-Csv "jni_signatures.csv" -NoTypeInformation
```

---

## Validation Execution Plan

### Step-by-Step Process

**Prerequisites:**
1. Download Apache Subversion 1.14.5 source code
2. Extract to `<svn-source>` directory
3. Install PowerShell 5.1+ or PowerShell Core
4. Install VS Code with Java extensions (for diff viewing)

**Phase 1: Quick Inventory Check (30 minutes)**
1. Run class inventory comparison script
2. Identify any missing or extra classes
3. Generate class count report

**Phase 2: SVNClient Deep Dive (2 hours)**
1. Compare SVNClient.java files side-by-side
2. Extract all 66 native method signatures
3. Create reference table of methods
4. Verify each method signature exactly

**Phase 3: Callback Validation (2 hours)**
1. For each of 29 callback files:
   - Open side-by-side diff
   - Verify interface methods match
   - Check parameter types
   - Validate return types

**Phase 4: Type Class Validation (3 hours)**
1. Validate enum ordinal values (Depth, NodeKind, Tristate)
2. Check complex classes (Revision, Status, Info)
3. Verify constructors and field types
4. Validate serialization compatibility

**Phase 5: JNI Extraction (4 hours)**
1. Run FindClass extraction script
2. Run GetMethodID extraction script
3. Run GetFieldID extraction script
4. Create JNI call reference database

**Phase 6: Cross-Reference Validation (3 hours)**
1. Match each native Java method to C++ JNI function
2. Verify JNI signatures match Java signatures
3. Check constructor calls
4. Validate enum conversions

**Phase 7: Edge Case Testing (2 hours)**
1. Array parameter handling
2. Null parameter handling
3. Exception propagation
4. Callback invocation from native code

**Total Estimated Time: 16 hours**

---

## Known Validation Results

### ✅ Already Validated

#### ClientNotifyInformation.java Constructor
**Issue Found:** Constructor signature mismatch  
**Symptoms:** JVM crash on move/rename operations  
**Root Cause:** Constructor parameter order didn't match JNI NewObject call  
**Fix Applied:** Corrected constructor signature to match C++ JNI call  
**Validation:** All SVN operations (checkout, update, commit, move, rename) working  
**Status:** ✅ FIXED and VERIFIED

---

## Validation Report Template

### Class: [ClassName.java]

**Package:** org.apache.subversion.javahl.[package]  
**Type:** [Interface/Class/Enum]  
**Complexity:** [Low/Medium/High/Critical]  
**Native Methods:** [count]

#### Signature Comparison
- [ ] Class declaration matches reference
- [ ] All methods present
- [ ] Method signatures match
- [ ] Field types match
- [ ] Constructor signatures match
- [ ] Exception declarations match

#### JNI Validation (if applicable)
- [ ] Native method names match C++ functions
- [ ] JNI signatures extracted from C++
- [ ] Parameter types align
- [ ] Return types align
- [ ] Constructor calls validated

#### Issues Found
[List any mismatches]

#### Changes Required
[List any fixes needed]

#### Status
- [ ] Pending Review
- [ ] Validated - No Issues
- [ ] Validated - Issues Found
- [ ] Fixed and Re-validated

---

## Priority Classification

### CRITICAL (Must validate immediately)
1. **SVNClient.java** - 66 native methods, primary API
2. **ClientNotifyInformation.java** - Already fixed, but verify others
3. **Enum classes** - Ordinal values must match C++ exactly
4. **Constructor-heavy classes** - Revision, Status, Info

### HIGH (Validate before release)
5. All callback interfaces (29 files)
6. Core type classes (26 files)
7. Exception classes

### MEDIUM (Validate for completeness)
8. Utility classes (8 files)
9. Remote package classes (6 files)
10. Core interfaces (6 files)

---

## Success Criteria

### Validation Complete When:
1. ✅ All 94 Java classes compared against reference
2. ✅ All class/method/field signatures match exactly
3. ✅ All JNI method calls extracted from C++ code
4. ✅ All JNI signatures cross-referenced with Java methods
5. ✅ All enum ordinal values verified against C++ enums
6. ✅ All constructors validated against JNI NewObject calls
7. ✅ No signature mismatches found
8. ✅ Comprehensive validation report generated

### Risk Mitigation:
- Any mismatch found → File GitHub issue immediately
- High/Critical issues → Fix before release
- Medium issues → Document and schedule fix
- Low issues → Document for future reference

---

## Maintenance

**This validation should be performed:**
- Before each major release
- When upgrading to new SVN version (e.g., 1.15.x)
- After any JNI-related code changes
- After any constructor/signature changes
- When investigating JVM crashes
- Annually as part of code review

**Document Updates:**
- Add validation results to this document
- Update MAINTENANCE_GUIDE.md with findings
- Create GitHub issues for any problems
- Update DEVELOPMENT_SESSION_SUMMARY.md with fixes

---

## Next Steps

1. **Obtain SVN 1.14.5 Source Code**
   ```powershell
   # Download from https://subversion.apache.org/download/
   # Extract to your <svn-source> directory
   ```

2. **Run Phase 1 Scripts**
   ```powershell
   # Execute class inventory comparison
   # Review results
   ```

3. **Begin SVNClient.java Deep Dive**
   ```powershell
   code --diff "<svn-source>\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java"
   ```

4. **Extract JNI Signatures**
   ```powershell
   # Run JNI extraction scripts
   # Build reference database
   ```

5. **Systematic Validation**
   - Work through each priority level
   - Document findings
   - Fix any issues
   - Re-validate fixes

---

## References

- **SVN JavaHL Bindings:** https://subversion.apache.org/docs/api/latest/javahl/
- **JNI Specification:** https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/jniTOC.html
- **JNI Type Signatures:** https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
- **SVN 1.14.5 Download:** https://subversion.apache.org/download.cgi
- **Our Implementation:** org.polarion.eclipse.team.svn.connector.javahl21/src/

---

*End of Validation Plan*
