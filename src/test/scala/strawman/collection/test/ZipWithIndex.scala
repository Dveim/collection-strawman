package strawman
package collection
package test

import immutable.{LazyList, List, Range, Vector}

import scala.{Array, Int, Unit}
import scala.Predef.{ArrowAssoc, assert}
import org.junit.Test

class ZipWithIndex {

  @Test
  def originalZipWithIndex(): Unit = {
    assert(List(1, 2, 3).zipWithIndex == List((1, 0), (2, 1), (3, 2)))
    assert(List(1, 2, 3).view.zipWithIndex.toArray sameElements Array[(Int, Int)]((1, 0), (2, 1), (3, 2)))
    assert(LazyList(1, 2, 3).zipWithIndex.toArray sameElements Array[(Int, Int)]((1, 0), (2, 1), (3, 2)))
    assert(Vector(1, 2, 3).zipWithIndex == Vector((1, 0), (2, 1), (3, 2)))
    assert(Array(1, 2, 3).zipWithIndex sameElements Array[(Int, Int)]((1, 0), (2, 1), (3, 2)))
    assert(Range(1, 4).zipWithIndex.toArray sameElements Array[(Int, Int)]((1, 0), (2, 1), (3, 2)))
  }

  @Test
  def zipWithIndexStartStep(): Unit = {
    assert(List(1, 2, 3).zipWithIndex(42, -1) == List((1, 42), (2, 41), (3, 40)))
    assert(List(1, 2, 3).view.zipWithIndex(42, -1).toArray sameElements Array[(Int, Int)]((1, 42), (2, 41), (3, 40)))
    assert(LazyList(1, 2, 3).zipWithIndex(42, -1).toArray sameElements Array[(Int, Int)]((1, 42), (2, 41), (3, 40)))
    assert(Vector(1, 2, 3).zipWithIndex(42, -1) == Vector((1, 42), (2, 41), (3, 40)))
    assert(Array(1, 2, 3).zipWithIndex(42, -1) sameElements Array[(Int, Int)]((1, 42), (2, 41), (3, 40)))
    assert(Range(1, 4).zipWithIndex(42, -1).toArray sameElements Array[(Int, Int)]((1, 42), (2, 41), (3, 40)))
  }
}
