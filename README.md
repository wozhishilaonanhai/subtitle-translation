# subtitle-translation

对srt字幕进行整理裁剪成完整句子。再翻译，能有效结合上下文。再裁剪分割。

本项目对 Youtube 课程进行翻译实践：
**Learn Blockchain, Solidity, and Full Stack Web3 Development with JavaScript – 32-Hour Course:**
https://www.youtube.com/watch?v=gyMwXuJrbJQ

项目中 cn_zimu_ultimately.rst 为最终的中文字幕。可以自行导入到视频中食用。我自己正在食用，过程中发现问题也会优化

项目中的代码并不严谨。我只是针对该字幕编写的代码。出现bug什么越位，空指针啥的请自行更改并最好提交。

# 步骤

* 经过 CompressionSrt.java 执行后
* 生成 all_zimu.rst
* 经过 TranslateWork.java 执行后
* 生成 cn_zimu.rst
* 经过 Cutting.java 执行后
* 生成 cn_zimu_ultimately.rst 最终字幕文件


# 工具
* hutool
