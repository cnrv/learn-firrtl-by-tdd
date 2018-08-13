package firrtl

object Driver {
  def execute(args: Array[String]) = {
    val optionsManager = new ExecutionOptionsManager("firrtl") with HasFirrtlOptions

    optionsManager.parse(args)
  }

  def main(args: Array[String]): Unit = {
    val ret_code = execute(args)
    println(s"The Return Code: $ret_code")
  }
}
