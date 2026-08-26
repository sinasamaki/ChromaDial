package com.sinasamaki.chroma.dial.recordings

import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    if ("record" in args) {
        // Any args after "record" are treated as a name filter, e.g. `record dial_colors`.
        val filter = args.filterNot { it == "record" }.toSet()
        SwingUtilities.invokeAndWait { recordAll(filter) }
    } else {
        showPreviewWindow()
    }
}
