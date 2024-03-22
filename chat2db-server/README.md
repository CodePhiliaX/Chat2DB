# 内部协作规范
## 接口规范
不会使用的参照：https://yuque.antfin-inc.com/docs/share/8a5ff21a-6367-4c77-9e3c-1d5ae9570060?# 《yapi》
## 国际化处理方案
* 在`messages.properties` 文件下新增code
  * 规范是 `作用域.描述` ，比如 `dataSource.sqlAnalysisError`
* 方案1：在需要提示用户的地方抛出业务异常
```java
// 框架会将 dataSource.sqlAnalysisError 翻译成对应的异常，并返回给前端
throw new BusinessException("dataSource.sqlAnalysisError");
```
* 方案2：不用异常直接获取国际化
```java
// 直接可以获取国际化翻译的文案
I18nUtils.getMessage("dataSource.sqlAnalysisError")
```
### 国际化中文乱码
Editor -> File Encodeings -> Defualt encoding for properties files: 改成 utf-8
### 编辑国际化文件
建议安装插件 `Resource Bundle Editor`去编辑，,点击`messages.properties`，下方有个`Resource Bundle` 就可以编辑了。