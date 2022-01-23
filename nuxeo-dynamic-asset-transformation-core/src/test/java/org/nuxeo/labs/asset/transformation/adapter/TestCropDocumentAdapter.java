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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.labs.asset.transformation.TestFeature;
import org.nuxeo.labs.asset.transformation.adapter.CropDocumentAdapter;
import org.nuxeo.labs.asset.transformation.api.CropBox;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(FeaturesRunner.class)
@Features({TestFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({"nuxeo-dynamic-asset-transformation-core:test-document-type.xml"})
public class TestCropDocumentAdapter {

    @Inject
    protected CoreSession session;

    @Test
    public void testAdapter() {
        DocumentModel doc = session.createDocumentModel(session.getRootDocument().getPathAsString(),"CropDocument","CropDocument");
        doc = session.createDocument(doc);
        CropDocumentAdapter adapter = doc.getAdapter(CropDocumentAdapter.class);
        Assert.assertNotNull(adapter);
    }

    @Test
    public void testGetCrop() {
        DocumentModel doc = session.createDocumentModel(session.getRootDocument().getPathAsString(),"CropDocument","CropDocument");
        List<Map<String, Serializable>> crops = new ArrayList<>();
        Map<String, Serializable> map = new HashMap<>();
        map.put("top", 1);
        map.put("left", 2);
        map.put("width", 3);
        map.put("height", 3);
        map.put("name","test");
        crops.add(map);
        doc.setPropertyValue("crops:crops", (Serializable) crops);
        doc = session.createDocument(doc);
        CropDocumentAdapter adapter = doc.getAdapter(CropDocumentAdapter.class);
        Assert.assertNotNull(adapter);
        CropBox box = adapter.getCropByName("test");
        Assert.assertNotNull(box);
    }

}

