package my.app.widgetro
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object BitmapUtils {

    /**
     * Decode and scale a bitmap from a file path to fit the specified width and height.
     *
     * @param filePath The path to the image file.
     * @param reqWidth The width to fit the image into.
     * @param reqHeight The height to fit the image into.
     * @return The decoded and scaled bitmap.
     */
    fun decodeSampledBitmapFromFile(filePath: File, reqWidth: Int, reqHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath.toString(), options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath.toString(), options)
    }

    /**
     * Calculate the sample size for decoding the bitmap based on the requested width and height.
     *
     * @param options The BitmapFactory.Options object containing the original image dimensions.
     * @param reqWidth The width to fit the image into.
     * @param reqHeight The height to fit the image into.
     * @return The sample size to use for decoding the bitmap.
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of the image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smaller ratio as inSampleSize value to ensure the final image is both
            // smaller than or equal to the requested height and width, and is a power of 2.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }
}
