/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.tms;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.AgendaComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.tms.agenda.TruthMaintenanceSystemAgendaItemImpl;
import org.drools.tms.agenda.TruthMaintenanceSystemRuleTerminalNodeLeftTuple;

public class TruthMaintenanceSystemAgendaComponentFactory implements AgendaComponentFactory {
    @Override
    public LeftTuple createTerminalTuple() {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple();
    }

    @Override
    public LeftTuple createTerminalTuple(InternalFactHandle factHandle,
                                         Sink sink,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createTerminalTuple(final InternalFactHandle factHandle,
                                         final LeftTuple leftTuple,
                                         final Sink sink) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         Sink sink,
                                         PropagationContext pctx,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         Sink sink) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         LeftTuple currentLeftChild,
                                         LeftTuple currentRightChild,
                                         Sink sink,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    @Override
    public RuleAgendaItem createAgendaItem(long activationNumber, Tuple tuple, int salience, PropagationContext context, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup) {
        return new TruthMaintenanceSystemAgendaItemImpl(activationNumber, tuple, salience, context, pmem, rtn, declarativeAgendaEnabled, agendaGroup);
    }
}
