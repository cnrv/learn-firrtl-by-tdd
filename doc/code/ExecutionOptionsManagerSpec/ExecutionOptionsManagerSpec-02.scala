// src/test/scala/firrtlTests/ExecutionOptionsManagerSpec.scala
package firrtlTests

import firrtl._
import org.scalatest.{Matchers, FreeSpec}

class ExecutionOptionsManagerSpec extends FreeSpec with Matchers {
  "ExecutionOptionsManager 是一个容器，用作很多组件的选项模块（ComposableOptions Block）" - {
    "它有一个默认的通用选项模块（CommonOptionsBlock）." in {
      val manager = new ExecutionOptionsManager("test")
      manager.commonOptions.targetDirName should be (".")
    }
    "但可以像下面那样重载默认值" in {
      val manager = new ExecutionOptionsManager("test") { commonOptions = CommonOptions(topName = "dog") }
      manager.commonOptions shouldBe a [CommonOptions]
      manager.topName should be ("dog")
      manager.commonOptions.topName should be ("dog")
    }
  }
}
