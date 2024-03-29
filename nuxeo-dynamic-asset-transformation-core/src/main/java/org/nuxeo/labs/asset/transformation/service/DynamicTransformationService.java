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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.labs.asset.transformation.api.Transformation;

public interface DynamicTransformationService {

    /**
     * Transform the document main media file
     * 
     * @param doc The input document
     * @param transformation the transformation parameters
     * @return the transformed media file
     */
    Blob transform(DocumentModel doc, Transformation transformation);

    /**
     * Transform an image blob
     *
     * @param blob the input blob
     * @param transformation the transformation parameters
     * @return the transformed media file
     */
    Blob transformPicture(Blob blob, Transformation transformation, CoreSession session);

    /**
     * Transform a video blob
     *
     * @param blob the input blob
     * @param transformation the transformation parameters
     * @return the transformed media file
     */
    Blob transformVideo(Blob blob, Transformation transformation, CoreSession session);

}
