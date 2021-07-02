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

public class TransformationImpl implements Transformation {

    protected int width;
    protected int height;
    protected String format;
    protected CropBox cropBox;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
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
    }

    @Override
    public Map<String, Serializable> toMap() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("width",""+width);
        map.put("height",""+height);
        map.put("format",format);
        map.put("crop",cropBox.toString());
        return map;
    }

}
