package com.github.saaay71.solr.query;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.solr.schema.SchemaField;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class VectorScoreQueryWeight extends Weight {

    private Weight subWeight;
    private List<Double> vector;
    private SchemaField field;

    public VectorScoreQueryWeight(Query query, Weight subWeight, List<Double> vector, SchemaField field) {
        super(query);
        this.subWeight = subWeight;
        this.vector = vector;
        this.field = field;
    }

    @Override
    public Scorer scorer(LeafReaderContext context) throws IOException {
        Scorer scorer = subWeight.scorer(context);
        if (scorer == null) {
            return null;
        } else {
            return new VectorScoreQueryScorer(this, subWeight.scorer(context), context, vector, field);
        }
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        subWeight.extractTerms(terms);
    }

    @Override
    public Explanation explain(LeafReaderContext context, int doc) throws IOException {
        return subWeight.explain(context, doc);
    }



    @Override
    public boolean isCacheable(LeafReaderContext ctx) {
        return subWeight.isCacheable(ctx);
    }
}