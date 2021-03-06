/*
 Copyright 2014 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.lightblue.hystrix.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;

import com.redhat.lightblue.ldap.test.LdapServerExternalResource;
import com.redhat.lightblue.ldap.test.LdapServerExternalResource.InMemoryLdapServer;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

@InMemoryLdapServer
public class InsertCommandTest {

    @Rule
    public LdapServerExternalResource ldapServer = LdapServerExternalResource.createDefaultInstance();

    @Test
    public void testExecute() throws LDAPException{
        LDAPConnection connection = new LDAPConnection("localhost", LdapServerExternalResource.DEFAULT_PORT);

        Entry entry = new Entry("uid=john.doe,dc=example,dc=com",
                new Attribute("objectClass", "top", "person", "organizationalPerson", "inetOrgPerson"),
                new Attribute("uid", "john.doe"),
                new Attribute("givenName", "John"),
                new Attribute("sn", "Doe"),
                new Attribute("cn", "John Doe")
                );

        InsertCommand command = new InsertCommand(connection, entry);

        LDAPResult result = command.execute();

        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getResultCode());
    }

}
