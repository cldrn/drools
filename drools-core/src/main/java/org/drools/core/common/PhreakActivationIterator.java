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

package org.drools.core.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.impl.RuleBase;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public class PhreakActivationIterator
    implements
    Iterator {

    private java.util.Iterator<AgendaItem> agendaItemIter;

    List<AgendaItem> agendaItems;

    PhreakActivationIterator() {

    }

    private PhreakActivationIterator(ReteEvaluator reteEvaluator,
                                     RuleBase kbase) {
        agendaItems = collectAgendaItems(kbase, reteEvaluator);
        agendaItemIter =  agendaItems.iterator();
    }


    public static PhreakActivationIterator iterator(ReteEvaluator reteEvaluator) {
        return new PhreakActivationIterator( reteEvaluator, reteEvaluator.getKnowledgeBase() );
    }

    public Object next() {
        if ( agendaItemIter.hasNext() ) {
            return agendaItemIter.next();
        } else {
            return null;
        }
    }


    public static List<RuleTerminalNode> populateRuleTerminalNodes(RuleBase kbase, Set<RuleTerminalNode>  nodeSet) {
        Collection<TerminalNode[]> nodesWithArray = kbase.getReteooBuilder().getTerminalNodes().values();

        for (TerminalNode[] nodeArray : nodesWithArray) {
            for (TerminalNode node : nodeArray) {
                if (node.getType() == NodeTypeEnums.RuleTerminalNode) {
                    nodeSet.add((RuleTerminalNode) node);
                }
            }
        }

        return Arrays.asList(nodeSet.toArray(new RuleTerminalNode[nodeSet.size()]));
    }

    public static List<AgendaItem> collectAgendaItems(RuleBase kbase, ReteEvaluator reteEvaluator) {
        Set<RuleTerminalNode> nodeSet = new HashSet<>();
        List<RuleTerminalNode> nodeList = populateRuleTerminalNodes(kbase, nodeSet);

        List<AgendaItem> agendaItems = new ArrayList<>();
        for ( RuleTerminalNode rtn : nodeList ) {
            if ( !nodeSet.contains(rtn) ) {
                // this node has already been processed
                continue;
            }
            processLeftTuples( rtn.getLeftTupleSource(), agendaItems, nodeSet, reteEvaluator);
        }
        return agendaItems;
    }

    public static void processLeftTuples(LeftTupleSource node, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        LeftTupleSource node1 = node;
        while (NodeTypeEnums.LeftInputAdapterNode != node1.getType()) {
            node1 = node1.getLeftTupleSource();
        }
        int maxShareCount = node1.getAssociationsSize();

        while (NodeTypeEnums.LeftInputAdapterNode != node.getType()) {
            Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) node);
            if (memory.getSegmentMemory() == null) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
            if ( node.getAssociationsSize() == maxShareCount ) {
                // the recurse must start from the first split node, otherwise we get partial overlaps in propagations
                if (NodeTypeEnums.isBetaNode(node)) {
                    BetaMemory bm;
                    if (NodeTypeEnums.AccumulateNode == node.getType()) {
                        AccumulateMemory am = (AccumulateMemory) memory;
                        bm = am.getBetaMemory();
                        FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                        Tuple lt = BetaNode.getFirstTuple( bm.getLeftTupleMemory(), it );
                        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                            AccumulateContext accctx = (AccumulateContext) lt.getContextObject();
                            collectFromPeers(accctx.getResultLeftTuple(), agendaItems, nodeSet, reteEvaluator);
                        }
                    } else if ( NodeTypeEnums.ExistsNode == node.getType() ) {
                        bm = (BetaMemory) reteEvaluator.getNodeMemory((MemoryFactory) node);
                        FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                        RightTuple rt = (RightTuple) BetaNode.getFirstTuple( bm.getRightTupleMemory(), it );
                        for (; rt != null; rt = (RightTuple) it.next(rt)) {
                            for ( LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext() ) {
                                if ( lt.getFirstChild() != null ) {
                                    collectFromPeers(lt.getFirstChild(), agendaItems, nodeSet, reteEvaluator);
                                }
                            }
                        }
                    } else {
                        bm = (BetaMemory) reteEvaluator.getNodeMemory((MemoryFactory) node);
                        FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                        Tuple lt = BetaNode.getFirstTuple( bm.getLeftTupleMemory(), it );
                        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                            if ( lt.getFirstChild() != null ) {
                                collectFromLeftInput(lt.getFirstChild(), agendaItems, nodeSet, reteEvaluator);
                            }
                        }
                    }
                    return;
                } else if (NodeTypeEnums.FromNode == node.getType()) {
                    FromMemory fm = (FromMemory) reteEvaluator.getNodeMemory((MemoryFactory) node);
                    TupleMemory ltm = fm.getBetaMemory().getLeftTupleMemory();
                    FastIterator it = ltm.fullFastIterator();
                    for (LeftTuple lt = (LeftTuple) ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                        if ( lt.getFirstChild() != null ) {
                            collectFromLeftInput(lt.getFirstChild(), agendaItems, nodeSet, reteEvaluator);
                        }
                    }
                    return;
                }
            }
            node = node.getLeftTupleSource();
        }

        // No beta or from nodes, so must retrieve LeftTuples from the LiaNode.
        // This is done by scanning all the LeftTuples referenced from the FactHandles in the ObjectTypeNode
        LeftInputAdapterNode lian = (LeftInputAdapterNode) node;
        if ( !lian.isTerminal() ) {
            Memory memory = reteEvaluator.getNodeMemory( ( MemoryFactory ) node );
            if ( memory.getSegmentMemory() == null ) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
        }

        ObjectSource os = lian.getObjectSource();
        while (os.getType() != NodeTypeEnums.ObjectTypeNode) {
            os = os.getParentObjectSource();
        }
        ObjectTypeNode otn = (ObjectTypeNode) os;
        final ObjectTypeNodeMemory omem = reteEvaluator.getNodeMemory(otn);
        LeftTupleSink firstLiaSink = lian.getSinkPropagator().getFirstLeftTupleSink();

        java.util.Iterator<InternalFactHandle> it = omem.iterator();
        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            fh.forEachLeftTuple( lt -> {
                if ( lt.getTupleSink() == firstLiaSink ) {
                    collectFromLeftInput(lt, agendaItems, nodeSet, reteEvaluator);
                }
            });
        }
    }

    private static void collectFromLeftInput(LeftTuple lt, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        for (; lt != null; lt = lt.getHandleNext()) {
            collectFromPeers(lt, agendaItems, nodeSet, reteEvaluator);
        }
    }

    private static void collectFromPeers(LeftTuple peer, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        while (peer != null) {
            if ( peer.getTupleSink().getType() == NodeTypeEnums.AccumulateNode ) {
                Object accctx = peer.getContextObject();
                if (accctx instanceof AccumulateContext) {
                    // lefttuple representing an accumulated value now have that value as context object (it was null before) and must be skipped here
                    collectFromLeftInput(((AccumulateContext) accctx).getResultLeftTuple(), agendaItems, nodeSet, reteEvaluator);
                }
            } else if ( peer.getFirstChild() != null ) {
                for (LeftTuple childLt = peer.getFirstChild(); childLt != null; childLt = childLt.getHandleNext()) {
                    collectFromLeftInput(childLt, agendaItems, nodeSet, reteEvaluator);
                }
            } else if ( peer.getTupleSink().getType() == NodeTypeEnums.RuleTerminalNode ) {
                agendaItems.add((AgendaItem) peer);
                nodeSet.remove(peer.getTupleSink()); // remove this RuleTerminalNode, as we know we've visited it already
            }
            peer = peer.getPeer();
        }
    }

}
