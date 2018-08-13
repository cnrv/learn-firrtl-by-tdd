package firrtlTests

import firrtl._
import org.scalatest.{Matchers, FreeSpec}

class DriverSpec extends FreeSpec with Matchers {
  "CommonOptions 是一些可在 chisel3 生态系统里使用的简单选项" - {
    "CommonOption 例化了 scopt 库里的 OptionParser 类" - {
      "通过传递 Array[String] 到 main 函数设置选项" - {
        "没有传递参数时使用默认值" in {
          val optionsManager = new ExecutionOptionsManager("test")
          optionsManager.parse(Array.empty[String]) should be(true)

          val commonOptions = optionsManager.commonOptions
          commonOptions.topName should be("")
          commonOptions.targetDirName should be(".")
        }
      }
    }
  }
}
