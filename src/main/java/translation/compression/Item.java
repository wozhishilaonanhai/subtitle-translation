package translation.compression;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字幕段落的抽象化类
 */
@Data
public class Item {
    public String currentIndex;
    public String currentStart;
    public String currentEnd;
    public translation.compression.Timer start;
    public translation.compression.Timer end;
    public List<String> currentContents;
    public String content;
    public long time;
    public int numberWords;

    // 通过句号，分解出 多个Item
    public List<Item> decomposition() {

        if (content.contains(".")) {

            String[] contentArr = content.split("\\.");

            translation.compression.Timer currentStart = start;
            List<Item> res = new ArrayList<>();

            List<String> contents = Arrays.stream(contentArr).filter(it -> !it.trim().isEmpty()).collect(Collectors.toList());

            for (int i = 0; i < contents.size(); i++) {
                String _content = contents.get(i);
                Item _item = new Item();
                double proportion = _content.length() / (double) content.length();
                long currentTime = (long) (time * proportion);
                _item.start = currentStart;
                _item.currentStart = currentStart.toString();
                _item.end = currentStart.addMs(Math.toIntExact(currentTime));
                _item.currentEnd = _item.end.toString();
                _item.currentContents = new ArrayList<>();
                _item.time = translation.compression.Timer.betweenMs(_item.start, _item.end);
                _item.numberWords = _content.length();
                if ((i < contents.size() - 1) || content.endsWith(".")) {
                    _item.content = _content.trim() + ".";
                } else {
                    _item.content = _content.trim();
                }
                res.add(_item);
                currentStart = _item.end;
            }

            return res;


        } else {
            return Collections.singletonList(copy());
        }

    }

    /**
     * 两个段落的相加
     *
     * @param item 段落
     * @return 新段落
     */
    public Item add(Item item) {

        Item newItem = copy();
        newItem.end = item.end;
        newItem.currentEnd = item.currentEnd;
        newItem.content = (newItem.content + " " + item.content).trim();
        newItem.currentContents.addAll(item.currentContents);
        newItem.time = newItem.time + item.time;
        newItem.numberWords = newItem.numberWords + item.numberWords;
        newItem.currentIndex = item.currentIndex;
        return newItem;
    }

    /**
     * 判断这是一个完整的句子吗？以句号结尾
     *
     * @return 如果是，返回true
     */
    public boolean isItCompleteSentence() {
        return content.trim().charAt(content.length() - 1) == '.';
    }

    /**
     * 段落中包含句号吗？
     *
     * @return 有的话返回true
     */
    public boolean haveFullStop() {
        return content.contains(".");
    }

    /**
     * 拷贝当前对象
     *
     * @return 新对象
     */
    public Item copy() {
        Item newItem = new Item();
        newItem.currentStart = currentStart;
        newItem.content = content;
        newItem.currentContents = new ArrayList<>(currentContents);
        newItem.start = start;
        newItem.end = end;
        newItem.currentEnd = currentEnd;
        newItem.time = time;
        newItem.numberWords = numberWords;
        newItem.currentIndex = currentIndex;
        return newItem;
    }

}
