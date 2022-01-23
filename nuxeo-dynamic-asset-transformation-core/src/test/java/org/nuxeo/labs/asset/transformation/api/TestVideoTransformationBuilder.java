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
import org.nuxeo.labs.asset.transformation.impl.VideoTransformationImpl;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.labs.asset.transformation.impl.builder.VideoTransformationBuilder;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.labs.asset.transformation.TestFeature.HEIGHT;
import static org.nuxeo.labs.asset.transformation.TestFeature.WIDTH;
import static org.nuxeo.labs.asset.transformation.impl.Constants.JPG;
import static org.nuxeo.labs.asset.transformation.impl.Constants.PNG;

@RunWith(FeaturesRunner.class)
@Features({TestFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestVideoTransformationBuilder {

    @Inject
    protected CoreSession session;

    @Test
    public void testWithVideoInfo() {
        VideoTransformationImpl transformation = new VideoTransformationBuilder(TestFeature.getVideoInfo()).build();
        Assert.assertNotNull(transformation.getCropBox());
        Assert.assertEquals(WIDTH,transformation.getWidth());
        Assert.assertEquals(HEIGHT,transformation.getHeight());
        Assert.assertEquals("mp4",transformation.getFormat());
        Assert.assertEquals("libx264",transformation.getVideoCodec());
        Assert.assertEquals("aac",transformation.getAudioCodec());
    }

    @Test
    public void testVideoDocumentConstructor() {
        DocumentModel doc = TestFeature.getDocWithVideoInfo(session);
        VideoTransformationImpl transformation = new VideoTransformationBuilder(doc).format("webm").build();
        Assert.assertEquals(WIDTH,transformation.getWidth());
        Assert.assertEquals(HEIGHT,transformation.getHeight());
        Assert.assertEquals("webm",transformation.getFormat());
        Assert.assertEquals("libvpx-vp9",transformation.getVideoCodec());
        Assert.assertEquals("libvorbis",transformation.getAudioCodec());
    }

    @Test
    public void testWithVideoAllFormatInfo() {
        VideoTransformationImpl transformation = new VideoTransformationBuilder(TestFeature.getVideoInfo()).audioCodec("libvorbis").videoCodec("libx265").format("mp4").build();
        Assert.assertNotNull(transformation.getCropBox());
        Assert.assertEquals(WIDTH,transformation.getWidth());
        Assert.assertEquals(HEIGHT,transformation.getHeight());
        Assert.assertEquals("mp4",transformation.getFormat());
        Assert.assertEquals("libx265",transformation.getVideoCodec());
        Assert.assertEquals("libvorbis",transformation.getAudioCodec());
    }

    @Test
    public void testWithVideoCropRatio() {
        VideoTransformationImpl transformation = new VideoTransformationBuilder(TestFeature.getVideoInfo()).cropRatio(1.0d).build();
        Assert.assertNotNull(transformation.getCropBox());
        Assert.assertEquals(HEIGHT,transformation.getWidth());
        Assert.assertEquals(HEIGHT,transformation.getHeight());
    }

    @Test
    public void testWithVideoCrop() {
        CropBox box = new CropBox(100,100,50,50);
        VideoTransformationImpl transformation = new VideoTransformationBuilder(TestFeature.getVideoInfo()).cropBox(box).build();
        Assert.assertEquals(box,transformation.getCropBox());
        Assert.assertEquals(box.getWidth(),transformation.getWidth());
        Assert.assertEquals(box.getHeight(),transformation.getHeight());
    }

}
