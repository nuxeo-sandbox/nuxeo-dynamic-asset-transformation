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

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;
import static org.nuxeo.labs.asset.transformation.api.Transformation.DEFAULT_FORMAT;

public class TransformationBuilder {

    protected DocumentModel doc;
    protected ImageInfo imageInfo;
    protected long width = 0;
    protected long height = 0;
    protected String format ;
    protected CropBox cropBox = null;
    protected double cropRatio = 0;
    protected Map<String,CropBox> cropPresets = new HashMap<>();
    protected String textWatermark = null;
    protected Blob imageWatermark = null;
    
    public TransformationBuilder(DocumentModel doc) {
        if (!doc.hasFacet(PICTURE_FACET)) {
            throw new IllegalArgumentException("doc is Not a Picture document");
        }
        this.doc = doc;
        this.imageInfo = ImageInfo.fromMap((Map<String, Serializable>) doc.getPropertyValue("picture:info"));

        CropDocumentAdapter adapter = doc.getAdapter(CropDocumentAdapter.class);
        if (adapter != null) {
            this.cropPresets = adapter.getCrops();
        }
    }

    protected TransformationBuilder(ImageInfo info) {
        this.imageInfo = info;
    }

    protected TransformationBuilder(ImageInfo info, Map<String,CropBox> cropPresets) {
        this.imageInfo = info;
        this.cropPresets = cropPresets;
    }
    
    public TransformationBuilder width(long width) {
        this.width = width;
        return this;
    }

    public TransformationBuilder height(long height) {
        this.height = height;
        return this;
    }

    public TransformationBuilder format(String format) {
        this.format = format;
        return this;
    }

    public TransformationBuilder cropBox(CropBox box) {
        this.cropBox = box;
        return this;
    }

    public TransformationBuilder cropRatio(double ratio) {
        this.cropRatio = ratio;
        return this;
    }

    public TransformationBuilder cropBox(String crop) {
        this.cropBox = crop != null ? new CropBox(crop) : null;
        return this;
    }

    public TransformationBuilder textWatermark(String text) {
        this.textWatermark = text;
        return this;
    }

    public TransformationBuilder imageWatermark(Blob image) {
        this.imageWatermark = image;
        return this;
    }
    
    public Transformation build() {

        Transformation transformation = new TransformationImpl();

        //first, crop
        if (this.cropBox == null) {
            if (this.cropRatio > 0) {
                //get preset if it exists
                CropBox box = cropPresets.get(String.format("%.2f",cropRatio));
                this.cropBox = box != null ? box : new CropBox(this.imageInfo,this.cropRatio);
            } else {
                this.cropBox = new CropBox(0,0,this.imageInfo.getWidth(), this.imageInfo.getHeight());
            }
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

        transformation.setFormat(StringUtils.isNotEmpty(this.format) ? this.format : DEFAULT_FORMAT);

        transformation.setTextWatermark(this.textWatermark);
        transformation.setImageWatermark(this.imageWatermark);

        return transformation;
    }
}
