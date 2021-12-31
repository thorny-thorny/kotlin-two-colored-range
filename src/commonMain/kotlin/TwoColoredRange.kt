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
  val length = math.getLength(math.add(range.endInclusive, step), range.start)
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
  fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>> {
    val subranges = ArrayDeque<ClosedRange<BoundType>>(otherColorSubranges.size + 1)
    subranges.add(range)
    otherColorSubranges.forEach {
      val subrange = subranges.removeLast()
      val splits = subrange.splitByRange(it, step, math)
      splits.forEach { split ->
        subranges.addLast(split)
      }
    }

    return subranges.toList()
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>> {
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
  fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = otherColorSubranges.filter { it.intersectsRange(subrange) }
    intersectingSubranges.forEach {
      if (subrange.containsRange(it)) {
        otherColorSubranges.remove(it)
      } else {
        val split = it.splitByRange(subrange, step, math)
        val itIndex = otherColorSubranges.indexOf(it)
        otherColorSubranges[itIndex] = split.first()

        val second = split.getOrNull(1)
        if (second != null) {
          otherColorSubranges.add(itIndex + 1, second)
        }
      }
    }
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = otherColorSubranges.filter { it.intersectsRange(subrange) }
    val touchingSubranges = otherColorSubranges.filter { it.touchesRange(subrange, step, math) }

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
      defaultColor -> setSubrangeDefaultColor(subrange)
      otherColor -> setSubrangeOtherColor(subrange)
    }
  }
}
