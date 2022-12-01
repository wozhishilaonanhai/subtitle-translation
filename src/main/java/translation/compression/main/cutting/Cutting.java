package translation.compression.main.cutting;

import cn.hutool.core.util.ReUtil;
import translation.compression.Item;
import translation.compression.Timer;
import translation.compression.main.compressionSrt.CompressionSrt;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 本对象用于给已经翻译好的中文进行字幕的切割。
 * 字幕中一段对话的长度可能太长了，看着会不舒服。
 * 需要按指定间隔时间进行切割，并且会切割后会把原先的一段字幕，拆分成多段。并保持时间总体是连续的
 */
public class Cutting {

    public static void main(String[] args) throws IOException {

        // 读取字幕
        List<Item> cnListItem = CompressionSrt.getZimuList("./data/dist/cn_zimu.txt");
        List<Item> newCuttingCnListItem = new ArrayList<>();
        // 间隔时间
        int threshold = 9000;

        for (Item item : cnListItem) {
            System.out.println(item.currentIndex);

            Timer start = item.start;
            Timer end = item.end;
            int diff = Timer.betweenMs(start, end);

            String content = item.content;
            int length = content.length();

            // 如果时长超过指定间隔
            if (diff >= threshold) {

                int count = (diff + threshold) / threshold;
                List<String> subContents = cuttingContent(content, count);
                int sumTimeMs = 0;
                Item currentTime = item.copy();

                // 切割并加入新数组
                for (int i = 0; i < subContents.size(); i++) {

                    String subContent = subContents.get(i);
                    int subContentLength = subContent.length();
                    int timeMs = (int) ((subContentLength / (double) length) * diff);
                    if (i == subContents.size() - 1) {
                        timeMs = diff - sumTimeMs;
                    }

                    Item subItem = currentTime.copy();
                    subItem.end = subItem.start.addMs(timeMs);
                    subItem.currentEnd = subItem.end.toString();
                    subItem.content = subContent;
                    subItem.currentContents = new ArrayList<>();
                    subItem.time = timeMs;
                    subItem.numberWords = subContent.length();
                    add(newCuttingCnListItem, subItem);
                    currentTime = subItem;

                    currentTime.start = subItem.end;
                    currentTime.currentStart = subItem.end.toString();

                    sumTimeMs += timeMs;
                }

            } else {
                add(newCuttingCnListItem, item.copy());
            }

        }


        Path writePath = Paths.get("./data/dist/cn_zimu_ultimately" + ".txt");
        CompressionSrt.writeList(writePath, newCuttingCnListItem);

    }

    /**
     * 辅助方法，将切割后的一段添加进数组。并标号下标
     *
     * @param list 数组
     * @param item 段落
     */
    public static void add(List<Item> list, Item item) {
        Item copy = item.copy();
        copy.currentIndex = String.valueOf(list.size() + 1);
        list.add(copy);
    }

    /**
     * 用于切割一个段落，保证切割完后的每一句中的英文单词不会分开。
     * 标点符号不会在开头
     *
     * @param x     原始段落
     * @param count 切割的份数
     * @return 返回切割后的段落
     */
    public static List<String> cuttingContent(String x, int count) {
        List<String> res = new ArrayList<>(count);
        int itemLen = x.length() / count;
        int offset = 0;

        for (int i = 1; i <= count; i++) {
            int len = itemLen * i;
            char c = 1;
            if (i == count) {
                len = x.length();
            } else {
                c = x.charAt(len);
            }
            String substring = x.substring(((i - 1) * itemLen) - offset, len);
            if (i < count && ReUtil.isMatch("^[0-9a-zA-Z]$", String.valueOf(c))) {

                int i1 = substring.lastIndexOf(" ");
                int i2 = substring.lastIndexOf("，");
                int i3 = substring.lastIndexOf("?");
                int i4 = substring.lastIndexOf("。");
                int i5 = substring.lastIndexOf(",");
                int i6 = substring.lastIndexOf("？");
                int i7 = substring.lastIndexOf(".");
                int max = Cutting.max(i1, i2, i3, i4, i5, i6, i7);

                if (max == -1) {
                    for (int j = substring.length() - 1; j >= 0; j--) {
                        char ch = substring.charAt(j);
                        if (!ReUtil.isMatch("[\u4E00-\u9FFF]", String.valueOf(ch))) {
                            max = j;
                            break;
                        }
                    }
                    if (max == -1) {
                        max = substring.length() - 1;
                    }
                }

                res.add(substring.substring(0, max + 1).trim());
                offset = substring.length() - max - 1;

            } else {

                if (c == '?' || c == ',' || c == '.' || c == '？' || c == '，' || c == '。') {
                    res.add(x.substring(((i - 1) * itemLen) - offset, len + 1).trim());
                    offset = -1;
                } else {
                    res.add(substring.trim());
                }

            }
        }
        return res;
    }

    /**
     * 辅助,求数组最大值
     *
     * @param val 数组
     * @return 最大值
     */
    public static int max(int... val) {
        return Arrays.stream(val).max().orElse(-1);
    }
}
