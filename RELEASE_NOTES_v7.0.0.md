# Subversive SVN 1.14 JavaHL Connector v7.0.0

**Release Date:** November 22, 2025  
**Status:** Production Ready  
**Eclipse Compatibility:** 2024-12 (and compatible versions)

## Release Highlights

### ✅ Critical Fixes

1. **Eclipse Native Library Loading** - Resolved `UnsatisfiedLinkError` in Eclipse
   - Added `Bundle-NativeCode` header for proper OSGi integration
   - Implements OpenSSL pre-loading for Windows platforms
   - Fixes missing dependent library errors

2. **HTTPS Support** - Full SSL/TLS repository access
   - Rebuilt native library with serf module enabled
   - Includes OpenSSL 3.x libraries
   - Fixes "Unrecognized URL scheme" errors for https:// repositories

### 📦 What's Included

- **Eclipse Plugin:** JavaHL 1.14.5 connector (100% reference implementation)
- **Update Site:** Ready for Eclipse installation
- **Native Libraries:** Windows x64 with HTTPS support
- **Test Suite:** 19 comprehensive unit tests + standalone validation
- **Documentation:** Installation, testing, and maintenance guides

### 🎯 Features

✅ Complete SVN 1.14.5 JavaHL reference implementation (92 classes)  
✅ Full Subversive integration (checkout, commit, update, branch, merge, etc.)  
✅ Native performance via JNI  
✅ Windows x64 platform support  
✅ HTTPS/SSL repository access  
✅ Comprehensive test coverage  

### 🔧 Installation

1. In Eclipse: **Help → Install New Software**
2. **Add** this update site:
   ```
   https://github.com/heavenize/subversive-javahl/releases/download/v7.0.0/subversive-javahl-updatesite-v7.0.0.zip
   ```
3. Select "Subversive SVN 1.14 JavaHL Connector"
4. **Restart Eclipse**

Or install locally from the ZIP file.

### 📋 Verified Operations

- ✅ Checkout from SVN repository
- ✅ Commit with HTTPS support
- ✅ Update from repository
- ✅ Branch and merge operations
- ✅ File add/delete/move/rename
- ✅ History and diff viewing
- ✅ Native library loading (standalone + Eclipse)

### 📚 Documentation

- **README.md** - Project overview and quick start
- **INSTALLATION_GUIDE.md** - Detailed installation instructions
- **TESTING.md** - Testing procedures and validation
- **CONTRIBUTING.md** - Development guidelines
- **CHANGELOG.md** - Complete change history

### 🐛 Bug Fixes

- Fixed Eclipse OSGi manifest for native library discovery
- Fixed HTTPS repository access (serf module)
- Fixed OpenSSL dependency resolution on Windows
- Fixed native library loading path in Eclipse environment

### 🛠 Technical Details

**Build Information:**
- SVN Version: 1.14.5 (r1922182)
- Java Version: 21
- Eclipse: 2024-12
- Maven: 3.9.11
- Tycho: 4.0.10

**Native Architecture:**
- libsvnjavahl-1.dll (5.2 MB) - HTTPS enabled with serf
- libcrypto-3-x64.dll (7.3 MB) - OpenSSL crypto
- libssl-3-x64.dll (1.3 MB) - OpenSSL SSL/TLS
- MSVCP140.dll, VCRUNTIME140.dll - MSVC runtime

**Platform:** Windows x64 (win32; processor=x86_64)

### 🔗 Links

- **GitHub Repository:** https://github.com/heavenize/subversive-javahl
- **Issue Tracker:** https://github.com/heavenize/subversive-javahl/issues
- **Source Code:** https://github.com/heavenize/subversive-javahl/tree/master
- **Apache Subversion:** https://subversion.apache.org/

### 📝 License

This connector integrates the Apache Subversion JavaHL library, which is distributed under the Apache License 2.0.

See individual library licenses:
- Subversion - Apache License 2.0
- OpenSSL - Apache License 2.0 compatible
- APR/APR-Util - Apache License 2.0
