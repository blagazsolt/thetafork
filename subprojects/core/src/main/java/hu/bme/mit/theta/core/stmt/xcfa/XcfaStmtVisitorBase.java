/*
 * Copyright 2019 Budapest University of Technology and Economics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.bme.mit.theta.core.stmt.xcfa;

import hu.bme.mit.theta.core.stmt.*;
import hu.bme.mit.theta.core.type.Type;

public class XcfaStmtVisitorBase<P, R> implements XcfaStmtVisitor<P, R>{
    @Override
    public R visit(XcfaCallStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(StoreStmt storeStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(LoadStmt loadStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(AtomicBeginStmt atomicBeginStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(AtomicEndStmt atomicEndStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(NotifyAllStmt notifyAllStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(NotifyStmt notifyStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(WaitStmt waitStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(MtxLockStmt lockStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(MtxUnlockStmt unlockStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(ExitWaitStmt exitWaitStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(EnterWaitStmt enterWaitStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(XcfaInternalNotifyStmt enterWaitStmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(SkipStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(AssumeStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public <DeclType extends Type> R visit(AssignStmt<DeclType> stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public <DeclType extends Type> R visit(HavocStmt<DeclType> stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(XcfaStmt xcfaStmt, P param) {
        return xcfaStmt.accept(this, param);
    }

    @Override
    public R visit(SequenceStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(NonDetStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public R visit(OrtStmt stmt, P param) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
