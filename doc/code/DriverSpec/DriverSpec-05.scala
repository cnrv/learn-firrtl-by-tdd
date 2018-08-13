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
        "可以设置顶层名字 top name 和生成的目标 target " in {
          val optionsManager = new ExecutionOptionsManager("test")
          optionsManager.parse(Array("--top-name", "dog", "--target-dir", "a/b/c")) should be(true)
          val commonOptions = optionsManager.commonOptions

          commonOptions.topName should be("dog")
          commonOptions.targetDirName should be("a/b/c")

          optionsManager.getBuildFileName(".fir") should be("a/b/c/dog.fir")
          optionsManager.getBuildFileName("fir") should be("a/b/c/dog.fir")
        }
      }
      "CommonOptions 能够新建一个目录" in {
        var dir = new java.io.File("a/b/c")
        if(dir.exists()) {
          dir.delete()
        }
        val optionsManager = new ExecutionOptionsManager("test")
        optionsManager.parse(Array("--top-name", "dog", "--target-dir", "a/b/c")) should be (true)
        val commonOptions = optionsManager.commonOptions

        commonOptions.topName should be ("dog")
        commonOptions.targetDirName should be ("a/b/c")

        optionsManager.makeTargetDir() should be (true)
        dir = new java.io.File("a/b/c")
        dir.exists() should be (true)
        FileUtils.deleteDirectoryHierarchy("a") should be (true)
      }
    }
    "commonOptions.programArgs 默认会返回多余的参数" in {
      val optionsManager = new ExecutionOptionsManager("test")

      optionsManager.parse(Array("--top-name", "dog", "fox", "tardigrade", "stomatopod")) should be(true)
      optionsManager.commonOptions.programArgs.length should be(3)
      optionsManager.commonOptions.programArgs should be("fox" :: "tardigrade" :: "stomatopod" :: Nil)

      optionsManager.commonOptions = CommonOptions()
      optionsManager.parse(
        Array("dog", "stomatopod")) should be(true)
      optionsManager.commonOptions.programArgs.length should be(2)
      optionsManager.commonOptions.programArgs should be("dog" :: "stomatopod" :: Nil)

      optionsManager.commonOptions = CommonOptions()
      optionsManager.parse(
        Array("fox", "--top-name", "dog", "tardigrade", "stomatopod")) should be(true)
      optionsManager.commonOptions.programArgs.length should be(3)
      optionsManager.commonOptions.programArgs should be("fox" :: "tardigrade" :: "stomatopod" :: Nil)
    }
  }
  "FirrtlOptions 保存用于 firrtl 编译器相关的选项信息" - {
    "包括通用选项 CommonOptions" in {
      val optionsManager = new ExecutionOptionsManager("test")
      optionsManager.commonOptions.targetDirName should be(".")
    }
  }
}
