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

package org.nuxeo.labs.asset.transformation.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.platform.picture.api.ImagingService;
import org.nuxeo.ecm.platform.picture.api.PictureView;
import org.nuxeo.ecm.platform.picture.api.PictureViewImpl;
import org.nuxeo.ecm.platform.picture.api.adapters.MultiviewPicture;


@Operation(id =  AddPictureViewOp.ID, category = Constants.CAT_DOCUMENT, label = "Add Picture View", description = "Add an picture view to a document which holds the Picture Facet")
public class AddPictureViewOp {

    public static final String ID = "Picture.AddView";

    @Param(name = "viewTitle", required = true)
    String title;

    @Param(name = "blob", required = true)
    Blob blob;

    @Param(name = "save", required = false)
    boolean save = false;

    @Context
    CoreSession session;

    @Context
    ImagingService imagingService;

    @OperationMethod
    public DocumentModel run(DocumentModel document) {
        MultiviewPicture adapter = document.getAdapter(MultiviewPicture.class);
        ImageInfo imageInfo = imagingService.getImageInfo(blob);
        PictureView pictureView = new PictureViewImpl();
        pictureView.setTitle(title);
        pictureView.setBlob(blob);
        pictureView.setImageInfo(imageInfo);
        adapter.addView(pictureView);
        if(save) {
            session.saveDocument(document);
        }
        return document;
    }

}
