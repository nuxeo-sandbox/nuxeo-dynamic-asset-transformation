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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.labs.asset.transformation.api.CropBox;
import org.nuxeo.labs.asset.transformation.api.Transformation;

public class ImageTransformationImpl implements Transformation {

    protected long width;

    protected long height;

    protected String format;

    protected CropBox cropBox;

    protected double ratio;

    protected String textWatermark;

    protected Blob imageWatermark;

    protected String watermarkId;

    protected int compressionLevel;

    protected String colorSpace;

    protected String backgroundColor;

    @Override
    public int getCompressionLevel() {
        return compressionLevel;
    }

    @Override
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public long getWidth() {
        return width;
    }

    @Override
    public void setWidth(long width) {
        this.width = width;
    }

    @Override
    public long getHeight() {
        return height;
    }

    @Override
    public void setHeight(long height) {
        this.height = height;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public CropBox getCropBox() {
        return cropBox;
    }

    @Override
    public void setCropBox(CropBox box) {
        this.cropBox = box;
        this.ratio = this.cropBox.getRatio();
    }

    @Override
    public double getCropRatio() {
        return ratio;
    }

    @Override
    public void setCropRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public String getTextWatermark() {
        return textWatermark;
    }

    @Override
    public void setTextWatermark(String textWatermark) {
        this.textWatermark = textWatermark;
    }

    @Override
    public Blob getImageWatermark() {
        return imageWatermark;
    }

    @Override
    public void setImageWatermark(Blob imageWatermark) {
        this.imageWatermark = imageWatermark;
    }

    @Override
    public String getWatermarkId() {
        return watermarkId;
    }

    @Override
    public void setWatermarkId(String watermarkId) {
        this.watermarkId = watermarkId;
    }

    @Override
    public String getColorSpace() {
        return colorSpace;
    }

    @Override
    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    @Override
    public String getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ImageTransformationImpl that = (ImageTransformationImpl) o;
        return width == that.width && height == that.height && Double.compare(that.ratio, ratio) == 0
                && compressionLevel == that.compressionLevel && Objects.equals(format, that.format)
                && Objects.equals(cropBox, that.cropBox) && Objects.equals(textWatermark, that.textWatermark)
                && Objects.equals(imageWatermark, that.imageWatermark) && Objects.equals(colorSpace, that.colorSpace)
                && Objects.equals(backgroundColor, that.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, format, cropBox, ratio, textWatermark, imageWatermark, compressionLevel,
                colorSpace, backgroundColor);
    }

    @Override
    public Map<String, Serializable> toMap() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("width", "" + width);
        map.put("height", "" + height);
        map.put("format", format);
        map.put("crop", cropBox);
        map.put("textWatermark", textWatermark);
        map.put("colorSpace", colorSpace);
        map.put("backgroundColor", backgroundColor);
        map.put("compressionLevel", "" + compressionLevel);
        return map;
    }

}
