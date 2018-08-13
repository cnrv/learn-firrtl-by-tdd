// src/main/scala/firrtl/ExecutionOptionsManager.scala
package firrtl

import scopt.OptionParser

import scala.collection.Seq

/**
  * 如果想在外部声明的 parser 添加一个新的私有命令行选项，请使用（继承）这个 trait 来定义该选项类。
  * Use this trait to define an options class that can add its private command line options to a externally
  * declared parser.
  * '''注意''' 在所有由此派生的 trait 或 类 中，如果你打算保持向后兼容的特性，确保是在当前的后面添加新的选项，不要删除任何现有的。
  * '''NOTE''' In all derived trait/classes, if you intend on maintaining backwards compatibility,
  *  be sure to add new options at the end of the current ones and don't remove any existing ones.
  */
trait ComposableOptions

abstract class HasParser(applicationName: String) {
  final val parser = new OptionParser[Unit](applicationName) {}
}

/**
  * TODO: 大部分的。。。。
  * Most of the chisel toolchain components require a topName which defines a circuit or a device under test.
  * Much of the work that is done takes place in a directory.
  * It would be simplest to require topName to be defined but in practice it is preferred to defer this.
  * For example, in chisel, by deferring this it is possible for the execute there to first elaborate the
  * circuit and then set the topName from that if it has not already been set.
  */
case class CommonOptions(
  topName:           String      = "",
  targetDirName:     String      = ".",
  programArgs:       Seq[String] = Seq.empty
) extends ComposableOptions

trait HasCommonOptions {
  // 依赖注入，表明继承的只能是 ExecutionOptionsManager，这样就可以使用 parser 属性
  // 因为 ExecutionOptionsManager 继承了 HasParser
  self: ExecutionOptionsManager =>

  // 后面会保存命令行的参数，因此声明为可变类型
  var commonOptions = CommonOptions()

  parser.note("通用选项")

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

  parser.help("help").text("输出下列使用帮助")

  parser.arg[String]("<arg>...").unbounded().optional().action( (x, c) =>
    commonOptions = commonOptions.copy(programArgs = commonOptions.programArgs :+ x) ).text("可选的没有指定用途的参数")
}

/**
  * Firrtl 输出由 [[FirrtlExecutionOptions]] 指定的配置
  * Firrtl output configuration specified by [[FirrtlExecutionOptions]]
  *
  * 由这个执行选项的该字段派生出来
  * Derived from the fields of the execution options
  * @see [[FirrtlExecutionOptions.getOutputConfig]]
  */
sealed abstract class OutputConfig
final case class SingleFile(targetFile: String) extends OutputConfig

/**
  * 该选项由 firrtl 以可调用组件的方式来使用
  * The options that firrtl supports in callable component sense
  *
  * @param inputFileNameOverride  default is targetDir/topName.fir
  */
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
    * Get the user-specified [[OutputConfig]]
    *
    * @param optionsManager this is needed to access build function and its common options
    *                       这是用来获取构建函数和它的通用选项
    * @return the output configuration
    *         返回输出配置
    */
  def getOutputConfig(optionsManager: ExecutionOptionsManager): OutputConfig = {
    SingleFile(optionsManager.getBuildFileName(outputSuffix, outputFileNameOverride))
  }

  /**
    * 获取用户定义的目标文件，这里要求 [[OutputConfig]] 是 [[SingleFile]]
    * Get the user-specified targetFile assuming [[OutputConfig]] is [[SingleFile]]
    *
    * @param optionsManager this is needed to access build function and its common options
    *                       这是用来获取构建函数和它的通用选项
    * @return the targetFile as a String
    *         以字符串的形式返回目标文件
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

/**
  *
  * @param applicationName  The name shown in the usage 用于在使用帮助里显示的应用程序名字
  */
class ExecutionOptionsManager(val applicationName: String) extends HasParser(applicationName) with HasCommonOptions {

  def parse(args: Array[String]): Boolean = {
    parser.parse(args)
  }

  /**
    * 确保 目标目录名的所有目录层都存在
    * make sure that all levels of targetDirName exist
    *
    * @return true if directory exists
    */
  def makeTargetDir(): Boolean = {
    FileUtils.makeDirectory(commonOptions.targetDirName)
  }

  def topName: String = commonOptions.topName

  def targetDirName: String = commonOptions.targetDirName

   /**
    * 基于目标目录，顶层模块名和后缀返回文件名
    * 如果顶层模块名已经包含了后缀，则不会添加新的后缀
    * return a file based on targetDir, topName and suffix
    * Will not add the suffix if the topName already ends with that suffix
    *
    * @param suffix suffix to add, removes . if present
    *               添加的后缀名，如果参数本身包含了 点，则把点去掉
    * @param fileNameOverride this will override the topName if nonEmpty, when using this targetDir is ignored
    *                         如果该参数非空，将会覆盖掉顶层模块名 topName，也会忽略目标目录名
    * @return
    */
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
