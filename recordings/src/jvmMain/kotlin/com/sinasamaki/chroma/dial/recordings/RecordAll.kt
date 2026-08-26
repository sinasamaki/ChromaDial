package com.sinasamaki.chroma.dial.recordings

import java.io.File

private val outputDir = File("../docs/public")

private val allAnimations: List<AnimationDefinition>
    get() = listOf(
        HeroAnimation(),
        IntroArcConfigAnimation(),
        IntroMultiRingAnimation(),
        BasicFullCircleAnimation(),
        BasicSemiCircleAnimation(),
        BasicSteppedAnimation(),
        BasicMultiRotationAnimation(),
        BasicProgrammaticAnimation(),
        BasicArcShapeAnimation(),
        BasicSnappingAnimation(),
        BasicCounterclockwiseAnimation(),
        BasicMappedValueAnimation(),
        BentoOvershootAnimation(),
        BentoDrawUtilitiesAnimation(),
        BentoDefaultDialAnimation(),
        CustomInvisibleThumbAnimation(),
        CustomOvershootAnimation(),
        CustomTickMarksAnimation(),
        CustomGradientAnimation(),
        StateOvershootAnimation(),
        DialColorsAnimation(),
        InputInteractionAnimation(),
        LayoutGaugeAnimation(),
    )

// When [filter] is non-empty, only animations whose name is in the set are recorded.
fun recordAll(filter: Set<String> = emptySet()) {
    allAnimations
        .filter { filter.isEmpty() || it.name in filter }
        .forEach { recorder(it, outputDir) }
}
