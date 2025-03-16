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
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.Rule;
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
import org.nuxeo.ecm.platform.picture.core.ImagingCoreFeature;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.http.test.CloseableHttpResponse;
import org.nuxeo.http.test.HttpClientTestRule;
import org.nuxeo.runtime.test.runner.*;

@RunWith(FeaturesRunner.class)
@Features({ WebEngineFeature.class, RestServerFeature.class, TransactionalFeature.class, AutomationFeature.class, ImagingCoreFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({ "nuxeo-dynamic-asset-transformation-core", "nuxeo-dynamic-asset-transformation-rest-api" })
public class TestTransformAPI {

    @Inject
    public CoreSession session;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Inject
    protected RestServerFeature restServerFeature;

    @Rule
    public final HttpClientTestRule httpClient =
            HttpClientTestRule.defaultClient(() -> restServerFeature.getRestApiUrl()+"/transform/");

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

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("height", "80");
        queryParams.put("format", PNG);

        //try get JSON response
        try (CloseableHttpResponse response =
                     httpClient.buildGetRequest(picture.getId()).accept(MediaType.APPLICATION_JSON)
                             .addQueryParameters(queryParams).execute()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

}
