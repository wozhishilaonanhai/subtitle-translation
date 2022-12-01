package translation.compression;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 有道词典翻译
public class TranslateTool {

    /**
     * 返回中文
     *
     * @param en 英文
     * @return 中文
     */
    public static String getCn(String en) {

        String id = "有道ID";
        String secret = "有道密钥";

        String url = "https://openapi.youdao.com/api";
        Map<String, Object> data = new HashMap<>();
        data.put("q", en);
        data.put("from", "en");
        data.put("to", "zh-CHS");
        data.put("appKey", id);

        String salt = IdUtil.randomUUID();

        data.put("salt", salt);
        long now = new Date().getTime() / 1000;

        String input = en;
        if (en.length() > 20) {
            input = en.substring(0, 10) + en.length() + en.substring(en.length() - 10);
        }

        String sign = SecureUtil.sha256(id + input + salt + now + secret);
        data.put("sign", sign);

        data.put("signType", "v3");
        data.put("curtime", now);

        String res = HttpUtil.post(url, data);
        JSONObject entries = JSONUtil.parseObj(res);
        return Objects.requireNonNull((String) entries.getJSONArray("translation").get(0));

    }

    // 如果你对区块链感兴趣，这门课很适合你。帕特里克·柯林斯是一位资深的软件工程师和长期的金融行业开发人员。
    // 如果你有兴趣了解区块链，这是||的课程。Patrick Collins|是一个资深的软件工程师||和长期的金融行业|开发人员||
    // 如果你对区块链感兴趣，这是||课程。帕特里克·柯林斯是一位资深的软件工程师和资深的金融行业开发人员
    public static void main(String[] args) {
//        String hello = getCn("If you're interested in learning|about blockchain, this is the||course for you. Patrick Collins|is a veteran software engineer||and longtime finance industry|developer.||");
        String hello = getCn("If you're interested in learning about blockchain, this is the||course for you. Patrick Collins is a veteran software engineer||and longtime finance industry developer.||");
        System.out.println(hello);
    }

}
