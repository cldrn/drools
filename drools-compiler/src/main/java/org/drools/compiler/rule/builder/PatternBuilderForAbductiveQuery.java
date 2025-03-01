/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder;

import java.util.Arrays;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.ObjectType;

public class PatternBuilderForAbductiveQuery extends PatternBuilderForQuery {

    @Override
    protected void postBuild(RuleBuildContext context, QueryDescr queryDescr, QueryImpl query, String[] params, String[] types, Declaration[] declarations) {
        int numParams = queryDescr.getParameters().length;
        String returnName = "";
        try {
            AnnotationDescr ann = queryDescr.getAnnotation( query.getAbductiveAnnotationClass() );
            Object[] argsVal = ((Object[]) ann.getValue( "args" ));
            String[] args = argsVal != null ? Arrays.copyOf( argsVal, argsVal.length, String[].class ) : null;

            returnName = types[ numParams ];
            Class<?> abductionReturnKlass = query.getAbductionClass(queryDescr::getTypedAnnotation);
            ObjectType objectType = context.getPkg().getClassFieldAccessorStore().wireObjectType( new ClassObjectType( abductionReturnKlass, false ), (AcceptsClassObjectType) query);

            query.setReturnType( objectType, params, args, declarations);
        } catch ( NoSuchMethodException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                    queryDescr,
                    e,
                    "Unable to resolve abducible constructor for type : " + returnName +
                            " with types " + Arrays.toString(types) ) );

        } catch ( IllegalArgumentException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(), queryDescr, e, e.getMessage() ) );
        }
    }

    @Override
    protected String[] getQueryParams(QueryDescr queryDescr) {
        String[] params = Arrays.copyOf( queryDescr.getParameters(), queryDescr.getParameters().length + 1 );
        params[ params.length-1 ] = "";
        return params;
    }

    @Override
    protected String[] getQueryTypes(QueryDescr queryDescr, QueryImpl query) {
        String[] types = Arrays.copyOf( queryDescr.getParameterTypes(), queryDescr.getParameterTypes().length + 1 );
        Class<?> abductionReturnKlass = query.getAbductionClass(queryDescr::getTypedAnnotation);
        types[types.length-1 ] = abductionReturnKlass.getName();
        return types;
    }
}
