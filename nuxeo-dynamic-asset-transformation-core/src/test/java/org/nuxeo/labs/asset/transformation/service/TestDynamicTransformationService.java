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

import static org.nuxeo.labs.asset.transformation.impl.Constants.PNG;

import java.io.File;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.platform.picture.api.ImagingService;
import org.nuxeo.labs.asset.transformation.TestFeature;
import org.nuxeo.labs.asset.transformation.api.CropBox;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ TestFeature.class })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestDynamicTransformationService {

    @Inject
    protected TestFeature testFeature;

    @Inject
    protected CoreSession session;

    @Inject
    protected DynamicTransformationService transformationService;

    @Inject
    protected ImagingService imagingService;

    @Test
    public void testResizePicture() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);

        Transformation transformation = new ImageTransformationBuilder(doc).format(PNG).height(100).build();

        Blob result = transformationService.transform(doc, transformation);

        Assert.assertNotNull(result);
        Assert.assertEquals("image/png", result.getMimeType());

        ImageInfo resultInfo = imagingService.getImageInfo(result);
        Assert.assertEquals(150, resultInfo.getWidth());
        Assert.assertEquals(100, resultInfo.getHeight());
        Assert.assertEquals(PNG, resultInfo.getFormat().toLowerCase());
    }

    @Test
    public void testCropPicture() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);

        CropBox box = new CropBox(0, 100, 100, 200);
        Transformation transformation = new ImageTransformationBuilder(doc).cropBox(box).build();

        Blob result = transformationService.transform(doc, transformation);

        Assert.assertNotNull(result);
        Assert.assertEquals("image/jpeg", result.getMimeType());

        ImageInfo resultInfo = imagingService.getImageInfo(result);
        Assert.assertEquals(100, resultInfo.getWidth());
        Assert.assertEquals(200, resultInfo.getHeight());
    }

    @Test
    public void testCropAndResizePicture() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);

        CropBox box = new CropBox(0, 100, 100, 200);
        Transformation transformation = new ImageTransformationBuilder(doc).cropBox(box).height(100).build();

        Blob result = transformationService.transform(doc, transformation);
        Assert.assertEquals("image/jpeg", result.getMimeType());

        Assert.assertNotNull(result);

        ImageInfo resultInfo = imagingService.getImageInfo(result);
        Assert.assertEquals(50, resultInfo.getWidth());
        Assert.assertEquals(100, resultInfo.getHeight());
    }

    @Test
    public void testWatermarkPictureWithText() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);

        CropBox box = new CropBox(0, 100, 100, 200);
        Transformation transformation = new ImageTransformationBuilder(doc).cropBox(box)
                                                                           .height(100)
                                                                           .textWatermark("Hello Nuxeo")
                                                                           .build();

        Blob result = transformationService.transform(doc, transformation);
        Assert.assertEquals("image/jpeg", result.getMimeType());

        Assert.assertNotNull(result);

        ImageInfo resultInfo = imagingService.getImageInfo(result);
        Assert.assertEquals(50, resultInfo.getWidth());
        Assert.assertEquals(100, resultInfo.getHeight());
    }

    @Test
    public void testWatermarkPictureWithImage() {
        DocumentModel doc = testFeature.getDocWithPictureInfo(session);

        Blob watermarkBlob = new FileBlob(new File(getClass().getResource("/files/text.png").getPath()));
        watermarkBlob.setMimeType("image/png");

        CropBox box = new CropBox(0, 100, 100, 200);
        Transformation transformation = new ImageTransformationBuilder(doc).cropBox(box)
                                                                           .height(100)
                                                                           .imageWatermark(watermarkBlob)
                                                                           .build();

        Blob result = transformationService.transform(doc, transformation);
        Assert.assertEquals("image/jpeg", result.getMimeType());

        Assert.assertNotNull(result);

        ImageInfo resultInfo = imagingService.getImageInfo(result);
        Assert.assertEquals(50, resultInfo.getWidth());
        Assert.assertEquals(100, resultInfo.getHeight());
    }

}
