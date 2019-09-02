/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.common.filter.impl;

/**
 * 几个符号
 * @author ;
 */
public class Operator extends Op {
    /**
     * 左括号
     */
    public static final Operator LEFTPARENTHESIS = new Operator("(", 30, false);
    /**
     * 右括号
     */
    public static final Operator RIGHTPARENTHESIS = new Operator(")", 30, false);
    /**
     * 并且
     */
    public static final Operator AND = new Operator("&&", 20, true);
    /**
     * or
     */
    public static final Operator OR = new Operator("||", 15, true);

    /**
     * 优先级
     */
    private int priority;
    /**
     * 是否可排序
     */
    private boolean compareable;

    private Operator(String symbol, int priority, boolean compareable) {
        super(symbol);
        this.priority = priority;
        this.compareable = compareable;
    }

    public static Operator createOperator(String operator) {
        if (LEFTPARENTHESIS.getSymbol().equals(operator)) {
            return LEFTPARENTHESIS;
        }
        else if (RIGHTPARENTHESIS.getSymbol().equals(operator)) {
            return RIGHTPARENTHESIS;
        }
        else if (AND.getSymbol().equals(operator)) {
            return AND;
        }
        else if (OR.getSymbol().equals(operator)) {
            return OR;
        }
        else {
            throw new IllegalArgumentException("unsupport operator " + operator);
        }
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompareable() {
        return compareable;
    }

    public int compare(Operator operator) {
        if (this.priority > operator.priority) {
            return 1;
        }
        else if (this.priority == operator.priority) {
            return 0;
        }
        else {
            return -1;
        }
    }

    /**
     * 是支持的操作
     * @param operator operator
     * @return ;
     */
    public boolean isSpecifiedOp(String operator) {
        return this.getSymbol().equals(operator);
    }
}
