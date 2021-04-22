package casia.isiteam.recommendsystem.utils;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
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
        String title = "习近平作出重要指示:纠正\"四风\"不能止步";
        String content = "习近平作出重要指示:纠正\"四风\"不能止步，13岁弑母少年:曾获优秀班干 会帮干活常遭父母打，外交部：中美需要相互适应 美国无法左右中国";
        List<Keyword> keywords = getKeywordsByTFIDE(title, content, 5);
        keywords.forEach(keyword ->
            System.out.println(keyword.getName() + " - " + keyword.getScore() + " - " + keyword.getFreq())
        );

        title = "AK-103突击步枪";
        keywords = getKeywordsByTFIDE(title, 5);
        keywords.forEach(keyword ->
            System.out.println(keyword.getName() + " - " + keyword.getScore() + " - " + keyword.getFreq())
        );
    }
}
