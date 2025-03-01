/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler.resources;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class KiePath implements Serializable {

    private static final boolean IS_WINDOWS_SEPARATOR = File.separatorChar == '\\';

    public static final KiePath ROOT_PATH = new KiePath("");

    private String path;

    public KiePath() { }

    private KiePath(String path) {
        this.path = path;
    }

    public static KiePath of(String s) {
        return of(s, IS_WINDOWS_SEPARATOR);
    }

    static KiePath of(String s, boolean isWindowsSeparator) {
        String normalized = normalizePath(s, isWindowsSeparator);
        return normalized.isEmpty() ? ROOT_PATH : new KiePath( normalized );
    }

    private static String normalizePath(String s, boolean isWindowsSeparator) {
        if (s == null) {
            throw new NullPointerException("A path cannot be null");
        }
        return trimTrailingSeparator( isWindowsSeparator ? s.replace('\\', '/') : s );
    }

    public KiePath getParent() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? new KiePath(path.substring( 0, lastSlash )) : ROOT_PATH;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public KiePath resolve(KiePath kiePath) {
        return isEmpty() ? kiePath : new KiePath(path + "/" + kiePath.asString());
    }

    public KiePath resolve(String name) {
        return resolve(of(name));
    }

    public String getFileName() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? path.substring( lastSlash+1 ) : path;
    }

    public String asString() {
        return path;
    }

    public String asClassName() {
        return path.substring(0, path.length() - ".class".length()).replace('/', '.');
    }

    @Override
    public String toString() {
        return "KiePath{" +
                "path='" + path + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KiePath)) return false;
        KiePath kiePath = (KiePath) o;
        return Objects.equals(path, kiePath.path);
    }

    public boolean endsWith(String s) {
        return path.endsWith(s);
    }

    public boolean startsWith(String s) {
        return path.startsWith(s);
    }

    public KiePath substring(int beginIndex) {
        return of(path.substring(beginIndex));
    }

    public KiePath substring(int beginIndex, int endIndex) {
        return of(path.substring(beginIndex, endIndex));
    }

    public static String trimTrailingSeparator(String p) {
        return !p.isEmpty() && p.charAt( p.length() -1 ) == '/' ? p.substring( 0, p.length() -1 ) : p;
    }
}
