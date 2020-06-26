package com.github.saaay71.solr.query;

import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

import java.io.IOException;

/**
 * This instance delegates all it's functionality to backing scorer, which is real Lucene scorer.
 */
public class ScorerWrapper extends Scorer {

    protected final Scorer backing;

    public ScorerWrapper(Weight weight, Scorer backing) {
        super(weight);
        this.backing = backing;
    }


    @Override
    public DocIdSetIterator iterator() {
        return backing.iterator();
    }

    @Override
    public float getMaxScore(int upTo) throws IOException {
        return backing.getMaxScore(upTo);
    }

    @Override
    public float score() throws IOException {
        return backing.score();
    }

    @Override
    public int docID() {
        return backing.docID();
    }
}
