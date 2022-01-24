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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.platform.video.VideoInfo;

public class CropBox implements Serializable {

    static Pattern p = Pattern.compile("([0-9]*),([0-9]*),([0-9]*),([0-9]*)");

    long top;

    long left;

    long width;

    long height;

    public CropBox(String crop) {

        Matcher m = p.matcher(crop);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid crop format");
        } else {
            this.top = Long.parseLong(m.group(4));
            this.left = Long.parseLong(m.group(3));
            this.width = Long.parseLong(m.group(1));
            this.height = Long.parseLong(m.group(2));
        }
    }

    public CropBox(long top, long left, long width, long height) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public CropBox(Map<String, Serializable> map) {
        this.top = (long) map.get("top");
        this.left = (long) map.get("left");
        this.width = (long) map.get("width");
        this.height = (long) map.get("height");
    }

    public CropBox(ImageInfo imageInfo, double cropRatio) {
        this(imageInfo.getWidth(), imageInfo.getHeight(), cropRatio);
    }

    public CropBox(VideoInfo videoInfo, double cropRatio) {
        this(videoInfo.getWidth(), videoInfo.getHeight(), cropRatio);
    }

    public CropBox(long width, long height, double cropRatio) {
        this(0, 0, width, height);
        double originalRatio = getRatio();

        if (originalRatio == cropRatio) {
            return;
        }

        if (width < height) {
            // invert ratio
            cropRatio = 1.0 / cropRatio;
        }

        if (originalRatio < cropRatio) {
            // if target ratio is greater than the original image ratio, crop height
            int targetHeight = (int) Math.round(width / cropRatio);
            this.top = (height - targetHeight) / 2;
            this.height = targetHeight;
        } else {
            // else if the target ratio is lower than the original image ratio, crop width
            int targetWidth = (int) Math.round(height * cropRatio);
            this.left = (width - targetWidth) / 2;
            this.width = targetWidth;
        }
    }

    public long getTop() {
        return top;
    }

    public long getLeft() {
        return left;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public double getRatio() {
        return (width * (1.0)) / height;
    }

    @Override
    public String toString() {
        return String.format("%dx%d+%d+%d", width, height, left, top);
    }

    public Map<String, Serializable> toMap() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("top", top);
        map.put("left", left);
        map.put("width", width);
        map.put("height", height);
        return map;
    }
}
