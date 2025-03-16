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

import static org.junit.Assert.assertEquals;

import jakarta.inject.Inject;

import jakarta.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.http.test.CloseableHttpResponse;
import org.nuxeo.http.test.HttpClientTestRule;
import org.nuxeo.labs.download.link.service.PublicDownloadLinkService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.ServletContainerFeature;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

@RunWith(FeaturesRunner.class)
@Features({ TestFeature.class, TransactionalFeature.class })

@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestTransform {

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

    @Rule
    public final HttpClientTestRule httpClient = HttpClientTestRule.builder()
            .url(() -> servletContainerFeature.getHttpUrl()+"/public/transform/")
            .build();

    @Test
    public void testValidRequest() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);
        String token = publicDownloadLinkService.setPublicDownloadPermission(doc, "file:content");
        transactionalFeature.nextTransaction();

        try (CloseableHttpResponse response =
                     httpClient.buildGetRequest(String.format("/%s/%s",doc.getRepositoryName(),doc.getId())).execute()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testNoTokenRequest() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);
        transactionalFeature.nextTransaction();

        try (CloseableHttpResponse response =
                     httpClient.buildGetRequest(String.format("/%s/%s",doc.getRepositoryName(),doc.getId())).execute()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

}
