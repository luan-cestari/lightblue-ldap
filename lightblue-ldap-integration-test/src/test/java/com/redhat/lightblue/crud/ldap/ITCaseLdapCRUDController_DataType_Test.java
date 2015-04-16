/*
 Copyright 2015 Red Hat, Inc. and/or its affiliates.

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
package com.redhat.lightblue.crud.ldap;

import static com.redhat.lightblue.test.Assert.assertNoDataErrors;
import static com.redhat.lightblue.test.Assert.assertNoErrors;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.Response;
import com.redhat.lightblue.crud.FindRequest;
import com.redhat.lightblue.crud.InsertionRequest;
import com.redhat.lightblue.ldap.test.AbstractLdapCRUDController;
import com.redhat.lightblue.metadata.types.DateType;
import com.redhat.lightblue.util.test.AbstractJsonNodeTest;

@RunWith(value = Parameterized.class)
public class ITCaseLdapCRUDController_DataType_Test extends AbstractLdapCRUDController {

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"date", "testdate", DateType.getDateFormat().format(new Date())},
                {"binary", "testbinary", DatatypeConverter.printBase64Binary("test binary data".getBytes())}
        });
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("ldap.datatype.basedn", "dc=example,dc=com");
    }

    private final String cn;
    private final String fieldName;
    private final String data;

    public ITCaseLdapCRUDController_DataType_Test(String cn, String fieldName, String data) throws Exception {
        super();
        this.cn = cn;
        this.fieldName = fieldName;
        this.data = data;
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{loadJsonNode("./metadata/datatype-metadata.json")};
    }

    @Test
    public void testInsertThenFindField() throws Exception {
        String insert = AbstractJsonNodeTest.loadResource("./crud/insert/datatype-insert-template.json")
                .replaceFirst("#cn", cn)
                .replaceFirst("#field", fieldName)
                .replaceFirst("#fielddata", data);

        Response insertResponse = getLightblueFactory().getMediator().insert(
                createRequest_FromJsonString(InsertionRequest.class, insert));

        assertNotNull(insertResponse);
        assertNoErrors(insertResponse);
        assertNoDataErrors(insertResponse);
        assertEquals(1, insertResponse.getModifiedCount());

        String find = AbstractJsonNodeTest.loadResource("./crud/find/datatype-find-template.json")
                .replaceFirst("#cn", cn)
                .replaceFirst("#field", fieldName);

        Response findResponse = getLightblueFactory().getMediator().find(
                createRequest_FromJsonString(FindRequest.class, find));

        assertNotNull(findResponse);
        assertNoErrors(findResponse);
        assertNoDataErrors(findResponse);
        assertEquals(1, findResponse.getMatchCount());

        JsonNode entityData = findResponse.getEntityData();
        assertNotNull(entityData);

        JSONAssert.assertEquals(
                "[{\"" + fieldName + "\":\"" + data + "\"}]",
                entityData.toString(), true);
    }

}
