package com.github.saaay17.solr;

import com.google.common.collect.Iterables;
import org.apache.solr.SolrTestCaseJ4;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public class VectorQueryTest extends SolrTestCaseJ4 {

    private static AtomicInteger idCounter = new AtomicInteger();
    private static String[] vectors = {
            "|1.55 |3.53 |2.3 |0.7 |3.44 |2.33",
            "|3.54 |0.4 |4.16 |4.88 |4.28 |4.25"
    };
    private static Iterator<String> vectorsIter = Iterables.cycle(vectors).iterator();

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCore("solrconfig.xml", "schema-vector.xml");
    }

    @Before
    public void before() throws Exception {
        deleteByQueryAndGetVersion("*:*", params());
        idCounter.set(0);
    }

    @Test
    public void checkTest() throws Exception {
        System.out.println("test runs!");
        indexSampleData();

        assertQ(req("q", "*:*"),
                "//*[@numFound='10']");

        assertQ(req("q", "{!vp f=vector vector=\"0.1,4.75,0.3,1.2,0.7,4.0\"}",
                "fl", "name,score,vector"), "//*[@numFound='10']");

        assertQ(req("q", "{!vp f=vector vector=" + vectors[0].replace("|", "").replace(" ", ",") + "}",
                "fl", "name,score,vector"),
                "//*[@numFound='10']",
                "//doc[1]/float[@name='score'][.='1.0']",
                "count(//float[@name='score'][.='1.0'])=5"
        );
    }

    private void indexSampleData() throws Exception {
        for(int i = 0; i < 10; i++) {
            assertU(adoc(sdoc("id", idCounter.incrementAndGet(), "vector", vectorsIter.next())));
        }
        assertU(commit());
    }
}