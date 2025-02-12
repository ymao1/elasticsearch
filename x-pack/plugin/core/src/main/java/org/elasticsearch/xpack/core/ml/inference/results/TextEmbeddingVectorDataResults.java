/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.core.ml.inference.results;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.search.vectors.VectorData;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class TextEmbeddingVectorDataResults extends NlpInferenceResults {

    public static final String NAME = "text_embedding_vector_data_result";

    private final String resultsField;
    private final VectorData inference;

    public TextEmbeddingVectorDataResults(String resultsField, VectorData inference, boolean isTruncated) {
        super(isTruncated);
        this.inference = inference;
        this.resultsField = resultsField;
    }

    public TextEmbeddingVectorDataResults(StreamInput in) throws IOException {
        super(in);
        inference = in.readOptionalWriteable(VectorData::new);
        resultsField = in.readString();
    }

    public String getResultsField() {
        return resultsField;
    }

    public VectorData getInferenceAsQueryVector() {
        return inference;
    }

    @Override
    void doXContentBody(XContentBuilder builder, Params params) throws IOException {
        builder.field(resultsField, inference);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    void doWriteTo(StreamOutput out) throws IOException {
        out.writeOptionalWriteable(inference);
        out.writeString(resultsField);
    }

    @Override
    void addMapFields(Map<String, Object> map) {
        map.put(resultsField, inference);
    }

    @Override
    public Map<String, Object> asMap(String outputField) {
        var map = super.asMap(outputField);
        map.put(outputField, inference);
        return map;
    }

    @Override
    public Object predictedValue() {
        throw new UnsupportedOperationException("[" + NAME + "] does not support a single predicted value");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (super.equals(o) == false) return false;
        TextEmbeddingVectorDataResults that = (TextEmbeddingVectorDataResults) o;
        return Objects.equals(resultsField, that.resultsField) && Objects.equals(inference, that.inference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resultsField, Objects.hashCode(inference));
    }
}
