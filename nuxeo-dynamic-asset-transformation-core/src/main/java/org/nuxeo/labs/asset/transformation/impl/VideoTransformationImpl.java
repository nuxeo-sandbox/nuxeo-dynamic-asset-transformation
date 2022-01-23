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

package org.nuxeo.labs.asset.transformation.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class VideoTransformationImpl extends ImageTransformationImpl {

    protected String videoCodec;
    protected String audioCodec;
    protected long fromTimeInMs;
    protected long toTimeInMs;

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public long getFromTimeInMs() {
        return fromTimeInMs;
    }

    public void setFromTimeInMs(long fromTimeInMs) {
        this.fromTimeInMs = fromTimeInMs;
    }

    public long getToTimeInMs() {
        return toTimeInMs;
    }

    public void setToTimeInMs(long toTimeInMs) {
        this.toTimeInMs = toTimeInMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoTransformationImpl)) return false;
        if (!super.equals(o)) return false;
        VideoTransformationImpl that = (VideoTransformationImpl) o;
        return fromTimeInMs == that.fromTimeInMs && toTimeInMs == that.toTimeInMs && Objects.equals(videoCodec, that.videoCodec) && Objects.equals(audioCodec, that.audioCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), videoCodec, audioCodec, fromTimeInMs, toTimeInMs);
    }

    @Override
    public Map<String, Serializable> toMap() {
        Map<String, Serializable> map =  super.toMap();
        map.put("videoCodec", videoCodec);
        map.put("audioCodec", audioCodec);
        map.put("fromTimeInMs",""+fromTimeInMs);
        map.put("toTimeInMs",""+toTimeInMs);
        return map;
    }
}
