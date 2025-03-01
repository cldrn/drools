/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.Tuple;

public class FakeContextEntry implements ContextEntry {

    private Tuple tuple;
    private InternalFactHandle handle;

    private transient ReteEvaluator reteEvaluator;

    public void updateFromTuple(ReteEvaluator reteEvaluator, Tuple tuple) {
        this.tuple = tuple;
        this.reteEvaluator = reteEvaluator;
    }

    public void updateFromFactHandle(ReteEvaluator reteEvaluator, InternalFactHandle handle) {
        this.reteEvaluator = reteEvaluator;
        this.handle = handle;
    }

    public void resetTuple() {
        tuple = null;
    }

    public void resetFactHandle() {
        reteEvaluator = null;
        handle = null;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tuple);
        out.writeObject(handle);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tuple = (Tuple) in.readObject();
        handle = (InternalFactHandle) in.readObject();
    }

    public Tuple getTuple() {
        return tuple;
    }

    public InternalFactHandle getHandle() {
        return handle;
    }

    public ContextEntry getNext() {
        throw new UnsupportedOperationException();
    }

    public void setNext(final ContextEntry entry) {
        throw new UnsupportedOperationException();
    }

    public ReteEvaluator getReteEvaluator() {
        return reteEvaluator;
    }
}
