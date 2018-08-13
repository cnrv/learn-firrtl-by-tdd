// src/main/scala/firrtl/ExecutionOptionsManager.scala
package firrtl

trait ComposableOptions

abstract class HasParser(applicationName: String)

case class CommonOptions (
  topName:       String = "",
  targetDirName: String = ".",
    programArgs: Seq[String] = Seq.empty
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

  parser.arg[String]("<arg>...").unbounded().optional().action( (x, c) =>
    commonOptions = commonOptions.copy(programArgs = commonOptions.programArgs :+ x) ).text("可选的没有指定用途的参数")
}

case class FirrtlExecutionOptions(
  inputFileNameOverride:  String = "",
    outputFileNameOverride: String = "",
  compilerName: String = "verilog"
) extends ComposableOptions {

  def outputSuffix: String = {
    compilerName match {
      case "verilog"   => "v"
      case "sverilog"  => "sv"
      case "low"       => "lo.fir"
      case "high"      => "hi.fir"
      case "middle"    => "mid.fir"
      case _ =>
        throw new Exception(s"Illegal compiler name $compilerName")
    }
  }

  /**
    * 获取用户定义的 [[OutputConfig]]
    * 参数 optionsManager 这是用来获取构建函数和它的通用选项
    * 返回输出配置
    */
  def getOutputConfig(optionsManager: ExecutionOptionsManager): OutputConfig = {
    SingleFile(optionsManager.getBuildFileName(outputSuffix, outputFileNameOverride))
  }

  /**
    * 获取用户定义的目标文件，这里要求 [[OutputConfig]] 是 [[SingleFile]]
    * 参数 optionsManager 这是用来获取构建函数和它的通用选项
    * 以字符串的形式返回目标文件
    */
  def getTargetFile(optionsManager: ExecutionOptionsManager): String = {
    getOutputConfig(optionsManager) match {
      case SingleFile(targetFile) => targetFile
      case other => throw new Exception("OutputConfig is not SingleFile!")
    }
  }
}

trait HasFirrtlOptions {
  // 依赖注入，表明继承的只能是 ExecutionOptionsManager，这样就可以使用 parser 属性
  // 因为 ExecutionOptionsManager 继承了 HasParser
  self: ExecutionOptionsManager =>

  // 后面会保存命令行的参数，因此声明为可变类型
  var firrtlOptions = FirrtlExecutionOptions()

  parser.note("firrtl options")

  parser.opt[String]("input-file")
    .abbr("i")                         // 添加缩写，-i 等价于 --input-file
    .valueName ("<firrtl-source>")     // 解释该参数的值的作用
    .foreach { x =>                    // 把解析到参数赋值给 firrtlOptions 的 inputFileNameOverride 属性
      firrtlOptions = firrtlOptions.copy(inputFileNameOverride = x)
    }.text {                           // 该参数的详细描述
      "输入文件名默认为空，可以指定该参数起新的名字"
    }

  parser.opt[String]("output-file")
    .abbr("o")                         // 添加缩写，-o 等价于 --output-file
    .valueName("<output>")             // 解释该参数的值的作用
    .foreach { x =>                    // 把解析到参数赋值给 firrtlOptions 的 outputFileNameOverride 属性
      firrtlOptions = firrtlOptions.copy(outputFileNameOverride = x)
    }.text {
      "输出文件名默认为空，可以指定该参数起新的名字"
    }
}

class ExecutionOptionsManager(val applicationName: String) extends HasParser(applicationName) with HasCommonOptions {

  def makeTargetDir(): Boolean = {
    FileUtils.makeDirectory(commonOptions.targetDirName)
  }

  def topName: String = commonOptions.topName

  def getBuildFileName(suffix: String, fileNameOverride: String = ""): String = {
    val baseName = if(fileNameOverride.nonEmpty) fileNameOverride else topName

    val directoryName = {
      if(fileNameOverride.nonEmpty) {
        ""
      }
      // 如果文件名自带了目录名则忽略目标目录名 targetDirName
      else if(baseName.startsWith("./") || baseName.startsWith("/")) {
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
