package org.sifrproject.annotatorclient;


import org.json.simple.parser.ParseException;
import org.sifrproject.annotations.api.input.AnnotationParser;
import org.sifrproject.annotations.api.model.Annotation;
import org.sifrproject.annotations.api.model.AnnotationFactory;
import org.sifrproject.annotations.exceptions.NCBOAnnotatorErrorException;
import org.sifrproject.annotations.input.BioPortalJSONAnnotationParser;
import org.sifrproject.annotations.model.BioPortalLazyAnnotationFactory;
import org.sifrproject.annotatorclient.api.BioPortalAnnotatorQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);
    public static void main(String[] args) throws IOException, NCBOAnnotatorErrorException, ParseException {




        DefaultBioPortalAnnotator annotator = new DefaultBioPortalAnnotator("services.stageportal.lirmm.fr/annotator?","APIKEYSTAGE");
        BioPortalAnnotatorQuery query = BioportalAnnotatorQueryBuilder.DEFAULT
                .semantic_groups(Collections.singletonList("DISO"))
                .ontologies("ONTOLOGY1,ONTOLOGY2,...").build();

        String output = annotator.runQuery(query);

        AnnotationFactory annotationFactory = new BioPortalLazyAnnotationFactory();
        AnnotationParser annotationParser = new BioPortalJSONAnnotationParser(annotationFactory);

        List<Annotation> annotations = annotationParser.parseAnnotations(output);

        for(Annotation annotation: annotations){
            logger.info(annotation.toString());
        }
    }
}
