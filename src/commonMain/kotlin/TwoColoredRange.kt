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

    val intersectingSubranges = otherColorSubranges.filter { it.intersectsRange(subrange) }
    val touchingSubranges = otherColorSubranges.filter { math.rangeTouchesRange(it, subrange, step) }

    if (intersectingSubranges.isEmpty() && touchingSubranges.isEmpty()) {
      val index = otherColorSubranges.indexOfLast { it.start < subrange.start }
      otherColorSubranges.add(index + 1, subrange)
    } else {
      var joinedSubrange = subrange
      var replacedSubrange: ClosedRange<BoundType>? = null

      if (intersectingSubranges.isNotEmpty()) {
        val first = intersectingSubranges.first()
        val last = intersectingSubranges.last()
        for (index in otherColorSubranges.indexOf(last) downTo otherColorSubranges.indexOf(first) + 1) {
          otherColorSubranges.removeAt(index)
        }

        joinedSubrange = joinedSubrange.joinRange(first).joinRange(last)
        replacedSubrange = first
      }

      if (touchingSubranges.isNotEmpty()) {
        if (replacedSubrange == null) {
          replacedSubrange = touchingSubranges.first()
        }

        touchingSubranges.reversed().forEachIndexed() { index, it ->
          joinedSubrange = joinedSubrange.joinRange(it)
          if (index != touchingSubranges.lastIndex || intersectingSubranges.isNotEmpty()) {
            otherColorSubranges.remove(it)
          }
        }
      }

      otherColorSubranges[otherColorSubranges.indexOf(replacedSubrange)] = joinedSubrange
    }
  }

  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType): Unit {
    when (color) {
      otherColor -> setSubrangeOtherColor(subrange)
    }
  }
}
