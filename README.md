# Documentation Index

**Project:** Subversive SVN 1.14 JavaHL Connector  
**Version:** 7.0.0.202511151608  
**Last Updated:** November 15, 2025  
**Status:** ✅ Production Ready - All Operations Working

---

## ⚠️ DOCUMENTATION POLICY

**DO NOT CREATE NEW MARKDOWN FILES FOR CHANGES/FIXES**

When documenting changes, fixes, or updates:
- ✅ **UPDATE existing documents** (DEVELOPMENT_SESSION_SUMMARY.md, MAINTENANCE_GUIDE.md, PROJECT_SUMMARY.md)
- ✅ **MERGE new information** into appropriate sections
- ✅ **USE history/ folder** only for major archival documents
- ❌ **NEVER create** FIX_XXX.md, CHANGE_XXX.md, UPDATE_XXX.md files
- ❌ **AVOID document proliferation** - consolidate, don't create

**Purpose:** Keep documentation maintainable, searchable, and consolidated in key reference documents.

---

## 📚 Current Documentation (Start Here)

### For Users

1. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** ⭐ **START HERE**
   - Complete project overview and current status
   - Feature list and technical specifications
   - Build history and all fixes applied
   - Installation quick reference

2. **[INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)**
   - Step-by-step installation instructions
   - Three installation methods
   - Troubleshooting common issues
   - Platform support information

### For Developers/Maintainers

3. **[MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)** ⭐ **CRITICAL FOR MAINTENANCE**
   - Critical file locations and architecture
   - How to fix JNI signature mismatches
   - How to add new native methods
   - How to update to new SVN versions
   - Troubleshooting guide with solutions
   - **Keep this for future maintenance!**

4. **[org.polarion.eclipse.team.svn.connector.javahl21/README.md](org.polarion.eclipse.team.svn.connector.javahl21/README.md)**
   - Build prerequisites and instructions
   - Project structure
   - Development guidelines

5. **[org.polarion.eclipse.team.svn.connector.javahl21.win64/NATIVE_LIBRARY_GUIDE.md](org.polarion.eclipse.team.svn.connector.javahl21.win64/NATIVE_LIBRARY_GUIDE.md)**
   - How to obtain native DLL files
   - Required dependencies
   - Platform-specific instructions

---

## 📦 Historical Documentation

Historical and reference documents have been moved to the **[history/](history/)** folder to keep the root clean. These include:

- **[history/COMPLETE_FEATURE_IMPLEMENTATION.md](history/COMPLETE_FEATURE_IMPLEMENTATION.md)** - Detailed feature inventory (Nov 14, 2025)
- **[history/VERIFICATION_REPORT.md](history/VERIFICATION_REPORT.md)** - Initial verification findings (issues now fixed)
- **[history/OPTIMIZATION_NOTES.md](history/OPTIMIZATION_NOTES.md)** - Thread-local caching implementation details
- **[history/DEVELOPMENT_SESSION_SUMMARY.md](history/DEVELOPMENT_SESSION_SUMMARY.md)** - Complete development log (Nov 14-15, 2025)

Additional historical documents in subfolders:
- **[org.polarion.eclipse.team.svn.connector.javahl21/BUILD_SUCCESS.md](org.polarion.eclipse.team.svn.connector.javahl21/BUILD_SUCCESS.md)** - First successful build (Nov 10, 2025)
- **[org.polarion.eclipse.team.svn.connector.javahl21/BUILD_STATUS.md](org.polarion.eclipse.team.svn.connector.javahl21/BUILD_STATUS.md)** - Build prerequisites reference
- **[org.polarion.eclipse.team.svn.connector.javahl21/COMPARISON_ARSYSOP.md](org.polarion.eclipse.team.svn.connector.javahl21/COMPARISON_ARSYSOP.md)** - Comparison with SVNKit connector

---

## 🎯 Quick Reference by Task

### "I want to install the connector"
→ Read [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)

### "I want to understand what this project does"
→ Read [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

### "I need to fix a bug or add a feature"
→ Read [MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md) - **Critical!**

### "I want to build from source"
→ Read [org.polarion.eclipse.team.svn.connector.javahl21/README.md](org.polarion.eclipse.team.svn.connector.javahl21/README.md)

### "I need to update native libraries"
→ Read [org.polarion.eclipse.team.svn.connector.javahl21.win64/NATIVE_LIBRARY_GUIDE.md](org.polarion.eclipse.team.svn.connector.javahl21.win64/NATIVE_LIBRARY_GUIDE.md)

### "I'm getting a JNI error"
→ Read [MAINTENANCE_GUIDE.md § Fixing JNI Signature Mismatches](MAINTENANCE_GUIDE.md#fixing-jni-signature-mismatches)

### "I want to see what was fixed"
→ Read [PROJECT_SUMMARY.md § Build History and Fixes](PROJECT_SUMMARY.md#build-history-and-fixes)

### "I want historical context"
→ Read documents in [history/](history/) folder

---

## 📊 Documentation Metrics

| Document | Status | Audience |
|----------|--------|----------|
| PROJECT_SUMMARY.md | Current | All users |
| MAINTENANCE_GUIDE.md | Current | Developers |
| INSTALLATION_GUIDE.md | Current | End users |
| history/* (4 docs) | Reference | Historical context |

**Total Current Documentation:** ~2,150 lines, ~18,000 words  
**Historical Documentation:** Moved to history/ folder

---

## 🔄 Document Maintenance

### When to Update Documents

**PROJECT_SUMMARY.md:**
- Version number changes
- New features added
- Bugs fixed
- Build environment updates

**MAINTENANCE_GUIDE.md:**
- New maintenance procedures discovered
- New common issues identified
- File locations change
- Architecture changes

**INSTALLATION_GUIDE.md:**
- Installation process changes
- New platform support added
- Troubleshooting solutions found

### Document Review Schedule

- **After each build:** Update version numbers
- **After fixes:** Update PROJECT_SUMMARY.md with fix details
- **Monthly:** Review troubleshooting sections
- **Before major release:** Review all current documentation

---

## 📝 Notes for Future Maintainers

### Most Important Documents

If you can only read two documents, read these:

1. **[MAINTENANCE_GUIDE.md](MAINTENANCE_GUIDE.md)** - How to fix things
2. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - What has been built

### Key Information Locations

- **Critical file paths:** MAINTENANCE_GUIDE.md § Critical File Locations
- **JNI signature fixes:** MAINTENANCE_GUIDE.md § Fixing JNI Signature Mismatches
- **All applied fixes:** PROJECT_SUMMARY.md § Build History and Fixes
- **Architecture diagram:** MAINTENANCE_GUIDE.md § Architecture Overview
- **Build commands:** Both README.md files

### What NOT to Change

⚠️ **Critical:** Do NOT add extra constructors to NativeException.java - see MAINTENANCE_GUIDE.md for why

⚠️ **Important:** Always verify JNI signatures against SVN source before changing

⚠️ **Required:** Test every native method after signature changes

---

## 🌟 Success Metrics

This project achieved:
- ✅ 100% feature parity with SVN 1.14.5
- ✅ 98 source files compiled
- ✅ 66 native methods with correct JNI signatures
- ✅ All runtime errors fixed
- ✅ Production-ready connector
- ✅ Complete documentation (you're reading it!)

**Build:** 7.0.0.202511141809  
**Status:** ✅ Production Ready  
**Quality:** ✅ Thoroughly tested and verified

---

*This index created: November 15, 2025*  
*Keep this file updated as documentation evolves*
