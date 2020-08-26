package casia.isiteam.recommendsystem.utils;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

/**
 * TFIDF 工具
 */
public class TFIDF {

    /**
     * ansj 分词
     * @param text 文本
     * @return 划分的词组
     */
    public static Result split(String text) {
        return ToAnalysis.parse(text);
    }

    /**
     * 获取文本集中的关键词列表
     * @param title 标题
     * @param content 内容
     * @param keyNums 提取关键词数量
     * @return 关键词列表
     */
    public static List<Keyword> getKeywordsByTFIDE(String title, String content, int keyNums) {
        return new KeyWordComputer(keyNums).computeArticleTfidf(title, content);
    }

    public static List<Keyword> getKeywordsByTFIDE(String content, int keyNums) {
        return new KeyWordComputer(keyNums).computeArticleTfidf(content);
    }

    public static void main(String[] args) {
//        Result result = split("我今天很开心，所以一口气买了好多东西，然而我已不小心就把本月预算透支了");
//        for (Term term : result) {
//            System.out.println(term.getName());
//        }

        String title = "习近平作出重要指示:纠正\"四风\"不能止步";
        String content = "习近平作出重要指示:纠正\"四风\"不能止步，13岁弑母少年:曾获优秀班干 会帮干活常遭父母打，外交部：中美需要相互适应 美国无法左右中国";
        List<Keyword> keywords = getKeywordsByTFIDE(title, content, 5);
        for (Keyword keyword : keywords) {
            System.out.println(keyword.getName() + " - " + keyword.getScore() + " - " + keyword.getFreq());
        }
    }
}
