# subtitle-translation

对srt字幕进行整理裁剪成完整句子。再翻译，能有效结合上下文。再裁剪分割。

本项目对 Youtube 课程进行翻译实践：
**Learn Blockchain, Solidity, and Full Stack Web3 Development with JavaScript – 32-Hour Course:**
https://www.youtube.com/watch?v=gyMwXuJrbJQ

因为youtebe 上这个视频太大了，字幕文件也大。机翻根本翻译不出来中文。只能自己翻译。

项目中 cn_zimu_ultimately.srt 为最终的中文字幕。可以自行导入到视频中食用。我自己正在食用，过程中发现问题也会优化

项目中的代码并不严谨。我只是针对该字幕编写的代码。出现bug什么越位，空指针啥的请自行更改并最好提交。

本项目要自行提供有道云翻译API。也可以自己编写翻译API。
在 TranslateTool.java文件更改 id和密钥 <br/>

`String id = "有道ID";
String secret = "有道密钥";`

如果觉得最终的中文字幕一段太长，可以自行调整缩短一段字幕长度

`int threshold = 9000; // 间隔时间ms`

# 代码步骤

* 经过 CompressionSrt.java 执行后
* 生成 all_zimu.srt （初步整理断句为完整句子）
* 经过 TranslateWork.java 执行后
* 生成 cn_zimu.srt （翻译后的字幕，完整句子）
* 经过 Cutting.java 执行后 （中文裁剪成小句）
* 生成 cn_zimu_ultimately.srt 最终字幕文件

# 字幕食用步骤
* 下载原视频 例如：https://www.youtube.com/watch?v=gyMwXuJrbJQ
* 用本地播放器打开
* 导入 cn_zimu_ultimately.srt 字幕
* 食用

# 工具
* hutool
* 有道翻译API
