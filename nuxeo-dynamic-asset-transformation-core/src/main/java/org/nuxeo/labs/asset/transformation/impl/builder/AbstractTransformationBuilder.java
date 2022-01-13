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

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.labs.asset.transformation.api.CropBox;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.ImageTransformationImpl;

public abstract class AbstractTransformationBuilder {

    protected long width = 0;
    protected long height = 0;
    protected String format ;
    protected CropBox cropBox = null;
    protected double cropRatio = 0;
    protected String textWatermark = null;
    protected Blob imageWatermark = null;
    protected String colorSpace = null;
    protected String backgroundColor = null;
    protected int compressionLevel = 0;


    public AbstractTransformationBuilder width(long width) {
        this.width = width;
        return this;
    }

    public AbstractTransformationBuilder height(long height) {
        this.height = height;
        return this;
    }

    public AbstractTransformationBuilder format(String format) {
        this.format = format;
        return this;
    }

    public AbstractTransformationBuilder cropBox(CropBox box) {
        this.cropBox = box;
        return this;
    }

    public AbstractTransformationBuilder cropRatio(double ratio) {
        this.cropRatio = ratio;
        return this;
    }

    public AbstractTransformationBuilder cropBox(String crop) {
        this.cropBox = crop != null ? new CropBox(crop) : null;
        return this;
    }

    public AbstractTransformationBuilder textWatermark(String text) {
        this.textWatermark = text;
        return this;
    }

    public AbstractTransformationBuilder imageWatermark(Blob image) {
        this.imageWatermark = image;
        return this;
    }

    public AbstractTransformationBuilder colorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    public AbstractTransformationBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public AbstractTransformationBuilder compressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    public Transformation build() {

        Transformation transformation = new ImageTransformationImpl();

        //first, crop
        if (this.cropBox == null) {
            this.cropBox = getCropBox();
        }

        transformation.setCropBox(this.cropBox);

        double imageRatio = cropBox.getRatio();

        if (this.width <= 0 && this.height <= 0) {
          this.width = cropBox.getWidth();
          this.height = cropBox.getHeight();
        } else if (this.width <= 0) {
            this.width = (int) (this.height * imageRatio);
        } else if (this.height <= 0) {
            this.height = (int) (this.width * (1 / imageRatio));
        }

        transformation.setWidth(this.width);
        transformation.setHeight(this.height);

        transformation.setFormat(StringUtils.isNotEmpty(this.format) ? this.format : getDefaultFormat());

        transformation.setTextWatermark(this.textWatermark);
        transformation.setImageWatermark(this.imageWatermark);

        transformation.setColorSpace(StringUtils.isNotEmpty(this.colorSpace) ? this.colorSpace : getDefaultColorSpace());
        transformation.setBackgroundColor(StringUtils.isNotEmpty(this.backgroundColor) ? this.backgroundColor : getDefaultBackgroundColor());
        transformation.setCompressionLevel(this.compressionLevel > 0 ? this.compressionLevel : getDefaultCompressionLevel());

        return transformation;
    }

    protected abstract CropBox getCropBox();

    protected abstract String getDefaultFormat();

    protected abstract String getDefaultColorSpace();

    protected abstract String getDefaultBackgroundColor();

    protected abstract int getDefaultCompressionLevel();
}
