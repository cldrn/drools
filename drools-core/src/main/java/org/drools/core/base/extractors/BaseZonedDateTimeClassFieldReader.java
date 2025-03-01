/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.extractors;

import java.time.ZonedDateTime;
import java.util.Date;

import org.drools.core.base.ValueType;
import org.drools.core.common.ReteEvaluator;

public class BaseZonedDateTimeClassFieldReader extends BaseDateClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseZonedDateTimeClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     *
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseZonedDateTimeClassFieldReader( final int index,
                                                 final Class fieldType,
                                                 final ValueType valueType ) {
        super( index,
               fieldType,
               valueType );
    }

    protected Date getDate(ReteEvaluator reteEvaluator, Object object ) {
        ZonedDateTime zdt = ((ZonedDateTime)getValue( reteEvaluator, object ));
        return Date.from( zdt.toInstant() );
    }
}
