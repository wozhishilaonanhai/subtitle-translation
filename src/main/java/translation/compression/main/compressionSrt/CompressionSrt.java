package translation.compression.main.compressionSrt;

import cn.hutool.core.util.ReUtil;
import translation.compression.Timer;
import translation.compression.Item;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 本类为第一步。
 * 主要用来对原始英文字幕进行整理。
 * 保证整理后的每一段字幕。一定是个完整的句子。当然，这需要原始字幕能够完整表达一句话，能以句号结尾。
 * 以一句完整的带有句号结尾的句子，翻译起来更能结合上下文，做出更好的翻译。这是本项目的特点。
 */
public class CompressionSrt {

    /**
     * 解析字幕文件为段落
     *
     * @param pathStr 路径
     * @return 段落列表
     * @throws IOException io
     */
    public static List<Item> getZimuList(String pathStr) throws IOException {

        Path path = Paths.get(pathStr);

        if (Files.notExists(path)) {
            return Collections.emptyList();
        }

        List<Item> listItem = new ArrayList<>();

        List<String> allLines = Files.lines(path).collect(Collectors.toList());
        if (allLines.isEmpty()){
            return Collections.emptyList();
        }
        if (!"".equals(allLines.get(allLines.size() - 1).trim())) {
            allLines.add("");
        }
        Item item = new Item();

        for (String line : allLines) {

            if (ReUtil.isMatch("^\\d+$", line)) {
                item.currentIndex = line.trim();
                continue;
            }
            if (line.contains("-->")) {

                String[] times = line.split("-->");
                assert times.length >= 2;
                item.currentStart = times[0].trim();
                item.currentEnd = times[1].trim();
                item.start = translation.compression.Timer.of(item.currentStart);
                item.end = translation.compression.Timer.of(item.currentEnd);
                item.time = Timer.betweenMs(item.start, item.end);
                continue;

            }
            if (!"".equals(line.trim())) {
                if (item.currentContents == null) {
                    item.currentContents = new ArrayList<>();
                }
                item.currentContents.add(line.trim());
                continue;
            }

            item.numberWords = item.currentContents.stream().mapToInt(String::length).sum() + 1;
            item.content = String.join(" ", item.currentContents).trim();
            listItem.add(item);
            item = new Item();

        }

        return listItem;
    }

    public static void main(String[] args) throws IOException {

        List<Item> listItem = getZimuList("./data/en-zimu.srt");

        System.out.println(listItem.size());

        List<Item> cnListItem = new ArrayList<>();

        //保存之前的段落
        List<Item> cacheItem = new ArrayList<>();

        for (Item _item : listItem) {

            if (!_item.haveFullStop()) {
                // 没找到
                cacheItem.add(_item);
                continue;
            }

            // 找到
            List<Item> decomposition = _item.decomposition();

            if (decomposition.size() == 0) {
                throw new NullPointerException();
            }

            if (cacheItem.isEmpty()) {

                if (decomposition.size() == 1) {
                    Item oneDecompositionItem = decomposition.get(0);
                    if (oneDecompositionItem.isItCompleteSentence()) {
                        assistAddCnItem(cnListItem, oneDecompositionItem);
                    } else {
                        cacheItem.add(oneDecompositionItem);
                    }
                } else {
                    // 保留最后一个
                    assistAddAllCnItem(cnListItem, decomposition.subList(0, decomposition.size() - 1));

                }

                continue;

            }

            Optional<Item> reduce = cacheItem.stream().reduce(Item::add);
            Item preCompoundItem = reduce.orElseThrow(NullPointerException::new);

            cacheItem.clear();

            if (decomposition.size() == 1) {
                Item oneDecompositionItem = decomposition.get(0);
                // 如果大小只有1 .那一定是完整的句子
                assistAddCnItem(cnListItem, preCompoundItem.add(oneDecompositionItem));
            } else {
                Item oneDecompositionItem = decomposition.get(0);
                assistAddCnItem(cnListItem, preCompoundItem.add(oneDecompositionItem));
                assistAddAllCnItem(cnListItem, decomposition.subList(1, decomposition.size() - 1));

                Item lastItem = decomposition.get(decomposition.size() - 1);
                if (lastItem.isItCompleteSentence()) {
                    assistAddCnItem(cnListItem, lastItem);
                } else {
                    cacheItem.add(decomposition.get(decomposition.size() - 1));
                }

            }

        }

        System.out.println(cnListItem);

        System.out.println(cnListItem);

        Path writePath = Paths.get("./data/dist/" + "all_zimu.txt");
        writeList(writePath, cnListItem);
    }

    /**
     * 写入段落为文件
     *
     * @param writePath 输出路径
     * @param listItem  段落列表
     * @throws IOException io
     */
    public static void writeList(Path writePath, List<Item> listItem) throws IOException {

        if (Files.notExists(writePath)) {
            Files.createFile(writePath);
        }
        StringBuilder writeStr = new StringBuilder();
        for (Item _item : listItem) {
            writeStr.append(_item.getCurrentIndex())
                    .append("\n")
                    .append(_item.getCurrentStart())
                    .append(" --> ")
                    .append(_item.getCurrentEnd())
                    .append("\n")
                    .append(_item.getContent())
                    .append("\n\n");
        }
        Files.write(writePath, writeStr.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    /**
     * 辅助添加段落，能自动完成下标
     *
     * @param cnListItem 段落列表
     * @param target     段落
     */
    private static void assistAddCnItem(List<Item> cnListItem, Item target) {
        int size = cnListItem.size();
        target.currentIndex = String.valueOf(size + 1);
        cnListItem.add(target);
    }

    private static void assistAddAllCnItem(List<Item> cnListItem, List<Item> targets) {
        for (Item target : targets) {
            assistAddCnItem(cnListItem, target);
        }
    }

}
