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

package org.nuxeo.labs.asset.transformation.adapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.labs.asset.transformation.api.CropBox;

public class CropDocumentAdapter {

    public static final String CROPS_PROPERTY = "crops:crops";

    public static final String CROP_NAME_SUBPROPERTY = "name";

    protected DocumentModel doc;

    public CropDocumentAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    public Map<String, CropBox> getCrops() {
        Map<String, CropBox> result = new HashMap<>();
        List<Map<String, Serializable>> crops = (List<Map<String, Serializable>>) doc.getPropertyValue(CROPS_PROPERTY);
        for (Map<String, Serializable> crop : crops) {
            result.put((String) crop.get(CROP_NAME_SUBPROPERTY), new CropBox(crop));
        }
        return result;
    }

    public CropBox getCropByName(String name) {
        List<Map<String, Serializable>> crops = (List<Map<String, Serializable>>) doc.getPropertyValue(CROPS_PROPERTY);
        crops = crops.stream()
                     .filter(element -> name.equals(element.get(CROP_NAME_SUBPROPERTY)))
                     .collect(Collectors.toList());
        return crops.size() > 0 ? new CropBox(crops.get(0)) : null;
    }

    public void addCrop(String name, CropBox box) {
        Map<String, CropBox> crops = getCrops();
        crops.put(name, box);
        saveCrops(crops);
    }

    public void saveCrops(Map<String, CropBox> crops) {
        List<Map<String, Serializable>> values = crops.entrySet().stream().map(entry -> {
            Map<String, Serializable> map = entry.getValue().toMap();
            map.put(CROP_NAME_SUBPROPERTY, entry.getKey());
            return map;
        }).collect(Collectors.toList());
        doc.setPropertyValue(CROPS_PROPERTY, (Serializable) values);
    }

}
