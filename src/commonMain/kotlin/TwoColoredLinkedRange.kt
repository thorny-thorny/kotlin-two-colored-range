package me.thorny.twoColoredRange

open class TwoColoredLinkedRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>, ColorType: Enum<ColorType>>(
  override val range: ClosedRange<BoundType>,
  override val step: LengthType,
  override val math: BoundMath<BoundType, LengthType>,
  override val defaultColor: ColorType,
  override val otherColor: ColorType,
): TwoColoredRange<BoundType, LengthType, ColorType> {
  override val length = math.getLength(range.start, math.add(range.endInclusive, step))
  internal val defaultColorSubranges = mutableListOf(range)

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
      math.getLength(range.start, math.add(range.start, step)) != step
    ) {
      throw Exception("Invalid math $math")
    }
  }

  internal fun checkSubrange(subrange: ClosedRange<BoundType>) {
    if (!range.containsRange(subrange)) {
      throw Exception("Subrange $subrange is out of range bounds $range")
    }

    if (subrange.start > subrange.endInclusive) {
      throw Exception("Invalid subrange $range")
    }
  }

  override fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>> {
    return defaultColorSubranges
  }

  override fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>> {
    return this
      .filter { (_, color) -> color == this.otherColor }
      .map { it.first }
  }

  override fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>> {
    return when (color) {
      defaultColor -> getSubrangesOfDefaultColor()
      otherColor -> getSubrangesOfOtherColor()
      else -> throw Exception("Invalid color $color")
    }
  }

  override fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>) {
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

  override fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>) {
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

  override fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType) {
    when (color) {
      defaultColor -> setSubrangeDefaultColor(subrange)
      otherColor -> setSubrangeOtherColor(subrange)
      else -> throw Exception("Invalid color $color")
    }
  }

  override fun getColor(bound: BoundType): ColorType {
    if (bound < range.start || bound > range.endInclusive) {
      throw Exception("Bound $bound is out of range bounds $range")
    }

    return when (defaultColorSubranges.find { it.start <= bound && it.endInclusive >= bound }) {
      null -> otherColor
      else -> defaultColor
    }
  }

  override fun getSubrangeOfColor(color: ColorType, maxLength: LengthType, segmentRange: ClosedRange<BoundType>): ClosedRange<BoundType>? {
    if (maxLength < step) {
      throw Exception("Max length $maxLength can't be lesser than step $step")
    }

    if (color != defaultColor && color != otherColor) {
      throw Exception("Invalid color $color")
    }

    val fullLengthPair = this.subrangesIterator(segmentRange).asSequence().find { (subrange, subrangeColor) ->
      math.getLength(subrange.start, math.add(subrange.endInclusive, step)) >= maxLength && subrangeColor == color
    }

    if (fullLengthPair != null) {
      val (subrange) = fullLengthPair
      return subrange.makeTypedRange(subrange.start, math.subtract(math.add(subrange.start, maxLength), step))
    }

    val firstMatchingPair = this.subrangesIterator(segmentRange).asSequence().find { (_, subrangeColor) -> subrangeColor == color }
    return firstMatchingPair?.first
  }

  override fun iterator(): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
    return TwoColoredLinkedRangeIterator(this)
  }

  override fun subrangesIterator(segmentRange: ClosedRange<BoundType>): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
    return TwoColoredLinkedRangeIterator(this, segmentRange)
  }
}

open class TwoColoredLinkedRangeIterator<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>, ColorType: Enum<ColorType>>(
  private val parent: TwoColoredLinkedRange<BoundType, LengthType, ColorType>,
  private val segmentRange: ClosedRange<BoundType> = parent.range,
): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
  private var start = segmentRange.start
  private var color = parent.getColor(start)
  private var defaultColorSubrangeIndex = parent.defaultColorSubranges.indexOfFirst {
    when (color) {
      parent.defaultColor -> it.start <= start && it.endInclusive >= start
      else -> it.start >= start
    }
  }

  init {
    parent.checkSubrange(segmentRange)
  }

  override fun hasNext(): Boolean {
    return start <= segmentRange.endInclusive
  }

  override fun next(): Pair<ClosedRange<BoundType>, ColorType> {
    var defaultColorSubrange = parent.defaultColorSubranges.getOrNull(defaultColorSubrangeIndex)
    if (defaultColorSubrange != null) {
      if (defaultColorSubrange.start < segmentRange.start) {
        defaultColorSubrange = defaultColorSubrange.makeTypedRange(maxOf(defaultColorSubrange.start, segmentRange.start), defaultColorSubrange.endInclusive)
      }
      if (defaultColorSubrange.endInclusive > segmentRange.endInclusive) {
        defaultColorSubrange = defaultColorSubrange.makeTypedRange(
          defaultColorSubrange.start,
          minOf(defaultColorSubrange.endInclusive, segmentRange.endInclusive)
        )
      }
    }

    val pair: Pair<ClosedRange<BoundType>, ColorType>

    if (color == parent.defaultColor) {
      pair = defaultColorSubrange!! to color
      color = parent.otherColor
      defaultColorSubrangeIndex += 1
    } else {
      val endInclusive = when (defaultColorSubrange) {
        null -> segmentRange.endInclusive
        else -> minOf(parent.math.subtract(defaultColorSubrange.start, parent.step), segmentRange.endInclusive)
      }
      pair = parent.range.makeTypedRange(start, endInclusive) to color
      color = parent.defaultColor
    }
    start = parent.math.add(pair.first.endInclusive, parent.step)

    return pair
  }
}

open class TwoColoredIntLinkedRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Int>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredLinkedRange<Int, Int, ColorType>(range, 1, IntBoundMath, defaultColor, otherColor)

open class TwoColoredLongLinkedRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Long>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredLinkedRange<Long, Long, ColorType>(range, 1, LongBoundMath, defaultColor, otherColor)
