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

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;
import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_FACET;
import static org.nuxeo.labs.asset.transformation.impl.Constants.PNG;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.runtime.api.Framework;

public class DynamicTransformationServiceImpl implements DynamicTransformationService {

    @Override
    public Blob transform(DocumentModel doc, Transformation transformation) {
        if (doc.hasFacet(PICTURE_FACET)) {
            Blob blob = (Blob) doc.getPropertyValue("file:content");
            return transformPicture(blob, transformation, doc.getCoreSession());
        } else if (doc.hasFacet(VIDEO_FACET)) {
            VideoDocument videoDocument = doc.getAdapter(VideoDocument.class);
            return transformVideo(videoDocument.getVideo().getBlob(), transformation, doc.getCoreSession());
        } else {
            throw new NuxeoException(String.format("Document %s cannot be transformed", doc.getId()));
        }
    }

    @Override
    public Blob transformPicture(Blob blob, Transformation transformation, CoreSession session) {
        ConversionService conversionService = Framework.getService(ConversionService.class);

        BlobHolder conversionResult = conversionService.convert("dynamicImageResizer", new SimpleBlobHolder(blob),
                transformation.toMap());
        Blob convertedBlob = conversionResult.getBlob();

        MimetypeRegistry registry = Framework.getService(MimetypeRegistry.class);
        String mimetype = registry.getMimetypeFromExtension(transformation.getFormat());

        convertedBlob.setFilename(getTargetFileName(blob, transformation));
        convertedBlob.setMimeType(mimetype);

        try {
            Blob watermarkBlob = getWatermarkBlob(transformation, session);
            if (watermarkBlob == null) {
                return convertedBlob;
            }

            Map<String, Serializable> params = new HashMap<>();
            params.put("format", transformation.getFormat());
            params.put("gravity","Center");
            params.put("watermarkFilePath", watermarkBlob.getCloseableFile().file.getPath());
            BlobHolder watermarkResult = conversionService.convert("composeGravityWatermarkedImage",
                    new SimpleBlobHolder(convertedBlob), params);
            Blob watermarkedBlob = watermarkResult.getBlob();
            watermarkedBlob.setFilename(getTargetFileName(blob, transformation));
            watermarkedBlob.setMimeType(mimetype);
            return watermarkedBlob;
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public Blob transformVideo(Blob blob, Transformation transformation, CoreSession session) {
        Map<String, Serializable> params = transformation.toMap();
        String converterName = "dynamicVideoTransform";

        try {
            Blob watermarkBlob = getWatermarkBlob(transformation, session);
            if (watermarkBlob != null) {
                converterName = "dynamicVideoTransformWithWatermark";
                params.put("watermarkFilePath", watermarkBlob.getCloseableFile().file.getPath());
            }
        } catch (IOException e) {
            throw new NuxeoException(e);
        }

        ConversionService conversionService = Framework.getService(ConversionService.class);
        BlobHolder conversionResult = conversionService.convert(converterName, new SimpleBlobHolder(blob), params);
        Blob convertedBlob = conversionResult.getBlob();
        MimetypeRegistry registry = Framework.getService(MimetypeRegistry.class);
        String mimetype = registry.getMimetypeFromExtension(transformation.getFormat());
        convertedBlob.setFilename(getTargetFileName(blob, transformation));
        convertedBlob.setMimeType(mimetype);
        return convertedBlob;
    }

    public Blob getWatermarkBlob(Transformation transformation, CoreSession session) throws IOException {
        // compose base watermark on target image size canvas
        // get base watermark
        Blob baseWatermark = getBaseWatermark(transformation, session);
        if (baseWatermark == null) {
            return null;
        }
        // get blank canvas
        Blob canvas = getTransparentCanvas(transformation.getWidth(), transformation.getHeight());
        // compose
        ConversionService conversionService = Framework.getService(ConversionService.class);
        String converterName;
        Map<String, Serializable> params = new HashMap<>();
        params.put("watermarkFilePath", baseWatermark.getCloseableFile().file.getPath());
        String watermarkGravity = transformation.getWatermarkGravity();
        if ("tile".equals(watermarkGravity)) {
            converterName = "composeTileWatermarkedImage";
        } else {
            converterName = "composeGravityWatermarkedImage";
            params.put("gravity", transformation.getWatermarkGravity());
        }
        params.put("format", "png");
        BlobHolder watermarkHolder = conversionService.convert(converterName, new SimpleBlobHolder(canvas),
                params);
        return watermarkHolder.getBlob();
    }

    public Blob getWatermarkBlobFromText(Transformation transformation) {
        ConversionService conversionService = Framework.getService(ConversionService.class);
        BlobHolder result = conversionService.convert("text2WatermarkImage",
                new SimpleBlobHolder(new StringBlob(transformation.getTextWatermark())), transformation.toMap());
        return result.getBlob();
    }

    public Blob getWatermarkFromId(String watermarkId, Transformation transformation, CoreSession session) {
        // assume the id is a documentId
        DocumentModel watermarkDoc = session.getDocument(new IdRef(watermarkId));

        long canvasWidth = transformation.getWidth();
        long watermarkWidth = Math.max(canvasWidth / 4, 256);

        Transformation watermarkTransformation = new ImageTransformationBuilder(watermarkDoc).width(watermarkWidth)
                                                                                             .format(PNG)
                                                                                             .build();
        return transform(watermarkDoc, watermarkTransformation);
    }

    public String getTargetFileName(Blob blob, Transformation transformation) {
        String name = FilenameUtils.removeExtension(blob.getFilename());
        return String.format("%s_%dx%d.%s", name, transformation.getWidth(), transformation.getHeight(),
                transformation.getFormat());
    }

    public Blob getTransparentCanvas(long width, long height) {
        ConversionService conversionService = Framework.getService(ConversionService.class);
        Map<String, Serializable> params = new HashMap<>();
        params.put("color", "none");
        params.put("height", "" + height);
        params.put("width", "" + width);
        // get transparent canvas with same dimension as the final transformed image
        BlobHolder blobHolder = conversionService.convert("solidCanvasGenerator",
                new SimpleBlobHolder(new StringBlob("","void")), params);
        return blobHolder.getBlob();
    }

    public Blob getBaseWatermark(Transformation transformation, CoreSession session) {
        Blob baseBlob = null;
        if (transformation.getImageWatermark() != null) {
            baseBlob = transformation.getImageWatermark();
        } else if (StringUtils.isNotBlank(transformation.getWatermarkId())) {
            baseBlob = getWatermarkFromId(transformation.getWatermarkId(), transformation, session);
        } else if (StringUtils.isNotBlank(transformation.getTextWatermark())) {
            baseBlob = getWatermarkBlobFromText(transformation);
        }
        return baseBlob;
    }
}
