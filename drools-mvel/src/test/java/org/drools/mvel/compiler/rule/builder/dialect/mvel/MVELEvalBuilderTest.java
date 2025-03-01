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

package org.drools.mvel.compiler.rule.builder.dialect.mvel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELDialect;
import org.drools.mvel.builder.MVELEvalBuilder;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.expr.MVELEvalExpression;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MVELEvalBuilderTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testSimpleExpression() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        final KnowledgeBuilderConfigurationImpl conf = pkgBuilder.getBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = ( MVELDialect ) dialectRegistry.getDialect( "mvel" );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                             "price" );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( int.class ) );
        final Declaration declaration = new Declaration( "a",
                                                         extractor,
                                                         pattern );
        final Map map = new HashMap();
        map.put( "a",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final EvalDescr evalDescr = new EvalDescr();
        evalDescr.setContent( "a == 10" );

        final MVELEvalBuilder builder = new MVELEvalBuilder();
        final EvalCondition eval = (EvalCondition) builder.build( context,
                                                                  evalDescr );
        (( MVELEvalExpression ) eval.getEvalExpression()).compile( ( MVELDialectRuntimeData ) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        BuildContext                             buildContext = new BuildContext(kBase, Collections.emptyList());
        org.drools.core.reteoo.MockLeftTupleSink sink         = new MockLeftTupleSink(buildContext);
        MockTupleSource                          source       = new MockTupleSource(1, buildContext);
        source.setObjectCount(1);
        sink.setLeftTupleSource(source);

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar );

        final LeftTupleImpl tuple = new LeftTupleImpl( f0, sink, true );
        f0.removeLeftTuple(tuple);
        
        Object evalContext = eval.createContext();

        assertTrue( eval.isAllowed( tuple,
                                    ksession,
                                    evalContext ) );

        cheddar.setPrice( 9 );
        ksession.update( f0,
                   cheddar );
        assertFalse( eval.isAllowed( tuple,
                                     ksession,
                                     evalContext ) );
    }

}
