/**
 * Copyright 2011 Archfirst
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
package org.archfirst.bfexch.infra.fixtrading.converters;

import org.archfirst.bfexch.domain.trading.order.ExecutionReportType;

import quickfix.field.ExecType;

/**
 * ExecutionTypeConverter
 *
 * @author Naresh Bhatia
 */
public class ExecutionTypeConverter {

    private static FixConverter<ExecutionReportType, ExecType> fixConverter =
        new FixConverter<ExecutionReportType, ExecType>();

    static {
        fixConverter.put(ExecutionReportType.Canceled, new ExecType(ExecType.CANCELED));
        fixConverter.put(ExecutionReportType.DoneForDay, new ExecType(ExecType.DONE_FOR_DAY));
        fixConverter.put(ExecutionReportType.New, new ExecType(ExecType.NEW));
        fixConverter.put(ExecutionReportType.PendingCancel, new ExecType(ExecType.PENDING_CANCEL));
        fixConverter.put(ExecutionReportType.Trade, new ExecType(ExecType.TRADE));
    }

    public static ExecType toFix(ExecutionReportType side) {
        return fixConverter.toFix(side);
    }

    public static ExecutionReportType toDomain(ExecType side) {
        return fixConverter.toDomain(side);
    }
}