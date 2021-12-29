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
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DynamicTransformationServiceImpl implements DynamicTransformationService {

    @Override
    public Blob transform(DocumentModel doc, Transformation transformation) {

        Blob blob = (Blob) doc.getPropertyValue("file:content");

        ConversionService conversionService = Framework.getService(ConversionService.class);
        BlobHolder conversionResult = conversionService.convert("dynamicImageResizer", new SimpleBlobHolder(blob), transformation.toMap());
        Blob convertedBlob = conversionResult.getBlob();

        MimetypeRegistry registry = Framework.getService(MimetypeRegistry.class);
        String mimetype = registry.getMimetypeFromExtension(transformation.getFormat());

        convertedBlob.setFilename(getTargetFileName(blob, transformation));
        convertedBlob.setMimeType(mimetype);

        Blob watermarkBlob = getWatermarkBlob(transformation);

        if (watermarkBlob == null) {
            return convertedBlob;
        }
        try {
            Map<String, Serializable> params = new HashMap<>();
            params.put("format", transformation.getFormat());
            params.put("watermarkFilePath", watermarkBlob.getCloseableFile().file.getPath());
            BlobHolder watermarkResult = conversionService.convert("composeWatermarkedImage", new SimpleBlobHolder(convertedBlob), params);
            Blob watermarkedBlob = watermarkResult.getBlob();
            watermarkedBlob.setFilename(getTargetFileName(blob, transformation));
            watermarkedBlob.setMimeType(mimetype);
            return watermarkedBlob;
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    public Blob getWatermarkBlob(Transformation transformation) {
        if (StringUtils.isNotEmpty(transformation.getTextWatermark())) {
            return getWatermarkBlobFromText(transformation);
        } else {
            return transformation.getImageWatermark();
        }
    }

    public Blob getWatermarkBlobFromText(Transformation transformation) {
        ConversionService conversionService = Framework.getService(ConversionService.class);
        BlobHolder result = conversionService.convert("text2WatermarkImage", new SimpleBlobHolder(new StringBlob(transformation.getTextWatermark())), transformation.toMap());
        return result.getBlob();
    }

    public String getTargetFileName(Blob blob, Transformation transformation) {
        String name = FilenameUtils.removeExtension(blob.getFilename());
        return String.format("%s_%dx%d.%s", name, transformation.getWidth(), transformation.getHeight(), transformation.getFormat());
    }
}
