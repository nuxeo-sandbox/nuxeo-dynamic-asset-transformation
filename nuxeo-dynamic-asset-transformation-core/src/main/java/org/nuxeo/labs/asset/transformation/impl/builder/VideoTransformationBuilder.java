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

import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_FACET;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.ecm.platform.video.VideoInfo;
import org.nuxeo.labs.asset.transformation.api.CropBox;
import org.nuxeo.labs.asset.transformation.impl.VideoTransformationImpl;

public class VideoTransformationBuilder
        extends AbstractTransformationBuilder<VideoTransformationBuilder, VideoTransformationImpl> {

    protected DocumentModel doc;

    protected VideoInfo videoInfo;

    protected String videoCodec;

    protected String audioCodec;

    protected long fromTimeInMs;

    protected long toTimeInMs;

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
    public VideoTransformationBuilder getThis() {
        return this;
    }

    public VideoTransformationBuilder fromTimeInMs(long fromTimeInMs) {
        this.fromTimeInMs = fromTimeInMs;
        return this;
    }

    public VideoTransformationBuilder toTimeInMs(long toTimeInMs) {
        this.toTimeInMs = toTimeInMs;
        return this;
    }

    public VideoTransformationBuilder videoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
        return this;
    }

    public VideoTransformationBuilder audioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
        return this;
    }

    @Override
    public VideoTransformationImpl build() {
        VideoTransformationImpl transformation = super.build();
        if (StringUtils.isBlank(videoCodec)) {
            switch (transformation.getFormat()) {
            case "mp4":
                transformation.setVideoCodec("libx264");
                break;
            case "webm":
                transformation.setVideoCodec("libvpx-vp9");
                break;
            default:
                throw new NuxeoException("no default codec for format " + transformation.getFormat());
            }
        } else {
            transformation.setVideoCodec(videoCodec);
        }

        if (StringUtils.isBlank(audioCodec)) {
            switch (transformation.getFormat()) {
            case "mp4":
                transformation.setAudioCodec("aac");
                break;
            case "webm":
                transformation.setAudioCodec("libvorbis");
                break;
            default:
                throw new NuxeoException("no default audio for format " + transformation.getFormat());
            }
        } else {
            transformation.setAudioCodec(audioCodec);
        }

        return transformation;
    }

    @Override
    protected VideoTransformationImpl getNewEmptyTransformation() {
        return new VideoTransformationImpl();
    }

    @Override
    protected CropBox getCropBox() {
        if (this.cropRatio > 0) {
            return new CropBox(this.videoInfo, this.cropRatio);
        } else {
            return new CropBox(0, 0, this.videoInfo.getWidth(), this.videoInfo.getHeight());
        }
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
        return 30;
    }
}
