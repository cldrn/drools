/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.test.model.Cheese;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(Parameterized.class)
public class AgendaEventSupportTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();

    private final boolean useLambdaConstraint;

    public AgendaEventSupportTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    //    public void testIsSerializable() {
    //        assertTrue( Serializable.class.isAssignableFrom( AgendaEventSupport.class ) );
    //    }

    @Ignore("This test already failed before changing to LambdaConstraint")
    @Test
    public void testAgendaEventListener() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        // create a simple package with one rule to test the events
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools.test" );
        final RuleImpl rule = new RuleImpl( "test1" );
        rule.setEager(true);
        rule.setAgendaGroup( "test group" );
        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Pattern pattern = new Pattern( 0,
                                             cheeseObjectType );

        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        pkg.getClassFieldAccessorStore().setEagerWire( true );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(pkg.getClassFieldAccessorStore(), "cheddar", useLambdaConstraint);

        pattern.addConstraint( constraint );
        rule.addPattern( pattern );

        rule.setConsequence( new Consequence() {
            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final ReteEvaluator reteEvaluator) throws Exception {
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
        } );
        pkg.addRule( rule );

        kbase.addPackages( Collections.singleton(pkg) );

        // create a new working memory and add an AgendaEventListener
        KieSession ksession = kbase.newKieSession();
        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void matchCreated(MatchCreatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event ); 
            }
        };
        ksession.addEventListener( agendaEventListener );
        
        assertEquals( 1, ksession.getAgendaEventListeners().size() );

        // assert the cheese fact
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        FactHandle cheddarHandle = ksession.insert( cheddar );

        InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();
        agenda.evaluateEagerList();

        // should be one MatchCreatedEvent
        assertEquals(1,
                     agendaList.size());
        MatchCreatedEvent createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getMatch().getFactHandles().toArray()[0] );

        // clear the agenda to check CLEAR events occur
        ksession.getAgenda().clear();
        MatchCancelledEvent cancelledEvent = (MatchCancelledEvent) agendaList.get( 1 );
        assertEquals( MatchCancelledCause.CLEAR,
                      cancelledEvent.getCause() );

        agendaList.clear();

        // update results in an MatchCreatedEvent
        cheddar.setPrice( 14 );
        ksession.update(cheddarHandle,
                        cheddar);

        agenda.evaluateEagerList();

        assertEquals( 1,
                      agendaList.size() );
        createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getMatch().getFactHandles().toArray()[0] );
        agendaList.clear();

        // update should not result in cancelation+activation events
        cheddar.setPrice( 14 );
        ksession.update( cheddarHandle,
                         cheddar );
        assertEquals( 0,
                      agendaList.size() );
        //cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        //assertEquals( ActivationCancelledCause.WME_MODIFY, cancelledEvent.getCause() );
//        assertSame( cheddarHandle,
//                    cancelledEvent.getActivation().toFactHandles().toArray()[0] );
//        createdEvent = (ActivationCreatedEvent) agendaList.get( 1 );
//        assertSame( cheddarHandle,
//                    createdEvent.getActivation().toFactHandles().toArray()[0] );
//        agendaList.clear();

        // retract results in a ActivationCancelledEvent, note the object is not resolveable now as it no longer exists
        ksession.retract( cheddarHandle );
        assertEquals( 1,
                      agendaList.size() );
        cancelledEvent = (MatchCancelledEvent) agendaList.get( 0 );
        // invalidated handles no longer set the object to null
        assertNotNull( ((InternalFactHandle) cancelledEvent.getMatch().getFactHandles().toArray()[0]).getObject() );

        // re-assert the fact so we can test the agenda group events
        cheddarHandle = ksession.insert( cheddar );
        agendaList.clear();

        // setFocus results in an AgendaGroupPushedEvent
        ksession.getAgenda().getAgendaGroup( "test group" ).setFocus();
        assertEquals( 1,
                      agendaList.size() );
        final AgendaGroupPushedEvent pushedEvent = (AgendaGroupPushedEvent) agendaList.get( 0 );
        assertEquals( "test group",
                      pushedEvent.getAgendaGroup().getName() );
        agendaList.clear();

        // fireAllRules results in a BeforeActivationFiredEvent and an AfterActivationFiredEvent
        // the AgendaGroup becomes empty, which results in a popped event.
        ksession.fireAllRules();
        assertEquals( 3,
                      agendaList.size() );
        final BeforeMatchFiredEvent beforeEvent = (BeforeMatchFiredEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    beforeEvent.getMatch().getFactHandles().toArray()[0] );
        final AfterMatchFiredEvent afterEvent = (AfterMatchFiredEvent) agendaList.get( 1 );
        assertSame( cheddarHandle,
                    afterEvent.getMatch().getFactHandles().toArray()[0] );
        final AgendaGroupPoppedEvent poppedEvent = (AgendaGroupPoppedEvent) agendaList.get( 2 );
        assertEquals( "test group",
                      poppedEvent.getAgendaGroup().getName() );
    }

}
