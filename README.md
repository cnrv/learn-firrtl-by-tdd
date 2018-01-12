通过测试驱动开发的方式学 FIRRTL
===============================

缘起
----

FIRRTL 的测试代码写的蛮详尽的，里面的注释也很丰富。本项目通过分析它的测试代码，参考其所测试的源码来学习 FIRRTL 是如何一点一点构建出来的。

Sifive SoC 产品的开源版本 [freedom](https://github.com/sifive/freedom/tree/f4375c22662f82b1b4f94e88b1aba6998b1f34ba) 使用 FIRRTL  [5b35f2d2 版本](https://github.com/freechipsproject/firrtl/tree/5b35f2d2722f72c81d2d6c507cd379be2a1476d8) 。因此本项目也是基于此版本。

目录结构
--------

### build.sbt ###

构建 SBT 项目的配置文件

### doc ###

本项目的教程

### orig/test/scala ###

原来的测试代码

### orig/main/scala ###

原来的 FIRRTL 源码

### src/test/scala ###

本项目教程配套的测试代码

### src/main/scala ###

本项目教程配套的代码
