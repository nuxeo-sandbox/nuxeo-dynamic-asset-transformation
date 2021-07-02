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

package org.nuxeo.labs.asset.transformation.service;

import org.apache.commons.io.FilenameUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.transientstore.api.TransientStoreService;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.runtime.api.Framework;

public class DynamicTransformationServiceImpl implements DynamicTransformationService {

    @Override
    public Blob transform(DocumentModel doc, Transformation transformation) {
        Blob blob = (Blob) doc.getPropertyValue("file:content");

        ConversionService converterService = Framework.getService(ConversionService.class);
        BlobHolder result = converterService.convert("dynamicImageResizer", new SimpleBlobHolder(blob), transformation.toMap());
        Blob resultBlob = result.getBlob();

        String name = FilenameUtils.removeExtension(blob.getFilename());

        resultBlob.setFilename(String.format("%s_%dx%d.%s", name, transformation.getWidth(), transformation.getHeight(), transformation.getFormat()));

        MimetypeRegistry registry = Framework.getService(MimetypeRegistry.class);
        String mimetype =registry.getMimetypeFromExtension(transformation.getFormat());

        resultBlob.setMimeType(mimetype);

        TransientStoreService transientStoreService = Framework.getService(TransientStoreService.class);

        TransientStore store = transientStoreService.getStore("image-transformation");

        String key = doc.getId() + transformation;
        store.putBlobs(key, result.getBlobs());
        store.setCompleted(key,true);

        return store.getBlobs(key).get(0);
    }
}
