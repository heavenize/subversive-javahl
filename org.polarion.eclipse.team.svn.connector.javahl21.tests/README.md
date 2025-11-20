# Unit Tests for JavaHL Connector

This test fragment bundle contains unit tests for the Subversive SVN 1.14 JavaHL Connector.

## Test Classes

1. **NativeResourcesTest** - Tests native library loading and OpenSSL pre-loading
2. **SVNClientTest** - Tests SVNClient instantiation
3. **JavaHLConnectorTest** - Tests connector instantiation
4. **OpenSSLDependencyTest** - Windows-specific OpenSSL dependency tests

## Running Tests

```powershell
cd org.polarion.eclipse.team.svn.connector.javahl21.tests
mvn verify
```

## Requirements

- Java 21
- Native DLLs in `../org.polarion.eclipse.team.svn.connector.javahl21.win64/native/`
- JUnit 5 (provided by Tycho)

All tests should pass when the native library and OpenSSL dependencies are properly configured.
