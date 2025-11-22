# Testing Guide

## Quick Test - Standalone Java

Validates that native libraries load correctly outside Eclipse:

```bash
cd simple-test
javac -cp ..\org.polarion.eclipse.team.svn.connector.javahl21\target\*.jar StandaloneJavaHLTest.java
java -cp ".;..\org.polarion.eclipse.team.svn.connector.javahl21\target\*.jar" ^
     -Djava.library.path=..\org.polarion.eclipse.team.svn.connector.javahl21.win64\native ^
     StandaloneJavaHLTest
```

**Expected Result:** All 6 tests pass
- ✅ Native library loads
- ✅ RuntimeVersion: 1.14.5
- ✅ SVNClient creation works
- ✅ Multiple instances supported

## Eclipse Integration Testing

### Prerequisites
1. Eclipse IDE (2024-12 or later)
2. Subversive SVN Team Provider installed
3. Test SVN repository (HTTP and/or HTTPS)

### Installation
1. Help → Install New Software
2. Add update site: `file:///[path-to-repo]/org.polarion.eclipse.team.svn.connector.javahl21.site/target/repository`
3. Install "Subversive SVN 1.14 JavaHL Connector"
4. **Restart Eclipse** (required to load native libraries)

### Configure Connector
1. Window → Preferences → Team → SVN
2. SVN Connector Tab → Select "SVNKit 1.14.x (Pure Java)" dropdown
3. Change to: **"JavaHL 1.14.x"**
4. Apply and Close

### Test Operations

#### 1. Repository Checkout (HTTPS)
```
File → Import → SVN → Checkout Projects from SVN
→ Create new repository location
→ URL: https://your-svn-server.com/repo
→ Verify: Should work without "Unrecognized URL scheme" error
```

#### 2. Commit Changes
```
Modify a file → Right-click → Team → Commit
→ Enter commit message
→ Verify: Commit succeeds without UnsatisfiedLinkError
```

#### 3. Update Working Copy
```
Right-click project → Team → Update
→ Verify: Update completes successfully
```

#### 4. Other Operations
- Move/Rename files
- Branch/Tag creation
- Merge operations
- Show history
- Compare with repository

### Troubleshooting

#### Check Eclipse Error Log
Window → Show View → Error Log
- Look for `UnsatisfiedLinkError` or `LinkageError`
- Should show clean loading of native libraries

#### Verify Native Libraries
Check that DLLs are present:
```
[eclipse-workspace]\.metadata\.plugins\org.eclipse.pde.core\
  org.polarion.eclipse.team.svn.connector.javahl21.win64\native\
    libsvnjavahl-1.dll
    libcrypto-3-x64.dll
    libssl-3-x64.dll
    MSVCP140.dll
    VCRUNTIME140.dll
```

#### Common Issues

**"Unrecognized URL scheme for 'https://'"**
- Native library doesn't have serf module
- Use version 7.0.0 or later

**"Can't find dependent libraries"**
- Bundle-NativeCode header missing
- Use version 7.0.0 or later

**"Native library version must be at least 1.14.0"**
- Wrong version of native library
- Check that libsvnjavahl-1.dll is from SVN 1.14.5 build

## Unit Tests (Optional)

Full unit test suite available in `org.polarion.eclipse.team.svn.connector.javahl21.tests/`

**Note:** Tests require JUnit Jupiter OSGi bundles (not in standard Eclipse P2 repos). Use standalone test or Eclipse integration testing instead.

## Test Coverage Matrix

| Feature | Standalone | Eclipse | Status |
|---------|-----------|---------|--------|
| Native library loading | ✅ | ⏳ | Standalone verified |
| OpenSSL dependencies | ✅ | ⏳ | Standalone verified |
| SVNClient creation | ✅ | ⏳ | Standalone verified |
| Multiple instances | ✅ | ⏳ | Standalone verified |
| Version information | ✅ | ⏳ | Standalone verified |
| HTTPS repositories | ❓ | ⏳ | Requires Eclipse test |
| Commit operations | ❓ | ⏳ | Requires Eclipse test |
| Update operations | ❓ | ⏳ | Requires Eclipse test |

**Legend:**
- ✅ Verified working
- ⏳ Pending testing
- ❓ Requires specific test environment
