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

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.FromNodeLeftTuple;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELAccumulateBuilder;
import org.drools.mvel.builder.MVELDialect;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.expr.MVELCompileable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MVELAccumulateBuilderTest {

    @Test
    public void testSimpleExpression() {
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl();
        pkgBuilder.addPackage( new PackageDescr( "pkg1" ) );

        InternalKnowledgePackage pkg = pkgBuilder.getPackage("pkg1");
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        final KnowledgeBuilderConfigurationImpl conf = pkgBuilder.getBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final AccumulateDescr accDescr = new AccumulateDescr();
        final PatternDescr inputPattern = new PatternDescr( "org.drools.mvel.compiler.Cheese",
                                                            "$cheese" );
        accDescr.setInputPattern( inputPattern );
        accDescr.setInitCode( "total = 0;" );
        accDescr.setActionCode( "total += $cheese.price;" );
        accDescr.setReverseCode( "total -= $cheese.price;" );
        accDescr.setResultCode( "new Integer(total)" );

        final MVELAccumulateBuilder builder = new MVELAccumulateBuilder();
        final Accumulate acc = (Accumulate) builder.build( context,
                                                           accDescr );

        (( MVELCompileable ) acc.getAccumulators()[0]).compile( ( MVELDialectRuntimeData ) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        BuildContext buildContext = new BuildContext(kBase, Collections.emptyList());
        MockLeftTupleSink sink = new MockLeftTupleSink(buildContext);
        MockTupleSource source = new MockTupleSource(1, buildContext);
        source.setObjectCount(1);
        sink.setLeftTupleSource(source);

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final Cheese cheddar2 = new Cheese( "cheddar",
                                            8 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( new InitialFactImpl() );
        final InternalFactHandle f1 = (InternalFactHandle) ksession.insert( cheddar1 );
        final InternalFactHandle f2 = (InternalFactHandle) ksession.insert( cheddar2 );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                                       sink,
                                                       true );


        Object wmContext = acc.createWorkingMemoryContext();
        AccumulateNode.AccumulateContext accContext = new AccumulateNode.AccumulateContext();
        Object funcContext = acc.createFunctionContext();

        funcContext = acc.init(wmContext, accContext, funcContext, tuple, ksession);
        accContext.setFunctionContext(funcContext);

        Object value1 = acc.accumulate( wmContext,
                                        accContext,
                                        tuple,
                                        f1,
                                        ksession );
        acc.accumulate( wmContext,
                        accContext,
                        tuple,
                        f2,
                        ksession );

        assertEquals( new Integer( 18 ),
                      acc.getResult( wmContext,
                                     accContext,
                                     tuple,
                                     ksession ) );

        LeftTuple match = new FromNodeLeftTuple();
        match.setContextObject(value1);
        acc.tryReverse(wmContext,
                       accContext,
                       tuple,
                       f1,
                       null,
                       match,
                       ksession);

        assertEquals( new Integer( 8 ),
                      acc.getResult( wmContext,
                                     accContext,
                                     tuple,
                                     ksession ) );
    }

}
