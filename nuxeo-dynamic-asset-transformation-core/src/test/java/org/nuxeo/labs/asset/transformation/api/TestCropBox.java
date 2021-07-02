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

import org.junit.Assert;
import org.junit.Test;

public class TestCropBox {

    @Test
    public void testStringConstructor() {
        String crop = "200,200,10,0";
        CropBox box = new CropBox(crop);
        Assert.assertEquals(200,box.getWidth());
        Assert.assertEquals(200,box.getHeight());
        Assert.assertEquals(10,box.getLeft());
        Assert.assertEquals(0,box.getTop());
    }

}
