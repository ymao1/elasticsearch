/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.compute.operator;

import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.compute.aggregation.AggregatorMode;
import org.elasticsearch.compute.aggregation.AvgLongAggregatorFunctionSupplier;
import org.elasticsearch.compute.aggregation.AvgLongAggregatorFunctionTests;
import org.elasticsearch.compute.aggregation.MaxLongAggregatorFunctionSupplier;
import org.elasticsearch.compute.aggregation.MaxLongAggregatorFunctionTests;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.Page;

import java.util.List;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class AggregationOperatorTests extends ForkingOperatorTestCase {
    @Override
    protected SourceOperator simpleInput(int size) {
        long max = randomLongBetween(1, Long.MAX_VALUE / size);
        return new SequenceLongBlockSourceOperator(LongStream.range(0, size).map(l -> randomLongBetween(-max, max)));
    }

    @Override
    protected Operator.OperatorFactory simpleWithMode(BigArrays bigArrays, AggregatorMode mode) {
        int maxChannel = mode.isInputPartial() ? 1 : 0;
        return new AggregationOperator.AggregationOperatorFactory(
            List.of(
                new AvgLongAggregatorFunctionSupplier(bigArrays, List.of(0)).aggregatorFactory(mode),
                new MaxLongAggregatorFunctionSupplier(bigArrays, List.of(maxChannel)).aggregatorFactory(mode)
            ),
            mode
        );
    }

    @Override
    protected String expectedDescriptionOfSimple() {
        return "AggregationOperator[mode = SINGLE, aggs = avg of longs, max of longs]";
    }

    @Override
    protected String expectedToStringOfSimple() {
        return "AggregationOperator[aggregators=["
            + "Aggregator[aggregatorFunction=AvgLongAggregatorFunction[channels=[0]], mode=SINGLE], "
            + "Aggregator[aggregatorFunction=MaxLongAggregatorFunction[channels=[0]], mode=SINGLE]]]";
    }

    @Override
    protected void assertSimpleOutput(List<Page> input, List<Page> results) {
        assertThat(results, hasSize(1));
        assertThat(results.get(0).getBlockCount(), equalTo(2));
        assertThat(results.get(0).getPositionCount(), equalTo(1));

        AvgLongAggregatorFunctionTests avg = new AvgLongAggregatorFunctionTests();
        MaxLongAggregatorFunctionTests max = new MaxLongAggregatorFunctionTests();

        Block avgs = results.get(0).getBlock(0);
        Block maxs = results.get(0).getBlock(1);
        avg.assertSimpleOutput(input.stream().map(p -> p.<Block>getBlock(0)).toList(), avgs);
        max.assertSimpleOutput(input.stream().map(p -> p.<Block>getBlock(0)).toList(), maxs);
    }

    @Override
    protected ByteSizeValue smallEnoughToCircuitBreak() {
        assumeTrue("doesn't use big array so never breaks", false);
        return null;
    }
}
