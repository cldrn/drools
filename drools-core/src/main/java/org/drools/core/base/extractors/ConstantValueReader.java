/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.base.extractors;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.base.ValueType;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.spi.InternalReadAccessor;

import static org.drools.core.base.evaluators.PointInTimeEvaluator.getTimestampFromDate;

public class ConstantValueReader implements InternalReadAccessor {

    private final Object value;

    public ConstantValueReader(Object value) {
        this.value = value;
    }

    public Object getValue(ReteEvaluator reteEvaluator, Object object) {
        return value;
    }

    public BigDecimal getBigDecimalValue(ReteEvaluator reteEvaluator, Object object) {
        return (BigDecimal)value;
    }

    public BigInteger getBigIntegerValue(ReteEvaluator reteEvaluator, Object object) {
        return (BigInteger)value;
    }

    public char getCharValue(ReteEvaluator reteEvaluator, Object object) {
        return (Character)value;
    }

    public int getIntValue(ReteEvaluator reteEvaluator, Object object) {
        return (Integer)value;
    }

    public byte getByteValue(ReteEvaluator reteEvaluator, Object object) {
        return (Byte)value;
    }

    public short getShortValue(ReteEvaluator reteEvaluator, Object object) {
        return (Short)value;
    }

    public long getLongValue(ReteEvaluator reteEvaluator, Object object) {
        return value instanceof Long ? (Long)value : getTimestampFromDate( value );
    }

    public float getFloatValue(ReteEvaluator reteEvaluator, Object object) {
        return (Float)value;
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator, Object object) {
        return (Double)value;
    }

    public boolean getBooleanValue(ReteEvaluator reteEvaluator, Object object) {
        return (Boolean)value;
    }

    public boolean isNullValue(ReteEvaluator reteEvaluator, Object object) {
        return value == null;
    }

    public int getHashCode(ReteEvaluator reteEvaluator, Object object) {
        return value.hashCode();
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public Object getValue(Object object) {
        return value;
    }

    public BigDecimal getBigDecimalValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBigDecimalValue -> TODO");
    }

    public BigInteger getBigIntegerValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBigIntegerValue -> TODO");
    }

    public char getCharValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getCharValue -> TODO");
    }

    public int getIntValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getIntValue -> TODO");
    }

    public byte getByteValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getByteValue -> TODO");
    }

    public short getShortValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getShortValue -> TODO");
    }

    public long getLongValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getLongValue -> TODO");
    }

    public float getFloatValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getFloatValue -> TODO");
    }

    public double getDoubleValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getDoubleValue -> TODO");
    }

    public boolean getBooleanValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBooleanValue -> TODO");
    }

    public boolean isNullValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.isNullValue -> TODO");
    }

    public ValueType getValueType() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getValueType -> TODO");
    }

    public Class<?> getExtractToClass() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getExtractToClass -> TODO");
    }

    public String getExtractToClassName() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getExtractToClassName -> TODO");
    }

    public Method getNativeReadMethod() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getNativeReadMethod -> TODO");
    }

    public String getNativeReadMethodName() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getNativeReadMethodName -> TODO");
    }

    public int getHashCode(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getHashCode -> TODO");
    }

    public int getIndex() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getIndex -> TODO");
    }
}
