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
    "重载的方式是添加一个给定类型的新版本" in {
      val manager = new ExecutionOptionsManager("test") { commonOptions = CommonOptions(topName = "dog") }
      val initialCommon = manager.commonOptions
      initialCommon.topName should be ("dog")

      manager.commonOptions = CommonOptions(topName = "cat")

      val afterCommon = manager.commonOptions
      afterCommon.topName should be ("cat")
      initialCommon.topName should be ("dog")
    }
    "多个组件化的选项模块（composable blocks）应该分开" in {
      val manager = new ExecutionOptionsManager("test") with HasFirrtlOptions {
        commonOptions = CommonOptions(topName = "spoon")
        firrtlOptions = FirrtlExecutionOptions(inputFileNameOverride = "fork")
      }

      manager.firrtlOptions.inputFileNameOverride should be ("fork")
      manager.commonOptions.topName should be ("spoon")
    }
  }
}
