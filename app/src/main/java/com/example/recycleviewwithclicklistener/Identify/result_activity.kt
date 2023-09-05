package com.example.recycleviewwithclicklistener.Identify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.example.recycleviewwithclicklistener.Collection.Identify
import com.example.recycleviewwithclicklistener.Description.Mangrove
import com.example.recycleviewwithclicklistener.Collection.MangroveModel
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.Collection.SQLiteHelper
import com.example.recycleviewwithclicklistener.ml.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.hdodenhof.circleimageview.CircleImageView
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class result_activity: AppCompatActivity() {
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var photoBitmap: Bitmap
    private lateinit var mangrovelist: ArrayList<Mangrove>
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val shareBtn: Button = findViewById(R.id.shareBtn)
        val collectionBtn: Button = findViewById(R.id.collectionBtn)

        val text = "Store to MyCollection"
        val startIndex = text.indexOf("MyCollection")

        val spannable = SpannableStringBuilder(text)
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC), // specify the text style (in this case, bold)
            startIndex,
            startIndex + "MyCollection".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // specify the spannable flags
        )

        collectionBtn.text = spannable

        // Inflate the second layout
        val inflater = LayoutInflater.from(this)
        val shareLayout = inflater.inflate(R.layout.activity_share, null)

        // Initialize the mangrovelist
        mangrovelist = ArrayList()
        addDataToList()

        // Get the parent layout of the main layout
        val shareContainer = findViewById<FrameLayout>(R.id.framelayout)
        // Add the second layout to the parent layout
        shareContainer.addView(shareLayout)

        sqLiteHelper = SQLiteHelper(this)

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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
            val scaledWidth = bitmap.width / 4
            val scaledHeight = bitmap.height / 4

            // Create the scaled bitmap using the createScaledBitmap method
            photoBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
        }else{
            photoBitmap = bitmap

        }

        // Set the image captured to imageview
        val photoImageView: CircleImageView = findViewById(R.id.result_image)
        photoImageView.setImageBitmap(photoBitmap)

        // for scan leaf or not leaf
        val input: InputStream = this.assets.open("leaf_nonLeaf")
        val leaf = input.bufferedReader().use { it.readLines() }

        // for scan mangrove leaf or not mangrove leaf
        val inputM: InputStream = this.assets.open("nMM")

        // for predict species
        val inputStream: InputStream = this.assets.open("label_result")
        val labels = inputStream.bufferedReader().use { it.readLines() }

        val resultTextView: TextView = findViewById(R.id.result)
        val description: TextView = findViewById(R.id.description)
        val commonName: TextView = findViewById(R.id.commonName)

        if(photoBitmap!=null){

            // call the loading class
            val loading = LoadingDialog(this)
            loading.startLoading()
            val handler = Handler(Looper.getMainLooper())

            // loading
            handler.post {
                // Run the prediction model on the photoBitmap and get the result
                val scanleaf = runLeaf(photoBitmap)
                val leafNot = leaf[scanleaf]

                if (leafNot == "Leaf"){
                    val predictionResult = runPredictionModel(photoBitmap)

                    resultTextView.text = labels[predictionResult].toString()
                    val words = labels[predictionResult].toString()
                    loading.isDismiss()

                    // function to get the explanation and common name according to result (species)
                    fun getExplanationByName(words: String): Pair<String, String> {
                        for (mangrove in mangrovelist) {
                            if (mangrove.name == words) {
                                val explanation = mangrove.explanation
                                val commonName = mangrove.commonName
                                return Pair(explanation, commonName)
                            }
                        }
                        return Pair("", "")
                    }


                    // call the function to get description and common name from array list
                    val(data1, data2) = getExplanationByName(words)
                    description.text = data1
                    commonName.text = data2

                    // link to wiki
                    val baseUrl = "https://en.wikipedia.org/wiki/"
                    val queryString = "$words"
                    val url = baseUrl + queryString

                    // link to wiki when click on image
                    photoImageView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }

                    val cardViewContainer: LinearLayout = findViewById(R.id.cardViewContainer)
                    val cardView: CardView = cardViewContainer.findViewById(R.id.cardview)
                    val cardimageView: ImageView = cardView.findViewById(R.id.cardview_image)
                    val cardtextView: TextView = cardView.findViewById(R.id.cardview_text)
                    val quote: TextView = cardView.findViewById(R.id.cardview_quote)


                    // textlist for sharing quote
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

                    // set the text and image on sharing card
                    val randomText = textList.random() // Generate a random text string from the text list
                    quote.text = randomText
                    cardimageView.setImageBitmap(photoBitmap)
                    cardtextView.text = labels[predictionResult].toString()

                    // cardView for sharing card
                    cardViewContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // Remove the listener to avoid multiple calls
                            cardViewContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            // // Load content into imageView and textView here
                            val bitmap = cardViewContainer.drawToBitmap()

                            // share
                            shareBtn.setOnClickListener {
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = "image/*"
                                val bytes = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

                                val path = MediaStore.Images.Media.insertImage(
                                    contentResolver,
                                    bitmap,
                                    "PlantMangro: Sharing is Caring!",
                                    null
                                )

                                val uri = Uri.parse(path)
                                intent.putExtra(Intent.EXTRA_STREAM, uri)
                                startActivity(Intent.createChooser(intent, "Share Image"))
                            }

                            // add the mangrove into local database
                            @RequiresApi(Build.VERSION_CODES.O)
                            fun addMangrove(){
                                val name = labels[predictionResult].toString()

                                if(name.isEmpty()){
                                    Toast.makeText(this@result_activity,"Something wrong with result", Toast.LENGTH_SHORT).show()
                                }else{
                                    val stream = ByteArrayOutputStream()
                                    photoBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                                    val image = stream.toByteArray()

                                    // Get the current date
                                    val calendar = Calendar.getInstance()
                                    val currentDateTime = calendar.time

                                    // Format the date as a string
                                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                                    val dateString = dateFormat.format(currentDateTime)

                                    getLocation { location ->
                                        // Use the location string here
                                        val loca =  location
                                        val localist = loca.split(",")
                                        val latitude = localist[0]
                                        val longitude = localist[1]

                                        //add the data into the data class
                                        val mg = MangroveModel(name= name, date=dateString, latitude = latitude, longitude = longitude, image = image)
                                        val status = sqLiteHelper.insertMangrove(mg)

                                        //Check insert success of not success
                                        if(status>-1){
                                            Toast.makeText(this@result_activity,"Mangrove Added...", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(this@result_activity,"Record not saved...", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }

                            // add to collection
                            collectionBtn.setOnClickListener {
                                Toast.makeText(this@result_activity, "Clicked store button", Toast.LENGTH_SHORT).show()
                                addMangrove()
                                }
                        }
                        }
                    )
                }
                else {
                    loading.isDismiss()
                    val start_intent = Intent(this, Identify::class.java)
                    startActivity(start_intent)
                    Toast.makeText(
                        this,
                        "This is not a mangrove leaf, please try again..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // pre-processing the image
    val imageProcessor = ImageProcessor.Builder()
        // Resize image to (224,224)
        .add(ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
        // Normalize the image
        .add(NormalizeOp(0.0f,225.0f))
        .build()


    // get current location
    fun getLocation(callback: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this@result_activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@result_activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101)
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if(it != null){
                val latlong = "${it.latitude},${it.longitude}"
                callback(latlong)
            }
        }
    }

    private fun runLeaf (photoBitmap: Bitmap): Int{
        var tensorImage =  TensorImage(DataType.FLOAT32)
        // pass bitmap
        tensorImage.load(photoBitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val model = NonleafLeaf.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var leafORNon = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[leafORNon] < fl) {
                leafORNon = index
            }
        }

        // Releases model resources if no longer used.
        model.close()
        return leafORNon
    }

    // differentiate mangrove leaf vs non-mangrove leaf
    private fun runMangrove (photoBitmap: Bitmap): Int{
        var tensorImage =  TensorImage(DataType.FLOAT32)
        // pass bitmap
        tensorImage.load(photoBitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val model = NmmLatest.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var MORNonM = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[MORNonM] < fl) {
                MORNonM = index
            }
        }

        // Releases model resources if no longer used.
        model.close()
        return MORNonM
    }

    // predict species
    private fun runPredictionModel(photoBitmap: Bitmap): Int {

        var tensorImage =  TensorImage(DataType.FLOAT32)
        // pass bitmap
        tensorImage.load(photoBitmap)

        tensorImage = imageProcessor.process(tensorImage)
        val background = detectBackground(photoBitmap)
        if (background == "This image has a white background") {
            val model = Thiswhite.newInstance(this)

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
            val model = This.newInstance(this)

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

    // detect background (white or complex)
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
                if (color == Color.GRAY || color == Color.WHITE){
                    whitePixelCount++
                }
            }
        }
        // Decide whether the background is white or complex
        val whitePixelPercentage = whitePixelCount.toFloat() / (width * height)
        val whiteBackgroundThreshold = 0.7f
        return if (whitePixelPercentage > 0.5) {
            Toast.makeText(this, "This image has a white background", Toast.LENGTH_SHORT).show()
            "This image has a white background"
        } else {
            Toast.makeText(this, "This image has a complex background", Toast.LENGTH_SHORT).show()
            "This image has a complex background"
        }

    }

    // add data to arraylist to retrieve
    private fun addDataToList(){
        mangrovelist.add(Mangrove(R.drawable.amarina, "Avicennia marina", "Api-api Jambu","Also known as Api-api Jambu, is a mangrove tree. Unlike other species, the young branches of Api-api Jambu is distinctly square shaped. The fruit is greyish green with a short beak at the tip.","Salt-tolerant mangrove with multiple ecological benefits."))
        mangrovelist.add(Mangrove(R.drawable.aofficinalis, "Avicennia officinalis", "Api Api Ludat", "Also known as Api Api Ludat, is a mangrove tree. It has large orange-yellow flowers that smell rancid. The leaves are oblong shaped and the underside are distinctly yellowish green.", "Medicinal, salt-tolerant mangrove species."))
        mangrovelist.add(Mangrove(R.drawable.bsexangula, "Bruguiera sexangula","Tumu Mata Buaya" , "A mangrove tree species that is commonly found in the coastal regions of Southeast Asia, including India, Bangladesh, and Sri Lanka. It is a salt-tolerant plant that grows in mudflats and tidal creeks and provides important habitats for various species of wildlife, including fish, crustaceans, and birds. It is also known for its strong, durable wood and is used for construction, fuel, and other purposes.", "Dense, saltwater-tolerant mangrove species."))
        mangrovelist.add(Mangrove(R.drawable.rapiculata, "Rhizophora apiculata","Bakau Minyak" ,"Also known as the mangrove red apple, is a species of mangrove tree native to Southeast Asia. It is commonly found in tidal mudflats and intertidal zones, and is known for its remarkable ability to survive in harsh coastal environments. The tree produces a red fruit that is important to wildlife, and provides essential habitat and protection for many species of marine life.", "Mangrove tree with red aerial roots."))
        mangrovelist.add(Mangrove(R.drawable.scaseolaris, "Sonneratia caseolaris", "Berembang", "A mangrove plant species with aerial prop roots, leathery leaves, and fragrant white flowers that bloom at night. It has salt-tolerant adaptations and is used in traditional medicine and as a source of food and fuel in coastal areas.", "Salt-tolerant mangrove plant with edible fruit"))
    }

}