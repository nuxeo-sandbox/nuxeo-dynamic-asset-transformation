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

package org.nuxeo.labs.asset.transformation.converter;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.platform.convert.plugins.CommandLineConverter;
import org.nuxeo.labs.asset.transformation.api.CropBox;

public class DynamicVideoConverter extends CommandLineConverter {

    @Override
    public BlobHolder convert(BlobHolder blobHolder, Map<String, Serializable> parameters) throws ConversionException {
        // Convert cropbox to ffmpeg format
        CropBox cropBox = (CropBox) parameters.get("crop");
        String ffmpegCrop = String.format("crop=%d:%d:%d:%d", cropBox.getWidth(), cropBox.getHeight(),
                cropBox.getLeft(), cropBox.getTop());
        parameters.put("crop", ffmpegCrop);
        return super.convert(blobHolder, parameters);
    }
}
