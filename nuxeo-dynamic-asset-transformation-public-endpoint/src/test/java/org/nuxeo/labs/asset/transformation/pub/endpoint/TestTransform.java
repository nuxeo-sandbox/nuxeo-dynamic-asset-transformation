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

package org.nuxeo.labs.asset.transformation.pub.endpoint;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.labs.download.link.service.PublicDownloadLinkService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.ServletContainerFeature;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(FeaturesRunner.class)
@Features({TestFeature.class})

@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestTransform {

    protected Client client;

    @Inject
    protected CoreSession session;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Inject
    protected ServletContainerFeature servletContainerFeature;

    @Inject
    protected TestFeature testFeature;

    @Inject
    protected PublicDownloadLinkService publicDownloadLinkService;


    @Before
    public void setup() {
        client = Client.create();
        client.setFollowRedirects(Boolean.FALSE);
    }

    @Test
    public void testValidRequest() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);
        String token = publicDownloadLinkService.setPublicDownloadPermission(doc,"file:content");
        transactionalFeature.nextTransaction();
        WebResource webResource = client.resource(getBaseURL()).path("public").path("transform").path(doc.getRepositoryName()).path(doc.getId());
        System.out.println(webResource);
        ClientResponse response = webResource.get(ClientResponse.class);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testNoTokenRequest() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);
        transactionalFeature.nextTransaction();
        WebResource webResource = client.resource(getBaseURL()).path("public").path("transform").path(doc.getRepositoryName()).path(doc.getId());
        System.out.println(webResource);
        ClientResponse response = webResource.get(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    protected String getBaseURL() {
        int port = servletContainerFeature.getPort();
        return "http://localhost:" + port;
    }
}
