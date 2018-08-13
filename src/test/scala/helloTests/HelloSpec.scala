// src/test/scala/helloTests/HelloSpec.scala

package helloTests

import org.scalatest.{Matchers, FreeSpec}
import hello._


class HelloSpec extends FreeSpec with Matchers {
  "Just a simple demo" - {
    "A stupid assertion" in {
      Hello.message should be ("Hello, world!")
    }
  }
}
