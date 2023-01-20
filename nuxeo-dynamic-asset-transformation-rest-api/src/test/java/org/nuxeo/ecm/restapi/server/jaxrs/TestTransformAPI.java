/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Vachette
 */

package org.nuxeo.ecm.restapi.server.jaxrs;

import static org.junit.Assert.assertEquals;
import static org.nuxeo.labs.asset.transformation.impl.Constants.PNG;

import java.io.File;
import java.io.Serializable;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.platform.picture.core.ImagingFeature;
import org.nuxeo.ecm.restapi.test.BaseTest;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class, TransactionalFeature.class, AutomationFeature.class, ImagingFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.platform.picture.core", "org.nuxeo.ecm.platform.tag",
        "nuxeo-dynamic-asset-transformation-core", "nuxeo-dynamic-asset-transformation-rest-api" })
public class TestTransformAPI extends BaseTest {

    @Inject
    public CoreSession session;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Before
    public void setup() {
        int port = this.servletContainerFeature.getPort();
        Framework.getProperties().put("nuxeo.url", "http://localhost:" + port);
    }

    @Test
    public void testCallAPI() {
        // create document
        DocumentModel picture = session.createDocumentModel(session.getRootDocument().getPathAsString(), "Picture",
                "Picture");
        Blob blob = new FileBlob(new File(getClass().getResource("/files/small.jpg").getPath()));
        blob.setMimeType("image/jpg");
        picture.setPropertyValue("file:content", (Serializable) blob);

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setWidth(300);
        imageInfo.setHeight(300);
        picture.setPropertyValue("picture:info", (Serializable) imageInfo.toMap());

        picture = session.createDocument(picture);

        // commit the transaction and record the change in the DB
        transactionalFeature.nextTransaction();

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("height", "80");
        queryParams.add("format", PNG);
        ClientResponse response = getResponse(RequestType.GET, "/transform/" + picture.getId(), queryParams);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

}
