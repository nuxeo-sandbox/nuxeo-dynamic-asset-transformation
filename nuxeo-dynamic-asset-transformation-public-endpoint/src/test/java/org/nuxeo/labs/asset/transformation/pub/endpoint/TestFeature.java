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

package org.nuxeo.labs.asset.transformation.pub.endpoint;

import java.io.File;
import java.io.Serializable;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ WebEngineFeature.class, AutomationFeature.class })
@Deploy({ "org.nuxeo.ecm.platform.picture.core", "org.nuxeo.ecm.platform.tag", "org.nuxeo.ecm.platform.web.common",
        "nuxeo-public-download-link-core", "nuxeo-dynamic-asset-transformation-core",
        "nuxeo-dynamic-asset-transformation-pub-endpoint" })
public class TestFeature implements RunnerFeature {

    public static final int WIDTH = 300;

    public static final int HEIGHT = 200;

    public static ImageInfo getImageInfo() {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setWidth(WIDTH);
        imageInfo.setHeight(HEIGHT);
        return imageInfo;
    }

    public DocumentModel getDocWithPictureInfo(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(session.getRootDocument().getPathAsString(), "Picture",
                "Picture");
        Blob blob = new FileBlob(new File(getClass().getResource("/files/small.jpg").getPath()));
        blob.setMimeType("image/jpg");
        doc.setPropertyValue("file:content", (Serializable) blob);
        ImageInfo imageInfo = getImageInfo();
        doc.setPropertyValue("picture:info", (Serializable) imageInfo.toMap());
        return session.createDocument(doc);
    }
}
