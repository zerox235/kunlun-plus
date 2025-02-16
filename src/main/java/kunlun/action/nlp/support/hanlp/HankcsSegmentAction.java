/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.nlp.support.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import kunlun.action.AbstractAction;
import kunlun.action.nlp.SegmentResult;
import kunlun.action.nlp.TextSegment;
import kunlun.action.nlp.Word;
import kunlun.util.Assert;
import kunlun.util.CollUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * HanLP Segment.
 * @see <a href="https://github.com/hankcs/HanLP">HanLP</a>
 * @see <a href="https://hanlp.hankcs.com">HanLP</a>
 * @author Kahle
 */
public class HankcsSegmentAction extends AbstractAction {

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        TextSegment textSegment = (TextSegment) input;
        String algorithm = textSegment.getAlgorithm();
        String text = textSegment.getText();
        Assert.notBlank(text, "Parameter \"TextSegment.text\" must not blank. ");
        List<Term> termList = HanLP.segment(text);
        if (CollUtil.isEmpty(termList)) { return null; }
        List<Word> list = new ArrayList<Word>();
        for (Term term : termList) {
            if (term == null) { continue; }
            list.add(new Word(term.word, String.valueOf(term.nature)));
        }
        return new SegmentResult(list, algorithm);
    }

}
