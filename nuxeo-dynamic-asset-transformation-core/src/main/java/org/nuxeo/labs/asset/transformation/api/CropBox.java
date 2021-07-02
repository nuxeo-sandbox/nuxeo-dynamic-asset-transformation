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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CropBox {

    static Pattern p = Pattern.compile("([0-9]*),([0-9]*),([0-9]*),([0-9]*)");

    int top;
    int left;
    int width;
    int height;

    public CropBox(String crop) {

        Matcher m = p.matcher(crop);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid crop format");
        } else {
            this.top = Integer.parseInt(m.group(4));
            this.left = Integer.parseInt(m.group(3));
            this.width = Integer.parseInt(m.group(1));
            this.height = Integer.parseInt(m.group(2));
        }
    }

    public CropBox(int top, int left, int width, int height) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return String.format("%dx%d+%d+%d",width,height,left,top);
    }
}
