# Installation Guide - Subversive SVN 1.14 JavaHL Connector

**Version:** 7.0.0.202511141809  
**Last Updated:** November 15, 2025  
**Status:** ✅ Production Ready

---

## Related Documentation

- **[Project Summary](PROJECT_SUMMARY.md)** - Complete overview and feature list
- **[Maintenance Guide](MAINTENANCE_GUIDE.md)** - Technical reference for updates
- **[Build Instructions](org.polarion.eclipse.team.svn.connector.javahl21/README.md)** - How to build from source

---

## Quick Installation Summary

✅ **Update Site ZIP Created:**
- Location: `org.polarion.eclipse.team.svn.connector.javahl21.site\target\org.polarion.eclipse.team.svn.connector.javahl21.site-7.0.0-SNAPSHOT.zip`
- Size: ~6 MB
- Contains: Main plugin, Windows x64 native libraries (22 DLLs), and p2 repository metadata

---

## Installation Method 1: Install from ZIP (Recommended)

### Step 1: Locate the ZIP file
The update site is at:
```
D:\users\Jose\development\polarion-javahl\org.polarion.eclipse.team.svn.connector.javahl21.site\target\org.polarion.eclipse.team.svn.connector.javahl21.site-7.0.0-SNAPSHOT.zip
```

### Step 2: Install in Eclipse

1. **Open Eclipse** (2024-12 or later)

2. **Go to: Help → Install New Software...**

3. **Click "Add..."** button

4. **In the "Add Repository" dialog:**
   - Name: `Subversive JavaHL 1.14 Connector`
   - Location: Click **"Archive..."** and select the ZIP file above
   - Click **OK**

5. **Select the feature:**
   - Check: **"Subversive SVN 1.14 JavaHL Connector"** under "SVN Connectors" category
   - Click **Next**

6. **Review and Accept:**
   - Review items to be installed
   - Click **Next**
   - Accept the Eclipse Public License v2.0
   - Click **Finish**

7. **Restart Eclipse** when prompted

### Step 3: Configure Subversive to Use JavaHL

1. **Go to: Window → Preferences → Team → SVN → SVN Connector**

2. **Select: "SVN 1.14 JavaHL Connector"** from the dropdown list

3. **Click: Apply and Close**

4. **Verify:** The connector should show as active

---

## Installation Method 2: Manual Installation

### Copy JARs directly to Eclipse plugins folder:

```powershell
# Navigate to your Eclipse installation
cd "C:\Eclipse\plugins"

# Copy both plugin JARs
Copy-Item "D:\users\Jose\development\polarion-javahl\org.polarion.eclipse.team.svn.connector.javahl21\target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar" .
Copy-Item "D:\users\Jose\development\polarion-javahl\org.polarion.eclipse.team.svn.connector.javahl21.win64\target\org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-SNAPSHOT.jar" .

# Restart Eclipse with clean flag
eclipse.exe -clean
```

---

## Installation Method 3: Deploy to Web Server

### Host the update site on a web server:

1. **Extract the ZIP** to a web-accessible directory:
   ```
   https://your-server.com/eclipse/svn-javahl-connector/
   ```

2. **In Eclipse:**
   - Help → Install New Software
   - Add → Location: `https://your-server.com/eclipse/svn-javahl-connector/`
   - Install as normal

---

## Verification

### Test the Connector

1. **Check Connector Status:**
   - Window → Preferences → Team → SVN → SVN Connector
   - Should show: "SVN 1.14 JavaHL Connector" as selected

2. **Test with Repository:**
   - File → New → Other → SVN → Checkout Projects from SVN
   - Enter a repository URL (e.g., `https://svn.apache.org/repos/asf/`)
   - The connector should successfully connect

3. **Check for Errors:**
   - Window → Show View → Error Log
   - Look for any JavaHL or native library errors

### Expected Output in Connector Tab
```
Connector Name: SVN 1.14 JavaHL Connector
Version: 7.0.0.qualifier
API Version: 1.10.x (Subversive compatibility)
Features: CREATE_REPOSITORY, ATOMIC_X_COMMIT
Platform: win32/x86_64
```

---

## Troubleshooting

### Connector Not Appearing

**Problem:** Connector doesn't show up in preferences

**Solution:**
1. Verify both JARs are in `eclipse/plugins/` folder:
   - `org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar`
   - `org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-SNAPSHOT.jar`
2. Restart Eclipse with `-clean` flag: `eclipse.exe -clean`
3. Check Help → About Eclipse → Installation Details → Plug-ins

### Native Library Load Failure

**Problem:** Error: `UnsatisfiedLinkError: no svnjavahl-1 in java.library.path`

**Note:** This should NOT happen with the Windows x64 version as all libraries are included.

**Solutions:**
1. **Verify Windows x64 Eclipse:** Check Help → About Eclipse → Installation Details
   - Must be 64-bit Eclipse
2. **Install VC++ Runtime:** Download "Visual C++ Redistributable for Visual Studio 2015-2022 (x64)"
   - https://aka.ms/vs/17/release/vc_redist.x64.exe
   - Usually already installed on Windows 10/11
3. **Verify Plugin Installation:**
   ```powershell
   # Check both JARs are installed
   Get-ChildItem "C:\Eclipse\plugins\org.polarion.eclipse.team.svn.connector.javahl21*"
   # Should show 2 files:
   # - org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-*.jar
   # - org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-*.jar
   ```
4. **Restart Eclipse with -clean:** `eclipse.exe -clean`

### OpenSSL Version Issues

**Problem:** Error related to OpenSSL 3.x

**Note:** This connector bundles OpenSSL 3.x (not 1.1.x). This is correct and expected.

**If you have conflicts:**
- Ensure no other SVN connectors are installed
- Check for conflicting DLLs in system PATH
- The bundled DLLs should be used automatically

---

## Platform Support

### Current Build
- ✅ **Windows x64:** Fully supported with native libraries
- ❌ **Linux x86_64:** Not included (create separate fragment)
- ❌ **macOS:** Not included (create separate fragment)

### Creating Additional Platform Fragments

To support Linux or macOS:
1. Copy `org.polarion.eclipse.team.svn.connector.javahl21.win64` structure
2. Rename to `.linux.x86_64` or `.macosx`
3. Update MANIFEST.MF platform filter:
   - Linux: `(& (osgi.os=linux) (osgi.arch=x86_64))`
   - macOS: `(& (osgi.os=macosx) (osgi.arch=x86_64))`
4. Replace DLLs with `.so` (Linux) or `.dylib` (macOS) files
5. Add to parent POM and rebuild

---

## System Requirements

### Minimum
- **OS:** Windows 7 or later (64-bit) ✅ **Native libraries included**
- **Eclipse:** 2024-12 or later
- **Java:** Java 21 or later
- **Subversive:** 5.1.0 or later
- **Additional:** Visual C++ 2015-2022 Redistributable (x64) - usually already installed

### Recommended
- **OS:** Windows 10/11 (64-bit)
- **Eclipse:** Eclipse 2024-12
- **Java:** Java 21 LTS
- **Memory:** 2GB RAM minimum for Eclipse

### Important Notes

✅ **All SVN native libraries are included** - No need to install TortoiseSVN, SlikSVN, or any other SVN client  
✅ **22 DLL files bundled** - libsvnjavahl-1.dll and all dependencies (~6 MB)  
⚠️ **Windows x64 only** - Linux and macOS are not currently supported

---

## What's Included

### Main Plugin (`org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar`)
- **Size:** 190 KB
- **Contents:** JavaHL connector implementation
- **Dependencies:** Eclipse Platform 2024-12, Subversive 5.1.0

### Native Libraries Fragment (`org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-SNAPSHOT.jar`)
- **Size:** 5.96 MB
- **Contents:** 22 Windows x64 DLL files
  - Apache Subversion 1.14.x (9 DLLs)
  - Apache Portable Runtime (2 DLLs)
  - OpenSSL 3.x (2 DLLs)
  - Berkeley DB 4.4 (1 DLL)
  - Visual C++ 2015-2022 Runtime (2 DLLs)
  - SASL (1 DLL)
  - Internationalization (1 DLL)
  - File System Backends (4 DLLs)

### Feature (`org.polarion.eclipse.team.svn.connector.javahl21.feature-7.0.0-SNAPSHOT.jar`)
- **Size:** 2.5 KB
- **Contents:** Feature definition with license and metadata

---

## Distribution

### For Internal Use
Share the ZIP file:
```
org.polarion.eclipse.team.svn.connector.javahl21.site-7.0.0-SNAPSHOT.zip
```

Users can install via:
- Help → Install New Software → Add → Archive → Select ZIP

### For Public Distribution
1. **Host on Web Server:**
   - Extract ZIP to web server
   - Share URL: `https://your-server.com/eclipse/updates/`

2. **Create GitHub Release:**
   - Upload ZIP to GitHub Releases
   - Users download and install from local archive

3. **Deploy to Nexus/Artifactory:**
   - Upload to internal artifact repository
   - Configure as p2 repository in Eclipse

---

## Support and Documentation

### API Compatibility
- **SVN API:** 1.14.x (native library)
- **Reported API:** 1.10.x (Subversive compatibility)
- **Features:** CREATE_REPOSITORY, ATOMIC_X_COMMIT

### Known Limitations
1. **Subversive 5.1.0 Limitation:** Reports API as 1.10.x even though native library is 1.14.x (this is correct)
2. **Windows x64 Only:** Current build includes only Windows x64 natives (no Linux/macOS)
3. **OpenSSL 3.x:** Uses OpenSSL 3.x (bundled, no installation needed)

### License
- **Code:** Eclipse Public License v2.0 (EPL-2.0)
- **Native Libraries:** Apache License 2.0 (SVN, APR), OpenSSL License (OpenSSL), Microsoft License (VC++ Runtime)

---

## Quick Start Commands

```powershell
# Build everything
cd D:\users\Jose\development\polarion-javahl
.\build-updatesite.ps1

# Locate ZIP
$zip = "org.polarion.eclipse.team.svn.connector.javahl21.site\target\org.polarion.eclipse.team.svn.connector.javahl21.site-7.0.0-SNAPSHOT.zip"

# Copy to desktop for easy access
Copy-Item $zip "$env:USERPROFILE\Desktop\svn-javahl-connector.zip"

# Open in Explorer
explorer "org.polarion.eclipse.team.svn.connector.javahl21.site\target\"
```

---

## Contact and Support

For issues, questions, or contributions:
- Review build logs in `target/` directories
- Check Eclipse Error Log: Window → Show View → Error Log
- Verify Java/Eclipse versions match requirements
- Ensure all 22 DLL files are present in fragment JAR

---

**Congratulations!** You now have a complete, installable Eclipse update site for the Subversive SVN 1.14 JavaHL Connector.
