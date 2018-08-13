// src/main/scala/firrtl/ExecutionOptionsManager.scala
package firrtl

trait ComposableOptions

abstract class HasParser(applicationName: String)

case class CommonOptions (
  topName:       String = "",
  targetDirName: String = "."
) extends ComposableOptions


trait HasCommonOptions {
  self: ExecutionOptionsManager =>
  // 后面会保存命令行的参数，因此声明为可变类型
  var commonOptions = CommonOptions()
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
}
