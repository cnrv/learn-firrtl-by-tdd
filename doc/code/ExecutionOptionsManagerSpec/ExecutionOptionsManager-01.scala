// src/main/scala/firrtl/ExecutionOptionsManager.scala
package firrtl

trait ComposableOptions

abstract class HasParser(applicationName: String)

case class CommonOptions (
  val targetDirName: String = "."
) extends ComposableOptions

trait HasCommonOptions {
  self: ExecutionOptionsManager =>
  // 后面会保存命令行的参数，因此声明为可变类型
  var commonOptions = CommonOptions()
}

class ExecutionOptionsManager(val applicationName: String) extends HasParser(applicationName) with HasCommonOptions{
  val commonOptions = new CommonOptions
}
