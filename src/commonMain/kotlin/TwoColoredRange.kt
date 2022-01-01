package me.thorny.twoColoredRange

open class TwoColoredRange<BoundType: Comparable<BoundType>, LengthType, ColorType: Enum<ColorType>>(
  val range: ClosedRange<BoundType>,
  val step: LengthType,
  val math: BoundMath<BoundType, LengthType>,
  val defaultColor: ColorType,
  val otherColor: ColorType,
) {
  val length = math.getLength(math.add(range.endInclusive, step), range.start)
  private val defaultColorSubranges = mutableListOf(range)

  init {
    if (range.start > range.endInclusive) {
      throw Exception("Invalid range $range")
    }

    if (defaultColor == otherColor) {
      throw Exception("Default color $defaultColor can't be equal to other color $otherColor")
    }

    // Better than nothing am I right?
    if (
      math.add(range.start, step) <= range.start ||
      math.subtract(range.endInclusive, step) >= range.endInclusive ||
      math.getLength(math.add(range.start, step), range.start) != step
    ) {
      throw Exception("Invalid math $math")
    }
  }

  private fun checkSubrange(subrange: ClosedRange<BoundType>) {
    if (!range.containsRange(subrange)) {
      throw Exception("Subrange $subrange is out of range bounds $range")
    }

    if (subrange.start > subrange.endInclusive) {
      throw Exception("Invalid subrange $range")
    }
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>> {
    return defaultColorSubranges
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>> {
    val subranges = ArrayDeque<ClosedRange<BoundType>>(defaultColorSubranges.size + 1)
    subranges.add(range)
    defaultColorSubranges.forEach {
      val subrange = subranges.removeLast()
      val splits = subrange.splitByRange(it, step, math)
      splits.forEach { split ->
        subranges.addLast(split)
      }
    }

    return subranges.toList()
  }

  fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>> {
    return when (color) {
      defaultColor -> getSubrangesOfDefaultColor()
      otherColor -> getSubrangesOfOtherColor()
      else -> throw Exception("Invalid color $color")
    }
  }

  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = defaultColorSubranges.filter { it.intersectsRange(subrange) }
    intersectingSubranges.forEach {
      if (subrange.containsRange(it)) {
        defaultColorSubranges.remove(it)
      } else {
        val split = it.splitByRange(subrange, step, math)
        val itIndex = defaultColorSubranges.indexOf(it)
        defaultColorSubranges[itIndex] = split.first()

        val second = split.getOrNull(1)
        if (second != null) {
          defaultColorSubranges.add(itIndex + 1, second)
        }
      }
    }
  }

  fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = defaultColorSubranges.filter { it.intersectsRange(subrange) }
    val touchingSubranges = defaultColorSubranges.filter { it.touchesRange(subrange, step, math) }

    if (intersectingSubranges.isEmpty() && touchingSubranges.isEmpty()) {
      val index = defaultColorSubranges.indexOfLast { it.start < subrange.start }
      defaultColorSubranges.add(index + 1, subrange)
    } else {
      var joinedSubrange = subrange
      var replacedSubrange: ClosedRange<BoundType>? = null

      if (intersectingSubranges.isNotEmpty()) {
        val first = intersectingSubranges.first()
        val last = intersectingSubranges.last()
        for (index in defaultColorSubranges.indexOf(last) downTo defaultColorSubranges.indexOf(first) + 1) {
          defaultColorSubranges.removeAt(index)
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
            defaultColorSubranges.remove(it)
          }
        }
      }

      defaultColorSubranges[defaultColorSubranges.indexOf(replacedSubrange)] = joinedSubrange
    }
  }

  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType) {
    when (color) {
      defaultColor -> setSubrangeDefaultColor(subrange)
      otherColor -> setSubrangeOtherColor(subrange)
      else -> throw Exception("Invalid color $color")
    }
  }
}

open class TwoColoredIntRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Int>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredRange<Int, Int, ColorType>(range, 1, IntBoundMath, defaultColor, otherColor)

open class TwoColoredLongRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Long>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredRange<Long, Long, ColorType>(range, 1, LongBoundMath, defaultColor, otherColor)
