/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.nlp.support.similarity;

import kunlun.action.AbstractAction;
import kunlun.action.nlp.TextSimilarity;
import kunlun.util.CollUtils;
import kunlun.util.StrUtils;
import org.xm.similarity.text.CosineSimilarity;
import org.xm.tokenizer.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TextSimilarityAction.
 * @see <a href="https://github.com/shibing624/similarity">Similarity</a>
 */
public class XmTextSimilarityAction extends AbstractAction {
    private static CosineSimilarity cosineSimilarity = new CosineSimilarity();

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        TextSimilarity textSimilarity = (TextSimilarity) input;
        List<String> words1 = textSimilarity.getWords1();
        List<String> words2 = textSimilarity.getWords2();
        String algorithm = textSimilarity.getAlgorithm();
        String text1 = textSimilarity.getText1();
        String text2 = textSimilarity.getText2();

        if (CollUtils.isNotEmpty(words1) &&
                CollUtils.isNotEmpty(words2)) {
            return cosineSimilarity.getSimilarity(convert(words1), convert(words2));
        }
        else if (StrUtils.isNotBlank(text1) &&
                StrUtils.isNotBlank(text2)) {
            return cosineSimilarity.getSimilarity(text1, text2);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    protected List<Word> convert(List<String> words) {
        if (CollUtils.isEmpty(words)) { return Collections.emptyList(); }
        List<Word> list = new ArrayList<Word>();
        for (String word : words) {
            list.add(new Word(word));
        }
        return list;
    }

}
