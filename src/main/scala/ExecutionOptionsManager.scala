// See License

package firrtl

/**
  * 大部分 Chisel 工具链上的组件需要一个顶层名字（top name）来定义一个电路或者是待测设备（device under test)。
  * 大部分都是在一个目录完成这些工作。
  * 强制要求指定一个顶层名字是最简单的方式，但实践中推迟这一步是更好的选择。
  * 举个例子，推迟指定一个顶层名字使得 Chisel 可以先 elaborate 电路，如果没有指定顶层名字的话，会根据该电路设置一个。
  */
case class CommonOptions (
  topName:        String = "",
  targetDirName:  String = "test_run_dir"
)

case class FirrtlExecutionOptions (
  inputFileNameOverride:  String = ""
)

trait HasFirrtlOptions {
  var firrtlOptions = FirrtlExecutionOptions()
}

trait HasCommonOptions {
  var commonOptions = CommonOptions()
}

class ExecutionOptionsManager extends HasCommonOptions{
  def topName       = commonOptions.topName
}

// package firrtl

// /**
//   * 大部分 Chisel 工具链上的组件需要一个顶层名字（top name）来定义一个电路或者是待测设备（device under test)。
//   * 大部分都是在一个目录完成这些工作。
//   * 强制要求指定一个顶层名字是最简单的方式，但实践中推迟这一步是更好的选择。
//   * 举个例子，推迟指定一个顶层名字使得 Chisel 可以先 elaborate 电路，如果没有指定顶层名字的话，会根据该电路设置一个。
//   */
// case class CommonOptions(
//     targetDirName:  String = "test_run_dir")

// trait HasCommonOptions {
//   self: ExecutionOptionsManager =>
//   val commonOptions = CommonOptions()
// }

// class ExecutionOptionsManager extends HasCommonOptions {
//   def targetDirName: String = commonOptions.targetDirName
// }
