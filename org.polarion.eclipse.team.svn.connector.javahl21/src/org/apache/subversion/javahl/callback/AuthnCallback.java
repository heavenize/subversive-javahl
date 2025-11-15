/**
 * @copyright
 * ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one
 *    or more contributor license agreements.  See the NOTICE file
 *    distributed with this work for additional information
 *    regarding copyright ownership.  The ASF licenses this file
 *    to you under the Apache License, Version 2.0 (the
 *    "License"); you may not use this file except in compliance
 *    with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 * ====================================================================
 * @endcopyright
 */

package org.apache.subversion.javahl.callback;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * The interface for requesting authentication credentials from the user.
 * @since 1.9
 */
public interface AuthnCallback
{
    /**
     * Abstract base class for callback results.
     */
    public abstract class AuthnResult
    {
        protected boolean save = false;
        protected boolean trust = false;
        protected String identity = null;
        protected String secret = null;
    }

    /**
     * The result type used by usernamePrompt.
     */
    public static final class UsernameResult extends AuthnResult
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        public UsernameResult(String username)
        {
            identity = username;
        }

        public UsernameResult(String username, boolean maySave)
        {
            save = maySave;
            identity = username;
        }
    }

    public UsernameResult usernamePrompt(String realm, boolean maySave);

    /**
     * The result type used by userPasswordPrompt.
     */
    public static final class UserPasswordResult extends AuthnResult
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        public UserPasswordResult(String username, String password)
        {
            identity = username;
            secret = password;
        }

        public UserPasswordResult(String username, String password, boolean maySave)
        {
            save = maySave;
            identity = username;
            secret = password;
        }
    }

    public UserPasswordResult userPasswordPrompt(String realm, String username, boolean maySave);

    /**
     * Information about why parsing a server SSL certificate failed.
     */
    public static class SSLServerCertFailures implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;
        private static final int NOT_YET_VALID = 0x00000001;
        private static final int EXPIRED       = 0x00000002;
        private static final int CN_MISMATCH   = 0x00000004;
        private static final int UNKNOWN_CA    = 0x00000008;
        private static final int OTHER         = 0x40000000;
        private static final int ALL_KNOWN     = (NOT_YET_VALID | EXPIRED | CN_MISMATCH | UNKNOWN_CA | OTHER);

        private int failures;

        private SSLServerCertFailures(int failures)
        {
            final int missing = (failures & ~ALL_KNOWN);
            if (missing != 0) {
                Logger log = Logger.getLogger("org.apache.subversion.javahl");
                log.warning(String.format("Unknown SSL certificate parsing failure flags: %1$x", missing));
            }
            this.failures = failures;
        }

        public boolean notYetValid() { return ((failures & NOT_YET_VALID) != 0); }
        public boolean expired() { return ((failures & EXPIRED) != 0); }
        public boolean cnMismatch() { return ((failures & CN_MISMATCH) != 0); }
        public boolean unknownCA() { return ((failures & UNKNOWN_CA) != 0); }
        public boolean other() { return ((failures & OTHER) != 0 || (failures & ~ALL_KNOWN) != 0); }
        public int getFailures() { return failures; }
    }

    /**
     * Detailed information about the parsed server SSL certificate.
     */
    public static class SSLServerCertInfo implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;
        private String subject;
        private String issuer;
        private Date validFrom;
        private Date validTo;
        private byte[] fingerprint;
        private List<String> hostnames;
        private String asciiCert;

        private SSLServerCertInfo(String subject, String issuer, long validFrom, long validTo,
                                  byte[] fingerprint, List<String> hostnames, String asciiCert)
        {
            this.subject = subject;
            this.issuer = issuer;
            this.validFrom = new Date(validFrom);
            this.validTo = new Date(validTo);
            this.fingerprint = fingerprint;
            this.hostnames = hostnames;
            this.asciiCert = asciiCert;
        }

        public String getSubject() { return subject; }
        public String getIssuer() { return issuer; }
        public Date getValidFrom() { return validFrom; }
        public Date getValidTo() { return validTo; }
        public byte[] getFingerprint() { return fingerprint; }
        public List<String> getHostnames() { return hostnames; }
        public String getCert() { return asciiCert; }
    }

    /**
     * The result type used by sslServerTrustPrompt.
     */
    public static final class SSLServerTrustResult extends AuthnResult
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        public static SSLServerTrustResult reject()
        {
            return new SSLServerTrustResult(false, false);
        }

        public static SSLServerTrustResult acceptTemporarily()
        {
            return new SSLServerTrustResult(true, false);
        }

        public static SSLServerTrustResult acceptPermanently()
        {
            return new SSLServerTrustResult(true, true);
        }

        private SSLServerTrustResult(boolean accept, boolean maySave)
        {
            save = maySave;
            trust = accept;
        }
    }

    public SSLServerTrustResult sslServerTrustPrompt(String realm, SSLServerCertFailures failures,
                                                      SSLServerCertInfo info, boolean maySave);

    /**
     * The result type used by sslClientCertPrompt.
     */
    public static final class SSLClientCertResult extends AuthnResult
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        public SSLClientCertResult(String path)
        {
            identity = path;
        }

        public SSLClientCertResult(String path, boolean maySave)
        {
            save = maySave;
            identity = path;
        }
    }

    public SSLClientCertResult sslClientCertPrompt(String realm, boolean maySave);

    /**
     * The result type used by sslClientCertPassphrasePrompt.
     */
    public static final class SSLClientCertPassphraseResult extends AuthnResult
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        public SSLClientCertPassphraseResult(String passphrase)
        {
            secret = passphrase;
        }

        public SSLClientCertPassphraseResult(String passphrase, boolean maySave)
        {
            save = maySave;
            secret = passphrase;
        }
    }

    public SSLClientCertPassphraseResult sslClientCertPassphrasePrompt(String realm, boolean maySave);

    public boolean allowStorePlaintextPassword(String realm);
    public boolean allowStorePlaintextPassphrase(String realm);
}
