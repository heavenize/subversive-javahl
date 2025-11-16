# Validation Plan

**Project:** Subversive SVN 1.14 JavaHL Connector  
**Purpose:** Ensure code quality and reference compliance  
**Version:** 1.0

---

## Table of Contents

1. [Pre-Commit Validation](#pre-commit-validation)
2. [Build Validation](#build-validation)
3. [Reference Compliance](#reference-compliance)
4. [Runtime Validation](#runtime-validation)
5. [Performance Validation](#performance-validation)
6. [Automated Validation Scripts](#automated-validation-scripts)

---

## Pre-Commit Validation

### Code Quality Checks

**Before committing ANY Java changes:**

```powershell
# 1. Verify no syntax errors
cd org.polarion.eclipse.team.svn.connector.javahl21
mvn compile
# Expected: BUILD SUCCESS

# 2. Check file count
$javaFiles = Get-ChildItem src\org\apache\subversion\javahl -Recurse -Filter *.java
Write-Host "Java files: $($javaFiles.Count)"
# Expected: 92 files

# 3. Verify no local modifications to reference files
git status src/org/apache/subversion/javahl/
# Expected: No unstaged changes (unless intentional)
```

### Documentation Checks

```powershell
# Verify all docs up to date
ls *.md
# Should include:
# - README.md
# - CHANGELOG.md
# - PROJECT_SUMMARY.md
# - MAINTENANCE_GUIDE.md
# - VALIDATION_PLAN.md
# - MIGRATION_TO_REFERENCE.md
# - LOCAL_PATHS.md
# - history\MIGRATION_HISTORY.md
```

### Commit Checklist

- [ ] All Java files compile
- [ ] No unintended changes to reference JavaHL files
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Build successful
- [ ] Basic functionality tested

---

## Build Validation

### Quick Build Test

**Purpose:** Verify connector builds successfully

```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 7-10 seconds
```

**Verification:**
```powershell
# Check output exists
ls target\*.jar
# Expected: org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar

# Check file size
$jar = Get-Item target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar
$jar.Length / 1KB
# Expected: ~354 KB
```

### Full Build Test

**Purpose:** Verify complete update site builds

```powershell
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1
```

**Expected Output:**
```
[INFO] Reactor Summary:
[INFO] polarion-javahl ...................................... SUCCESS [  0.123 s]
[INFO] org.polarion.eclipse.team.svn.connector.javahl21 ..... SUCCESS [  7.812 s]
[INFO] org.polarion.eclipse.team.svn.connector.javahl21.win64 SUCCESS [  0.456 s]
[INFO] org.polarion.eclipse.team.svn.connector.javahl21.feature SUCCESS [  0.234 s]
[INFO] org.polarion.eclipse.team.svn.connector.javahl21.site . SUCCESS [  1.567 s]
[INFO] BUILD SUCCESS
[INFO] Total time: 10-12 seconds
```

**Verification:**
```powershell
# Check update site
cd org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository
ls
# Expected:
# - artifacts.jar
# - content.jar
# - plugins\ (with JARs)
# - features\ (with JARs)

# Verify plugin exists
ls plugins\org.polarion.eclipse.team.svn.connector.javahl21*.jar
# Expected: File exists

# Verify feature exists
ls features\org.polarion.eclipse.team.svn.connector.javahl21.feature*.jar
# Expected: File exists
```

---

## Reference Compliance

### File Comparison Validation

**Purpose:** Ensure all JavaHL files match SVN 1.14.5 reference

**Prerequisites:**
- Reference source at: `D:\Work\code\subversion-1.14.5`

**Validation Script:**

```powershell
# validate-reference-compliance.ps1

$ourPath = "D:\users\Jose\development\polarion-javahl\org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl"
$refPath = "D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src\org\apache\subversion\javahl"

Write-Host "=== Reference Compliance Validation ===" -ForegroundColor Cyan
Write-Host ""

# Get all Java files
$ourFiles = Get-ChildItem $ourPath -Recurse -Filter "*.java" | Sort-Object Name
$refFiles = Get-ChildItem $refPath -Recurse -Filter "*.java" | Sort-Object Name

Write-Host "Our files: $($ourFiles.Count)"
Write-Host "Reference files: $($refFiles.Count)"

if ($ourFiles.Count -ne $refFiles.Count) {
    Write-Host "❌ FAILED: File count mismatch!" -ForegroundColor Red
    exit 1
}

# Compare each file
$differences = 0
$checked = 0

foreach ($ourFile in $ourFiles) {
    $relativePath = $ourFile.FullName.Substring($ourPath.Length)
    $refFile = Get-Item "$refPath$relativePath" -ErrorAction SilentlyContinue
    
    if (-not $refFile) {
        Write-Host "❌ Missing in reference: $relativePath" -ForegroundColor Red
        $differences++
        continue
    }
    
    $ourHash = Get-FileHash $ourFile.FullName -Algorithm MD5
    $refHash = Get-FileHash $refFile.FullName -Algorithm MD5
    
    if ($ourHash.Hash -ne $refHash.Hash) {
        Write-Host "❌ Different: $relativePath" -ForegroundColor Red
        Write-Host "   Our size: $($ourFile.Length) bytes"
        Write-Host "   Ref size: $($refFile.Length) bytes"
        $differences++
    }
    
    $checked++
}

Write-Host ""
Write-Host "Files checked: $checked"
Write-Host "Differences: $differences"

if ($differences -eq 0) {
    Write-Host "✅ PASSED: All files match reference!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "❌ FAILED: $differences files differ from reference!" -ForegroundColor Red
    exit 1
}
```

**Usage:**
```powershell
.\validate-reference-compliance.ps1
```

**Expected Output:**
```
=== Reference Compliance Validation ===

Our files: 92
Reference files: 92
Files checked: 92
Differences: 0
✅ PASSED: All files match reference!
```

### Signature Validation

**Purpose:** Verify JNI method signatures match native library

**Key Files to Check:**
- `ISVNClient.java` - 83 throws clauses, 192 methods
- `SVNClient.java` - 77 native methods
- `SVNRepos.java` - 22 throws clauses

**Manual Validation:**

```powershell
# Check for "native" keyword
Select-String -Path "src\org\apache\subversion\javahl\SVNClient.java" -Pattern "native" | Measure-Object
# Expected: 77 matches (77 native methods)

# Check for "throws" keyword
Select-String -Path "src\org\apache\subversion\javahl\ISVNClient.java" -Pattern "throws" | Measure-Object
# Expected: ~83 matches

# Verify no try-catch masking exceptions
Select-String -Path "src\org\apache\subversion\javahl\**\*.java" -Pattern "catch.*ParseException" -Recurse
# Expected: 0 matches (should not catch ParseException)
```

### Throws Clause Validation

**Critical Check:** Ensure all JNI methods declare correct throws clauses

**Validation Commands:**

```powershell
# ISVNClient.java - Check critical methods
Select-String -Path "src\org\apache\subversion\javahl\ISVNClient.java" -Pattern "void checkout.*throws ClientException"
Select-String -Path "src\org\apache\subversion\javahl\ISVNClient.java" -Pattern "void commit.*throws ClientException"
Select-String -Path "src\org\apache\subversion\javahl\ISVNClient.java" -Pattern "CommitInfo doImport.*throws ClientException"

# SVNClient.java - Check native methods
Select-String -Path "src\org\apache\subversion\javahl\SVNClient.java" -Pattern "private native.*throws ClientException"

# SVNRepos.java - Check repository methods
Select-String -Path "src\org\apache\subversion\javahl\SVNRepos.java" -Pattern "public.*throws"
```

**Expected:** All methods that interact with native code should declare `throws ClientException` (and ParseException where appropriate).

---

## Runtime Validation

### Installation Test

**Purpose:** Verify connector installs correctly in Eclipse

**Steps:**

1. **Start Eclipse:**
   ```powershell
   cd C:\bin\eclipse
   .\eclipse.exe -clean
   ```

2. **Install Connector:**
   - Help → Install New Software
   - Add → Local
   - Location: `D:\users\Jose\development\polarion-javahl\org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`
   - Name: "Local Connector Build"
   - Select "Subversive SVN 1.14 JavaHL Connector"
   - Finish installation
   - Restart Eclipse

3. **Verify Installation:**
   - Window → Preferences → Team → SVN
   - SVN Connector tab
   - Expected: "JavaHL 1.14.x" appears in list
   - Select it and click Apply

4. **Check Connector Status:**
   - Should show: "Connected"
   - Should show: Version information

### Operation Testing

**Purpose:** Verify all SVN operations work correctly

**Test Repository Setup:**
```powershell
# Create test repository
svnadmin create D:\temp\test-repo

# Checkout
# In Eclipse: File → Import → SVN → Checkout
```

**Test Cases:**

#### 1. Checkout Test
```
Action: Checkout a project
Expected: ✓ Project checked out successfully
Validation: Files appear in workspace
```

#### 2. Commit Test
```
Action: Modify file → Team → Commit
Expected: ✓ File committed
Validation: No NoSuchMethodError
Notes: THIS WAS THE ORIGINAL BUG - Critical test!
```

#### 3. Update Test
```
Action: Team → Update
Expected: ✓ Update successful
Validation: Status shows up-to-date
```

#### 4. Add Test
```
Action: New file → Team → Add to Version Control
Expected: ✓ File added
Validation: File shows as added
```

#### 5. Status Test
```
Action: Team → Synchronize with Repository
Expected: ✓ Status displayed
Validation: Modified/added/deleted files shown correctly
```

#### 6. Move Test
```
Action: Team → Move/Rename
Expected: ✓ Move successful
Validation: SVN history preserved
```

#### 7. Branch Test
```
Action: Team → Branch/Tag
Expected: ✓ Branch created
Validation: Branch visible in repository
```

#### 8. Merge Test
```
Action: Team → Merge
Expected: ✓ Merge successful
Validation: Changes merged correctly
```

#### 9. Revert Test
```
Action: Team → Revert
Expected: ✓ Changes reverted
Validation: File restored to repository version
```

#### 10. Diff Test
```
Action: Team → Show Differences
Expected: ✓ Diff displayed
Validation: Changes highlighted correctly
```

**Test Result Template:**

```
Date: _______________
Tester: _______________
Eclipse Version: _______________
Connector Version: _______________

Test Results:
[ ] Checkout - PASS / FAIL
[ ] Commit - PASS / FAIL
[ ] Update - PASS / FAIL
[ ] Add - PASS / FAIL
[ ] Status - PASS / FAIL
[ ] Move - PASS / FAIL
[ ] Branch - PASS / FAIL
[ ] Merge - PASS / FAIL
[ ] Revert - PASS / FAIL
[ ] Diff - PASS / FAIL

Notes:
_________________________________
```

### Error Log Validation

**Purpose:** Verify no errors in Eclipse log

**Steps:**

1. **Open Error Log:**
   - Window → Show View → Error Log

2. **Check for Errors:**
   - Look for entries with connector name
   - Check severity: Error > Warning > Info

3. **Common Issues to Check:**
   - NoSuchMethodError (should NOT appear)
   - UnsatisfiedLinkError (should NOT appear)
   - ClassNotFoundException (should NOT appear)
   - NullPointerException (investigate if appears)

4. **Log Location:**
   ```
   <workspace>/.metadata/.log
   ```

---

## Performance Validation

### Operation Timing

**Purpose:** Ensure operations complete in reasonable time

**Benchmark Tests:**

```
Test Repository Size: 1000 files, 100 MB

Operation          | Expected Time | Acceptable Range
-------------------|---------------|------------------
Checkout           | 10-30s        | < 60s
Commit (1 file)    | 1-2s          | < 5s
Update (no changes)| 1-2s          | < 5s
Status             | 1-3s          | < 10s
Diff (1 file)      | 0.5-1s        | < 3s
```

**Performance Test Script:**

```powershell
# performance-test.ps1
$results = @()

# Measure operation
$start = Get-Date
# [Perform operation in Eclipse]
$end = Get-Date
$duration = ($end - $start).TotalSeconds

$results += [PSCustomObject]@{
    Operation = "Checkout"
    Duration = $duration
    Status = if ($duration -lt 60) { "PASS" } else { "FAIL" }
}

# Repeat for each operation
# Output results
$results | Format-Table
```

### Memory Usage

**Purpose:** Verify no memory leaks

**Test:**
1. Start Eclipse
2. Note initial memory usage
3. Perform 100 operations
4. Note final memory usage

**Expected:**
- Memory increase: < 100 MB
- No OutOfMemoryError

**Monitor:**
```
Eclipse: Help → About → Installation Details → Configuration
Look for: -Xmx setting
```

---

## Automated Validation Scripts

### Complete Validation Script

```powershell
# complete-validation.ps1

Write-Host "=== Complete Validation Script ===" -ForegroundColor Cyan
Write-Host ""

$passed = 0
$failed = 0

# Test 1: Build
Write-Host "Test 1: Build Validation..." -ForegroundColor Yellow
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Build: PASSED" -ForegroundColor Green
    $passed++
} else {
    Write-Host "❌ Build: FAILED" -ForegroundColor Red
    $failed++
}

# Test 2: File Count
Write-Host "Test 2: File Count Validation..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem "org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl" -Recurse -Filter *.java
if ($javaFiles.Count -eq 92) {
    Write-Host "✅ File Count: PASSED (92 files)" -ForegroundColor Green
    $passed++
} else {
    Write-Host "❌ File Count: FAILED ($($javaFiles.Count) files, expected 92)" -ForegroundColor Red
    $failed++
}

# Test 3: Update Site
Write-Host "Test 3: Update Site Validation..." -ForegroundColor Yellow
$updateSitePath = "org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository"
if ((Test-Path "$updateSitePath\artifacts.jar") -and (Test-Path "$updateSitePath\content.jar")) {
    Write-Host "✅ Update Site: PASSED" -ForegroundColor Green
    $passed++
} else {
    Write-Host "❌ Update Site: FAILED" -ForegroundColor Red
    $failed++
}

# Test 4: Reference Compliance
Write-Host "Test 4: Reference Compliance..." -ForegroundColor Yellow
# Run reference validation script
.\validate-reference-compliance.ps1 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Reference Compliance: PASSED" -ForegroundColor Green
    $passed++
} else {
    Write-Host "❌ Reference Compliance: FAILED" -ForegroundColor Red
    $failed++
}

# Summary
Write-Host ""
Write-Host "=== Validation Summary ===" -ForegroundColor Cyan
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red

if ($failed -eq 0) {
    Write-Host ""
    Write-Host "✅ ALL VALIDATIONS PASSED" -ForegroundColor Green
    exit 0
} else {
    Write-Host ""
    Write-Host "❌ VALIDATION FAILED" -ForegroundColor Red
    exit 1
}
```

**Usage:**
```powershell
.\complete-validation.ps1
```

### Quick Validation (Pre-Commit)

```powershell
# quick-validation.ps1
Write-Host "Quick Validation..." -ForegroundColor Cyan

# Build only
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Quick validation PASSED" -ForegroundColor Green
} else {
    Write-Host "❌ Quick validation FAILED" -ForegroundColor Red
}
```

---

## Validation Schedule

### Before Every Commit
- [ ] Quick build test
- [ ] File count check
- [ ] Documentation updated

### Before Every Release
- [ ] Complete validation script
- [ ] Reference compliance check
- [ ] Full build test
- [ ] Manual operation testing
- [ ] Performance validation
- [ ] Error log review

### Monthly
- [ ] Complete validation
- [ ] Extended operation testing
- [ ] Performance benchmarks

### After Major Changes
- [ ] Full validation suite
- [ ] Extensive manual testing
- [ ] Performance regression testing
- [ ] Multiple Eclipse version testing

---

**Document Version:** 1.0  
**Last Updated:** November 16, 2025  
**Next Review:** February 16, 2026
