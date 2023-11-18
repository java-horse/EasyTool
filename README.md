# EasyChar

> ![JetBrains](https://img.shields.io/jetbrains/plugin/v/21589)
> [![star](https://gitee.com/milubin/easy-char-plugin/badge/star.svg?theme=dark)](https://gitee.com/milubin/easy-char-plugin/stargazers)

#### 插件说明
> * 支持中文字符自动转为英文字符
> * 支持根据 javadoc 生成 Swagger2.x 相关注解，如果没有 javadoc 会自动尝试自动翻译处理(自动翻译可能会有点慢, 请耐心等待)。支持选中类名、字段名、方法名生成指定Swagger注解
> * 支持中英文互译，且支持设置多种免费高效翻译引擎
>   * 百度翻译
>   * 阿里翻译
>   * 有道翻译
>   * 腾讯翻译
>   * 华为翻译
>   * 火山翻译
>   * 讯飞翻译
>   * 小牛翻译
>   * 彩云翻译
>   * 微软翻译
>   * 谷歌翻译(API)
>   * 谷歌翻译(Free)
> * 支持蛇形格式变量转驼峰格式
> * 支持选中文本右键快捷Web搜索
> * 支持快捷生成 serialVersionUID, 快捷键: CTRL + SHIFT + ALT + \
> * 支持自动复制接口完整URL

#### 使用说明
> * 在JetBrains IED 的插件市场收索 EasyChar 并安装成功
> * 输入以下常用中文字符 `( ，。；！（）「」《》)` 将自动转换成对应的英文字符
> * 如果想输入中文字符，则需要先输入 `/` 字符，然后再输入想要的中文字符即可
> * 点击 View -> Tool Windows -> EasyChar 可以查看中英文字符转换次数统计
> * 选中中文字符串，快捷键 `CTRL + \ ` 右键选择 Translate 选项即可翻译或自动替换 (需先配置翻译渠道密钥)
> * 选中变量, 快捷键 `CTRL + ALT + \ ` 即可自动转换蛇形命名或者驼峰命名
> * 选中文本, 右键选择 `WebSearch`后, 选择对应搜索引擎进行快捷Web搜索
> * 在代码编辑器区域, 快捷键 `CTRL + SHIFT + ALT + \ ` 即可自动从光标处开始生成 `serialVersionUID`
> * 支持自动复制接口完整URL, 在@RequestMapping或Restful风格Mapping注解的接口上右键选择CopyUrl

#### 版本说明
> * 支持IDEA 2022.x~2023.x 全系列版本，适用于 JetBrains IDE 全家桶

#### 插件图示
