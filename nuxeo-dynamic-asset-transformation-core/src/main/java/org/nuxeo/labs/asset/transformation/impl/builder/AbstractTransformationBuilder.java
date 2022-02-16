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

public abstract class AbstractTransformationBuilder<T extends AbstractTransformationBuilder<T, V>, V extends Transformation> {

    protected long width = 0;

    protected long height = 0;

    protected String format;

    protected CropBox cropBox = null;

    protected double cropRatio = 0;

    protected String textWatermark = null;

    protected Blob imageWatermark = null;

    protected String watermarkId = null;

    protected String colorSpace = null;

    protected String backgroundColor = null;

    protected int compressionLevel = 0;

    /** The solution for the unchecked cast warning. */
    public abstract T getThis();

    public T width(long width) {
        this.width = width;
        return getThis();
    }

    public T height(long height) {
        this.height = height;
        return getThis();
    }

    public T format(String format) {
        this.format = format;
        return getThis();
    }

    public T cropBox(CropBox box) {
        this.cropBox = box;
        return getThis();
    }

    public T cropRatio(double ratio) {
        this.cropRatio = ratio;
        return getThis();
    }

    public T cropRatio(String ratio) {
        if (StringUtils.isNotEmpty(ratio)) {
            this.cropRatio = Double.parseDouble(ratio);
        }
        return getThis();
    }

    public T cropBox(String crop) {
        this.cropBox = crop != null ? new CropBox(crop) : null;
        return getThis();
    }

    public T textWatermark(String text) {
        this.textWatermark = text;
        return getThis();
    }

    public T imageWatermark(Blob image) {
        this.imageWatermark = image;
        return getThis();
    }

    public T watermarkId(String watermarkId) {
        this.watermarkId = watermarkId;
        return getThis();
    }

    public T colorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
        return getThis();
    }

    public T backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return getThis();
    }

    public T compressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
        return getThis();
    }

    public V build() {

        V transformation = getNewEmptyTransformation();

        // first, crop
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
        transformation.setWatermarkId(this.watermarkId);

        transformation.setColorSpace(
                StringUtils.isNotEmpty(this.colorSpace) ? this.colorSpace : getDefaultColorSpace());
        transformation.setBackgroundColor(
                StringUtils.isNotEmpty(this.backgroundColor) ? this.backgroundColor : getDefaultBackgroundColor());
        transformation.setCompressionLevel(
                this.compressionLevel > 0 ? this.compressionLevel : getDefaultCompressionLevel());

        return transformation;
    }

    protected abstract V getNewEmptyTransformation();

    protected abstract CropBox getCropBox();

    protected abstract String getDefaultFormat();

    protected abstract String getDefaultColorSpace();

    protected abstract String getDefaultBackgroundColor();

    protected abstract int getDefaultCompressionLevel();
}
