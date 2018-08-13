package firrtl

object Driver {
  /**
    * 输出红色的信息
    * print the message in red
    *
    * @param message error message
    */
  def dramaticError(message: String): Unit = {
    println(Console.RED + "-"*78)
    println(s"Error: $message")
    println("-"*78 + Console.RESET)
  }

  def execute(args: Array[String]) = {
    val optionsManager = new ExecutionOptionsManager("firrtl") with HasFirrtlOptions

    optionsManager.parse(args)
  }

  def main(args: Array[String]): Unit = {
    val ret_code = execute(args)
    println(s"The Return Code: $ret_code")
  }
}

object FileUtils {
  /**
    * 递归地创建目录以及所有父目录
    */
  def makeDirectory(directoryName: String): Boolean = {
    val dirFile = new java.io.File(directoryName)
    if(dirFile.exists()) {
      if(dirFile.isDirectory) {
        true
      }
      else {
        false
      }
    }
    else {
      dirFile.mkdirs()
    }
  }

  /**
    * 递归地删除相对路径里的所有目录
    */
  def deleteDirectoryHierarchy(directoryPathName: String): Boolean = {
    deleteDirectoryHierarchy(new File(directoryPathName))
  }
  /**
    * 递归地删除相对路径里的所有目录
    */
  def deleteDirectoryHierarchy(file: File, atTop: Boolean = true): Boolean = {
    if(file.getPath.split("/").last.isEmpty ||
      file.getAbsolutePath == "/" ||
      file.getPath.startsWith("/")) {
      Driver.dramaticError(s"delete directory ${file.getPath} will not delete absolute paths")
      false
    }
    else {
      val result = {
        if(file.isDirectory) {
          file.listFiles().forall( f => deleteDirectoryHierarchy(f)) && file.delete()
        }
        else {
          file.delete()
        }
      }
      result
    }
  }
}