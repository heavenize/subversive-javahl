# Contributing Guide

## Development Setup

### Prerequisites
- Java 21 or later
- Maven 3.9+
- Git
- Visual Studio 2022 (for native library compilation)
- Eclipse IDE 2024-12 (for testing)

### Build Project

```bash
# Full build
mvn clean install

# Build without tests (recommended)
mvn clean install -pl '!org.polarion.eclipse.team.svn.connector.javahl21.tests'

# Quick connector rebuild
cd org.polarion.eclipse.team.svn.connector.javahl21
mvn clean package
```

## Project Structure

```
polarion-javahl/
├── org.polarion.eclipse.team.svn.connector.javahl21/       # Main connector plugin
│   ├── src/                                                 # JavaHL sources (92 classes)
│   └── META-INF/MANIFEST.MF                                # Plugin manifest
├── org.polarion.eclipse.team.svn.connector.javahl21.win64/ # Native library fragment
│   ├── native/                                             # Windows x64 DLLs (6 files)
│   └── META-INF/MANIFEST.MF                                # Fragment manifest with Bundle-NativeCode
├── org.polarion.eclipse.team.svn.connector.javahl21.feature/ # Feature definition
├── org.polarion.eclipse.team.svn.connector.javahl21.site/  # P2 update site
├── org.polarion.eclipse.team.svn.connector.javahl21.tests/ # Unit tests (19 tests)
├── simple-test/                                            # Standalone test
└── history/                                                # Historical documentation
```

## Making Changes

### JavaHL Source Code

All JavaHL sources match the official SVN 1.14.5 reference implementation:
- **Do not modify** JavaHL classes unless updating to a new SVN version
- All changes should maintain 100% compatibility with reference

### Native Libraries

Native libraries are pre-built and included. To update:
1. See `org.polarion.eclipse.team.svn.connector.javahl21.win64/NATIVE_LIBRARY_GUIDE.md`
2. Required: Visual Studio 2022, SVN dependencies
3. Critical: Include `SVN_LIBSVN_RA_LINKS_RA_SERF` preprocessor definition for HTTPS

### Testing Changes

```bash
# Standalone test
cd simple-test
javac -cp ..\org.polarion.eclipse.team.svn.connector.javahl21\target\*.jar StandaloneJavaHLTest.java
java -cp ".;..\org.polarion.eclipse.team.svn.connector.javahl21\target\*.jar" ^
     -Djava.library.path=..\org.polarion.eclipse.team.svn.connector.javahl21.win64\native ^
     StandaloneJavaHLTest

# Eclipse integration test
# See TESTING.md for complete guide
```

## Commit Guidelines

### Commit Messages

Follow conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `test`: Adding or updating tests
- `refactor`: Code refactoring
- `chore`: Maintenance tasks

**Examples:**
```
fix(native): Add Bundle-NativeCode header for Eclipse OSGi

Eclipse OSGi runtime requires Bundle-NativeCode header to properly
configure java.library.path for native libraries in fragment bundles.

Fixes #123
```

```
feat(https): Add OpenSSL pre-loading for Windows

Pre-loads OpenSSL DLLs before main native library to ensure
proper dependency resolution in Eclipse plugin environment.

Related to #124
```

### Versioning

This project uses [Semantic Versioning](https://semver.org/):
- **MAJOR.MINOR.PATCH.TIMESTAMP**
- Example: `7.0.0`

Timestamp format: `YYYYMMDDHHmm` (UTC)

## Pull Request Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/my-feature`)
3. Make your changes
4. Run tests (`mvn clean verify` or standalone test)
5. Update CHANGELOG.md
6. Commit with conventional commit message
7. Push to your fork
8. Submit pull request

### PR Checklist

- [ ] Code builds successfully
- [ ] Standalone test passes
- [ ] Eclipse integration tested (if applicable)
- [ ] CHANGELOG.md updated
- [ ] Documentation updated (if applicable)
- [ ] Commit messages follow convention

## Release Process

1. Update version in all `pom.xml` files
2. Update CHANGELOG.md with release notes
3. Update README.md version badge
4. Build update site: `mvn clean install`
5. Tag release: `git tag v7.0.0`
6. Push: `git push origin master --tags`
7. Create GitHub release with update site ZIP

## Code Style

- Follow existing code formatting
- Use spaces (4) for indentation
- Keep lines under 120 characters
- Add Javadoc for public APIs
- Use descriptive variable names

## Questions?

- Open an issue for bugs or feature requests
- Check existing documentation in `/docs`
- Review CHANGELOG.md for recent changes
