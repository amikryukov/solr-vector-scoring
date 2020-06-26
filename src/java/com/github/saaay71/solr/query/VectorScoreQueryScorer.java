package com.github.saaay71.solr.query;

import com.github.saaay71.solr.VectorUtils;
import com.github.saaay71.solr.query.score.VectorQueryScorerFactory;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrException;
import org.apache.solr.schema.SchemaField;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VectorScoreQueryScorer extends ScorerWrapper {

    private static final String DEFAULT_BINARY_FIELD_NAME = "_vector_";
    private static final Set<String> FIELDS = new HashSet<String>(){{add(DEFAULT_BINARY_FIELD_NAME);}};

    private LeafReaderContext context;
    private List<Double> vector;
    private SchemaField field;

    public VectorScoreQueryScorer(Weight weight, Scorer backing, LeafReaderContext context, List<Double> vector, SchemaField field) {
        super(weight, backing);
        this.context = context;
        this.vector = vector;
        this.field = field;
    }


    @Override
    public float score() throws IOException {

        BytesRef vecBytes = context.reader().document(docID(), FIELDS).getBinaryValue(DEFAULT_BINARY_FIELD_NAME);
        if(vecBytes == null) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Could not find vector for docId: \"" + docID() + "\"");
        }
        VectorUtils.VectorType vecType = VectorUtils.VectorType.valueOf(
                (String)((Map<String, Object>)(field.getArgs())).getOrDefault("vectorType", "AUTO")
        );

        return VectorQueryScorerFactory.getScorer(vecType, vecBytes).score(vector, VectorQuery.VectorQueryType.COSINE, vecBytes);
    }
}
