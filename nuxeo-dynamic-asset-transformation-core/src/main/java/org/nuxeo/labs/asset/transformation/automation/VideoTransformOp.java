/*
 * (C) Copyright 2022 Nuxeo (http://nuxeo.com/) and others.
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

package org.nuxeo.labs.asset.transformation.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.builder.VideoTransformationBuilder;
import org.nuxeo.labs.asset.transformation.service.DynamicTransformationService;

@Operation(id = VideoTransformOp.ID, category = Constants.CAT_DOCUMENT, label = "Video Transformation", description = "Perform video transformation such as resize and format change")
public class VideoTransformOp {

    public static final String ID = "Blob.VideoTransform";

    @Param(name = "width", required = false)
    long width;

    @Param(name = "height", required = false)
    long height;

    @Param(name = "format", required = false)
    String format;

    @Param(name = "videoCodec", required = false)
    String videoCodec;

    @Param(name = "audioCodec", required = false)
    String audioCodec;

    // using a string because automation type adapters don't support integer to float conversion
    // and web browsers will stringify integer numbers without decimals
    @Param(name = "autoCropRatio", required = false)
    String autoCropRatio;

    @Param(name = "crop", required = false)
    String crop;

    @Param(name = "textWatermark", required = false)
    String textWatermark;

    @Param(name = "imageWatermark", required = false)
    Blob imageWatermark;

    @Param(name = "watermarkId", required = false)
    String watermarkId;

    @Param(name = "compressionLevel", required = false)
    int compressionLevel;

    @Context
    DynamicTransformationService transformationService;

    @OperationMethod
    public Blob run(DocumentModel document) {
        Transformation transformation = new VideoTransformationBuilder(document).videoCodec(videoCodec)
                                                                                .audioCodec(audioCodec)
                                                                                .width(width)
                                                                                .height(height)
                                                                                .cropRatio(autoCropRatio)
                                                                                .cropBox(crop)
                                                                                .format(format)
                                                                                .textWatermark(textWatermark)
                                                                                .imageWatermark(imageWatermark)
                                                                                .watermarkId(watermarkId)
                                                                                .compressionLevel(compressionLevel)
                                                                                .build();
        return transformationService.transform(document, transformation);
    }
}
