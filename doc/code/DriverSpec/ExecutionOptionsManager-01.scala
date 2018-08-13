// src/main/scala/firrtl/ExecutionOptionsManager.scala
package firrtl

trait ComposableOptions

abstract class HasParser(applicationName: String)

case class CommonOptions (
  topName:       String = "",
  targetDirName: String = "."
) extends ComposableOptions

trait HasCommonOptions {
  // 依赖注入，表明继承的只能是 ExecutionOptionsManager，这样就可以使用 parser 属性
  // 因为 ExecutionOptionsManager 继承了 HasParser
  self: ExecutionOptionsManager =>

  // 后面会保存命令行的参数，因此声明为可变类型
  var commonOptions = CommonOptions()

  parser.note("common options")

  parser.opt[String]("top-name")
    .abbr("tn")                                              // 添加缩写，-tn 等价于 --top-name
    .valueName("<top-level-circuit-name>")                   // 解释该参数的值的作用
    .foreach { x =>
      commonOptions = commonOptions.copy(topName = x)        // 把解析到参数赋值给 commonOptions 的 topName 属性
    }
    .text("该选项定义了顶层电路，默认名字尽可能跟待测模块（dut）一致") // 该参数的详细描述

  parser.opt[String]("target-dir")
    .abbr("td")                                              // 添加缩写，-td 等价于 --target-dir
    .valueName("<target-directory>")                         // 解释该参数的值的作用
    .foreach { x =>
      commonOptions = commonOptions.copy(targetDirName = x)  // 把解析到参数赋值给 commonOptions 的 targetDirName 属性
    }                                                        // 该参数的详细描述
    .text(s"该选项定义了用于存放程序运行过程中产生的文件，默认值是 ${commonOptions.targetDirName}") 

  parser.help("help").text("prints this usage text")
}

case class FirrtlExecutionOptions(
  inputFileNameOverride:  String = ""
) extends ComposableOptions

trait HasFirrtlOptions {
  self: ExecutionOptionsManager =>
  // 后面会保存命令行的参数，因此声明为可变类型
  var firrtlOptions = FirrtlExecutionOptions()
}

class ExecutionOptionsManager(val applicationName: String) extends HasParser(applicationName) with HasCommonOptions {
  def topName: String = commonOptions.topName

  def getBuildFileName(suffix: String): String = {
    val baseName = topName

    val directoryName = {
      // 如果文件名自带了目录名则忽略目标目录名 targetDirName
      if(baseName.startsWith("./") || baseName.startsWith("/")) {
        ""
      }
      else {
        // 如果目标目录名字里没有 "/" 分隔符的话，加上 "/"
        if(targetDirName.endsWith("/")) targetDirName else targetDirName + "/"
      }
    }
    val normalizedSuffix = {
      // 如果后缀名没有带点就加上
      val dottedSuffix = if(suffix.startsWith(".")) suffix else s".$suffix"
      // 如果名字本身就带有相同的后缀就不用重复添加了
      if(baseName.endsWith(dottedSuffix)) "" else dottedSuffix
    }
    // 把目录名、文件名和后缀名拼接起来作为返回值
    s"$directoryName$baseName$normalizedSuffix"
  }
}
