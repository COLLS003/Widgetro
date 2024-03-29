package my.app.widgetro
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val frameRate = 1 // Number of frames to extract per second
    private lateinit var outputDir: String // Output directory for storing the images
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var imageContainer: LinearLayout
    private val PICK_VIDEO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        setupViews()

        // Setup button click listener
        setupButton()
    }

    private fun setupViews() {
        outputDir = getExternalFilesDir(null)?.absolutePath + File.separator + "outputImages" // Output directory for storing the images
        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        imageContainer = findViewById(R.id.imageContainer)
    }

    private fun setupButton() {
        val uploadButton: Button = findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            // Call the function to handle file upload
            selectVideoFile()
        }
    }

    private fun selectVideoFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
            val selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                // Call the function to convert selected video to images
                convertVideoToImages(selectedVideoUri)
            }
        }
    }

    private fun convertVideoToImages(videoUri: Uri) {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(this, videoUri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            val totalFrames = (duration / 1000 * frameRate).toInt()

            // Clear the existing images
            imageContainer.removeAllViews()

            for (i in 0 until totalFrames) {
                val time = (i * 1000 / frameRate).toLong()
                val frame = retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST)
                frame?.let {
                    val imageView = ImageView(this)
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    imageView.setImageBitmap(it)
                    imageContainer.addView(imageView)
                }
            }

            Toast.makeText(this, "Video converted to images successfully!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Video converted to images successfully!")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error converting video to images", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error converting video to images: ${e.message}")
        } finally {
            retriever.release()
        }
    }

    private fun reloadImages() {
        // Clear existing images
        imageContainer.removeAllViews()

        // Load images from output directory
        val outputDirectory = File(outputDir)
        if (outputDirectory.exists() && outputDirectory.isDirectory) {
            val files = outputDirectory.listFiles()
            files?.forEach { file ->
                val imageView = ImageView(this)
                imageView.setImageURI(Uri.fromFile(file))
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                imageView.layoutParams = layoutParams
                imageContainer.addView(imageView)
            }
        }

        // Scroll to the last image
        horizontalScrollView.postDelayed({
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }, 100)
    }
}
