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

package org.nuxeo.labs.asset.transformation.impl.builder;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.ecm.platform.video.VideoInfo;
import org.nuxeo.labs.asset.transformation.api.CropBox;

import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_FACET;

public class VideoTransformationBuilder extends AbstractTransformationBuilder{

    protected DocumentModel doc;
    protected VideoInfo videoInfo;

    public VideoTransformationBuilder(DocumentModel doc) {
        if (!doc.hasFacet(VIDEO_FACET)) {
            throw new IllegalArgumentException("doc is Not a Video document");
        }
        this.doc = doc;
        VideoDocument videoDocument = doc.getAdapter(VideoDocument.class);
        this.videoInfo = videoDocument.getVideo().getVideoInfo();
    }

    public VideoTransformationBuilder(VideoInfo info) {
        this.videoInfo = info;
    }

    @Override
    protected CropBox getCropBox() {
        return new CropBox(0,0,this.videoInfo.getWidth(), this.videoInfo.getHeight());
    }

    @Override
    protected String getDefaultFormat() {
        return "mp4";
    }

    @Override
    protected String getDefaultColorSpace() {
        return "sRGB";
    }

    @Override
    protected String getDefaultBackgroundColor() {
        return "transparent";
    }

    @Override
    protected int getDefaultCompressionLevel() {
        return 90;
    }
}
