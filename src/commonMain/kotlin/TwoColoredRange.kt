package me.thorny.twoColoredRange

interface TwoColoredRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>, ColorType: Enum<ColorType>>: Iterable<Pair<ClosedRange<BoundType>, ColorType>> {
  val range: ClosedRange<BoundType>
  val step: LengthType
  val math: BoundMath<BoundType, LengthType>
  val defaultColor: ColorType
  val otherColor: ColorType
  val length: LengthType

  fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>>
  fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>>
  fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>>

  fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>)
  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>)
  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType)

  fun getColor(bound: BoundType): ColorType
  fun getSubrangeOfColor(color: ColorType, maxLength: LengthType = step, segmentRange: ClosedRange<BoundType> = range): ClosedRange<BoundType>?
  fun subrangesIterator(segmentRange: ClosedRange<BoundType> = range): Iterator<Pair<ClosedRange<BoundType>, ColorType>>
}
