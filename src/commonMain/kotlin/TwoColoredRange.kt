package me.thorny.twoColoredRange

import kotlin.math.max

open class TwoColoredRange<BoundType: Comparable<BoundType>, LengthType, ColorType: Enum<ColorType>>(
  val range: ClosedRange<BoundType>,
  private val step: LengthType,
  private val math: BoundMath<BoundType, LengthType>,
  private val defaultColor: ColorType,
  private val otherColor: ColorType,
) {
  val length = math.subtract(math.add(range.endInclusive, step), range.start)
  private val otherColorSubranges = mutableListOf<ClosedRange<BoundType>>()

  init {
    if (range.start > range.endInclusive) {
      throw Exception("Range start can't be greater than end")
    }

//    if (fullRange.start == math.add(fullRange.start, increment)) {
//      throw Exception("Bad increment")
//    }
  }

  private fun checkSubrange(subrange: ClosedRange<BoundType>): Unit {
    if (!range.containsRange(subrange)) {
      throw Exception("Subrange is out of range bounds")
    }

    if (subrange.start > subrange.endInclusive) {
      throw Exception("Subrange start can't be greater than end")
    }
  }

  fun getSubrangesOfDefaultColor(): Collection<ClosedRange<BoundType>> {
    return arrayListOf(range)
  }

  fun getSubrangesOfOtherColor(): Collection<ClosedRange<BoundType>> {
    return otherColorSubranges
  }

  fun getSubrangesOfColor(color: ColorType): Collection<ClosedRange<BoundType>> {
    return when (color) {
      defaultColor -> getSubrangesOfDefaultColor()
      otherColor -> getSubrangesOfOtherColor()
      else -> emptyList()
    }
  }

  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>): Unit {
    checkSubrange(subrange)

    var didModifyByIntersection = false
    val intersectingSubranges = otherColorSubranges.filter { it.intersectsRange(subrange) }

    if (intersectingSubranges.isEmpty()) {
      // NOP
    } else if (intersectingSubranges.size == 1) {
      val intersectingSubrange = intersectingSubranges[0]
      otherColorSubranges[otherColorSubranges.indexOf(intersectingSubrange)] = subrange.joinRange(intersectingSubrange)
      didModifyByIntersection = true
    }

    val touchingSubranges = otherColorSubranges.filter { math.rangeTouchesRange(it, subrange, step) }

    if (touchingSubranges.isEmpty() && !didModifyByIntersection) {
      val index = otherColorSubranges.indexOfLast { it.start < subrange.start }
      otherColorSubranges.add(index + 1, subrange)
    } else if (touchingSubranges.size == 1) {
      val touchingSubrange = touchingSubranges[0]
      otherColorSubranges[otherColorSubranges.indexOf(touchingSubrange)] = subrange.joinRange(touchingSubrange)
    } else if (touchingSubranges.size == 2) {
      val first = touchingSubranges[0]
      val second = touchingSubranges[1]
      otherColorSubranges.remove(second)
      otherColorSubranges[otherColorSubranges.indexOf(first)] = first.joinRange(second)
    }
  }

  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType): Unit {
    when (color) {
      otherColor -> setSubrangeOtherColor(subrange)
    }
  }
}
