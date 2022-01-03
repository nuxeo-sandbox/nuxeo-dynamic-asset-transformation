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

package org.nuxeo.labs.asset.transformation.service;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.picture.api.PictureView;
import org.nuxeo.ecm.platform.video.Video;
import org.nuxeo.labs.asset.transformation.api.Transformation;

public interface DynamicTransformationService {

    /**
     * Transform the document main media file
     * @param doc
     * @param transformation
     * @return the transformed media file
     */
    Blob transform(DocumentModel doc, Transformation transformation);

    /**
     * Transform the document main media file
     * @param pictureView the input picture view which contains the blob and the image info
     * @param transformation
     * @return the transformed picture view
     */
    Blob transformPicture(PictureView pictureView, Transformation transformation);

    /**
     * Transform the document main media file
     * @param video the input video which contains the blob and the video info
     * @param transformation
     * @return the transformed video
     */
    Blob transformVideo(Video video, Transformation transformation);

}
