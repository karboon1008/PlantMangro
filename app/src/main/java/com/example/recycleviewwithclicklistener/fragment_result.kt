package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.example.recycleviewwithclicklistener.ml.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.InputStream


class fragment_result : Fragment() {

    companion object {
        const val ARG_PHOTO_BITMAP = "image"

        fun newInstance(photoBitmap: Bitmap): fragment_result {
            val args = Bundle()
            args.putParcelable(ARG_PHOTO_BITMAP, photoBitmap)

            val fragment = fragment_result()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var photoBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoBitmap = it.getParcelable(ARG_PHOTO_BITMAP)?:return
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val photoImageView: ImageView = view.findViewById(R.id.result_image)
        photoImageView.setImageBitmap(photoBitmap)

        val inputStream: InputStream = requireContext().assets.open("label_result")
        val labels = inputStream.bufferedReader().use { it.readLines() }

        val predictButton: Button = view.findViewById(R.id.button2)
        predictButton.setOnClickListener {
            // Run the prediction model on the photoBitmap and get the result
            val predictionResult = runPredictionModel(photoBitmap)
            val resultTextView: TextView = view.findViewById(R.id.result)
            resultTextView.text = labels[predictionResult].toString()

            val words = labels[predictionResult].toString()
            val baseUrl = "https://en.wikipedia.org/wiki/"
            val queryString = "$words"
            val url = baseUrl + queryString

            photoImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }



            val cardViewContainer: LinearLayout = view.findViewById(R.id.cardViewContainer)
            val cardView: CardView = cardViewContainer.findViewById(R.id.cardview)
            val imageView: ImageView = cardView.findViewById(R.id.cardview_image)
            val textView: TextView = cardView.findViewById(R.id.cardview_text)
            val quote: TextView = cardView.findViewById(R.id.cardview_quote)
            val shareBtn: Button = view.findViewById(R.id.shareBtn)


            val textList = listOf("\"Mangroves are not just trees, they are an ecosystem and they have been protecting us for thousands of years. When we protect them, we protect ourselves and our future.\" - Angélica María García Arzola, Mexican conservationist and community organizer.",
                "\"Mangroves are the cornerstone of the coastal ecosystem, their root systems preventing erosion, filtering out pollution and providing a vital habitat for wildlife.\" - Richard Branson",
                "\"Mangroves provide essential goods and services, like protecting our coasts from storms and sea level rise, improving water quality, and providing habitat for fish and wildlife.\" - The Nature Conservancy",
                "\"Mangroves are like the superheroes of our coasts, providing a range of ecological and economic services that are critical to the health and well-being of coastal communities.\" - Michael Beck",
                "\"Mangroves are our first line of defense against climate change, protecting us from storms and providing us with the oxygen we breathe.\" - Ban Ki-moon",
                "\"Let the mangroves remind us that the things we do on land impact the things we cherish in the sea.\" - Karen A. Brown",
                "\"In the midst of adversity, the mangrove stands strong and resilient, a symbol of endurance and adaptability.\" - Unknown",
                "\"Mangroves are the lungs of the coast, providing vital oxygen and a home for a myriad of marine life.\" - Sir David Attenborough",
                "\"The survival of mangrove ecosystems is not just a matter of conservation, but also of human survival.\" - Amartya Sen",
                "\"Mangroves are a symbol of resilience and adaptation. They have survived and thrived in some of the most challenging environments on earth.\" - Celine Cousteau",
                "\"The roots of mangrove trees run deep and wide, just like the community and relationships that grow around them.\" - Sharon Salzberg",
                "\"Mangroves are a natural defense against the forces of nature, protecting coastlines from storms, floods, and erosion.\" - Ban Ki-moon",
                "\"Mangroves are not just plants, they are entire ecosystems that support a complex web of life, from microbes to mammals.\" - Dr. Jane Goodall",
                "\"Mangroves have the unique ability to tolerate saltwater, making them well adapted to coastal environments.\" - National Geographic",
                "\"The roots of mangrove plants help to stabilize the soil, preventing erosion and protecting coastlines from storm surges.\" - Smithsonian Magazine",
                "\"Mangrove forests are home to a variety of wildlife, including monkeys, snakes, and crocodiles.\" - World Wildlife Fund",
                "\"Mangrove plants play an important role in filtering pollutants and absorbing carbon from the atmosphere, helping to mitigate the effects of climate change.\" - National Ocean Service",
                "\"The bark of mangrove plants is used to make traditional medicines, while the leaves and branches are used as animal feed.\" - ScienceDirect.",
                "\"Mangrove plants have adapted to survive in low-oxygen environments, with specialized roots that can take in air directly from the atmosphere.\" - Florida Museum",
                "\"The wood of mangrove plants is highly prized for its durability and resistance to decay, making it a valuable resource for building and construction.\" - The New York Times",
                "\"Mangrove plants are an important source of livelihood for millions of people around the world, providing food, fuel, and other resources.\" - Food and Agriculture Organization of the United Nations",
                "\"Mangrove forests are among the most productive ecosystems on earth, with a high level of biodiversity and a variety of ecosystem services.\" - Yale Environment 360"
                ) // Add all the quotes to assign to the TextViews to a list

            val randomText = textList.random() // Generate a random text string from the text list
            quote.text = randomText


            imageView.setImageBitmap(photoBitmap)
            textView.text = labels[predictionResult].toString()

            cardViewContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Remove the listener to avoid multiple calls
                    cardViewContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // // Load content into imageView and textView here
                    val bitmap = cardViewContainer.drawToBitmap()

                    shareBtn.setOnClickListener {
                        // Do something with the bitmap
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

                        val path = MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            bitmap,
                            "MangroveID: Sharing is Caring!",
                            null
                        )

                        val uri = Uri.parse(path)
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(intent, "Share Image"))
                    }
                }
            })
        }

        return view
    }

    val imageProcessor = ImageProcessor.Builder()
        // Normalize the image
        //.add(NormalizeOp(0.0f,225.0f))
        // Resize image to (224,224)
        .add(ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
        .build()


    private fun runPredictionModel(photoBitmap: Bitmap): Int {
        var tensorImage =  TensorImage(DataType.FLOAT32)
        // pass bitmap
        tensorImage.load(photoBitmap)

        tensorImage = imageProcessor.process(tensorImage)
        val background = detectBackground(photoBitmap)
        if (background == "This image has a white background") {
            val model = VggWhite8020Converted.newInstance(requireContext() )

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed { index, fl ->
                if (outputFeature0[maxIdx] < fl) {
                    maxIdx = index
                }
            }


            // Releases model resources if no longer used.
            model.close()
            return maxIdx
        }
        else{
            val model = VggComplex8020Converted.newInstance(requireContext() )

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed { index, fl ->
                if (outputFeature0[maxIdx] < fl) {
                    maxIdx = index
                }
            }
            // Releases model resources if no longer used.
            model.close()
            return maxIdx
        }
    }
    fun detectBackground(image: Bitmap): String {
        // Convert the image to grayscale
        val width = image.width
        val height = image.height
        val grayImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = image.getPixel(x, y)
                val gray = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
                grayImage.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }

        // Apply a threshold to the grayscale image
        val threshold = 128
        val thresholdedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = grayImage.getPixel(x, y)
                val gray = Color.red(color)
                if (gray > threshold) {
                    thresholdedImage.setPixel(x, y, Color.WHITE)
                } else {
                    thresholdedImage.setPixel(x, y, Color.BLACK)
                }
            }
        }

        // Count the number of white pixels in the thresholded image
        var whitePixelCount = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = thresholdedImage.getPixel(x, y)
                if (color == Color.WHITE) {
                    whitePixelCount++
                }
            }
        }
        // Decide whether the background is white or complex
        val whitePixelPercentage = whitePixelCount.toFloat() / (width * height)
        val whiteBackgroundThreshold = 0.7f
        return if (whitePixelPercentage > whiteBackgroundThreshold) {
            Toast.makeText(requireContext(), "This image has a white background", Toast.LENGTH_SHORT).show()
            "This image has a white background"
        } else {
            Toast.makeText(requireContext(), "This image has a complex background", Toast.LENGTH_SHORT).show()
            "This image has a complex background"
        }

    }



}
