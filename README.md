# EasyTool

> ![JetBrains](https://img.shields.io/jetbrains/plugin/v/21589)
> [![star](https://gitee.com/milubin/easy-tool-plugin/badge/star.svg?theme=dark)](https://gitee.com/milubin/easy-tool-plugin/stargazers)

#### 插件说明
> * 支持中文字符自动转为英文字符，可自定义字符映射关系
> * 支持根据 `Javadoc` 生成 `Swagger2.x` 相关注解，如果没有 `Javadoc` 会尝试翻译处理(翻译可能会有点慢, 请耐心等待)。支持选中类名、方法名、字段名等细粒度生成
> * 支持中英文互译，且支持设置多种免费高效翻译引擎
>   * _百度翻译_
>   * _阿里翻译_
>   * _有道翻译_
>   * _金山翻译_
>   * _腾讯翻译_
>   * _华为翻译_
>   * _火山翻译_
>   * _讯飞翻译_
>   * _小牛翻译_
>   * _彩云翻译_
>   * _同花顺翻译_
>   * _微软翻译(API)_
>   * _微软翻译(Free)_
>   * _谷歌翻译(API)_
>   * _谷歌翻译(Free)_
> * 支持并接入多种开源大模型翻译引擎
>   * _通义千问_
> * 支持蛇形格式和驼峰格式互转
> * 支持选中文本右键快捷Web搜索
> * 支持快捷生成 `serialVersionUID`, 快捷键: `CTRL + SHIFT + ALT + \`
> * 支持自动复制接口完整URL

#### 使用说明
> * 在 JetBrains IED 的插件市场收索 `EasyTool` 并安装成功
> * 输入以下常用中文字符 `( ，。；！（）「」《》)` 将自动转换成对应的英文字符
> * 如果想输入中文字符，则需要先输入 `/` 字符，然后再输入想要的中文字符即可
> * 点击 `View` -> `Tool Windows` -> `EasyTool` 可以查看中英文字符转换次数统计
> * 选中中文字符串，快捷键 `CTRL + \ ` 右键选择 Translate 选项即可翻译或自动替换 (需先配置翻译渠道密钥)
> * 选中变量, 快捷键 `CTRL + ALT + \ ` 即可自动转换蛇形命名或者驼峰命名
> * 选中文本, 右键选择 `WebSearch`后, 选择对应搜索引擎进行快捷Web搜索
> * 在代码编辑器区域, 快捷键 `CTRL + SHIFT + ALT + \ ` 即可自动从光标处开始生成 `serialVersionUID`
> * 支持自动复制接口完整URL, 在@RequestMapping或Restful风格Mapping注解的接口上右键选择CopyUrl

#### 版本说明
> * 支持IDEA 2022.x~2023.x 全系列版本，适用于 `JetBrains IDE` 全家桶

#### 插件图示
