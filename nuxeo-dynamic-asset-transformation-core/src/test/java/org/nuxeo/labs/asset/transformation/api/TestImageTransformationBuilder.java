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

package org.nuxeo.labs.asset.transformation.api;

import static org.nuxeo.labs.asset.transformation.TestFeature.HEIGHT;
import static org.nuxeo.labs.asset.transformation.TestFeature.WIDTH;
import static org.nuxeo.labs.asset.transformation.impl.Constants.JPG;
import static org.nuxeo.labs.asset.transformation.impl.Constants.PNG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.labs.asset.transformation.TestFeature;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ TestFeature.class })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestImageTransformationBuilder {

    @Inject
    protected CoreSession session;

    @Inject
    protected TestFeature testFeature;

    @Test
    public void testDocConstructor() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);
        Transformation transformation = new ImageTransformationBuilder(doc).build();
        Assert.assertEquals(WIDTH, transformation.getWidth());
        Assert.assertEquals(TestFeature.HEIGHT, transformation.getHeight());
        Assert.assertEquals(JPG, transformation.getFormat());
    }

    @Test
    public void testWithNoParameters() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Transformation transformation = new ImageTransformationBuilder(imageInfo).build();
        Assert.assertEquals(WIDTH, transformation.getWidth());
        Assert.assertEquals(TestFeature.HEIGHT, transformation.getHeight());
        Assert.assertEquals(JPG, transformation.getFormat());

    }

    @Test
    public void testWithAllParameters() throws IOException {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        CropBox box = new CropBox(0, 100, 100, 100);
        Transformation transformation = new ImageTransformationBuilder(imageInfo).width(50)
                                                                                 .height(50)
                                                                                 .cropBox(box)
                                                                                 .format(PNG)
                                                                                 .colorSpace("RGB")
                                                                                 .textWatermark("Hello")
                                                                                 .imageWatermark(
                                                                                         new StringBlob("TheBlob"))
                                                                                 .watermarkId("TheNiceWatermark")
                                                                                 .backgroundColor("white")
                                                                                 .compressionLevel(10)
                                                                                 .build();
        Assert.assertEquals(50, transformation.getWidth());
        Assert.assertEquals(50, transformation.getHeight());
        Assert.assertEquals(PNG, transformation.getFormat());
        Assert.assertEquals(box, transformation.getCropBox());
        Assert.assertEquals("RGB", transformation.getColorSpace());
        Assert.assertEquals("white", transformation.getBackgroundColor());
        Assert.assertEquals("Hello", transformation.getTextWatermark());
        Assert.assertEquals("TheBlob", transformation.getImageWatermark().getString());
        Assert.assertEquals("TheNiceWatermark",transformation.getWatermarkId());
    }

    @Test
    public void testKeepOriginalRatioFromWidth() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Transformation transformation = new ImageTransformationBuilder(imageInfo).width(600).build();
        Assert.assertEquals(600, transformation.getWidth());
        Assert.assertEquals(400, transformation.getHeight());
        Assert.assertEquals(JPG, transformation.getFormat());
    }

    @Test
    public void testKeepOriginalRatioFromHeight() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Transformation transformation = new ImageTransformationBuilder(imageInfo).height(400).format(PNG).build();
        Assert.assertEquals(600, transformation.getWidth());
        Assert.assertEquals(400, transformation.getHeight());
        Assert.assertEquals(PNG, transformation.getFormat());
    }

    @Test
    public void testWithCropRatio() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Transformation transformation = new ImageTransformationBuilder(imageInfo).cropRatio(1.0).build();
        Assert.assertEquals(TestFeature.HEIGHT, transformation.getWidth());
        Assert.assertEquals(TestFeature.HEIGHT, transformation.getHeight());
    }

    @Test
    public void testWithNoCrop() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Transformation transformation = new ImageTransformationBuilder(imageInfo).build();
        Assert.assertNotNull(transformation.getCropBox());
        Assert.assertEquals(WIDTH, transformation.getCropBox().getWidth());
        Assert.assertEquals(HEIGHT, transformation.getCropBox().getHeight());
    }

    @Test
    public void testWithPreset() {
        ImageInfo imageInfo = TestFeature.getImageInfo();
        Map<String, CropBox> cropPresets = new HashMap<>();
        cropPresets.put("2.00", new CropBox(0, 0, 200, 100));
        Transformation transformation = new ImageTransformationBuilder(imageInfo, cropPresets).cropRatio(2.0).build();
        Assert.assertNotNull(transformation.getCropBox());
        Assert.assertEquals(200, transformation.getCropBox().getWidth());
        Assert.assertEquals(100, transformation.getCropBox().getHeight());
    }

}
