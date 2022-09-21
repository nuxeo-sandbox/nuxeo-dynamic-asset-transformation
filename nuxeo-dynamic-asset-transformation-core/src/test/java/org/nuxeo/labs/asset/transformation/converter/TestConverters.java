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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.labs.asset.transformation.TestFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ TestFeature.class })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestConverters {

    @Inject
    ConversionService conversionService;

    @Test
    public void TestText2WatermarkConverter() {
        Map<String, Serializable> params = new HashMap<>();
        params.put("textWatermark", "Hello");
        params.put("color", "graya(50%, 0.5)");
        params.put("pointSize", "20");
        params.put("width", "240");
        params.put("height", "120");
        BlobHolder result = conversionService.convert("text2WatermarkImage",
                new SimpleBlobHolder(new StringBlob("hello")), params);
        Blob watermarkImage = result.getBlob();
        Assert.assertNotNull(watermarkImage);
        Assert.assertEquals("image/png", watermarkImage.getMimeType());
    }

    @Test
    public void TestTileWatermarkConverter() throws IOException {
        Blob inputBlob = new FileBlob(new File(getClass().getResource("/files/small.jpg").getPath()));
        inputBlob.setMimeType("image/jpeg");

        Blob watermarkBlob = new FileBlob(new File(getClass().getResource("/files/text.png").getPath()));
        watermarkBlob.setMimeType("image/png");

        Map<String, Serializable> params = new HashMap<>();
        params.put("format", "jpeg");
        params.put("watermarkFilePath", watermarkBlob.getCloseableFile().file.getPath());

        BlobHolder result = conversionService.convert("composeTileWatermarkedImage", new SimpleBlobHolder(inputBlob),
                params);
        Blob resultBlob = result.getBlob();
        Assert.assertNotNull(resultBlob);
    }

    @Test
    public void TestGravityWatermarkConverter() throws IOException {
        Blob inputBlob = new FileBlob(new File(getClass().getResource("/files/small.jpg").getPath()));
        inputBlob.setMimeType("image/jpeg");

        Blob watermarkBlob = new FileBlob(new File(getClass().getResource("/files/text.png").getPath()));
        watermarkBlob.setMimeType("image/png");

        Map<String, Serializable> params = new HashMap<>();
        params.put("format", "jpeg");
        params.put("gravity", "Center");
        params.put("watermarkFilePath", watermarkBlob.getCloseableFile().file.getPath());

        BlobHolder result = conversionService.convert("composeGravityWatermarkedImage", new SimpleBlobHolder(inputBlob),
                params);
        Blob resultBlob = result.getBlob();
        Assert.assertNotNull(resultBlob);
    }

    @Test
    public void TestSolidCanvasGeneratorConverter() {
        Map<String, Serializable> params = new HashMap<>();
        params.put("color", "none");
        params.put("width", "240");
        params.put("height", "240");
        BlobHolder result = conversionService.convert("solidCanvasGenerator", new SimpleBlobHolder(new StringBlob("")), params);
        Blob canvas = result.getBlob();
        Assert.assertNotNull(canvas);
        Assert.assertEquals("image/png", canvas.getMimeType());
    }
}
