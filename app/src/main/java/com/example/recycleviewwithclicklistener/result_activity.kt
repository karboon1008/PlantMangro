package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.drawToBitmap
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.recycleviewwithclicklistener.ml.VggComplex8020Converted
import com.example.recycleviewwithclicklistener.ml.VggWhite8020Converted
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.InputStream


class result_activity: AppCompatActivity() {
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var photoBitmap: Bitmap
    private lateinit var btn_add: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_result)
        sqLiteHelper = SQLiteHelper(this)

        //receive uri from custom camera
        val intent = intent
        val uriString = intent.getStringExtra("image")
        val uri = Uri.parse(uriString)


        //convert uri to bitmap
        val `is` = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(`is`)
        `is`!!.close()

        if(bitmap.width>1024) {
            // Specify the new width and height of the scaled bitmap
            val scaledWidth = bitmap.width / 2
            val scaledHeight = bitmap.height / 2

            // Create the scaled bitmap using the createScaledBitmap method
            photoBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
        }else{
            photoBitmap = bitmap

        }

        // Set the image captured to imageview
        val photoImageView: ImageView = findViewById(R.id.result_image)
        photoImageView.setImageBitmap(photoBitmap)



        val inputStream: InputStream = this.assets.open("label_result")
        val labels = inputStream.bufferedReader().use { it.readLines() }

        val progressbar:ProgressBar = findViewById(R.id.progressbar)
        val predictButton: Button = findViewById(R.id.button2)

        predictButton.setOnClickListener {
            progressbar.setVisibility(VISIBLE)
            if(photoBitmap!=null){
                // Run the prediction model on the photoBitmap and get the result
                val predictionResult = runPredictionModel(photoBitmap)
                progressbar.isInvisible
                val resultTextView: TextView =findViewById(R.id.result)
                resultTextView.text = labels[predictionResult].toString()

                val words = labels[predictionResult].toString()

                //share
                val baseUrl = "https://en.wikipedia.org/wiki/"
                val queryString = "$words"
                val url = baseUrl + queryString

                photoImageView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }

                val cardViewContainer: LinearLayout = findViewById(R.id.cardViewContainer)
                val cardView: CardView = cardViewContainer.findViewById(R.id.cardview)
                val imageView: ImageView = cardView.findViewById(R.id.cardview_image)
                val textView: TextView = cardView.findViewById(R.id.cardview_text)
                val quote: TextView = cardView.findViewById(R.id.cardview_quote)
                val shareBtn: Button = findViewById(R.id.shareBtn)
                val collectionBtn: Button = findViewById(R.id.collectionBtn)


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
                                contentResolver,
                                bitmap,
                                "MangroveID: Sharing is Caring!",
                                null
                            )

                            val uri = Uri.parse(path)
                            intent.putExtra(Intent.EXTRA_STREAM, uri)
                            startActivity(Intent.createChooser(intent, "Share Image"))
                        }

                        fun addMangrove(){
                            val name = labels[predictionResult].toString()

                            if(name.isEmpty()){
                                Toast.makeText(this@result_activity,"Something wrong with result", Toast.LENGTH_SHORT).show()
                            }else{

                                val stream = ByteArrayOutputStream()
                                photoBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                                val image = stream.toByteArray()

                                val mg = MangroveModel(name= name, image = image)
                                val status = sqLiteHelper.insertMangrove(mg)
                                //Check insert success of not success
                                if(status>-1){
                                    Toast.makeText(this@result_activity,"Mangrove Added...", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this@result_activity,"Record not saved...", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }


                        collectionBtn.setOnClickListener{
                            collectionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_24))
                            addMangrove()

                        }

                    }
                })
            }
        }
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
            val model = VggWhite8020Converted.newInstance(this)

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
            val model = VggComplex8020Converted.newInstance(this)

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
            Toast.makeText(this, "This image has a white background", Toast.LENGTH_SHORT).show()
            "This image has a white background"
        } else {
            Toast.makeText(this, "This image has a complex background", Toast.LENGTH_SHORT).show()
            "This image has a complex background"
        }

    }
}