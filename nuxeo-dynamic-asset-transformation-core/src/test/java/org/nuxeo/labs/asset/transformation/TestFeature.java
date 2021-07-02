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

package org.nuxeo.labs.asset.transformation;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

import java.io.Serializable;

@Features({AutomationFeature.class})
@Deploy({
        "org.nuxeo.ecm.platform.picture.core",
        "org.nuxeo.ecm.platform.tag",
        "nuxeo-dynamic-asset-transformation-core"
})
public class TestFeature implements RunnerFeature {


    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;

    public static DocumentModel getDocWithPictureInfo(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(session.getRootDocument().getPathAsString(),"Picture","Picture");
        ImageInfo imageInfo = getImageInfo();
        doc.setPropertyValue("picture:info", (Serializable) imageInfo.toMap());
        return session.createDocument(doc);
    }

    public static ImageInfo getImageInfo() {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setWidth(WIDTH);
        imageInfo.setHeight(HEIGHT);
        return imageInfo;
    }
}
