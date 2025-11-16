# Subversive SVN 1.14 JavaHL Connector

**Version:** 7.0.0.202511141809  
**Status:** ✅ Production Ready  
**Last Updated:** November 15, 2025

Eclipse plugin providing complete JavaHL 1.14.5 connector for Subversive SVN integration with 100% feature parity.

## Quick Links

- **[Project Summary](../PROJECT_SUMMARY.md)** - Complete overview, features, and status
- **[Maintenance Guide](../MAINTENANCE_GUIDE.md)** - Technical guide for updates and fixes
- **[Installation Guide](../INSTALLATION_GUIDE.md)** - Detailed installation instructions
- **[Build Success Report](BUILD_SUCCESS.md)** - Build history and milestones

## Prerequisites

### Required Software

1. **JDK 21** (Latest LTS version)
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
   - Set `JAVA_HOME` environment variable to JDK 21 installation path

2. **Apache Maven 3.9+**
   - Download from: https://maven.apache.org/download.cgi
   - Add Maven `bin` directory to your `PATH`

3. **Eclipse IDE 2024-12** (or later) - *Optional, only for Eclipse-based development*
   - Download from: https://www.eclipse.org/downloads/
   - Install "Eclipse IDE for RCP and RAP Developers" or add PDE features

### Native Libraries (Included)

4. **Subversion JavaHL 1.14.x Native Libraries** ✅ **INCLUDED**
   - **Windows x64:** All 22 DLL files are included in the plugin (no separate installation needed)
   - **Linux/macOS:** You would need to provide native libraries for these platforms (not currently included)
   - The Windows version includes libsvnjavahl-1.dll and all dependencies (OpenSSL, APR, etc.)

## Build Instructions

### Method 1: Maven/Tycho Build (Recommended)

This project uses Eclipse Tycho for building OSGi bundles.

#### Quick Build

```powershell
# Verify prerequisites
java -version    # Should show Java 21
mvn -version     # Should show Maven 3.9+

# Clean and build
mvn clean package
```

#### Build Output

The plugin JAR will be created at:
```
target/org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar
```

#### Build Commands

```powershell
# Clean build artifacts
mvn clean

# Compile and package
mvn package

# Skip tests (if any)
mvn package -DskipTests

# Install to local Maven repository
mvn install

# Generate source JAR
mvn source:jar
```

### Method 2: Eclipse PDE Build

If you prefer to build within Eclipse IDE:

1. **Import Project**
   ```
   File > Import > Existing Projects into Workspace
   Select this directory
   ```

2. **Configure Target Platform**
   ```
   Window > Preferences > Plug-in Development > Target Platform
   Add Eclipse 2024-12 or later as target platform
   ```

3. **Build Plugin**
   ```
   Right-click project > Export > Plug-in Development > Deployable plug-ins and fragments
   Choose destination directory
   ```

## Project Structure

```
org.polarion.eclipse.team.svn.connector.javahl21/
├── src/                          # Java source code
│   ├── org/apache/subversion/    # JavaHL API classes
│   └── org/polarion/team/svn/    # Connector implementation
├── META-INF/
│   └── MANIFEST.MF               # OSGi bundle manifest
├── about_files/                  # License files
├── bin/                          # Compiled classes (ignored)
├── build.properties              # Eclipse build configuration
├── plugin.xml                    # Eclipse plugin extension points
├── plugin.properties             # Localized plugin metadata
├── pom.xml                       # Maven/Tycho build configuration
└── README.md                     # This file
```

## Configuration

### Manifest Configuration

Key settings in `META-INF/MANIFEST.MF`:
- **Bundle-Version**: `7.0.0.qualifier`
- **Bundle-RequiredExecutionEnvironment**: `JavaSE-21`
- **Require-Bundle**: 
  - `org.eclipse.core.runtime` (3.20.0+)
  - `org.eclipse.team.svn.core` (4.0.0-6.0.0)

### Build Properties

Configured in `build.properties`:
- Source folder: `src/`
- Binary includes: plugin files, metadata, licenses
- Output folder: `bin/`

## Troubleshooting

### Common Build Issues

**Issue**: `Cannot resolve org.eclipse.core.runtime`
- **Solution**: Maven will auto-download from Eclipse p2 repository. Ensure internet connection.

**Issue**: `Unsupported class file major version 65`
- **Solution**: Ensure you're using JDK 21 (not older versions)

**Issue**: `JAVA_HOME not set`
- **Solution**: Set environment variable (replace `<java-home>` with your JDK path):
  ```powershell
  $env:JAVA_HOME = "<java-home>"
  ```
  Example: `$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.4.7-hotspot"`

**Issue**: Maven build fails with target platform errors
- **Solution**: Clear Maven cache and rebuild:
  ```powershell
  mvn clean install -U
  ```

### Verify Installation

Check JDK installation:
```powershell
java -version
# Should output: openjdk version "21.x.x" or java version "21.x.x"
```

Check Maven installation:
```powershell
mvn -version
# Should output: Apache Maven 3.9.x or later
```

## Installation

### Installing Built Plugin

1. Copy the JAR from `target/` directory
2. Place in Eclipse `dropins` folder or install via:
   ```
   Help > Install New Software > Add > Archive
   ```

3. Restart Eclipse
4. Verify installation: `Help > About Eclipse > Installation Details`

## Runtime Dependencies

### Windows x64 ✅ **No Installation Required**

**All native libraries are included in the plugin!** The plugin contains:
- libsvnjavahl-1.dll (JavaHL JNI bridge)
- All SVN 1.14.5 libraries (8 DLLs)
- OpenSSL 3.x (2 DLLs)
- Apache Portable Runtime (3 DLLs)
- VC++ Runtime (2 DLLs)
- Other dependencies (7 DLLs)

**Total: 22 DLL files (~6 MB) bundled with the plugin**

### Linux/macOS ⚠️ **Not Currently Supported**

Linux and macOS are not included in this build. To add support, you would need to:
- Create platform-specific fragment bundles
- Provide native `.so` (Linux) or `.dylib` (macOS) files
- See MAINTENANCE_GUIDE.md § Platform Support for instructions

## Current Status

**Build Version:** 7.0.0.202511141809  
**Source Files:** 98 Java files  
**Native Methods:** 66 (all verified correct)  
**Feature Parity:** 100% with SVN 1.14.5

### Recent Fixes (Nov 14-15, 2025)
- ✅ Fixed list() method JNI signature (ListCallback → ListItemCallback)
- ✅ Added nativeOpenRemoteSession() for ISVNRemote support
- ✅ Added 16 missing support classes (remote/, util/ packages)
- ✅ Fixed auto-add unversioned files during commit
- ✅ Fixed NativeException constructor causing NoSuchMethodError

## Development

### Code Style
- Java 21 language features allowed
- Follow Eclipse code formatter settings
- Maintain EPL-2.0 license headers with SPDX identifiers

### Key Implementation Files
- **SVNClient.java** (778 lines) - 66 native methods, main JavaHL API
- **JavaHLConnector.java** (2344 lines) - Subversive bridge
- **JavaHLConnectorFactory.java** (~250 lines) - Factory + native loading
- **NativeException.java** (125 lines) - Exception base class (CRITICAL: only 4-param constructor)

### Testing
- Ensure compatibility with Eclipse 2024-12+
- Test with Subversion 1.14.x repositories
- Verify native library loading on target platform
- Check for JNI errors in Eclipse Error Log

## License

Eclipse Public License v2.0 (EPL-2.0)  
SPDX: EPL-2.0  
Copyright (c) 2005-2025 Polarion AG

Third-party components:
- Apache Subversion 1.14.5 (Apache-2.0)
- OpenSSL 3.x (OpenSSL License)
- See `about.html` for complete license information

## Version History

- **7.0.0.202511141809** (Nov 15, 2025) - Fixed NativeException constructor JNI mismatch
- **7.0.0.202511141802** (Nov 14, 2025) - Fixed list() signature, added auto-add files
- **7.0.0.202511140952** (Nov 14, 2025) - 100% feature parity with SVN 1.14.5
- **7.0.0.202511101142** (Nov 10, 2025) - Initial Java 21 + Eclipse 2024-12 migration
- **6.0.0** (legacy) - Java 5/6, JavaHL 1.6-1.7, Eclipse 2016

## Support

**For maintenance and updates:** See [MAINTENANCE_GUIDE.md](../MAINTENANCE_GUIDE.md)

**For installation issues:**
- Check Eclipse Error Log: Window → Show View → Error Log
- Verify Java 21+ runtime: Help → About Eclipse → Installation Details
- Ensure native libraries loaded: Check for UnsatisfiedLinkError
- Verify Visual C++ 2015-2022 Redistributable installed (Windows)

**For build issues:**
- Verify JDK 21 and Maven 3.9+ installed
- Clear Maven cache: `mvn clean install -U`
- For signature verification: Compare with official Apache SVN 1.14.5 source code
  - Download from: https://subversion.apache.org/download/
  - Extract and reference JavaHL bindings: `<svn-source>/subversion/bindings/javahl/src`

## References

- **Project Documentation:**
  - [Project Summary](../PROJECT_SUMMARY.md) - Complete overview
  - [Maintenance Guide](../MAINTENANCE_GUIDE.md) - Technical reference
  - [Installation Guide](../INSTALLATION_GUIDE.md) - User installation
  - LOCAL_PATHS.md (local only) - Your development paths
  
- **External Resources:**
  - Apache Subversion: https://subversion.apache.org/
  - Eclipse Subversive: https://www.eclipse.org/subversive/
  - Eclipse Tycho: https://eclipse.dev/tycho/
  - JavaHL Documentation: https://subversion.apache.org/docs/api/latest/
