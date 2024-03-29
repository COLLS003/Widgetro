package my.app.widgetro
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun convertVideoToImages(videoFile: File, outputDir: File) {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(videoFile.path)

    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val duration = durationStr?.toLong() ?: 0L

    val frameRateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
    val frameRate = frameRateStr?.toFloat() ?: 30f

    val numFrames = (duration / 1000 * frameRate).toInt()

    for (i in 0 until numFrames) {
        val timeUs = (i * 1000000L / frameRate).takeIf { it < duration * 1000 } ?: duration * 1000 - 1000
        val bitmap = retriever.getFrameAtTime(timeUs as Long, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        saveBitmapToFile(bitmap, File(outputDir, "image_$i.jpg"))
    }

    retriever.release()
}

fun saveBitmapToFile(bitmap: Bitmap?, outputFile: File) {
    if (bitmap == null) return

    try {
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
