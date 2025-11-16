# Subversive SVN 1.14 JavaHL Connector

**Version:** 7.0.0.202511162055  
**Status:** ✅ Production Ready - 100% Reference Implementation  
**Last Updated:** November 16, 2025

---

## Overview

Eclipse plugin providing **Apache Subversion 1.14.x** support via JavaHL native interface. This connector enables all Subversive SVN Team Provider operations in Eclipse.

**Key Features:**
- ✅ Complete SVN 1.14.5 JavaHL reference implementation (92 classes, 100% match)
- ✅ Full Subversive integration (checkout, commit, update, branch, merge, etc.)
- ✅ Native performance via JNI
- ✅ Windows x64 support (native libraries included)
- ✅ Eclipse 2024-12 compatible

---

## Quick Start

### For Users

**1. Install Plugin:**
- In Eclipse: Help → Install New Software
- Add update site: `[your-update-site-url]`
- Select "Subversive SVN 1.14 JavaHL Connector"
- Install and restart Eclipse

**2. Configure Subversive:**
- Window → Preferences → Team → SVN
- SVN Connector: Select "JavaHL 1.14.x"
- Apply and Close

**3. Start Using SVN:**
- File → Import → SVN → Checkout Projects from SVN
- Or right-click project → Team → Share Project → SVN

**Detailed Instructions:** See [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)

### For Contributors

**1. Clone Repository:**
```bash
git clone [repository-url]
cd polarion-javahl
```

**2. Build Plugin:**
```powershell
# Quick build (connector only)
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1

# Full build (all modules + update site)
cd ..
.\build-updatesite.ps1
```

**3. Install in Eclipse:**
- Help → Install New Software → Add → Local
- Browse to `org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`
- Install and restart

**Detailed Instructions:** See [MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)

---

## Documentation

### Essential Documents (Start Here)

**For Users:**
- **[INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)** - Installation instructions and troubleshooting

**For Contributors/Maintainers:**
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** ⭐ **START HERE** - Complete project overview
- **[MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)** ⭐ **CRITICAL** - Build procedures, update workflows
- **[VALIDATION_PLAN.md](VALIDATION_PLAN.md)** - Validation checklist and testing procedures
- **[LOCAL_PATHS.md](LOCAL_PATHS.md)** - Development environment paths

**Project Information:**
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and changes
- **[MIGRATION_TO_REFERENCE.md](MIGRATION_TO_REFERENCE.md)** - Migration to reference implementation details
- **[history/MIGRATION_HISTORY.md](history/MIGRATION_HISTORY.md)** - Complete migration timeline

---

## Requirements

### Runtime Requirements
- Eclipse 2024-12 (or compatible version)
- Subversive SVN Team Provider 5.1.x
- Windows x64 (native libraries included)
- Subversion 1.14.x repository

### Build Requirements
- Java JDK 21 (or later LTS)
- Apache Maven 3.9.x
- Git

---

## Project Structure

```
polarion-javahl/
├── org.polarion.eclipse.team.svn.connector.javahl21/     # Main connector
│   ├── src/org/apache/subversion/javahl/                # JavaHL (92 classes)
│   └── src/org/polarion/team/svn/connector/             # Polarion adapter
├── org.polarion.eclipse.team.svn.connector.javahl21.win64/  # Native libraries
│   └── native/                                           # DLLs (svnjavahl-1.dll, etc.)
├── org.polarion.eclipse.team.svn.connector.javahl21.feature/  # Eclipse feature
└── org.polarion.eclipse.team.svn.connector.javahl21.site/     # Update site
```

---

## Supported SVN Operations

✅ **Repository Operations:**
- Checkout projects from SVN
- Browse repository structure
- Switch between branches/tags

✅ **Working Copy Operations:**
- Commit changes
- Update from repository
- Revert modifications
- Resolve conflicts

✅ **File Operations:**
- Add files to version control
- Delete tracked files
- Move/rename with history preservation
- Copy with history

✅ **Advanced Operations:**
- Create branches and tags
- Merge changes between branches
- View revision history and logs
- Diff files and directories
- Annotate/blame
- Lock/unlock files
- Set and manage properties

---

## Architecture

### Components

**1. JavaHL Layer (org.apache.subversion.javahl)**
- 92 Java classes (100% Apache Subversion 1.14.5 reference)
- JNI interfaces to native Subversion library
- Core SVN operations: checkout, commit, update, merge, etc.

**2. Polarion Adapter (org.polarion.team.svn.connector)**
- Bridge between Subversive and JavaHL
- Implements Subversive connector interface
- 6 adapter classes

**3. Native Libraries (win64)**
- svnjavahl-1.dll (10 MB) - JavaHL native implementation
- APR/APR-Util - Apache Portable Runtime
- OpenSSL - Secure communications
- Berkeley DB, zlib, libsasl - Supporting libraries

### Technology Stack
- **Language:** Java 21
- **Build:** Maven 3.9.11 + Tycho 4.0.10
- **Platform:** Eclipse RCP 4.32 (2024-12)
- **JNI:** Native code integration
- **P2:** Eclipse provisioning (update site)

---

## Build Instructions

### Quick Build (Connector Only)
```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1
```

**Output:** `target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar` (~354 KB)  
**Time:** ~7-10 seconds

### Full Build (All Modules + Update Site)
```powershell
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1
```

**Output:**
- Connector JAR (~354 KB)
- Native library JAR (~10 MB)
- Feature JAR (~2.5 KB)
- Update site at `org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository`

**Time:** ~10-12 seconds

### Clean Build
```powershell
mvn clean
.\build-updatesite.ps1
```

**Details:** See [MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)

---

## Testing

### Manual Testing Checklist

**Basic Operations:**
- [ ] Checkout project from repository
- [ ] Commit file changes
- [ ] Update from repository
- [ ] Add new file to version control
- [ ] Delete tracked file
- [ ] Move/rename file

**Advanced Operations:**
- [ ] Create branch
- [ ] Switch to branch
- [ ] Merge changes
- [ ] Resolve conflicts
- [ ] View history/log
- [ ] Compare (diff) files

**Details:** See [VALIDATION_PLAN.md](VALIDATION_PLAN.md)

---

## Contributing

### Before Committing

**1. Validate Changes:**
```powershell
# Run quick validation
cd org.polarion.eclipse.team.svn.connector.javahl21
.\quick-build.ps1

# Check build successful
echo $LASTEXITCODE  # Should be 0
```

**2. Update Documentation:**
- Update CHANGELOG.md with changes
- Update relevant documentation files
- Add to PROJECT_SUMMARY.md if significant

**3. Test in Eclipse:**
- Install from local update site
- Test affected operations
- Check Eclipse error log

### Code Guidelines

**DO:**
- ✅ Use reference JavaHL files from Apache Subversion 1.14.5
- ✅ Preserve JNI method signatures exactly
- ✅ Include proper `throws` clauses
- ✅ Document significant changes
- ✅ Test before committing

**DON'T:**
- ❌ Modify JavaHL files without comparing to reference
- ❌ Remove `throws` clauses from JNI methods
- ❌ Add try-catch blocks that mask exceptions
- ❌ Change method signatures
- ❌ Commit untested code

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature

# Make changes
git add .
git commit -m "Description of changes"

# Test thoroughly
.\build-updatesite.ps1
# [Test in Eclipse]

# Push
git push origin feature/your-feature

# Create pull request
```

---

## Troubleshooting

### Build Issues

**Problem:** Maven build fails

**Solution:**
```powershell
# Clean build
mvn clean
rm -r target

# Check Java version
java -version  # Should be 21+

# Check Maven version
mvn -version  # Should be 3.9+
```

### Runtime Issues

**Problem:** NoSuchMethodError when using SVN operations

**Cause:** JNI signature mismatch (missing `throws` clause)

**Solution:**
1. Compare affected file to reference: `D:\Work\code\subversion-1.14.5\subversion\bindings\javahl\src\...`
2. Replace with reference version
3. Rebuild connector

**Problem:** UnsatisfiedLinkError - native library not found

**Cause:** Missing or incorrect native libraries

**Solution:**
1. Check `org.polarion.eclipse.team.svn.connector.javahl21.win64\native\` contains DLLs
2. Verify svnjavahl-1.dll exists
3. Rebuild native library module

**Problem:** Eclipse doesn't see connector

**Solution:**
1. Restart Eclipse with `-clean` flag
2. Reinstall from update site
3. Check Window → Preferences → Team → SVN → SVN Connector

**More:** See [MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md) Troubleshooting section

---

## Version Information

**Current Version:** 7.0.0.202511162055

**Version Format:** `MAJOR.MINOR.PATCH.YYYYMMDDHHmmss`
- Major: 7 (SVN 1.14 series)
- Minor: 0
- Patch: 0
- Qualifier: Build timestamp

**Compatibility:**
- Subversion: 1.14.x
- Eclipse: 2024-12 and compatible
- Java: 21+
- Platform: Windows x64

**Reference Implementation:**
- Apache Subversion: 1.14.5
- JavaHL classes: 92 files (100% match)
- Last sync: November 16, 2025

---

## License

This project includes:

**JavaHL (Apache License 2.0):**
- Apache Subversion JavaHL bindings
- Copyright © Apache Software Foundation

**Polarion Adapter:**
- Polarion Software
- See `about.html` for license details

**Native Libraries:**
- Subversion, APR, OpenSSL: See `about_files/` for individual licenses
- Multiple open-source licenses (Apache, LGPL, BSD)

**Full License Information:** See `org.polarion.eclipse.team.svn.connector.javahl21/about_files/`

---

## Support

### Documentation
- **Installation:** [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)
- **Maintenance:** [MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)
- **Validation:** [VALIDATION_PLAN.md](VALIDATION_PLAN.md)
- **Project Details:** [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

### External Resources
- **Subversive:** https://www.eclipse.org/subversive/
- **Apache Subversion:** https://subversion.apache.org/
- **Eclipse:** https://www.eclipse.org/

### Issue Reporting
When reporting issues, include:
- Eclipse version
- Connector version
- SVN server version
- Operation being performed
- Eclipse error log (Window → Show View → Error Log)
- Steps to reproduce

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for complete version history.

**v7.0.0.202511162055 (November 16, 2025):**
- ✅ Complete migration to SVN 1.14.5 reference implementation
- ✅ All 92 JavaHL classes now 100% match reference
- ✅ Fixed NoSuchMethodError in commit operations
- ✅ Added 50 missing `throws` clauses
- ✅ Resolved all JNI signature mismatches
- ✅ Added missing NativeResources.java
- ✅ Clean, professional documentation package

---

**Project Status:** ✅ Production Ready  
**Last Updated:** November 16, 2025  
**Maintained By:** José [GitHub handle if available]

**Quick Links:**
- [Installation](INSTALLATION_GUIDE.md) | [Maintenance](MAINTENANCE_GUIDE.md) | [Validation](VALIDATION_PLAN.md) | [Changelog](CHANGELOG.md)
