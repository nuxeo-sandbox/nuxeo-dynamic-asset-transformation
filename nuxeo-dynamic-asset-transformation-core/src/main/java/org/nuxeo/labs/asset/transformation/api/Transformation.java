/*
 * (C) Copyright  Nuxeo (http://nuxeo.com/) and others.
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
import java.util.Map;

public interface Transformation {

    String DEFAULT_FORMAT = "jpg";

    /**
     *
     * @return the target width of the transformation
     */
    long getWidth();

    /**
     *
     * @param width the target width of the transformation
     */
    void setWidth(long width);

    /**
     *
     * @return the target height of the transformation
     */
    long getHeight();

    /**
     *
     * @param height the target height of the transformation
     */
    void setHeight(long height);

    /**
     *
     * @return the target file format of the transformation
     */
    String getFormat();

    /**
     *
     * @param format the target format of the transformation
     */
    void setFormat(String format);

    /**
     *
     * @return the crop box coordinates of the transformation.
     */
    CropBox getCropBox();

    /**
     *
     * @param box the crop box of the transformation
     */
    void setCropBox(CropBox box);

    /**
     *
     * @param ratio the target image size ratio
     */
    void setCropRatio(double ratio);

    /**
     *
     * @return the target image size ratio
     */
    double getCropRatio();

    /**
     *
     * @return a map of the transformation parameters
     */
    Map<String, Serializable> toMap();
}
