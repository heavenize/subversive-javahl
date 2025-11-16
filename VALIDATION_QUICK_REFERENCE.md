# JavaHL Validation Quick Reference

**Purpose:** Quick guide to validate Java signatures against SVN 1.14.5 reference

---

## Our Implementation: 94 Java Classes

### By Package:
- **Main:** 10 core classes (SVNClient, ISVNClient, exceptions, etc.)
- **Callback:** 29 callback interfaces
- **Types:** 26 type classes (enums, data structures)
- **Remote:** 6 remote operation classes
- **Util:** 8 utility classes

### Critical Classes (Validate First):
1. **SVNClient.java** - 66 native methods (PRIMARY API)
2. **ClientNotifyInformation.java** - ✅ Already fixed (constructor signature)
3. **Depth.java** - Enum ordinals must match C++ svn_depth_t
4. **NodeKind.java** - Enum ordinals must match C++ svn_node_kind_t
5. **Tristate.java** - Enum ordinals
6. **Revision.java** - Complex constructors
7. **Status.java** - Large class with many fields

---

## Automated Validation

### Run Full Validation:
```powershell
.\validate-javahl.ps1 -SvnSourcePath "D:\Work\code\subversion-1.14.5"
```

**Output:**
- `validation-reports/class-inventory.md` - Missing/extra classes
- `validation-reports/svnclient-native-methods.md` - Native method comparison
- `validation-reports/jni-calls.md` - JNI calls from C++ code
- `validation-reports/method-counts.md` - Method count per class
- CSV files for detailed analysis

---

## Manual Validation Steps

### 1. Compare SVNClient.java
```powershell
code --diff "D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src\org\apache\subversion\javahl\SVNClient.java" "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java"
```

### 2. Extract Native Methods
```powershell
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java" -Pattern "native " | Select-Object Line, LineNumber
```

### 3. Compare Callback Interface
```powershell
code --diff "D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src\org\apache\subversion\javahl\callback\ClientNotifyCallback.java" "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\callback\ClientNotifyCallback.java"
```

### 4. Validate Enum Ordinals
```powershell
# Check Depth enum
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\types\Depth.java" -Pattern "^\s+\w+[,;]"
```

---

## JNI Signature Reference

### JNI Type Encoding:
```
Z - boolean          L<classname>; - object (e.g., Ljava/lang/String;)
B - byte             [<type> - array (e.g., [I for int[])
C - char             V - void
S - short            (params)returntype - method signature
I - int
J - long
F - float
D - double
```

### Example Conversions:
```java
// Java: void method(String arg)
// JNI:  (Ljava/lang/String;)V

// Java: int[] getArray(boolean flag)
// JNI:  (Z)[I

// Java: void callback(String path, int action)
// JNI:  (Ljava/lang/String;I)V
```

---

## Known Issues

### ✅ Fixed: ClientNotifyInformation Constructor
**Issue:** Constructor parameter order mismatch  
**Impact:** JVM crash on move/rename operations  
**Fix:** Matched constructor signature to JNI NewObject call  
**Validation:** All SVN operations working

---

## Validation Checklist

### Before Each Release:
- [ ] Run `validate-javahl.ps1` script
- [ ] Review class inventory for changes
- [ ] Compare SVNClient.java native methods (66 total)
- [ ] Validate callback interfaces (29 files)
- [ ] Check enum ordinals (Depth, NodeKind, Tristate)
- [ ] Verify constructor signatures
- [ ] Cross-reference JNI calls from C++ code
- [ ] Test all SVN operations (checkout, update, commit, move, rename, etc.)

### When Upgrading SVN Version:
- [ ] Download new SVN source
- [ ] Run full validation against new reference
- [ ] Update native library DLLs
- [ ] Rebuild and test

---

## Quick Commands

### Count Our Classes:
```powershell
(Get-ChildItem -Recurse "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\*.java").Count
```

### List Native Methods:
```powershell
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\SVNClient.java" -Pattern "native " | Measure-Object
```

### Find All Enums:
```powershell
Select-String -Path "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\types\*.java" -Pattern "public enum"
```

### Compare Package Structure:
```powershell
Get-ChildItem -Recurse "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl\" -Directory | Select-Object Name
```

---

## Priority Order

1. **CRITICAL** - SVNClient.java (all 66 native methods)
2. **HIGH** - Callback interfaces (29 files) - incorrect signatures break callbacks
3. **HIGH** - Enums (Depth, NodeKind, Tristate) - wrong ordinals cause crashes
4. **MEDIUM** - Type classes (Status, Info, Revision) - complex constructors
5. **LOW** - Utility classes - less likely to cause issues

---

## References

- **Full Plan:** VALIDATION_PLAN.md
- **Validation Script:** validate-javahl.ps1
- **Maintenance Guide:** MAINTENANCE_GUIDE.md
- **SVN JavaHL API:** https://subversion.apache.org/docs/api/latest/javahl/
- **JNI Specification:** https://docs.oracle.com/javase/8/docs/technotes/guides/jni/

---

*For detailed validation procedures, see VALIDATION_PLAN.md*
