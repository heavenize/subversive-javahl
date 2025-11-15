# Subversive SVN 1.14 JavaHL Connector - Windows x64 Native Libraries

This fragment bundle contains the native Windows x64 libraries required for the JavaHL 1.14 connector.

## Required Native Libraries for SVN 1.14.x

### Visual C++ Runtime (2015-2022)
- `VCRUNTIME140.dll` - Visual C++ 2015-2022 Runtime
- `MSVCP140.dll` - Visual C++ 2015-2022 Standard Library

### Apache Portable Runtime (APR)
- `libapr-1.dll` - Apache Portable Runtime
- `libapriconv-1.dll` - APR iconv (character encoding conversion)
- `libaprutil-1.dll` - APR utility library

### OpenSSL 1.1.x
- `libcrypto-1_1-x64.dll` - OpenSSL cryptography library
- `libssl-1_1-x64.dll` - OpenSSL SSL/TLS library

### Subversion 1.14.x Libraries
- `libsvn_client-1.dll` - SVN client library
- `libsvn_delta-1.dll` - SVN delta/diff library
- `libsvn_diff-1.dll` - SVN diff library
- `libsvn_fs-1.dll` - SVN filesystem library
- `libsvn_ra-1.dll` - SVN repository access library
- `libsvn_repos-1.dll` - SVN repository library
- `libsvn_subr-1.dll` - SVN subroutines library
- `libsvn_wc-1.dll` - SVN working copy library

### Additional Dependencies
- `libsasl.dll` - Simple Authentication and Security Layer

### JavaHL Binding
- `libsvnjavahl-1.dll` - JavaHL JNI binding library

## Installation

Place all DLL files in the `native/` directory of this fragment bundle.

## Library Sources

You can obtain these libraries from:

1. **Official Apache Subversion Binaries:**
   - Windows: https://subversion.apache.org/packages.html#windows
   - Recommended: Apache Subversion 1.14.x Windows binaries

2. **CollabNet Subversion:**
   - https://www.collab.net/downloads/subversion

3. **Build from Source:**
   - https://subversion.apache.org/source-code.html

## Architecture Compatibility

This fragment is specifically for:
- **OS:** Windows (win32)
- **Architecture:** x86_64 (64-bit)
- **Java:** JavaSE-21 or higher

## Comparison with Previous Version

### Old Libraries (SVN 1.9.x / JavaHL 1.9)
- MSVCR100.dll, MSVCP100.dll (Visual C++ 2010)
- libeay32.dll, ssleay32.dll (OpenSSL 1.0.x)

### New Libraries (SVN 1.14.x / JavaHL 1.14)
- VCRUNTIME140.dll, MSVCP140.dll (Visual C++ 2015-2022)
- libcrypto-1_1-x64.dll, libssl-1_1-x64.dll (OpenSSL 1.1.x)

## License

All native libraries maintain their original licenses:
- APR: Apache License 2.0
- OpenSSL: OpenSSL License
- Subversion: Apache License 2.0
- Visual C++ Runtime: Microsoft Software License

This fragment bundle is licensed under EPL-2.0.

## Copyright

Copyright (c) 2005-2025 Polarion Software. All rights reserved.
