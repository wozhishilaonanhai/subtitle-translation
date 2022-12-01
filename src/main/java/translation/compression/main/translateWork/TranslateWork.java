package translation.compression.main.translateWork;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import translation.compression.Item;
import translation.compression.TranslateTool;
import translation.compression.main.compressionSrt.CompressionSrt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 对文件翻译的类，
 * 为什么要把这一步骤分开，因为使用翻译价格挺贵。50块钱100万字符。
 * 本篇字幕大概需要100多块的翻译。可以用有道云新用户注册送100的体验金翻译。
 * 而翻译过程中可能会出现网络，或者限速，或者余额不足等原因。所以拆分任务开来，一步一步做
 * 即使中途出问题。也能重来。翻译好的文件很珍贵。只有一份，再翻译就要马内。
 * 本类支持读取已翻译好的文件，续翻，不用担心中断要重来，会自动从没有的地方开始翻译。
 */
public class TranslateWork {

    public static void main(String[] args) throws IOException, InterruptedException {

        List<Item> listItem = CompressionSrt.getZimuList("./data/dist/all_zimu.rst");
        System.out.println(listItem.size());

        List<Item> cnListItem = CompressionSrt.getZimuList("./data/dist/cn_zimu.rst");

        Map<String, Item> haveItemMap = cnListItem.stream().collect(Collectors.toMap(Item::getCurrentIndex, a -> a));

        Path writePath = Paths.get("./data/dist/" + "cn_zimu" + ".rst");

        if (Files.notExists(writePath)) {
            Files.createFile(writePath);
        }

        int fineCount = 0;
        int allCount = listItem.size() - cnListItem.size();

        for (Item item : listItem) {

            TimeInterval timer = DateUtil.timer();

            if (haveItemMap.containsKey(item.currentIndex)) {
                continue;
            }

            item.content = TranslateTool.getCn(item.content.trim());

            Files.write(writePath, (item.getCurrentIndex() + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(writePath, (item.getCurrentStart() + " --> " + item.getCurrentEnd() + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(writePath, (item.getContent() + "\n\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            fineCount++;
            long longTime = timer.intervalRestart();

            System.out.println(item.currentIndex + " 预计剩余: " + TimeUnit.MILLISECONDS.toMinutes(((allCount - fineCount) * longTime)) + " 分钟");

        }

    }

}
