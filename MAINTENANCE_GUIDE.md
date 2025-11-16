# Maintenance Guide

**Project:** Subversive SVN 1.14 JavaHL Connector  
**Version:** 7.0.0.202511162055  
**Last Updated:** November 16, 2025

---

## Table of Contents

1. [Regular Maintenance Tasks](#regular-maintenance-tasks)
2. [Building the Project](#building-the-project)
3. [Testing Procedures](#testing-procedures)
4. [Updating Dependencies](#updating-dependencies)
5. [Updating to New SVN Versions](#updating-to-new-svn-versions)
6. [Troubleshooting](#troubleshooting)
7. [Release Process](#release-process)

---

## Regular Maintenance Tasks

### Weekly Checks

**Build Verification:**
```powershell
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1
```

Expected: BUILD SUCCESS in ~10 seconds

**Update Site Validation:**
- Check `org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`
- Verify `artifacts.jar` and `content.jar` exist
- Verify `plugins/` and `features/` directories populated

### Monthly Checks

**Dependency Versions:**
- Eclipse Platform: Check for updates to 2024-12
- Subversive: Check for updates to 5.1.x
- Maven/Tycho: Check for updates

**Documentation Review:**
- Verify all paths in `LOCAL_PATHS.md` are still valid
- Update `PROJECT_SUMMARY.md` if changes made
- Update `CHANGELOG.md` for any fixes

### Quarterly Checks

**SVN Version:**
- Check Apache Subversion for new 1.14.x releases
- Review release notes for critical fixes
- Consider updating if security issues fixed

**Native Libraries:**
- Verify svnjavahl-1.dll is latest 1.14.x
- Check for OpenSSL security updates
- Update if necessary

---

## Building the Project

### Prerequisites

**Required Software:**
- Java JDK 21 (or later LTS)
- Apache Maven 3.9.x
- Git

**Environment Variables:**
```powershell
JAVA_HOME=C:\bin\jdk-21
MAVEN_HOME=C:\bin\maven-3.9.11
PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;...
```

### Build Commands

**Quick Build (Connector only):**
```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1
```

Output: `target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar`

**Full Build (All modules + Update Site):**
```powershell
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1
```

Output:
- Connector JAR
- Native libraries JAR
- Feature JAR
- Update site at `org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`

**Clean Build:**
```powershell
mvn clean
.\build-updatesite.ps1
```

### Build Verification

**Check Build Output:**
```powershell
# Should show BUILD SUCCESS
ls org.polarion.eclipse.team.svn.connector.javahl21\target\*.jar
ls org.polarion.eclipse.team.svn.connector.javahl21.win64\target\*.jar
ls org.polarion.eclipse.team.svn.connector.javahl21.feature\target\*.jar
```

**Verify Update Site:**
```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository
ls
```

Should contain:
- `artifacts.jar`
- `content.jar`
- `plugins/` folder with JARs
- `features/` folder with JARs

---

## Testing Procedures

### Manual Testing in Eclipse

**1. Install from Local Update Site**

In Eclipse:
1. Help → Install New Software
2. Add → Local → Browse to `org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`
3. Select "Subversive SVN 1.14 JavaHL Connector"
4. Install and restart Eclipse

**2. Configure Subversive**

1. Window → Preferences → Team → SVN
2. SVN Connector: Select "JavaHL 1.14.x"
3. Apply and Close

**3. Test Operations**

Create test workspace with SVN repository:

**Checkout:**
```
File → Import → SVN → Checkout Projects from SVN
URL: [your-test-repo]
Expected: ✓ Success
```

**Commit:**
```
1. Modify a file
2. Right-click → Team → Commit
3. Enter commit message
Expected: ✓ File committed
```

**Update:**
```
Right-click project → Team → Update
Expected: ✓ Update successful
```

**Add:**
```
1. Create new file
2. Right-click → Team → Add to Version Control
Expected: ✓ File added
```

**Move/Rename:**
```
Right-click file → Team → Move/Rename
Expected: ✓ Operation successful
```

**Status:**
```
Right-click project → Team → Synchronize with Repository
Expected: ✓ Status shown correctly
```

**Branch/Merge:**
```
Right-click project → Team → Branch/Tag
Expected: ✓ Branch created
```

### Automated Testing

**Build Test:**
```powershell
.\build-updatesite.ps1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Build test PASSED"
} else {
    Write-Host "✗ Build test FAILED"
}
```

**File Verification:**
```powershell
# Verify all files match reference
cd org.polarion.eclipse.team.svn.connector.javahl21\src\org\apache\subversion\javahl
$ourPath = Get-Location
$refPath = "D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src\org\apache\subversion\javahl"

$ourFiles = Get-ChildItem -Recurse -Filter "*.java" | Measure-Object
$refFiles = Get-ChildItem $refPath -Recurse -Filter "*.java" | Measure-Object

if ($ourFiles.Count -eq $refFiles.Count) {
    Write-Host "✓ File count matches: $($ourFiles.Count)"
} else {
    Write-Host "✗ File count mismatch: Ours=$($ourFiles.Count), Ref=$($refFiles.Count)"
}
```

---

## Updating Dependencies

### Eclipse Platform Update

**When:** New Eclipse release (e.g., 2025-03)

**Steps:**
1. Update `pom.xml` repository URLs:
   ```xml
   <repository>
       <id>eclipse-2025-03</id>
       <url>https://download.eclipse.org/releases/2025-03</url>
   </repository>
   ```

2. Test build:
   ```powershell
   mvn clean verify
   ```

3. Test in new Eclipse version

### Subversive Update

**When:** New Subversive release

**Steps:**
1. Update `pom.xml`:
   ```xml
   <repository>
       <id>subversive</id>
       <url>https://download.eclipse.org/technology/subversive/updates/release/5.2.0</url>
   </repository>
   ```

2. Update dependencies if API changed

3. Test all operations

### Maven/Tycho Update

**When:** Security updates or new features needed

**Steps:**
1. Update parent `pom.xml`:
   ```xml
   <tycho.version>4.0.11</tycho.version>
   ```

2. Test build:
   ```powershell
   mvn clean verify
   ```

---

## Updating to New SVN Versions

### For Patch Releases (e.g., 1.14.5 → 1.14.6)

**Process:**
1. Download new SVN source
2. Compare JavaHL files
3. Replace if different
4. Build and test

**Commands:**
```powershell
# Download new SVN source
cd D:\Work\code
# Extract to subversion-1.14.6

# Compare
$old = "D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src"
$new = "D:\Work\code\subversion-1.14.6\subversion\bindings\javahl\src"

# Run comparison (create script or manual compare)
# If files differ, replace and rebuild
```

### For Minor/Major Releases (e.g., 1.14.x → 1.15.x)

**⚠️ Major Process - Requires Careful Planning**

**Pre-Update Checklist:**
- [ ] Review SVN release notes for API changes
- [ ] Check native library compatibility
- [ ] Verify Eclipse/Subversive compatibility
- [ ] Create backup branch in git

**Update Steps:**
1. Update version numbers in all `pom.xml`
2. Replace all JavaHL files with new reference
3. Update native libraries (svnjavahl-1.dll)
4. Update APR/OpenSSL if needed
5. Full build and test
6. Update all documentation

**Testing Requirements:**
- All manual tests must pass
- Test with multiple Eclipse versions
- Test with large repositories
- Performance regression testing

---

## Troubleshooting

### Build Failures

**Problem:** Maven build fails

**Solutions:**
1. Clean build:
   ```powershell
   mvn clean
   rm -r target
   ```

2. Check Java version:
   ```powershell
   java -version  # Should be 21+
   ```

3. Check Maven version:
   ```powershell
   mvn -version  # Should be 3.9+
   ```

4. Clear Maven cache:
   ```powershell
   rm -r ~/.m2/repository/org/polarion
   ```

### Runtime Errors

**Problem:** NoSuchMethodError

**Cause:** JNI signature mismatch

**Solution:**
1. Verify throws clauses match reference
2. Verify parameter types match exactly
3. Rebuild connector

**Problem:** UnsatisfiedLinkError

**Cause:** Native library not found

**Solution:**
1. Check native libraries in win64 module
2. Verify PATH includes native library location
3. Check svnjavahl-1.dll exists

**Problem:** Eclipse doesn't see connector

**Cause:** Installation issue

**Solution:**
1. Restart Eclipse with `-clean` flag
2. Reinstall from update site
3. Check connector appears in Preferences → Team → SVN

### Performance Issues

**Problem:** Slow operations

**Solutions:**
1. Check network connectivity
2. Verify SVN server performance
3. Check Eclipse heap size (-Xmx)
4. Review connector logging

---

## Release Process

### Version Numbering

Format: `7.0.0.YYYYMMDDHHmmss`

- Major: 7 (SVN version 1.14 = 7.x)
- Minor: 0
- Patch: 0
- Qualifier: Timestamp

### Release Checklist

**Pre-Release:**
- [ ] All tests passing
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version numbers updated
- [ ] Build successful

**Release Build:**
```powershell
# Clean build
mvn clean

# Full build with tests
.\build-updatesite.ps1

# Verify outputs
ls org.polarion.eclipse.team.svn.connector.javahl21.site\target\*.zip
```

**Post-Release:**
- [ ] Tag release in git
- [ ] Upload update site
- [ ] Update documentation
- [ ] Notify users

### Git Workflow

**Create Release Branch:**
```bash
git checkout -b release-7.0.0.202511162055
git add .
git commit -m "Release 7.0.0.202511162055"
git tag v7.0.0.202511162055
git push origin release-7.0.0.202511162055
git push origin v7.0.0.202511162055
```

**Merge to Main:**
```bash
git checkout master
git merge release-7.0.0.202511162055
git push origin master
```

---

## Maintenance Schedule

### Daily
- Monitor build status
- Check for critical issues

### Weekly
- Run full build
- Verify update site

### Monthly
- Review dependencies
- Update documentation
- Check for SVN updates

### Quarterly
- Full validation
- Performance testing
- Security review

### Annually
- Major version planning
- Architecture review
- Long-term roadmap

---

## Emergency Procedures

### Critical Bug Found

1. **Assess Impact:**
   - Does it affect all users?
   - Is data at risk?
   - Can it be worked around?

2. **Immediate Actions:**
   - Document the bug
   - Create hotfix branch
   - Implement fix
   - Test thoroughly

3. **Emergency Release:**
   ```powershell
   # Hotfix build
   git checkout -b hotfix-critical-bug
   # Make fix
   git commit -m "Hotfix: [description]"
   .\build-updatesite.ps1
   # Deploy immediately
   ```

4. **Post-Release:**
   - Notify all users
   - Update documentation
   - Merge hotfix to main

---

## Contact & Escalation

**For Build Issues:**
- Check build logs
- Review Maven output
- Consult Tycho documentation

**For Runtime Issues:**
- Check Eclipse error log
- Review connector logs
- Test with reference SVN client

**For Native Library Issues:**
- Check DLL dependencies
- Verify SVN version compatibility
- Consult SVN documentation

---

**Document Version:** 1.0  
**Last Reviewed:** November 16, 2025  
**Next Review:** February 16, 2026
