# Native Library Acquisition Guide

⚠️ **FOR MAINTAINERS ONLY** - End users don't need this guide!

**Windows x64 users:** All 22 DLL files are already included in the plugin JAR. You don't need to download or install anything.

**This guide is for maintainers** who need to update the bundled native libraries to newer SVN versions.

---

## Overview
This guide explains how to obtain and update the native DLL files bundled in the JavaHL Windows x64 connector.

## Currently Bundled (22 files, ~6 MB)
The plugin includes these DLL files in `native/`:

### Microsoft Visual C++ Runtime (2 files)
- VCRUNTIME140.dll
- MSVCP140.dll

### Apache Portable Runtime (3 files)
- libapr-1.dll
- libapriconv-1.dll
- libaprutil-1.dll

### OpenSSL 3.x (2 files) - **Updated from 1.1.x**
- libcrypto-3-x64.dll
- libssl-3-x64.dll

### SASL (1 file)
- libsasl.dll

### Apache Subversion 1.14 (8 files)
- libsvn_client-1.dll
- libsvn_delta-1.dll
- libsvn_diff-1.dll
- libsvn_fs-1.dll
- libsvn_ra-1.dll
- libsvn_repos-1.dll
- libsvn_subr-1.dll
- libsvn_wc-1.dll
- libsvnjavahl-1.dll

---

## Option 1: Download from Apache Subversion Binaries

### Step 1: Download Subversion 1.14 for Windows
Visit one of these sources:
- **Apache Lounge**: https://www.apachelounge.com/download/ (Windows binaries)
- **WANdisco**: https://www.wandisco.com/subversion/download (commercial support)
- **SlikSVN**: https://sliksvn.com/download/ (community builds)
- **VisualSVN**: https://www.visualsvn.com/downloads/ (includes server)

**Recommended**: SlikSVN or WANdisco typically provide the most complete binary distributions.

### Step 2: Extract the Archive
1. Download the Windows x64 ZIP/installer for Subversion 1.14.x
2. Extract or install to a temporary location
3. Navigate to the `bin` directory where all DLLs are located

### Step 3: Copy Required DLLs
Copy all 16 DLL files listed above from the Subversion `bin` directory to:
```
org.polarion.eclipse.team.svn.connector.javahl21.win64/native/
```

**Important**: Ensure you're using the x64 (64-bit) versions of all libraries.

---

## Option 2: Extract from Existing Subversion Installation

If you already have Apache Subversion 1.14 installed on Windows:

### Step 1: Locate Installation Directory
Typical installation paths (set `<svn-install>` to your path):
- `<svn-install>` = `C:\Program Files\Subversion\` (most common)
- `<svn-install>` = `C:\Program Files\SlikSvn\`
- `<svn-install>` = `C:\Program Files\VisualSVN Server\`
- `<svn-install>` = `C:\Program Files (x86)\CollabNet\`

*See LOCAL_PATHS.md (if available) for your specific installation path.*

### Step 2: Navigate to bin Directory
```powershell
cd "<svn-install>\bin"
```

### Step 3: Copy DLLs
```powershell
# Navigate to fragment bundle directory (replace <workspace> with your project path)
cd "<workspace>\org.polarion.eclipse.team.svn.connector.javahl21.win64\native"

# Copy VC++ Runtime from Windows System32
Copy-Item "<system32>\VCRUNTIME140.dll" .
Copy-Item "<system32>\MSVCP140.dll" .

# Copy Subversion and dependencies (replace <svn-install> with your SVN path)
Copy-Item "<svn-install>\bin\libapr-1.dll" .
Copy-Item "<svn-install>\bin\libapriconv-1.dll" .
Copy-Item "<svn-install>\bin\libaprutil-1.dll" .
Copy-Item "<svn-install>\bin\libcrypto-1_1-x64.dll" .
Copy-Item "<svn-install>\bin\libssl-1_1-x64.dll" .
Copy-Item "<svn-install>\bin\libsasl.dll" .
Copy-Item "<svn-install>\bin\libsvn_client-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_delta-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_diff-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_fs-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_ra-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_repos-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_subr-1.dll" .
Copy-Item "<svn-install>\bin\libsvn_wc-1.dll" .
Copy-Item "<svn-install>\bin\libsvnjavahl-1.dll" .
```

**Note**: Replace placeholders with your actual paths:
- `<workspace>` = Your project directory
- `<svn-install>` = Your Subversion installation (e.g., `C:\Program Files\Subversion`)
- `<system32>` = `C:\Windows\System32` (standard location)

---

## Option 3: Build from Source

For advanced users who want to build from Apache Subversion source:

### Prerequisites
- Visual Studio 2015 or later
- CMake 3.15 or later
- Python 3.x
- NASM (for OpenSSL)

### Build Steps
1. Clone Apache Subversion: https://github.com/apache/subversion
2. Build dependencies (APR, OpenSSL, SASL, BDB/SQLite)
3. Configure with CMake enabling JavaHL bindings
4. Build with Visual Studio in x64 Release mode
5. Locate compiled DLLs in build output directory

**Documentation**: https://subversion.apache.org/docs/community-guide/building.html

---

## Verification

After copying all DLL files to `native/` directory:

### Check File Count
```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21.win64\native
(Get-ChildItem -Filter *.dll).Count
# Should output: 16
```

### List All Files
```powershell
Get-ChildItem -Name *.dll | Sort-Object
```

Expected output:
```
libapr-1.dll
libapriconv-1.dll
libaprutil-1.dll
libcrypto-1_1-x64.dll
libsasl.dll
libssl-1_1-x64.dll
libsvn_client-1.dll
libsvn_delta-1.dll
libsvn_diff-1.dll
libsvn_fs-1.dll
libsvn_ra-1.dll
libsvn_repos-1.dll
libsvn_subr-1.dll
libsvn_wc-1.dll
libsvnjavahl-1.dll
MSVCP140.dll
VCRUNTIME140.dll
```

### Verify Architecture
All DLLs must be **64-bit (x64)**. Check with:
```powershell
dumpbin /headers libsvnjavahl-1.dll | Select-String "machine"
# Should show: x64 or AMD64
```

**Note**: `dumpbin` is part of Visual Studio command-line tools.

---

## Building the Fragment Bundle

Once all 16 DLL files are in place:

```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21.win64
mvn clean package
```

This will create:
- `target/org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-SNAPSHOT.jar`

The JAR will contain all native DLLs in the `native/` folder.

---

## Troubleshooting

### Missing DLLs
If you get "DLL not found" errors at runtime:
1. Verify all 16 DLLs are present in `native/` directory
2. Check DLLs are 64-bit (x64 architecture)
3. Ensure fragment bundle JAR is deployed alongside main plugin JAR

### Version Mismatch
If you get "incompatible library version" errors:
- Ensure using Subversion 1.14.x libraries (not 1.10, 1.12, or 1.13)
- Verify OpenSSL is 1.1.x (not 1.0.x or 3.x)
- Confirm VC++ runtime is 2015-2022 (VCRUNTIME140, not MSVCR100)

### Alternative: System-Wide Installation
If DLLs are installed system-wide (e.g., in Windows\System32 or PATH), JavaHL may find them automatically. However, bundling in fragment ensures predictable behavior across different Eclipse installations.

---

## License Compliance

When distributing the fragment bundle with native libraries:

1. **Apache Subversion**: Apache License 2.0 - attribution required
2. **OpenSSL**: Dual licensed (OpenSSL + SSLeay) - acknowledgment required
3. **APR**: Apache License 2.0 - attribution required
4. **VC++ Runtime**: Redistributable under Microsoft license terms
5. **SASL**: Custom license - check specific implementation used

Include all relevant LICENSE.txt files from `about_files/` directory.

---

## Next Steps

After building the fragment bundle:

1. Deploy both JARs to Eclipse:
   - `org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar`
   - `org.polarion.eclipse.team.svn.connector.javahl21.win64-7.0.0-SNAPSHOT.jar`

2. Install in Eclipse:
   - Copy to `eclipse/plugins/` directory, OR
   - Use "Install New Software" with local site, OR
   - Deploy via p2 update site

3. Verify connector loads:
   - Window → Preferences → Team → SVN → SVN Connector
   - Should see "SVN 1.14 JavaHL Connector" in list

4. Test with SVN repository:
   - Create/import SVN project
   - Verify operations work (checkout, commit, update)

---

## Support

For issues with:
- **JavaHL connector code**: Contact Polarion support or file GitHub issue
- **Native library compatibility**: Check Apache Subversion documentation
- **Build problems**: Review Maven/Tycho logs and Eclipse build output
