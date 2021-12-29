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

package org.nuxeo.labs.asset.transformation.converter;

import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.platform.convert.plugins.CommandLineConverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Text2WatermarkImageConverter extends CommandLineConverter {

    @Override
    public BlobHolder convert(BlobHolder blobHolder, Map<String, Serializable> parameters) throws ConversionException {
        String text = (String) parameters.get("textWatermark");
        int imageWidth = Integer.parseInt((String) parameters.get("width"));

        int watermarkMaxRepeat = Integer.parseInt(this.initParameters.getOrDefault("watermarkMaxRepeat","4"));
        int watermarkMinRepeatWidthInPx = Integer.parseInt(this.initParameters.getOrDefault("watermarkMinRepeatWidthInPx","256"));

        int watermarkWidth;

        if (imageWidth / watermarkMinRepeatWidthInPx > watermarkMaxRepeat) {
            // repeat 4 times
            watermarkWidth = imageWidth / watermarkMaxRepeat;
        } else if (imageWidth / watermarkMinRepeatWidthInPx < 1) {
            // no repeat, enough space for 1 watermark instance
            watermarkWidth = imageWidth;
        } else {
            // repeat as many times as width permit
            watermarkWidth = watermarkMinRepeatWidthInPx;
        }

        //try to fit text
        int pointSize = Math.max(watermarkWidth / text.length(), 1);
        Map<String, Serializable> params = new HashMap<>();
        params.put("textWatermark", text);
        params.put("color", parameters.getOrDefault("textColor",this.initParameters.getOrDefault("textColor","graya(50%, 0.8)")));
        params.put("pointSize", "" + pointSize);
        params.put("width", "" + watermarkWidth);
        params.put("height", "" + watermarkWidth);

        return super.convert(blobHolder, params);
    }
}
