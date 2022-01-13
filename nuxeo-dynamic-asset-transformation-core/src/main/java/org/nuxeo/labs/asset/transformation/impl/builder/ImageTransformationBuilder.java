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
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.labs.asset.transformation.adapter.CropDocumentAdapter;
import org.nuxeo.labs.asset.transformation.api.CropBox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;
import static org.nuxeo.labs.asset.transformation.impl.Constants.JPG;
import static org.nuxeo.labs.asset.transformation.impl.Constants.SRGB;
import static org.nuxeo.labs.asset.transformation.impl.Constants.TRANSPARENT;

public class ImageTransformationBuilder extends AbstractTransformationBuilder {

    protected DocumentModel doc;
    protected ImageInfo imageInfo;
    protected Map<String,CropBox> cropPresets = new HashMap<>();


    public ImageTransformationBuilder(DocumentModel doc) {
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

    public ImageTransformationBuilder(ImageInfo info) {
        this.imageInfo = info;
    }

    public ImageTransformationBuilder(ImageInfo info, Map<String,CropBox> cropPresets) {
        this.imageInfo = info;
        this.cropPresets = cropPresets;
    }
    
    protected CropBox getCropBox() {
        if (this.cropRatio > 0) {
            //get preset if it exists
            CropBox box = cropPresets.get(String.format("%.2f",cropRatio));
            return box != null ? box : new CropBox(this.imageInfo,this.cropRatio);
        } else {
            return new CropBox(0,0,this.imageInfo.getWidth(), this.imageInfo.getHeight());
        }
    }

    @Override
    protected String getDefaultFormat() {
        return JPG;
    }

    @Override
    protected String getDefaultColorSpace() {
        return SRGB;
    }

    @Override
    protected String getDefaultBackgroundColor() {
        return TRANSPARENT;
    }

    @Override
    protected int getDefaultCompressionLevel() {
        return 90;
    }
}
