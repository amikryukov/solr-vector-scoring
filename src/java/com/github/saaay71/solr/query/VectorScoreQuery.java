package com.github.saaay71.solr.query;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;
import org.apache.solr.schema.SchemaField;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Query which matches documents with subQuery, and scoring with vectors similarity.
 */
public class VectorScoreQuery extends Query {

	private static final String DEFAULT_BINARY_FIELD_NAME = "_vector_";
	private static final Set<String> FIELDS = new HashSet<String>(){{add(DEFAULT_BINARY_FIELD_NAME);}};

	private Query subQuery;
	private List<Double> vector;
	private SchemaField field;

	public VectorScoreQuery(Query subQuery, List<Double> vector, SchemaField field) {
		this.subQuery = subQuery;
		this.field = field;
		this.vector = vector;
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		if (subQuery == null) {
			return null;
		}
		return new VectorScoreQueryWeight(this, subQuery.createWeight(searcher, scoreMode, 0), vector, field);
	}


	@Override
	public String toString(String s) {
		return "VectorScoreQuery(" + subQuery + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VectorScoreQuery that = (VectorScoreQuery) o;
		return  Objects.equals(subQuery, that.subQuery) &&
				Objects.equals(vector, that.vector) &&
				Objects.equals(field, that.field);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subQuery, vector, field);
	}
}