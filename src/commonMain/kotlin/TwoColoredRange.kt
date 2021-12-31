package me.thorny.twoColoredRange

open class TwoColoredRange<BoundType: Comparable<BoundType>, LengthType, ColorType: Enum<ColorType>>(
  @Suppress("MemberVisibilityCanBePrivate")
  val range: ClosedRange<BoundType>,
  @Suppress("MemberVisibilityCanBePrivate")
  val step: LengthType,
  @Suppress("MemberVisibilityCanBePrivate")
  val math: BoundMath<BoundType, LengthType>,
  @Suppress("MemberVisibilityCanBePrivate")
  val defaultColor: ColorType,
  @Suppress("MemberVisibilityCanBePrivate")
  val otherColor: ColorType,
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

  private fun checkSubrange(subrange: ClosedRange<BoundType>) {
    if (!range.containsRange(subrange)) {
      throw Exception("Subrange is out of range bounds")
    }

    if (subrange.start > subrange.endInclusive) {
      throw Exception("Subrange start can't be greater than end")
    }
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun getSubrangesOfDefaultColor(): Collection<ClosedRange<BoundType>> {
    return arrayListOf(range)
  }

  @Suppress("MemberVisibilityCanBePrivate")
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

  @Suppress("MemberVisibilityCanBePrivate")
  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>) {
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

        touchingSubranges.reversed().forEachIndexed { index, it ->
          joinedSubrange = joinedSubrange.joinRange(it)
          if (index != touchingSubranges.lastIndex || intersectingSubranges.isNotEmpty()) {
            otherColorSubranges.remove(it)
          }
        }
      }

      otherColorSubranges[otherColorSubranges.indexOf(replacedSubrange)] = joinedSubrange
    }
  }

  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType) {
    when (color) {
      otherColor -> setSubrangeOtherColor(subrange)
    }
  }
}
