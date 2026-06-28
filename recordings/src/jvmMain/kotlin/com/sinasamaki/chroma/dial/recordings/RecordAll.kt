package com.sinasamaki.chroma.dial.recordings

import java.io.File

private val outputDir = File("../docs/public")

fun recordAll() {
    recorder(HeroAnimation(), outputDir)
    recorder(IntroArcConfigAnimation(), outputDir)
    recorder(IntroMultiRingAnimation(), outputDir)
    recorder(BasicFullCircleAnimation(), outputDir)
    recorder(BasicSemiCircleAnimation(), outputDir)
    recorder(BasicSteppedAnimation(), outputDir)
    recorder(BasicMultiRotationAnimation(), outputDir)
    recorder(BasicProgrammaticAnimation(), outputDir)
    recorder(BasicArcShapeAnimation(), outputDir)
    recorder(BasicSnappingAnimation(), outputDir)
    recorder(BasicCounterclockwiseAnimation(), outputDir)
    recorder(BasicMappedValueAnimation(), outputDir)
    recorder(BentoOvershootAnimation(), outputDir)
    recorder(BentoDrawUtilitiesAnimation(), outputDir)
    recorder(BentoDefaultDialAnimation(), outputDir)
    recorder(CustomInvisibleThumbAnimation(), outputDir)
    recorder(CustomOvershootAnimation(), outputDir)
    recorder(CustomTickMarksAnimation(), outputDir)
    recorder(CustomGradientAnimation(), outputDir)
    recorder(StateOvershootAnimation(), outputDir)
    recorder(DialColorsAnimation(), outputDir)
    recorder(InputInteractionAnimation(), outputDir)
    recorder(LayoutGaugeAnimation(), outputDir)
}
