package org.sifrproject.annotations.output.brat;

import org.sifrproject.annotations.api.model.AnnotatedClass;
import org.sifrproject.annotations.api.model.Annotation;
import org.sifrproject.annotations.api.model.AnnotationToken;
import org.sifrproject.annotations.api.output.OutputGenerator;
import org.sifrproject.annotations.umls.groups.UMLSGroup;

import java.util.*;

/**
 * Produces a BRAT output from a list of bioportal {@code {@link Annotation}} objects
 */
public class BratOutputGenerator implements OutputGenerator {
    @Override
    public String generate(Iterable<Annotation> annotations) {
        Map<AnnotationToken, List<Annotation>> perTokenAnnotations = new HashMap<>();
        for (Annotation annotation : annotations) {
            for (AnnotationToken annotationToken : annotation.getAnnotations()) {
                if (annotationToken != null) {
                    if (!perTokenAnnotations.containsKey(annotationToken)) {
                        perTokenAnnotations.put(annotationToken, new ArrayList<>());
                    }
                    perTokenAnnotations.get(annotationToken).add(annotation);
                }
            }
        }
        int termCounter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (AnnotationToken token : perTokenAnnotations.keySet()) {
            termCounter++;
            List<Annotation> annotationsForToken = perTokenAnnotations.get(token);
            annotationsForToken.sort(Comparator.comparingDouble(Annotation::getScore));
            Annotation annotation = null;
            if (!annotationsForToken.isEmpty()) {
                Annotation current = annotationsForToken.get(0);
                int currentIndex = 1;
                while (current.getAnnotatedClass().getCuis().isEmpty() && currentIndex < annotationsForToken.size()) {
                    current = annotationsForToken.get(currentIndex);
                    currentIndex++;

                }
                annotation = current;
            }

            if (annotation != null) {
                AnnotatedClass annotatedClass = annotation.getAnnotatedClass();
                stringBuilder.append(String.format("T%d\t%s %d %d\t%s", termCounter, buildGroupList(annotatedClass.getSemanticGroups()),
                        token.getFrom(),
                        token.getTo(),
                        token.getText()
                                .toLowerCase())).append("\n");
                stringBuilder.append(String.format("#%d\tAnnotatorNotes T%d\t%s", termCounter, termCounter, buildCUILIst(annotatedClass.getCuis()))).append("\n");
            }

        }

        return stringBuilder.toString();
    }

    private String buildCUILIst(Set<String> set) {
        StringBuilder stringBuilder = new StringBuilder("");
        boolean first = true;
        for (String elem : set) {
            if (!first) {
                stringBuilder.append(",");
            }
            stringBuilder.append(elem);
            first = false;
        }
        return stringBuilder.toString();
    }

    private String buildGroupList(Set<UMLSGroup> set) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (UMLSGroup elem : set) {
            if (!first) {
                stringBuilder.append(",");
            }
            stringBuilder.append(elem.name());
            first = false;
        }
        return stringBuilder.toString();
    }
}
