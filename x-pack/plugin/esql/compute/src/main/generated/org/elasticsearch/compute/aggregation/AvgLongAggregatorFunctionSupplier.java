// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.compute.aggregation;

import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.elasticsearch.common.util.BigArrays;

/**
 * {@link AggregatorFunctionSupplier} implementation for {@link AvgLongAggregator}.
 * This class is generated. Do not edit it.
 */
public final class AvgLongAggregatorFunctionSupplier implements AggregatorFunctionSupplier {
  private final BigArrays bigArrays;

  private final List<Integer> channels;

  public AvgLongAggregatorFunctionSupplier(BigArrays bigArrays, List<Integer> channels) {
    this.bigArrays = bigArrays;
    this.channels = channels;
  }

  @Override
  public AvgLongAggregatorFunction aggregator() {
    return AvgLongAggregatorFunction.create(channels);
  }

  @Override
  public AvgLongGroupingAggregatorFunction groupingAggregator() {
    return AvgLongGroupingAggregatorFunction.create(channels, bigArrays);
  }

  @Override
  public String describe() {
    return "avg of longs";
  }
}
