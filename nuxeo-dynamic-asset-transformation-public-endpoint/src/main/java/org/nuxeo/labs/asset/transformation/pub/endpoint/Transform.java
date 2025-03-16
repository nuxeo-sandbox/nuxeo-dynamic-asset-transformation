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

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.transientstore.api.TransientStoreService;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.labs.asset.transformation.service.DynamicTransformationService;
import org.nuxeo.labs.download.link.service.PublicDownloadLinkService;
import org.nuxeo.runtime.api.Framework;

/**
 * Endpoint to get a dynamic rendition of an asset using an authentication token
 */
@Path("/public/transform")
@WebObject(type = "publicTransform")
public class Transform {

    private static final Logger log = LogManager.getLogger(Transform.class);

    @GET
    @Path("/{repository}/{id}")
    public Object getTransform(@PathParam("repository") String repository, @PathParam("id") String documentId,
            @QueryParam("width") long width, @QueryParam("height") long height, @QueryParam("format") String format,
            @QueryParam("crop") String crop, @QueryParam("autoCropRatio") double autoCropRatio,
            @QueryParam("textWatermark") String textWatermark, @QueryParam("watermarkId") String watermarkId,
            @QueryParam("watermarkGravity") String watermarkGravity, @QueryParam("colorSpace") String colorSpace,
            @QueryParam("backgroundColor") String backgroundColor,
            @QueryParam("compressionLevel") int compressionLevel) {

        return Framework.doPrivileged(() -> {
            CoreSession session = CoreInstance.getCoreSessionSystem(repository);
            DocumentModel document = session.getDocument(new IdRef(documentId));
            PublicDownloadLinkService downloadLinkService = Framework.getService(PublicDownloadLinkService.class);
            if (!downloadLinkService.hasPublicDownloadPermission(document, "file:content")) {
                return buildError(Response.Status.NOT_FOUND);
            }
            DynamicTransformationService service = Framework.getService(DynamicTransformationService.class);
            Transformation transformation = new ImageTransformationBuilder(document).width(width)
                                                                                    .height(height)
                                                                                    .cropBox(crop)
                                                                                    .cropRatio(autoCropRatio)
                                                                                    .format(format)
                                                                                    .textWatermark(textWatermark)
                                                                                    .watermarkId(watermarkId)
                                                                                    .watermarkGravity(watermarkGravity)
                                                                                    .colorSpace(colorSpace)
                                                                                    .backgroundColor(backgroundColor)
                                                                                    .compressionLevel(compressionLevel)
                                                                                    .build();
            Blob renditionBlob = service.transform(document, transformation);
            TransientStoreService transientStoreService = Framework.getService(TransientStoreService.class);
            TransientStore store = transientStoreService.getStore("image-transformation");
            String key = document.getId() + transformation;
            store.putBlobs(key, List.of(renditionBlob));
            store.setCompleted(key, true);
            return store.getBlobs(key).getFirst();
        });
    }

    private Response buildError(Response.Status status) {
        return Response.status(status).entity("Invalid request.").type("text/plain").build();
    }
}
