package com.sinasamaki.chroma.dial.recordings

import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    if ("record" in args) {
        SwingUtilities.invokeAndWait { recordAll() }
    } else {
        showPreviewWindow()
    }
}
